package com.pslab.calendar;
 
import java.util.ArrayList;
import java.util.Calendar;

import com.pslab.snsdiary.R;
import com.pslab.snsdiary.SettingView;
import com.pslab.snsdiary.daily.Daily;
import com.pslab.snsdiary.freenote.NoteList;
import com.pslab.snsdiary.note.Note;
import com.pslab.snsdiary.note.NoteActivity;
import com.pslab.snsdiary.note.NoteControler;
import com.pslab.snsdiary.note.NoteDiary;
import com.pslab.snsdiary.weekly.Weekly;
 
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
 
public class CalendarView extends CTCalendarView {
     
    private Oneday basisDay;
    private int during;
    private Note selectedDayNote;
    private SubjectControler subjectControler;
    ArrayList<NoteDiary> components;
    String year;
    String month;
    String date;
    
    Calendar datePicker = Calendar.getInstance();
    
	private Oneday selectedOneday;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle("원하는 날짜를 선택해 주세요.");
        initialize();
         
        basisDay = new Oneday(this);
       
        
        Intent intent = getIntent();
        int[] b = intent.getIntArrayExtra("basisDay");
        during = intent.getIntExtra("during", 0);
        if(b != null){
            basisDay.setYear(b[0]);
            basisDay.setMonth(b[1]);
            basisDay.setDay(b[2]);
        } else {
            Calendar cal = Calendar.getInstance();
            basisDay.setYear(cal.get(Calendar.YEAR));
            basisDay.setMonth(cal.get(Calendar.MONTH));
            basisDay.setDay(cal.get(Calendar.DAY_OF_MONTH));
        }
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		selectedOneday=null;
		initialize(iYear, iMonth);
	}

    
	@Override
    protected void onTouched(final Oneday touchedDay){
		selectedOneday = touchedDay;
		year = String.valueOf(touchedDay.getYear());
        month = doubleString(touchedDay.getMonth() + 1);
        date = doubleString(touchedDay.getDay());
        if(NoteControler.checkNoteExist(getFilesDir().getAbsolutePath(), year+month+date+".xml")){
        	selectedDayNote = NoteControler.getXmlStringDate(getFilesDir().getAbsolutePath(), year, month, date, CalendarView.this);
        	Intent intent = new Intent(CalendarView.this, Daily.class);
			intent.putExtra("note_object", selectedDayNote);
			intent.putExtra("completed", false);
			startActivity(intent);
        }else{
        	components = new ArrayList<NoteDiary>();
        	NoteDiary component = new NoteDiary();
        	component.setTitle("New diary");
        	components.add(component);
        	
        }
    }
	public void onMenuClick(View v){
		final Calendar today = Calendar.getInstance();
        String curYear = String.valueOf(today.get(Calendar.YEAR));
        String curMonth = String.valueOf(doubleString(today.get(Calendar.MONTH)+1));
        String curDay = String.valueOf(doubleString(today.get(Calendar.DAY_OF_MONTH)));
		if(v.getId()==R.id.btn_cal_daily){
		//goto daily page of today 
		//if exist today xml 
			if(NoteControler.checkNoteExist(getFilesDir().getAbsolutePath(), curYear+curMonth+curDay+".xml")){
				Note note = NoteControler.getXmlStringDate(getFilesDir().getAbsolutePath(), curYear, curMonth, curDay, CalendarView.this);
				Intent intent = new Intent(CalendarView.this, Daily.class);
				intent.putExtra("note_object", note);
				intent.putExtra("completed", false);
				startActivity(intent);
				finish();
			}else{
				Note note = new Note(curYear, curMonth, curDay);
				Intent intent = new Intent(CalendarView.this,Daily.class);
				intent.putExtra("note_object", note);
				intent.putExtra("isempty", true);
				startActivity(intent);
				finish();
			}
		}else if(v.getId()==R.id.btn_cal_weekly){
			Intent intent = new Intent(CalendarView.this, Weekly.class);
			startActivity(intent);
			finish();
		}else if(v.getId()==R.id.btn_cal_freenote){
			Intent intent = new Intent(CalendarView.this, NoteList.class);
			startActivity(intent);
			finish();
		}else if(v.getId()==R.id.btn_cal_setting){
			Intent intent = new Intent(CalendarView.this, SettingView.class);
			startActivity(intent);
			finish();
		}else if(v.getId()==R.id.btn_cal_new_diary){
			Intent intent = new Intent(CalendarView.this, NoteActivity.class);
			Note note;
			if(selectedOneday!=null){
				note = new Note(year, month, date);
			}else{
		        if(NoteControler.checkNoteExist(getFilesDir().getAbsolutePath(), curYear+curMonth+curDay+".xml")){
		        	note = NoteControler.getXmlStringDate(getFilesDir().getAbsolutePath(), curYear,curMonth,curDay, CalendarView.this);
		        }else{
		        	note = new Note(curYear,curMonth,curDay);
		        }
			}
			intent.putExtra("note_object", note);
			intent.putExtra("isnew", true);

			if (note.getNoteDiaries().size()==0) {
				intent.putExtra("index", 0);
			} else {
				intent.putExtra("index", note.getNoteDiaries().size());
			}
			startActivity(intent);
			finish();
		}else if(v.getId()==R.id.btn_move_date){
			new DatePickerDialog(CalendarView.this, dp, datePicker.get(Calendar.YEAR), datePicker.get(Calendar.MONTH), datePicker.get(Calendar.DAY_OF_MONTH)).show();
		}else if(v.getId()==R.id.btn_prev_month){
			if(iMonth==0){
				iYear--;
				iMonth=11;
				initialize(iYear, iMonth);
			}else{
				iMonth--;
				initialize(iYear, iMonth);
			}
		}else if(v.getId()==R.id.btn_next_month){
			if(iMonth==11){
				iYear++;
				iMonth=0;
				initialize(iYear, iMonth);
			}else{
				iMonth++;
				initialize(iYear, iMonth);
			}
		}else if(v.getId()==R.id.btn_goto_today){
			gotoToday();
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.calendar_menu, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         
        switch (item.getItemId()) {
             
        //오늘
        case R.id.menuitem_calendar_0:
            gotoToday();
            return true;
        }
         
        return false;
    }
    public Bitmap loadThumbnail(String filename){
    	String imgPath = getFilesDir().getAbsolutePath()+"/"+filename;
		return BitmapFactory.decodeFile(imgPath);
    	
    }
    DatePickerDialog.OnDateSetListener dp = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			datePicker.set(Calendar.YEAR, year);
			datePicker.set(Calendar.MONTH, monthOfYear);
			datePicker.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			initialize(year, monthOfYear);
		}
	};
}