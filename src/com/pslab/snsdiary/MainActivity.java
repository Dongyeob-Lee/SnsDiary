package com.pslab.snsdiary;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.Session.StatusCallback;
import com.pslab.calendar.CalendarView;
import com.pslab.snsdiary.dropbox.Dropbox_controler;
import com.sileria.android.Timer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	LinearLayout ll_start;
	Dropbox_controler controler;
	private ProgressDialog progressDialog;
	// ////////facebook Define/////////////////////////
	public static final String APP_ID = "493738797351270";
	static final String PENDING_REQUEST_BUNDLE_KEY = "com.facebook.samples.graphapi:PendingRequest";
	ProgressDialog dialog;
	boolean pendingRequest;
	Session session;
	int flag=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ll_start = (LinearLayout)findViewById(R.id.backgroundofmain);
		ll_start.setOnClickListener(new LinearLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (Dropbox_controler.isValid(MainActivity.this)) {
							controler = new Dropbox_controler(
									MainActivity.this, handler);
							controler.resume();
							progressDialog = ProgressDialog.show(
									MainActivity.this, "", "잠시만 기다려주세요", true);
							controler.download_xml();
						} else {
							Intent intent = new Intent(MainActivity.this,
									CalendarView.class);
							startActivity(intent);
						}
					}
				});
			}
		});
		// ///facebook init();
		 this.session = createSession();
	        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
	}

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 0) {
				Intent intent = new Intent(MainActivity.this,
						CalendarView.class);
				progressDialog.dismiss();
				startActivity(intent);

			}
		}

	};
	 @Override
	    protected void onRestoreInstanceState(Bundle savedInstanceState) {
	        super.onRestoreInstanceState(savedInstanceState);

	        pendingRequest = savedInstanceState.getBoolean(PENDING_REQUEST_BUNDLE_KEY, pendingRequest);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (this.session.onActivityResult(this, requestCode, resultCode, data) &&
                pendingRequest &&
                this.session.getState().isOpened()) {
         //   sendRequests();
        }
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	
	private Session createSession() {
		Session activeSession = Session.getActiveSession();
		if (activeSession == null || activeSession.getState().isClosed()) {
			activeSession = new Session.Builder(this).setApplicationId(
					APP_ID).build();
			Session.setActiveSession(activeSession);
			
		}
		return activeSession;
	}

}
