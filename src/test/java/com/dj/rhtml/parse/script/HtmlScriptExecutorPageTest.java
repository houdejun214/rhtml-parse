package com.dj.rhtml.parse.script;

import org.junit.Test;

public class HtmlScriptExecutorPageTest {


	@Test
	public void testLoadJQuery() {
		HtmlScriptExecutor executor = new HtmlScriptExecutor();
		//executor.evalScript("Envjs({ scriptTypes: { "": true, "text/javascript": true } });");
		executor.loadJS("jquery.js");
		executor.loadJS("dynamic-page.js");
		//window.location = "http://mikegrace.s3.amazonaws.com/geek-blog/rhino-envjs.html";
//		executor.evalScript("window.location = \"http://mikegrace.s3.amazonaws.com/geek-blog/rhino-envjs.html\";");
//		executor.evalScript("var body = $(\"body\");");
//		executor.evalScript("console.log(\"The body is: \"+ $.trim(body));");
	}
}
