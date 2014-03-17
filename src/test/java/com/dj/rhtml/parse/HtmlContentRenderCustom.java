package com.dj.rhtml.parse;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Undefined;

import com.dj.rhtml.parse.script.HtmlScriptExecutor;
import com.dj.rhtml.parse.script.ScriptHook;
import com.lakeside.core.utils.StringUtils;
import com.lakeside.download.http.HttpPage;
import com.lakeside.download.http.HttpPageLoader;

public class HtmlContentRenderCustom {
	
	/**
	 * download the http content.
	 */
	private static HttpPageLoader pageLoader = HttpPageLoader.getDefaultPageLoader();

	public String parse(String url,Map<String, String> header) throws IOException{
		HttpPage page = pageLoader.download(header,url);
		String html = page.getContentHtml();
		Document doc = Jsoup.parse(html);
		try {
			HtmlScriptExecutor scriptExecutor = new HtmlScriptExecutor();
			scriptExecutor.updateDocument(html,url);
			Elements scripts = doc.select("script");
			boolean add = false;
			for(Element el:scripts){
				String type = el.attr("type");
				boolean isJavaScript = false;
				if(StringUtils.isEmpty(type) || "text/javascript".equals(type)){
					isJavaScript = true;
				}
				if(isJavaScript){
					String script = el.html();
					if(script.startsWith("FM.view(") && !add){
						addHook(scriptExecutor);
						add = true;
					}
					scriptExecutor.evalScript(script);
				}
			}
			String documentHtml = scriptExecutor.getDocumentHtml();
			return documentHtml;
		} finally {
			Context.exit();
		}
	}

	private void addHook(HtmlScriptExecutor scriptExecutor) {
		String hook="FM.view = function(obj){ if(typeof obj == 'object'){hook_apply(obj.html)}};";
		scriptExecutor.applyHookOperation(hook, new ScriptHook(){
			@Override
			public void apply(Object arg) {
				if(!Undefined.instance.equals(arg)){
					System.out.println(arg);
				}
			}
		});
	}
}
