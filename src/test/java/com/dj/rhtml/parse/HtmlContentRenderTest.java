package com.dj.rhtml.parse;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.google.common.collect.Maps;

public class HtmlContentRenderTest{

	@Test
	public void testParse() throws IOException {
		Map<String, String> header = downloadPageHeader();
		String url="http://www.weibo.com/pickrideyejianfei";
		HtmlContentRender render = new HtmlContentRender();
		String content = render.parse(url,header);
		Assert.assertNotNull(content);
		IOUtils.write(content, new FileWriter("/home/houdejun/working/temp/test.html"));
	}
	
	private Map<String, String> downloadPageHeader(){
		String cookie = "SINAGLOBAL=6584957162849.605.1394448506672; un=nextcentre@gmail.com; _s_tentry=news.ifeng.com; SSOLoginState=1395019655; SUS=SID-2815446972-1395019656-XD-0eqot-8904fe8095dbb38d36f673f797464261; SUE=es%3D835bcaa17891b503d46626b2d7016136%26ev%3Dv1%26es2%3D8d7ce0a0c2c0c826bae9968b57cfd2fc%26rs0%3DP%252B6h4Y17TVY%252FgtxmBppr3P%252BWyjVX2Vj1exOqB5IOCC0pSx0X8%252B7BfCoww6so9uE%252Fg3mYjmT%252BWfUkPOn%252BrcRbxRA5swcE%252BsgGHBUk3MOolG4%252FR4useOCFx8KjAZ32AtqOTN6AoC6Pk6keX9KGKV9u2ufO%252Bixt0xnYOzNySu5Dc%252B8%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1395019656%26et%3D1395106056%26d%3Dc909%26i%3Df4f6%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D41%26st%3D0%26uid%3D2815446972%26name%3Dnextcentre%2540gmail.com%26nick%3Dnextcentre%26fmp%3D%26lcp%3D; SUB=AXxguI2sZ3fFLHxAxVWSX3TbW6wYTGFPqpIgEt1KR%2B2gh9MoJ%2FVgzlhANPGkgjQcAlN3%2FXo7ILayWn%2FasSeX2nTEWxwM%2B0r2qbrIAIS5gQq4Y%2BYflYDTaYF1gm5N107Bd9KLgo85djcw4OPZNKFICh4%3D; SUBP=002A2c-gVlwEm1uAWxfgXELuuu1xVxBxAAmEKhyCgXnJYl3GrLTlKtuuHY-u_F=; ALF=1397611654; UOR=,,ent.ifeng.com; wvr=5; Apache=7743191726040.095.1395021719866; ULV=1395021719916:2:2:2:7743191726040.095.1395021719866:1394448506676";
		Map<String,String> httpHeader = Maps.newHashMap();
		httpHeader.put("Cookie", cookie);
		return httpHeader;
	}

}
