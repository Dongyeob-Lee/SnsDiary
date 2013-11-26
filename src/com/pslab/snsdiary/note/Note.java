package com.pslab.snsdiary.note;

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable{
	

	private String year;
	private String month;
	private String date;
	private ArrayList<NoteDiary> noteDiaries;
	public Note(String year, String month, String date) {
		super();
		this.year = year;
		this.month = month;
		this.date = date;
		this.noteDiaries = new ArrayList<NoteDiary>();
	}
	public Note(){
		
	}
	public Note(Parcel in){
		readFromParcel(in);
	}
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(year);
		dest.writeString(month);
		dest.writeString(date);
	}
	private void readFromParcel(Parcel in) {
		// TODO Auto-generated method stub
		year = in.readString();
		month= in.readString();
		date = in.readString();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Parcelable.Creator<Note> CREATOR = new Parcelable.Creator() {

		@Override
		public Object createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Note(source);
		}

		@Override
		public Object[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Note[size];
		}

	};
	
	public ArrayList<NoteDiary> getNoteDiaries() {
		return noteDiaries;
	}
	public void setNoteDiaries(ArrayList<NoteDiary> noteDiaries) {
		this.noteDiaries = noteDiaries;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}
