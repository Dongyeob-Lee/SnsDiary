package com.pslab.snsdiary.note;

import java.util.ArrayList;

public class NoteDiary {

	// ==============================
	// Diary Subject Constants
	// ==============================
	private String title;
	private String SCanvasFileName;
	private String temperature;
	private String weatherImg;
	private String RecordFileName;
	private ArrayList<String> feedlist = new ArrayList<String>();
	public String getRecordFileName() {
		return RecordFileName;
	}
	public void setRecordFileName(String recordFileName) {
		RecordFileName = recordFileName;
	}
	private int subject;

	public int getSubject() {
		return subject;
	}
	public void setSubject(int subject) {
		this.subject = subject;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSCanvasFileName() {
		return SCanvasFileName;
	}
	public void setSCanvasFileName(String sCanvasFileName) {
		SCanvasFileName = sCanvasFileName;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getWeatherImg() {
		return weatherImg;
	}
	public void setWeatherImg(String weatherImg) {
		this.weatherImg = weatherImg;
	}
	public ArrayList<String> getFeedlist() {
		return feedlist;
	}
	public void setFeedlist(ArrayList<String> feedlist) {
		this.feedlist = feedlist;
	}
	
}
