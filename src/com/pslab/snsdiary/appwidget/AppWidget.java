package com.pslab.snsdiary.appwidget;

import java.util.ArrayList;
import java.util.Calendar;

import com.pslab.snsdiary.MainActivity;
import com.pslab.snsdiary.R;
import com.pslab.snsdiary.note.Note;
import com.pslab.snsdiary.note.NoteControler;
import com.pslab.snsdiary.spentools.SubjectSelect;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class AppWidget extends AppWidgetProvider{
	final static String BTN_CLICK1 = "android.action.BTN_CLICK1";
	final static String BTN_CLICK2 = "android.action.BTN_CLICK2";
	final static String BTN_CLICK3 = "android.action.BTN_CLICK3";
	final static String BTN_CLICK4 = "android.action.BTN_CLICK4";
	final static String UPDATE_OPTIONS = "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS";
	
	private String wClassName = this.getClass().getName();
	
	private int curyear;
	private int curmonth;
	private int curdate;
	int check=0;
	private String dirpath;
	SubjectSelect subjectSelect = new SubjectSelect();
	public static ArrayList<Integer> DayList = new ArrayList<Integer>(); 
	Calendar LastMonthCalendar;
	Calendar ThisMonthCalendar;
	Calendar NextMonthCalendar;
	public static int year_m=0;
	public static int month_m=0;
	Context context;
	Calendar now_temp = Calendar.getInstance();
/*	private int year_n = now_temp.get(Calendar.YEAR);
	private int month_n = now_temp.get(Calendar.MONTH)+1;*/
	RemoteViews views;
	@Override
	public void onReceive(Context context, Intent intent){
		
		String action = intent.getAction();
		Log.d("T3:onReceive",action);
		this.context = context;
		dirpath = context.getFilesDir().getAbsolutePath();
		ComponentName widget = new ComponentName(context, wClassName);
		AppWidgetManager awm = AppWidgetManager.getInstance(context);
		int[] appWidgetIds = awm.getAppWidgetIds(widget);
		final int N = appWidgetIds.length;
		Calendar cal = Calendar.getInstance();
		curyear = cal.get(Calendar.YEAR);
		curmonth = cal.get(Calendar.MONTH)+1;
		curdate = cal.get(Calendar.DAY_OF_MONTH);
		if(action.equals(BTN_CLICK1)){
			Log.d("BTN1","BTN1 in the hole");
			//year_m = year_n;
			//month_m = month_n;
			
			for(int i=0;i<N;i++){
				int appWidgetId = appWidgetIds[i];
				int cal_id;
				
				views = new RemoteViews(context.getPackageName(),awm.getAppWidgetInfo(appWidgetId).initialLayout);
				
				views.setTextViewText(R.id.widget_title, Integer.toString(curyear)+"년 "+Integer.toString(curmonth)+"월");
				views.setTextColor(R.id.widget_title, Color.DKGRAY);

				setArray();
				check=0;
				for(int j=1;j<=42;j++){
					
					cal_id = context.getResources().getIdentifier("widget_cal"+Integer.toString(j), "id", context.getPackageName());

					views.addView(cal_id, getRemoteviewOfCal(String.valueOf(curyear), doubleString(curmonth), j, cal_id));
				}

				awm.updateAppWidget(appWidgetId, views);

				Toast Msg = Toast.makeText(context,"Miary : 동기화 완료!",Toast.LENGTH_LONG);
				Msg.show();
				Log.d("BTN1 id",Integer.toString(appWidgetId)+" update!");
			}
		}
		else if(action.equals(BTN_CLICK2)){
			Intent btn2_intent = new Intent(context,MainActivity.class);
			btn2_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(btn2_intent);
		}
		else if(action.equals(UPDATE_OPTIONS)){
			views = new RemoteViews(context.getPackageName(),R.layout.t5_layout);

			for(int i=0;i<N;i++){
				int appWidgetId = appWidgetIds[i];

				Intent active = new Intent(context, AppWidget.class);
				active.setAction(BTN_CLICK1);
				PendingIntent pending = PendingIntent.getBroadcast(context, 0, active, 0);
				views.setOnClickPendingIntent(R.id.widget_button_left, pending);

				active = new Intent(context,AppWidget.class);
				active.setAction(BTN_CLICK2);
				pending = PendingIntent.getBroadcast(context, 0, active, 0);
				views.setOnClickPendingIntent(R.id.widget_button_right, pending);
				
				active = new Intent(context,AppWidget.class);
				active.setAction(BTN_CLICK3);
				pending = PendingIntent.getBroadcast(context, 0, active, 0);
				views.setOnClickPendingIntent(R.id.widget_button_pre_month, pending);
				
				active = new Intent(context,AppWidget.class);
				active.setAction(BTN_CLICK4);
				pending = PendingIntent.getBroadcast(context, 0, active, 0);
				views.setOnClickPendingIntent(R.id.widget_button_next_month, pending);
				
				/////////////////////////////////////////////////////////////////////////////////
				views.setTextViewText(R.id.widget_title, Integer.toString(year_m)+"년 "+Integer.toString(month_m+1)+"월");
				views.setTextColor(R.id.widget_title, Color.DKGRAY);
				

				int cal_id;
				
				settingArray(year_m, month_m+1);
				
				check=0;
				for(int j=1;j<=42;j++){
					cal_id = context.getResources().getIdentifier("widget_cal"+Integer.toString(j), "id", context.getPackageName());
					views.addView(cal_id, getRemoteviewOfCal(String.valueOf(year_m), doubleString(month_m+1), j, cal_id));
				}
				//////////////////////////////////////////////////////////////////////////////////
				awm.updateAppWidget(appWidgetId, views);
			}
			Toast Msg = Toast.makeText(context,"Miary : 화면 설정중!",Toast.LENGTH_LONG);
			Msg.show();
			Log.d("UPDATE_OPIONS","BUTTON SETTING");
		}
		else if(action.equals(BTN_CLICK3)){ // 앞의 달
			month_m--;
			if( month_m <0 ){
				month_m=11;
				year_m--;
			}
			
			for(int i=0;i<N;i++){
				int appWidgetId = appWidgetIds[i];
				views = new RemoteViews(context.getPackageName(),awm.getAppWidgetInfo(appWidgetId).initialLayout);
				views.setTextViewText(R.id.widget_title, Integer.toString(year_m)+"년 "+Integer.toString(month_m+1)+"월");
				views.setTextColor(R.id.widget_title, Color.DKGRAY);

				int cal_id;
				check=0;
				settingArray(year_m, month_m+1);
				for(int j=1;j<=42;j++){
					cal_id = context.getResources().getIdentifier("widget_cal"+Integer.toString(j), "id", context.getPackageName());
					views.addView(cal_id, getRemoteviewOfCal(String.valueOf(year_m), doubleString(month_m+1), j,cal_id));
				}
				awm.updateAppWidget(appWidgetId, views);

				
				Log.d("BTN3 id",Integer.toString(appWidgetId)+" update!");
			}	
		}
		else if(action.equals(BTN_CLICK4)){ // 뒤의 달
			month_m++;
			if(month_m>11){
				month_m=0;
				year_m++;
			}
			
			for(int i=0;i<N;i++){
				int appWidgetId = appWidgetIds[i];
				views = new RemoteViews(context.getPackageName(),awm.getAppWidgetInfo(appWidgetId).initialLayout);
				views.setTextViewText(R.id.widget_title, Integer.toString(year_m)+"년 "+Integer.toString(month_m+1)+"월");
				views.setTextColor(R.id.widget_title, Color.DKGRAY);

				int cal_id;
				check=0;
				settingArray(year_m, month_m+1);
				for(int j=1;j<=42;j++){
					cal_id = context.getResources().getIdentifier("widget_cal"+Integer.toString(j), "id", context.getPackageName());
					views.addView(cal_id, getRemoteviewOfCal(String.valueOf(year_m), doubleString(month_m+1), j, cal_id));
				}
				awm.updateAppWidget(appWidgetId, views);

				//Toast Msg = Toast.makeText(context,"Miary : 동기화 완료!",Toast.LENGTH_LONG);
				//Msg.show();
				Log.d("BTN4 id",Integer.toString(appWidgetId)+" update!");
			}	
		}

		super.onReceive(context, intent);
		
	}

	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		Log.d("Diary","onUpdate()");
		final int N = appWidgetIds.length;

		RemoteViews remote = new RemoteViews(context.getPackageName(),R.layout.t5_layout);

		for(int i=0;i<N;i++){
			int appWidgetId = appWidgetIds[i];

			// Original
			Intent active = new Intent(context, AppWidget.class);
			active.setAction(BTN_CLICK1);
			PendingIntent pending = PendingIntent.getBroadcast(context, 0, active, 0);
			remote.setOnClickPendingIntent(R.id.widget_button_left, pending);

			active = new Intent(context,AppWidget.class);
			active.setAction(BTN_CLICK2);
			pending = PendingIntent.getBroadcast(context, 0, active, 0);
			remote.setOnClickPendingIntent(R.id.widget_button_right, pending);
			
			active = new Intent(context,AppWidget.class);
			active.setAction(BTN_CLICK3);
			pending = PendingIntent.getBroadcast(context, 0, active, 0);
			remote.setOnClickPendingIntent(R.id.widget_button_pre_month, pending);
			
			active = new Intent(context,AppWidget.class);
			active.setAction(BTN_CLICK4);
			pending = PendingIntent.getBroadcast(context, 0, active, 0);
			remote.setOnClickPendingIntent(R.id.widget_button_next_month, pending);

			appWidgetManager.updateAppWidget(appWidgetId, remote);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}


	@Override
	public void onEnabled(Context context){
		Log.d("Diary","onEnabled()");
		super.onEnabled(context);
	}

	@Override
	public void onDeleted(Context context,int[] appWidgetIds){
		Log.d("Diary","onDeleted()");
		super.onDeleted(context, appWidgetIds);
	}

	public void updateView(Context context,AppWidgetManager appWidgetManager, int widgetId){
		/*Log.d("T:updateView","widgetId : "+widgetId);
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.t2_layout);
		appWidgetManager.updateAppWidget(widgetId, views);*/
	}
/*	static void updateWidget(Context context,  AppWidgetManager appWidgetManager,  int appWidgetId){

	}*/
	private void setArray(){
		DayList.clear();
		int cnt = 0;
		ThisMonthCalendar = Calendar.getInstance();
		int year = ThisMonthCalendar.get(Calendar.YEAR);
		int month = ThisMonthCalendar.get(Calendar.MONTH) + 1;
		//int day = ThisMonthCalendar.get(Calendar.DATE);
		year_m = year;
		month_m = month-1;
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
	
	private void settingArray(int year,int month){
		DayList.clear();
		int cnt=0;
		Calendar scal = Calendar.getInstance();
		scal.set(year, month-1, 1); // 앞의 달
		Calendar ecal = Calendar.getInstance();
		ecal.set(year, month, 1); // 이번달
		ecal.add(Calendar.DATE,-1); // 다음달 첫날짜 - 1일 -> 이번달 마지막 날짜
		
		int start_day = scal.get(Calendar.DAY_OF_WEEK); // 앞달 첫날짜의 요일
		int end_day = ecal.get(Calendar.DATE);;
		scal.add(Calendar.DATE, -1); // 이번달 첫날짜 -1일 -> 다음달 마지막 날짜
		int last_month_end = scal.get(Calendar.DATE); // 전달 마지막 날짜
		
		cnt=0;
		
		if(start_day==1){ // 달의 첫날짜가 일요일 이면!
			for(int i=1;i<=end_day;i++){
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
			for(int j=1;j<=end_day;j++){
				DayList.add(j);
				cnt++;
			}
			for(int j=1;j<42;j++){
				DayList.add(j);
				cnt++;
			}
			
		}
	}
	private RemoteViews getRemoteviewOfCal(String iYear, String iMonth, int cnt, int cal_id){
		RemoteViews low_views = new RemoteViews(context.getPackageName(),R.layout.t5_low_level);
		
		int date = DayList.get(cnt-1);
		views.removeAllViews(cal_id);
		low_views.setTextViewText(R.id.widget_cal_date, String.valueOf(date));
		Log.d("dir", dirpath);
		Note note;
		// 글 제목 계산
		if(NoteControler.checkNoteExist(dirpath,iYear+iMonth+String.valueOf(date)+".xml")){
			note = NoteControler.getXmlStringDate(dirpath, iYear, iMonth, String.valueOf(date),context);
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
	protected void onItemClick(View v) {
		Log.d("dsijfweiof", "sifosjfiowe");
	}
}