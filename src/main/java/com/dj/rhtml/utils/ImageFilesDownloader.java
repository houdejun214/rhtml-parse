package com.dj.rhtml.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;

/**
 * this is a image file downloader, it will download http image files with multiple thread
 * @author houdejun
 *
 */
public class ImageFilesDownloader {
	
	private static final int POOLED_THREAD_N = 3;
	private ExecutorService downloadExecutor;
	
	public ImageFilesDownloader(ExecutorService downloadExecutor) {
		this.downloadExecutor = downloadExecutor;
	}
	public ImageFilesDownloader(int number) {
		this.downloadExecutor = Executors.newFixedThreadPool(number);
	}
	public ImageFilesDownloader() {
		this.downloadExecutor = Executors.newFixedThreadPool(POOLED_THREAD_N);
	}

	/**
	 * download images
	 * 
	 * @param images
	 * @return
	 */
	public  Map<String, byte[]> download(List<String> images){
		Map<String,byte[]> result = new HashMap<String,byte[]>();
		if(images!=null && images.size()>0){
			CountDownLatch lock = new CountDownLatch(images.size());
			for(String img:images){
				downloadExecutor.execute(new Downloader(img, lock,result));
			}
			try {
				lock.await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			
		}
		return result;
	}
	
	/**
	 * download images with one thread
	 * 
	 * @param images
	 * @return
	 */
	public Map<String, byte[]> downloadLinear(List<String> images){
		Map<String,byte[]> result = new HashMap<String,byte[]>();
		if(images!=null && images.size()>0){
			for(String img:images){
				new Downloader(img, null,result).run();
			}
		}
		return result;
	}
	
	/**
	 * internal file downloader
	 * @author houdejun
	 *
	 */
	private static class Downloader implements Runnable {
		private String url = null;
		private CountDownLatch lock = null;
		private Map<String, byte[]> result = null;
		Downloader(String url,CountDownLatch lock, Map<String, byte[]> result){
			this.url = url;
			this.lock = lock;
			this.result = result;
		}

		public void run() {
			try {
				if(url != null){
					byte[] contentBytes =toByteArray(url);
					result.put(url, contentBytes);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(lock!=null){
					lock.countDown();
				}
			}
		}
		
		public static byte[] toByteArray(String imgageURL) throws IOException {
			URL url = new URL(imgageURL);
			URLConnection connect = url.openConnection();
			connect.addRequestProperty("User-Agent", "Chrome/28.0.1500.72");
			InputStream inputStream = null;
			try {
				inputStream = connect.getInputStream();
				byte[] byteArray = IOUtils.toByteArray(inputStream);
				return byteArray;
			} finally{
				if(inputStream!=null){
					IOUtils.closeQuietly(inputStream);
				}
			}
		}
	}
}
