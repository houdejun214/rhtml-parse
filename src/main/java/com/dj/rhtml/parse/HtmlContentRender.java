package com.dj.rhtml.parse;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dj.rhtml.parse.script.HtmlScriptExecutor;
import com.lakeside.core.utils.StringUtils;
import com.lakeside.download.http.HttpPage;
import com.lakeside.download.http.HttpPageLoader;

public class HtmlContentRender {
	
	/**
	 * download the http content.
	 */
	private static HttpPageLoader pageLoader = HttpPageLoader.getDefaultPageLoader();

	public String parse(String url,Map<String, String> header) throws IOException{
		HttpPage page = pageLoader.download(header,url);
		String html = page.getContentHtml();
		Document doc = Jsoup.parse(html);
		HtmlScriptExecutor scriptExecutor = new HtmlScriptExecutor();
		try {
			scriptExecutor.updateDocument(html,url);
			Elements scripts = doc.select("script");
			for(Element el:scripts){
				String type = el.attr("type");
				boolean isJavaScript = false;
				if(StringUtils.isEmpty(type) || "text/javascript".equals(type)){
					isJavaScript = true;
				}
				if(isJavaScript){
					String script = el.html();
					scriptExecutor.evalScript(script);
				}
			}
			String documentHtml = scriptExecutor.getDocumentHtml();
			return documentHtml;
		} finally {
			scriptExecutor.close();
		}
	}
}
