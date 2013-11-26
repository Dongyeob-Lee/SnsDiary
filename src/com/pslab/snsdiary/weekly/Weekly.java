package com.pslab.snsdiary.weekly;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pslab.calendar.CTCalendarView;
import com.pslab.calendar.CalendarView;
import com.pslab.calendar.Oneday;
import com.pslab.snsdiary.R;
import com.pslab.snsdiary.SettingView;
import com.pslab.snsdiary.R.layout;
import com.pslab.snsdiary.R.menu;
import com.pslab.snsdiary.daily.Daily;
import com.pslab.snsdiary.freenote.Freenote_memo;
import com.pslab.snsdiary.freenote.NoteList;
import com.pslab.snsdiary.note.Note;
import com.pslab.snsdiary.note.NoteActivity;
import com.pslab.snsdiary.note.NoteControler;
import com.pslab.snsdiary.spentools.SubjectSelect;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.Toast;

public class Weekly extends Activity {

	private ImageView btn_monthly;
	private ImageView btn_daily;
	private ImageView btn_freenote;
	private ImageView btn_setting;
	private ImageView btn_new_diary;
	private ImageView btn_move_date;
	private ImageView btn_prev_week;
	private ImageView btn_next_week;

	private Calendar rightNow;
	private GregorianCalendar gCal;
	protected int iYear = 0;
	protected int iMonth = 0;
	protected int iWeek = 0;
	Calendar datePicker = Calendar.getInstance();
	private int startDayOfweek = 0;
	private int maxDay = 0;
	private int oneday_width = 0;
	private int oneday_height = 0;
	private int dayCnt;
	private int mSelect = -1;
	private String dirpath;
	int[] weekDays = new int[7];
	ArrayList<String> daylist; // 일자 목록을 가지고 있는다. 1,2,3,4,.... 28?30?31?
	ArrayList<String> actlist; // 일자에 해당하는 활동내용을 가지고 있는다.
	DayOfWeek selectedOneday;
	final DayOfWeek[] dayOfWeeks = new DayOfWeek[7];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weekly);
		// /button setting
		btn_monthly = (ImageView) findViewById(R.id.weekly_icon_monthly);
		btn_daily = (ImageView) findViewById(R.id.weekly_icon_daily);
		btn_freenote = (ImageView)findViewById(R.id.weekly_icon_freenote);
		btn_setting = (ImageView) findViewById(R.id.weekly_icon_setting);
		btn_monthly.setOnClickListener(menuBtnClickListener);
		btn_daily.setOnClickListener(menuBtnClickListener);
		btn_freenote.setOnClickListener(menuBtnClickListener);
		btn_setting.setOnClickListener(menuBtnClickListener);

		btn_new_diary = (ImageView) findViewById(R.id.btn_weekly_add);
		btn_move_date = (ImageView) findViewById(R.id.btn_weekly_move);
		btn_prev_week = (ImageView) findViewById(R.id.btn_prev_week);
		btn_next_week = (ImageView) findViewById(R.id.btn_next_week);
		btn_new_diary.setOnClickListener(bottomBtnClickListener);
		btn_move_date.setOnClickListener(bottomBtnClickListener);
		btn_prev_week.setOnClickListener(bottomBtnClickListener);
		btn_next_week.setOnClickListener(bottomBtnClickListener);

		dirpath = getFilesDir().getAbsolutePath();
		rightNow = Calendar.getInstance();
		gCal = new GregorianCalendar();
		iYear = rightNow.get(Calendar.YEAR);
		iMonth = rightNow.get(Calendar.MONTH) + 1;
		iWeek = rightNow.get(Calendar.WEEK_OF_MONTH);
		// /////여기서 주차를 나눠야 된다.
		initialize(iYear, iMonth, iWeek);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_weekly, menu);
		return true;
	}

	protected void initialize(int year, int month, int week) {
		
		iYear = year;
		iMonth = month;
		iWeek = week;
		
		makeWeeklydata(year, month - 1, week);

		updateUI();
	}

	private void updateUI() {

		TableLayout tableLayout = (TableLayout)findViewById(R.id.tl_weekly);
    	tableLayout.removeAllViews();
    	
    	int maxRow = 3;
    	int count=-1;
    	int cntId=0;
    	for(int i=0; i<maxRow; i++){
    		
    		
    		TableRow tr = new TableRow(this);
    		tr.setLayoutParams(new TableRow.LayoutParams(android.widget.TableRow.LayoutParams.WRAP_CONTENT, android.widget.TableRow.LayoutParams.WRAP_CONTENT));
    		for(int j=0; j<maxRow; j++){
    			if(count==-1){
    				WeekIntro weekIntro = new WeekIntro(this);
    				weekIntro.setYearMonth(iYear+"."+iMonth);
    				weekIntro.setWeek(String.valueOf(iWeek));
    				tr.addView(weekIntro);
    			}else{
    				Note load_note;
    				dayOfWeeks[cntId] = new DayOfWeek(this);
    				String notename = String.valueOf(iYear) + doubleString(iMonth)
    						+ doubleString(weekDays[count]) + ".xml";
    				dayOfWeeks[cntId].setYear(iYear);
    				dayOfWeeks[cntId].setMonth(iMonth);
    				dayOfWeeks[cntId].setDay(weekDays[count]);
    				if (NoteControler.checkNoteExist(dirpath, notename)) {
    					SubjectSelect subjectSelect = new SubjectSelect();
    					load_note = NoteControler.getXmlStringDate(dirpath,
    							String.valueOf(iYear), doubleString(iMonth),
    							doubleString(weekDays[count]), Weekly.this);
    					dayOfWeeks[cntId]
    							.setTitle(load_note.getNoteDiaries().get(0).getTitle());
    					dayOfWeeks[cntId].setSubject(subjectSelect.getDrawable(load_note
    							.getNoteDiaries().get(0).getSubject()));
    					dayOfWeeks[cntId].setCanvasname(load_note.getNoteDiaries().get(0)
    							.getSCanvasFileName());
    					dayOfWeeks[cntId].setIsEmpty(false);
    				} else {
    					load_note = new Note(String.valueOf(iYear),
    							doubleString(iMonth), doubleString(weekDays[count]));
    					dayOfWeeks[cntId].setCanvasname("pleasenewdiary");
    				}
    				if (count == 0) {
    					dayOfWeeks[cntId].setTextDateColor(Color.RED);
    				} else if (count == 6) {
    					dayOfWeeks[cntId].setTextDateColor(Color.BLUE);
    				}
    				dayOfWeeks[cntId].setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Toast.makeText(Weekly.this, String.valueOf(v.getId()), Toast.LENGTH_SHORT).show();
							if(mSelect != -1){
								dayOfWeeks[mSelect].setmSelected(false);
								dayOfWeeks[mSelect].invalidate();
							}
							dayOfWeeks[v.getId()].setmSelected(true);
							dayOfWeeks[v.getId()].invalidate();
							mSelect = v.getId();
							
							onTouched(dayOfWeeks[mSelect]);
						}
					});
    				dayOfWeeks[cntId].setDate(String.valueOf(weekDays[count]));
    				dayOfWeeks[cntId].setId(cntId);
    				dayOfWeeks[cntId].invalidate();
    				tr.addView(dayOfWeeks[cntId]);
    				cntId++;
    			}
    			
    			count++;
    			if(count==7)
    				break;
    		}
    		tableLayout.addView(tr, new TableLayout.LayoutParams(android.widget.TableRow.LayoutParams.WRAP_CONTENT, android.widget.TableRow.LayoutParams.WRAP_CONTENT));
    		
    	}
	}

	OnClickListener bottomBtnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Calendar today = Calendar.getInstance();
			String curYear = String.valueOf(today.get(Calendar.YEAR));
			String curMonth = String.valueOf(doubleString(today
					.get(Calendar.MONTH) + 1));
			String curDay = String.valueOf(doubleString(today
					.get(Calendar.DAY_OF_MONTH)));
			int btn_id = v.getId();
			if (btn_id == btn_new_diary.getId()) {
				Intent intent = new Intent(Weekly.this, NoteActivity.class);
				Note note;
				if (selectedOneday != null) {
					String year = String.valueOf(selectedOneday.getYear());
					String month = doubleString(selectedOneday.getMonth());
					String date = doubleString(selectedOneday.getDay());
					note = new Note(year, month, date);
					intent.putExtra("completed", false);
					intent.putExtra("isempty", true);
				} else if (NoteControler.checkNoteExist(getFilesDir()
						.getAbsolutePath(), curYear + curMonth + curDay
						+ ".xml")) {
					note = NoteControler.getXmlStringDate(getFilesDir()
							.getAbsolutePath(), curYear, curMonth, curDay,
							Weekly.this);
				} else {
					note = new Note(curYear, curMonth, curDay);
				}
				intent.putExtra("note_object", note);
				intent.putExtra("isnew", true);

				if (note.getNoteDiaries().size() == 0) {
					intent.putExtra("index", 0);
				} else {
					intent.putExtra("index", note.getNoteDiaries().size());
				}
				startActivity(intent);
				finish();
			} else if (btn_id == btn_move_date.getId()) {
				new DatePickerDialog(Weekly.this, dp,
						datePicker.get(Calendar.YEAR),
						datePicker.get(Calendar.MONTH),
						datePicker.get(Calendar.DAY_OF_MONTH)).show();
			} else if (btn_id == btn_prev_week.getId()) {
				// 첫번째 주인 경우, 달을 바꾸고 불러옴.
				int lastweekday;
				Calendar prevCal = Calendar.getInstance();
				if (weekDays[0] > weekDays[6]) {

					if (iMonth == 1) {
						iYear--;
						iMonth = 12;
					} else {
						iMonth--;
					}
					lastweekday = weekDays[0] - 1;
					prevCal.set(iYear, iMonth, lastweekday);
					iWeek = prevCal.get(Calendar.WEEK_OF_MONTH);
				} else if (weekDays[0] == 1) {
					if (iMonth == 1) {
						iYear--;
						iMonth = 12;
						lastweekday = 31;
					} else {
						iMonth--;
						lastweekday = 28;
					}
					prevCal.set(iYear, iMonth, lastweekday);
					iWeek = prevCal.get(Calendar.WEEK_OF_MONTH);
				} else {
					iWeek--;
				}
				initialize(iYear, iMonth, iWeek);
			} else if (btn_id == btn_next_week.getId()) {
				// 첫번째 주인 경우, 달을 바꾸고 불러옴.
				int firstweekday;
				Calendar nextCal = Calendar.getInstance();

				if (weekDays[0] > weekDays[6]) {

					if (iMonth == 12) {
						iYear++;
						iMonth = 1;
					} else {
						iMonth++;
					}
					firstweekday = weekDays[6] + 1;
					nextCal.set(iYear, iMonth, firstweekday);
					iWeek = nextCal.get(Calendar.WEEK_OF_MONTH);
				} else if (weekDays[0] == rightNow
						.getMaximum(Calendar.DAY_OF_MONTH)) {
					if (iMonth == 12) {
						iYear++;
						iMonth = 1;
					} else {
						iMonth++;
					}
					firstweekday = 1;
					nextCal.set(iYear, iMonth, firstweekday);
					iWeek = nextCal.get(Calendar.WEEK_OF_MONTH);
				} else {
					iWeek++;
				}
				initialize(iYear, iMonth, iWeek);
			}
		}
	};

	protected Note loadNoteFromFile(Note paramnote) {
		Note load_note;
		load_note = NoteControler.getXmlStringDate(dirpath,
				paramnote.getYear(), paramnote.getMonth(), paramnote.getDate(),
				this);
		return load_note;
	}

	public Bitmap loadThumbnail(String filename) {
		String imgPath = dirpath + "/" + filename;
		return BitmapFactory.decodeFile(imgPath);

	}

	protected void onTouched(final DayOfWeek touchedDay) {
		selectedOneday = touchedDay;
		String year = String.valueOf(touchedDay.getYear());
		String month = doubleString(touchedDay.getMonth());
		String date = doubleString(touchedDay.getDay());
		Note selectedDayNote;
		if (!touchedDay.getIsEmpty()) {
			selectedDayNote = NoteControler.getXmlStringDate(getFilesDir()
					.getAbsolutePath(), year, month, date, Weekly.this);
			Intent intent = new Intent(Weekly.this, Daily.class);
			intent.putExtra("note_object", selectedDayNote);
			intent.putExtra("completed", false);
			startActivity(intent);
		} else {

		}
	}


	// 달력에 표시할 일자를 배열에 넣어 구성한다.
	private void makeMonthdate(int thisYear, int thisMonth) {
		rightNow.set(thisYear, thisMonth, 1);
		gCal.set(thisYear, thisMonth, 1);
		startDayOfweek = rightNow.get(Calendar.DAY_OF_WEEK);

		maxDay = gCal.getActualMaximum((Calendar.DAY_OF_MONTH));
		if (daylist == null)
			daylist = new ArrayList<String>();
		daylist.clear();

		if (actlist == null)
			actlist = new ArrayList<String>();
		actlist.clear();

		if (startDayOfweek != 1) {
			gCal.set(thisYear, thisMonth - 1, 1);
			int prevMonthMaximumDay = (gCal
					.getActualMaximum((Calendar.DAY_OF_MONTH)) + 2);
			for (int i = startDayOfweek; i > 1; i--) {
				daylist.add(Integer.toString(prevMonthMaximumDay - i));
				actlist.add("p");
			}
		}

		for (int i = 1; i <= maxDay; i++) // 일자를 넣는다.
		{
			daylist.add(Integer.toString(i));
			actlist.add("");
		}

		int dayDummy = (startDayOfweek - 1) + maxDay;
		if (dayDummy > 35) {
			dayDummy = 42 - dayDummy;
		} else {
			dayDummy = 35 - dayDummy;
		}

		// 자투리..그러니까 빈칸을 넣어 달력 모양을 이쁘게 만들어 준다.
		if (dayDummy != 0) {
			for (int i = 1; i <= dayDummy; i++) {
				daylist.add(Integer.toString(i));
				actlist.add("n");
			}
		}
	}

	private void makeWeeklydata(int thisYear, int thisMonth, int thisWeek) {
		makeMonthdate(thisYear, thisMonth);
		switch (thisWeek) {
		case 1:
			for (int i = 0; i < 7; i++) {
				weekDays[i] = Integer.parseInt(daylist.get(i));
			}
			break;
		case 2:
			for (int i = 7; i < 14; i++) {
				weekDays[i - 7] = Integer.parseInt(daylist.get(i));
			}
			break;
		case 3:
			for (int i = 14; i < 21; i++) {
				weekDays[i - 14] = Integer.parseInt(daylist.get(i));
			}
			break;
		case 4:
			for (int i = 21; i < 28; i++) {
				weekDays[i - 21] = Integer.parseInt(daylist.get(i));
			}
			break;
		case 5:
			for (int i = 28; i < 35; i++) {
				weekDays[i - 28] = Integer.parseInt(daylist.get(i));
			}
			break;
		case 6:
			for (int i = 35; i < 42; i++) {
				weekDays[i - 35] = Integer.parseInt(daylist.get(i));
			}
			break;
		default:
			break;
		}
	}

	public String intDateToNotename(int year, int month, int day) {
		String totalString = String.valueOf(year) + doubleString(month + 1)
				+ doubleString(day) + ".xml";
		return totalString;
	}

	/**
	 * 숫자를 2자리 문자로 변환, 2 -> 02
	 * 
	 * @param value
	 * @return
	 */
	protected String doubleString(int value) {
		String temp;

		if (value < 10) {
			temp = "0" + String.valueOf(value);
		} else {
			temp = String.valueOf(value);
		}
		return temp;
	}

	OnClickListener menuBtnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent;
			int btn_id = v.getId();
			if (btn_id == btn_monthly.getId()) {
				intent = new Intent(Weekly.this, CalendarView.class);
				startActivity(intent);
				finish();
			} else if (btn_id == btn_daily.getId()) {
				final Calendar today = Calendar.getInstance();
				String curYear = String.valueOf(today.get(Calendar.YEAR));
				String curMonth = String.valueOf(doubleString(today
						.get(Calendar.MONTH) + 1));
				String curDay = String.valueOf(doubleString(today
						.get(Calendar.DAY_OF_MONTH)));
				if (NoteControler.checkNoteExist(getFilesDir()
						.getAbsolutePath(), curYear + curMonth + curDay
						+ ".xml")) {
					Note note = NoteControler.getXmlStringDate(getFilesDir()
							.getAbsolutePath(), curYear, curMonth, curDay,
							Weekly.this);
					intent = new Intent(Weekly.this, Daily.class);
					intent.putExtra("note_object", note);
					intent.putExtra("completed", false);
					startActivity(intent);
					finish();
				} else {
					Note note = new Note(curYear, curMonth, curDay);
					intent = new Intent(Weekly.this, Daily.class);
					intent.putExtra("note_object", note);
					intent.putExtra("isempty", true);
					startActivity(intent);
					finish();
				}
			} else if (btn_id == btn_setting.getId()) {
				intent = new Intent(Weekly.this, SettingView.class);
				startActivity(intent);
				finish();
			} else if(btn_id == btn_freenote.getId()){
				intent = new Intent(Weekly.this, NoteList.class);
				startActivity(intent);
				finish();
			}
		}
	};

	DatePickerDialog.OnDateSetListener dp = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			datePicker.set(Calendar.YEAR, year);
			datePicker.set(Calendar.MONTH, monthOfYear);
			datePicker.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			initialize(year, monthOfYear,
					datePicker.get(Calendar.WEEK_OF_MONTH));
		}
	};
}
