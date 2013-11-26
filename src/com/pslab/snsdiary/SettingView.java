package com.pslab.snsdiary;

import java.util.ArrayList;
import java.util.Calendar;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.Session.StatusCallback;
import com.pslab.calendar.CalendarView;
import com.pslab.calendar.SubjectControler;
import com.pslab.snsdiary.daily.Daily;
import com.pslab.snsdiary.dropbox.Dropbox_controler;
import com.pslab.snsdiary.freenote.NoteList;
import com.pslab.snsdiary.note.Note;
import com.pslab.snsdiary.note.NoteControler;
import com.pslab.snsdiary.weekly.Weekly;
import com.sileria.util.Log;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.SwitchPreference;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SettingView extends Activity {
	private ImageView btn_monthly;
	private ImageView btn_weekly;
	private ImageView btn_daily;
	private ImageView btn_freenote;
	private ProgressDialog progressDialog;
	private Dropbox_controler dropbox_controler;
	private CheckBox dropbox_checkbox;
	private CheckBox facebook_checkbox;
	LayoutInflater inflater;
	ArrayList<String> subjectlist;
	// ////////facebook Define/////////////////////////
	public static final String APP_ID = "493738797351270";
	static final String PENDING_REQUEST_BUNDLE_KEY = "com.facebook.samples.graphapi:PendingRequest";
	ProgressDialog dialog;
	boolean pendingRequest;
	Session session;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting_view);
		
		btn_monthly = (ImageView)findViewById(R.id.setting_icon_monthly);
		btn_weekly = (ImageView)findViewById(R.id.setting_icon_weekly);
		btn_daily = (ImageView)findViewById(R.id.setting_icon_daily);
		btn_freenote = (ImageView)findViewById(R.id.setting_icon_freenote);
		btn_monthly.setOnClickListener(menuBtnClickListener);
		btn_weekly.setOnClickListener(menuBtnClickListener);
		btn_daily.setOnClickListener(menuBtnClickListener);
		btn_freenote.setOnClickListener(menuBtnClickListener);
		dropbox_checkbox = (CheckBox) findViewById(R.id.cb_dropboxlogin);
		facebook_checkbox = (CheckBox) findViewById(R.id.cb_facebooklogin);
		if (Dropbox_controler.isValid(this)) {
			dropbox_checkbox.setChecked(true);
		} else {
			dropbox_checkbox.setChecked(false);
		}
		dropbox_checkbox
				.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						onDropboxLogin();
					}
				});

		// ///facebook init();
		this.session = createSession();
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		
		if(session !=null){
			if(session.isOpened()){
				facebook_checkbox.setChecked(true);
			}else{
				facebook_checkbox.setChecked(false);
			}
		}else{
			facebook_checkbox.setChecked(false);
		}
		facebook_checkbox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClickLogin();
			}
		});
	}

	OnClickListener menuBtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent;
			int btn_id = v.getId();
			if(btn_id==btn_monthly.getId()){
				intent = new Intent(SettingView.this, CalendarView.class);
				startActivity(intent);
				finish();
			}else if(btn_id==btn_weekly.getId()){
				intent = new Intent(SettingView.this, Weekly.class);
				startActivity(intent);
				finish();
			}else if(btn_id==btn_daily.getId()){
				final Calendar today = Calendar.getInstance();
		        String curYear = String.valueOf(today.get(Calendar.YEAR));
		        String curMonth = String.valueOf(doubleString(today.get(Calendar.MONTH)+1));
		        String curDay = String.valueOf(doubleString(today.get(Calendar.DAY_OF_MONTH)));
				if(NoteControler.checkNoteExist(getFilesDir().getAbsolutePath(), curYear+curMonth+curDay+".xml")){
					Note note = NoteControler.getXmlStringDate(getFilesDir().getAbsolutePath(), curYear, curMonth, curDay, SettingView.this);
					intent = new Intent(SettingView.this, Daily.class);
					intent.putExtra("note_object", note);
					intent.putExtra("completed", false);
					startActivity(intent);
					finish();
				}else{
					Note note = new Note(curYear, curMonth, curDay);
					intent = new Intent(SettingView.this,Daily.class);
					intent.putExtra("note_object", note);
					intent.putExtra("isempty", true);
					startActivity(intent);
					finish();
				}
			}else if(btn_id==btn_freenote.getId()){
				intent = new Intent(SettingView.this, NoteList.class);
				startActivity(intent);
				finish();
			}
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_setting_view, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (this.session.onActivityResult(this, requestCode, resultCode, data)
				&& pendingRequest && this.session.getState().isOpened()) {
			// sendRequests();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		pendingRequest = savedInstanceState.getBoolean(
				PENDING_REQUEST_BUNDLE_KEY, pendingRequest);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean(PENDING_REQUEST_BUNDLE_KEY, pendingRequest);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 0) {
				progressDialog.dismiss();
			}
		}

	};
	private void onDropboxLogin() {

		if (Dropbox_controler.isValid(this)) {
			
			Dropbox_controler.unlinkSession(this);
			if(Dropbox_controler.isValid(this)){
				dropbox_checkbox.setChecked(true);
			}else{
				dropbox_checkbox.setChecked(false);
			}
		} else {
			dropbox_controler = new Dropbox_controler(
					SettingView.this);
			dropbox_controler.resume();
			if(Dropbox_controler.isValid(this)){
				dropbox_checkbox.setChecked(true);
			}else{
				dropbox_checkbox.setChecked(false);
			}
		}
	}
	private void onClickLogin() {

		if (!this.session.isOpened()) {
			StatusCallback callback = new StatusCallback() {
				public void call(Session session, SessionState state,
						Exception exception) {
					if (exception != null) {
						new AlertDialog.Builder(SettingView.this)
								.setTitle("login failed")
								.setMessage(exception.getMessage())
								.setPositiveButton("OK", null).show();
						SettingView.this.session = createSession();
						// change to logout Button.
						SettingView.this.facebook_checkbox.setChecked(false);
					}
				}
			};
			pendingRequest = true;
			this.session.openForRead(new Session.OpenRequest(this)
					.setCallback(callback));
		} else {

			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
				// session.close();
				this.session = createSession();
				// change to login button.
				if (session == null){
					facebook_checkbox.setChecked(false);
				}
			}

		}
	}
	private Session createSession() {
		Session activeSession = Session.getActiveSession();
		if (activeSession == null || activeSession.getState().isClosed()) {
			activeSession = new Session.Builder(this).setApplicationId(APP_ID)
					.build();
			Session.setActiveSession(activeSession);

		}
		return activeSession;
	}
	 protected String doubleString(int value)
	    {
	        String temp;
	 
	        if(value < 10){
	            temp = "0"+ String.valueOf(value);
	             
	        }else {
	            temp = String.valueOf(value);
	        }
	        return temp;
	    }
}
