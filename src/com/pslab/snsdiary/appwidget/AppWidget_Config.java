package com.pslab.snsdiary.appwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Calendar;

import com.pslab.snsdiary.R;

import com.pslab.snsdiary.note.Note;
import com.pslab.snsdiary.note.NoteControler;
import com.pslab.snsdiary.note.NoteDiary;
import com.pslab.snsdiary.spentools.SubjectSelect;

public class AppWidget_Config extends Activity{
	
	public static ArrayList<Integer> DayList = new ArrayList<Integer>();
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	static int ryear=0;
	static int rmonth=0;
	SubjectSelect subjectSelect = new SubjectSelect();
	Calendar LastMonthCalendar;
	Calendar ThisMonthCalendar;
	Calendar NextMonthCalendar;

	Context context;
	RemoteViews views;
	int check=0;
	String dirpath;
	int curyear;
	int curmonth;
	int curdate;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		this.context= AppWidget_Config.this;
		
		dirpath = getFilesDir().getAbsolutePath();
		
		
		Log.d("config","coming");

		if(extras != null){
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		views = new RemoteViews(context.getPackageName(),R.layout.t5_layout);
		
		//views.setTextViewText(R.id.appwidget_month, Integer.toString(month));
		//Calendar cal = Calendar.getInstance();
		ThisMonthCalendar = Calendar.getInstance();
		curyear = ThisMonthCalendar.get(Calendar.YEAR);
		curmonth = ThisMonthCalendar.get(Calendar.MONTH)+1;
		curdate = ThisMonthCalendar.get(Calendar.DATE);
		
		setArray(); // 리스트 생성
		
		views.setTextViewText(R.id.widget_title, Integer.toString(curyear)+"년 "+Integer.toString(curmonth)+"월");
		views.setTextColor(R.id.widget_title, Color.DKGRAY);
		
		/////////subject controler
		
		for(int i=1;i<=42;i++){
			
			//views.setTextViewText(id, Integer.toString(DayList.get(i-1)));
			//views.setTextColor(id, Color.BLACK);
			int cal_id = context.getResources().getIdentifier("widget_cal"+Integer.toString(i), "id", context.getPackageName());
			views.addView(cal_id, getRemoteviewOfCal(String.valueOf(curyear),doubleString(curmonth),i,cal_id));
		}
		
		appWidgetManager.updateAppWidget(mAppWidgetId,views);

		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
		
	}
	private RemoteViews getRemoteviewOfCal(String iYear, String iMonth, int cnt, int cal_id){
		RemoteViews low_views = new RemoteViews(context.getPackageName(),R.layout.t5_low_level);
		
		int date = DayList.get(cnt-1);
		views.removeAllViews(cal_id);
		low_views.setTextViewText(R.id.widget_cal_date, String.valueOf(date));
		
		Note note;
		// 글 제목 계산
		if(NoteControler.checkNoteExist(dirpath,iYear+iMonth+String.valueOf(date)+".xml")){
			note = NoteControler.getXmlStringDate(dirpath, iYear, iMonth, String.valueOf(date),AppWidget_Config.this);
		}
		else{
			note = new Note(iYear,iMonth,String.valueOf(date));
		}
		
		if(date==1){check++;}
		
		if(check==1){
			if(cnt%7 == 1){
				//일요일
				low_views.setTextColor(R.id.widget_cal_date, Color.RED);
			}
			else if(cnt%7 == 0){
				//토요일
				low_views.setTextColor(R.id.widget_cal_date, Color.BLUE);
			}
			else if(String.valueOf(curyear).equals(iYear)&&doubleString(curmonth).equals(iMonth)&&curdate==date){
				//오늘
				low_views.setTextColor(R.id.widget_cal_date, Color.GREEN);
			}
			else{
				//Log.d("black","black");
				low_views.setTextColor(R.id.widget_cal_date, Color.BLACK);
			}
			if(note.getNoteDiaries().size()!=0){
				low_views.setTextViewText(R.id.widget_cal_title, note.getNoteDiaries().get(0).getTitle());
				low_views.setImageViewResource(R.id.widget_cal_image, subjectSelect.getDrawable(note.getNoteDiaries().get(0).getSubject()));	
			}
		}
		else{
			low_views.setTextColor(R.id.widget_cal_date, Color.GRAY);
		}
		return low_views;
	}
	private void setArray(){
		int cnt = 0;
		ThisMonthCalendar = Calendar.getInstance();
		int year = ThisMonthCalendar.get(Calendar.YEAR);
		int month = ThisMonthCalendar.get(Calendar.MONTH) + 1;
		//int day = ThisMonthCalendar.get(Calendar.DATE);
		ryear=year;
		rmonth=month; 
		
		DayList.clear();
		
		// start calendar
		Calendar scal = Calendar.getInstance();
		// end calendar
		Calendar ecal = Calendar.getInstance();
		scal.set(year, month-1, 1); // 이번달 첫날짜
		int start_day = scal.get(Calendar.DAY_OF_WEEK); // 이번달 첫날짜의 요일
		ecal.set(year, month,1); // 다음달 첫날짜
		ecal.add(Calendar.DATE,-1); // 다음달 첫날짜 - 1일 -> 이번달 마지막 날짜
		int end = ecal.get(Calendar.DATE); // 이번달 마지막 날짜
		scal.add(Calendar.DATE, -1); // 이번달 첫날짜 -1일 -> 다음달 마지막 날짜
		int last_month_end = scal.get(Calendar.DATE); // 전달 마지막 날짜
		
		cnt=0;
		
		if(start_day==1){ // 달의 첫날짜가 일요일 이면!
			for(int i=1;i<=end;i++){
				DayList.add(i);
				cnt++;
			}
			for(int i=1;cnt<42;i++){
				DayList.add(i);
				cnt++;
			}
		}
		else{
			for(int j=last_month_end-start_day+2;j<=last_month_end;j++){
				DayList.add(j);
				cnt++;
			}
			for(int j=1;j<=end;j++){
				DayList.add(j);
				cnt++;
			}
			for(int j=1;j<42;j++){
				DayList.add(j);
				cnt++;
			}
			
		}
	}
	public String intDateToNotename(int year, int month, int day){
		String totalString  = String.valueOf(year)+doubleString(month+1)+doubleString(day)+".xml";
		return totalString;
	}
	protected String doubleString(int value){
        String temp;
 
        if(value < 10){
            temp = "0"+ String.valueOf(value);
             
        }else {
            temp = String.valueOf(value);
        }
        return temp;
    }
}