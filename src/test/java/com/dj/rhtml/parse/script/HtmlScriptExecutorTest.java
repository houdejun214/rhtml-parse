package com.dj.rhtml.parse.script;

import static org.junit.Assert.*;

import org.junit.Test;

import com.dj.rhtml.parse.script.HtmlScriptExecutor;

public class HtmlScriptExecutorTest {

	@Test
	public void testLoadJS() {
		HtmlScriptExecutor executor = new HtmlScriptExecutor();
	}

	@Test
	public void testLoadJQuery() {
		HtmlScriptExecutor executor = new HtmlScriptExecutor();
		executor.loadJS("jquery.js");
		executor.loadJS("jquery-test.js");
		executor.evalScript("console.log(typeof jQuery);");
	}
}
