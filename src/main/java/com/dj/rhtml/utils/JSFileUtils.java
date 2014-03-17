package com.dj.rhtml.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.lakeside.core.utils.ClassUtils;

public class JSFileUtils {

	public static InputStream getResourceStream(String path){
		ClassLoader defaultClassLoader = ClassUtils.getDefaultClassLoader();
		return defaultClassLoader.getResourceAsStream(path);
	}
	
	/**
	 * get js content from resource
	 * @param path
	 * @return
	 */
	public static String getResourceJS(String path){
		InputStream input = getResourceStream(path);
		try{
			return IOUtils.toString(input);
		} catch (IOException e) {
			return null;
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
}
