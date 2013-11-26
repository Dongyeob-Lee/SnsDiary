package com.facebook.samples.graphapi;

import android.content.Context;
import android.graphics.Bitmap;

public class CData {
	
	private String s_name;
	private String s_message;
	private Bitmap s_profile;
	private Bitmap s_photo;
	private String s_date;
	
	//댓글가져올때
	private String s_msgNum;
	
	
	public CData(String p_name, String p_message, Bitmap p_profile, Bitmap p_photo, String p_date,String p_msgNum){
		
		s_name = p_name;
		s_message = p_message;
		s_profile = p_profile;
		s_photo = p_photo;
		s_date = p_date;
		s_msgNum = p_msgNum;
	}

	public String getS_dadte(){
		return s_date;
	}
	public String getS_name() {
		return s_name;
	}

	public void setS_name(String s_name) {
		this.s_name = s_name;
	}

	public String getS_message() {
		return s_message;
	}

	public void setS_message(String s_message) {
		this.s_message = s_message;
	}

	
	public Bitmap getS_profile() {
		return s_profile;
	}

	public void setS_profile(Bitmap s_profile) {
		this.s_profile = s_profile;
	}

	public Bitmap getS_photo() {
		return s_photo;
	}

	public void setS_photo(Bitmap s_photo) {
		this.s_photo = s_photo;
	}

	public String getS_msgNum() {
		return s_msgNum;
	}

	public void setS_msgNum(String s_msgNum) {
		this.s_msgNum = s_msgNum;
	}
	
	
	
	
}
