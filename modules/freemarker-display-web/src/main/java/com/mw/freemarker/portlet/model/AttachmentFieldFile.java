package com.mw.freemarker.portlet.model;

public class AttachmentFieldFile {

	private String url;
	private long size;
	
	public AttachmentFieldFile(String url, long size) {
		super();
		
		this.url = url;
		this.size = size;
	}
	
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}