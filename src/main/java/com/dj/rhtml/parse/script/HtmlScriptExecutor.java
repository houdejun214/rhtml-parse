package com.dj.rhtml.parse.script;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.mozilla.javascript.tools.shell.Global;

import com.dj.rhtml.parse.script.ScriptHook.HookFunctionObject;
import com.dj.rhtml.utils.JSFileUtils;

public class HtmlScriptExecutor implements Closeable {

	private static String ENV_JS_PATH = "envjs/env.rhino.1.2.35.js";
	
	private final Context cx;

	private final ScriptCache scriptCache = new ScriptCache(32);

	private final Global global;
	
	public HtmlScriptExecutor() {
		cx = Context.enter();
		cx.setOptimizationLevel(-1);
		cx.setLanguageVersion(Context.VERSION_1_5);
		global = new Global();
		global.init(cx);
		this.loadJS(ENV_JS_PATH);
	}

	/**
	 * load js and execute it.
	 * @param path
	 */
	public void loadJS(String path) {
		try {
			this.processFileSecure(cx, global, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * set execute script block automatic
	 */
	public void setAutoExcuteScript(){
		this.evalScript("Envjs({ scriptTypes: { \"\": true, \"text/javascript\": true } });");
	}
	
	/**
	 * eval a block of script code.
	 * @param scriptText
	 * @return
	 */
	public Object evalScript(String scriptText) {
		try {
			Script script = cx.compileString(scriptText, "<command>", 1, null);
			if (script != null) {
				Object result = script.exec(cx, global);
				return result;
			}
		} catch (RhinoException rex) {
			ToolErrorReporter.reportException(cx.getErrorReporter(), rex);
		} catch (VirtualMachineError ex) {
			// Treat StackOverflow and OutOfMemory as runtime errors
			ex.printStackTrace();
			String msg = ToolErrorReporter.getMessage(
					"msg.uncaughtJSException", ex.toString());
			Context.reportError(msg);
		}
		return null;
	}
	
	public void applyHookOperation(String script,ScriptHook hook){
		this.loadJS("hook.js");
		Function fn = (Function) global.get("__hook_init", global);
		try {
			//-- Get a reference to the instance method this is to be made available in javascript as a global function.
			Method scriptableInstanceMethod = ScriptHook.class.getMethod("apply", new Class[]{Object.class});
			////-- Create the FunctionObject that binds the above function name to the instance method.
			HookFunctionObject hookFunction = new HookFunctionObject("hookFunction",scriptableInstanceMethod, hook);
			fn.call(cx, global, global, new Object[]{hookFunction});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.evalScript(script);
	}
	
	public void updateDocument(String html,String url){
		this.evalScript("document.baseURI = new Location(\""+url+"\", document);");
		applyDocumentMethod("write",new Object[]{html});
		applyDocumentMethod("close",new Object[]{});
	}
	
	public void documentWrite(String html){
		applyDocumentMethod("write",new Object[]{html});
	}
	
	public String getDocumentHtml(){
		return cx.toString(this.getDocumentProperty("innerHTML"));
	}
	
	public Object applyDocumentMethod(String name,Object[] args){
		Scriptable obj = (Scriptable) global.get("document", global);
		Function fn = (Function) ScriptableObject.getProperty(obj, name);
		return fn.call(cx, global, obj, args);
	}
	
	public Object getDocumentProperty(String name){
		Scriptable obj = (Scriptable) global.get("document", global);
		Object property = ScriptableObject.getProperty(obj, name);
		return property;
	}

	private void processFileSecure(Context cx, Scriptable scope, String path) throws IOException {
		Object source = readFileOrUrl(path);
		byte[] digest = getDigest(source);
		String key = path + "_" + cx.getOptimizationLevel();
		ScriptReference ref = scriptCache.get(key, digest);
		Script script = ref != null ? ref.get() : null;
		if (script == null) {
			String strSrc = (String) source;
			// Support the executable script #! syntax: If
			// the first line begins with a '#', treat the whole
			// line as a comment.
			if (strSrc.length() > 0 && strSrc.charAt(0) == '#') {
				for (int i = 1; i != strSrc.length(); ++i) {
					int c = strSrc.charAt(i);
					if (c == '\n' || c == '\r') {
						strSrc = strSrc.substring(i);
						break;
					}
				}
			}
			script = cx.compileString(strSrc, path, 1, null);
			scriptCache.put(key, digest, script);
		}

		if (script != null) {
			script.exec(cx, scope);
		}
	}

	private Object readFileOrUrl(String path) {
		return JSFileUtils.getResourceJS(path);
	}

	private static byte[] getDigest(Object source) {
		byte[] bytes, digest = null;
		if (source != null) {
			if (source instanceof String) {
				try {
					bytes = ((String) source).getBytes("UTF-8");
				} catch (UnsupportedEncodingException ue) {
					bytes = ((String) source).getBytes();
				}
			} else {
				bytes = (byte[]) source;
			}
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				digest = md.digest(bytes);
			} catch (NoSuchAlgorithmException nsa) {
				// Should not happen
				throw new RuntimeException(nsa);
			}
		}

		return digest;
	}

	@Override
	public void close() throws IOException {
		Context.exit();
	}
}
