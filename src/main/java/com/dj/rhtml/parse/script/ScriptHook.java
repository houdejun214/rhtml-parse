package com.dj.rhtml.parse.script;

import java.lang.reflect.Member;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;


public abstract class ScriptHook extends ScriptableObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1274739692022678403L;

	public abstract void apply(Object arg);

	@Override
	public String getClassName() {
		return ScriptHook.class.getName();
	}
	
	public static class HookFunctionObject extends FunctionObject {

	    HookFunctionObject(String name, Member methodOrConstructor, Scriptable parentScope) {
	      super(name, methodOrConstructor, parentScope);
	    }
	    @Override
	    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
	      return super.call(cx, scope, getParentScope(), args);
//	      return super.call(cx, scope, thisObj, args);
	    }
	  }
}
