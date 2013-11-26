package com.pslab.snsdiary.daily;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import javax.crypto.spec.IvParameterSpec;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.facebook.Session;
import com.facebook.samples.graphapi.CData;
import com.facebook.samples.graphapi.CustomAdapter;
import com.pslab.calendar.CalendarView;
import com.pslab.snsdiary.R;
import com.pslab.snsdiary.SettingView;
import com.pslab.snsdiary.R.layout;
import com.pslab.snsdiary.R.menu;
import com.pslab.snsdiary.dropbox.Dropbox_controler;
import com.pslab.snsdiary.freenote.NoteList;
import com.pslab.snsdiary.note.Note;
import com.pslab.snsdiary.note.NoteActivity;
import com.pslab.snsdiary.note.NoteControler;
import com.pslab.snsdiary.spentools.SPenSDKUtils;
import com.pslab.snsdiary.spentools.SubjectSelect;
import com.pslab.snsdiary.weekly.Weekly;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.pslab.jaehwan.api.SAPI;
import android.pslab.jaehwan.object.Feed;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Daily extends Activity {

	int numOfdiary;
	int index;
	boolean isCompleted;
	boolean isEmpty;
	String dirPath;
	private Note note;
	private ImageView btn_monthly;
	private ImageView btn_weekly;
	private ImageView btn_freenote;
	private ImageView btn_setting;
	private ImageView btn_record_play;
	private ImageView btn_feedlist_dialog;
	private TextView tv_daily_title;;
	private TextView tv_daily_yearmonth;
	private TextView tv_daily_date;
	private TextView tv_daily_dayofweek;
	private ImageView iv_daily_subject;
	private TextView tv_daily_temperature;
	private ImageView iv_daily_weather;
	private RelativeLayout cur_canvas;
	Button btn_prev;
	Button btn_next;
	TextView diary_page;
	SubjectSelect subjectSelect;
	Dropbox_controler dropbox_controler;
	MediaPlayer player;
	File file;
	Handler wHandler;
	SAPI sapi = new SAPI();
	Feed feed;
	ArrayList<Feed> savefeedlist = new ArrayList<Feed>();
	Session session;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_daily);
		dirPath = getFilesDir().getAbsolutePath();
		session = Session.getActiveSession();
		///button setting
		btn_monthly = (ImageView)findViewById(R.id.daily_icon_monthly);
		btn_weekly = (ImageView)findViewById(R.id.daily_icon_weekly);
		btn_freenote = (ImageView)findViewById(R.id.daily_icon_freenote);
		btn_setting = (ImageView)findViewById(R.id.daily_icon_setting);
		btn_monthly.setOnClickListener(menuBtnClickListener);
		btn_weekly.setOnClickListener(menuBtnClickListener);
		btn_freenote.setOnClickListener(menuBtnClickListener);
		btn_setting.setOnClickListener(menuBtnClickListener);
		btn_record_play = (ImageView)findViewById(R.id.btn_record_play);
		btn_feedlist_dialog = (ImageView)findViewById(R.id.btn_feedlist_dialog);
		Bundle bundle = getIntent().getExtras();
		isCompleted = getIntent().getBooleanExtra("completed", false);
		isEmpty = getIntent().getBooleanExtra("isempty", false);
		note = (Note)bundle.getParcelable("note_object");
		note = loadNoteFromFile(note);
		index = getIntent().getIntExtra("index", 0);
		Log.d("note", note.getYear()+","+note.getMonth()+","+note.getDate());
		file = new File(dirPath, note.getYear()+note.getMonth()+note.getDate()+".xml");
		tv_daily_title = (TextView)findViewById(R.id.tv_daily_title);
		tv_daily_yearmonth = (TextView)findViewById(R.id.tv_daily_yearmonth);
		tv_daily_date = (TextView)findViewById(R.id.tv_daily_date);
		tv_daily_dayofweek = (TextView)findViewById(R.id.tv_daily_dayofweek);
		iv_daily_subject = (ImageView)findViewById(R.id.iv_daily_subject);
		tv_daily_temperature = (TextView)findViewById(R.id.tv_daily_temperature);
		iv_daily_weather = (ImageView)findViewById(R.id.iv_daily_weather);
		cur_canvas = (RelativeLayout)findViewById(R.id.img_cur_diary);
		
		btn_prev = (Button)findViewById(R.id.btn_daily_prev);
		btn_next = (Button)findViewById(R.id.btn_daily_next);
		diary_page = (TextView)findViewById(R.id.tv_daily_page);
		btn_prev.setOnClickListener(mBtnClickListener);
		btn_next.setOnClickListener(mBtnClickListener);
		
		numOfdiary = note.getNoteDiaries().size();
		Log.d("numofdiary", String.valueOf(numOfdiary));
		subjectSelect = new SubjectSelect();
		
		if (Dropbox_controler.isValid(this)) {
			dropbox_controler = new Dropbox_controler(this);
		}
//		if(isCompleted){
//			index = numOfdiary-1;
//			updateDailyDiary();
//		}else{
//			index=0;
//			updateDailyDiary();
//		}
		updateDailyDiary();
		
		btn_record_play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playStart();
			}
		});
		btn_feedlist_dialog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("feedclick", "ddddd");
				
				sapi.setFacebookAccessToken(Session.getActiveSession());
				
				for(int i=0; i<note.getNoteDiaries().get(index).getFeedlist().size();i++){
					Log.d("djfiejfi", note.getNoteDiaries().get(index).getFeedlist().get(i));
					feed = sapi.getFeed("facebook", note.getNoteDiaries().get(index).getFeedlist().get(i));
					savefeedlist.add(feed);
				}
				
				if(savefeedlist!=null){
				final CustomAdapter ca = setAdapterSave();
			     
			     
			     AlertDialog.Builder builder = new AlertDialog.Builder(Daily.this);
			     builder.setTitle("저장한 글")
			     .setCancelable(true)
			     
			     .setAdapter(ca, new DialogInterface.OnClickListener() {
			   
			   public void onClick(DialogInterface dialog, int which) {
			    // TODO Auto-generated method stub
			    Toast.makeText(Daily.this, ca.getItems(which).getS_name()+" "+ca.getItems(which).getS_message(), Toast.LENGTH_SHORT).show();
			    
			   
			   }
			  });
			  builder.create().show();
				
				}
			}
		});
		
	}
	protected void updateDailyDiary(){
		
		Drawable d;
		if(isEmpty){
			//
			wHandler = new Handler();
			Runnable r = new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					parser();
				}
			};
			wHandler.postDelayed(r, 1000);
			tv_daily_title.setText("");
			tv_daily_yearmonth.setText(note.getYear()+", "+note.getMonth());
			tv_daily_date.setText(note.getDate());
			tv_daily_dayofweek.setText(SPenSDKUtils.get_day_of_week(note.getYear(), note.getMonth(), note.getDate()));
			iv_daily_subject.setImageResource(subjectSelect.getDrawable(0));
			d = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.pleasenewdiary));
			btn_next.setVisibility(View.INVISIBLE);
			btn_prev.setVisibility(View.INVISIBLE);
			diary_page.setText("");
		}else{
			tv_daily_title.setText(note.getNoteDiaries().get(index).getTitle());
			tv_daily_yearmonth.setText(note.getYear()+", "+note.getMonth());
			tv_daily_date.setText(note.getDate());
			tv_daily_dayofweek.setText(SPenSDKUtils.get_day_of_week(note.getYear(), note.getMonth(), note.getDate()));
			iv_daily_subject.setImageResource(subjectSelect.getDrawable(note.getNoteDiaries().get(index).getSubject()));
			d = new BitmapDrawable(getResources(), loadThumbnail(note.getNoteDiaries().get(index).getSCanvasFileName()));
			diary_page.setText((index+1)+"/"+numOfdiary);
			setWeatherImage("울산",note.getNoteDiaries().get(index).getWeatherImg(),note.getNoteDiaries().get(index).getTemperature());
		}
		cur_canvas.setBackground(d);
		if(isEmpty){
			btn_record_play.setVisibility(View.INVISIBLE);
			btn_feedlist_dialog.setVisibility(View.INVISIBLE);
		}else{
			if(note.getNoteDiaries().get(index).getRecordFileName().equals("null")){
				btn_record_play.setVisibility(View.INVISIBLE);
			}else{
				btn_record_play.setVisibility(View.VISIBLE);
			}
			if(note.getNoteDiaries().get(index).getFeedlist().size()==0){
				btn_feedlist_dialog.setVisibility(View.INVISIBLE);
			}else{
				btn_feedlist_dialog.setVisibility(View.VISIBLE);
			}
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_daily, menu);
		return true;
	}
	public void onMenuClick(View v){
		
		if(v.getId()==R.id.btn_daily_add){
			Intent intent = new Intent(Daily.this, NoteActivity.class);
			intent.putExtra("note_object", note);
			intent.putExtra("isnew", true);
			if (note.getNoteDiaries().size()==0) {
				intent.putExtra("index", 0);
			} else {
				intent.putExtra("index", note.getNoteDiaries().size());
			}
			startActivity(intent);
			finish();
		}else if(v.getId()==R.id.btn_daily_edit){
			Intent intent = new Intent(Daily.this, NoteActivity.class);
			intent.putExtra("note_object", note);
			if (note.getNoteDiaries().size()==0) {
				Toast.makeText(this, "There is no diary to edit,please add diary" ,Toast.LENGTH_LONG).show();
			} else {
				intent.putExtra("index", index);
				startActivity(intent);
				finish();
			}
			
		}else if(v.getId()==R.id.btn_daily_share){
			Log.d("file", note.getNoteDiaries().get(index).getSCanvasFileName());
			File f = new File(dirPath,note.getNoteDiaries().get(index).getSCanvasFileName());
			if(f.exists()){
				Log.d("file", "exist");
			}
			sapi.setFacebookAccessToken(Session.getActiveSession());
			//sapi.postFeed("facebook", "wth");
			sapi.postPhoto("facebook", "", dirPath, note.getNoteDiaries().get(index).getSCanvasFileName(), "me");
			Toast.makeText(Daily.this, "업로드되었습니다.", Toast.LENGTH_SHORT).show();
			
			
			
		}else if(v.getId()==R.id.btn_daily_delete){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("삭제");
			builder.setMessage("삭제하시겠습니까?");
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							if (note.getNoteDiaries().size() == 1) {
								if (Dropbox_controler
										.isValid(Daily.this)) {
									delete_xml_dropbox();
								}
								deletePngFile();
								delete_cur_file();
								isEmpty=true;
							} else {
								deletePngFile();
								deleteRecord(note.getNoteDiaries().get(index).getRecordFileName());
								note.getNoteDiaries().remove(index);
								if (Dropbox_controler
										.isValid(Daily.this)) {
									upload_to_dropbox();
								}
								updateNoteFile(file);
								if(index!=0){
									index--;
								}
								numOfdiary--;
							}
							updateDailyDiary();
						}
					});
			builder.setNegativeButton("No", null);
			builder.show();
		}
	}
	protected Note loadNoteFromFile(Note paramnote) {
		Note load_note;
		load_note = NoteControler.getXmlStringDate(dirPath,
				paramnote.getYear(), paramnote.getMonth(), paramnote.getDate(),
				this);
		return load_note;
	}
	public Bitmap loadThumbnail(String filename){
    	String imgPath = dirPath+"/"+filename;
		return BitmapFactory.decodeFile(imgPath);
    	
    }

	private OnClickListener mBtnClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v==btn_prev){
				if(index==0){
					return;
				}
				index--;
				updateDailyDiary();
			}else if(v==btn_next){
				if(index<numOfdiary-1){
					index++;
					updateDailyDiary();
				}else{
					return;
				}
			}
		}
	};
	private void delete_cur_file() {
		file.delete();
	}
	private void delete_xml_dropbox() {
		
		dropbox_controler.delete_xml(note.getYear()+note.getMonth()+note.getDate()+".xml", note);
	}

	private void upload_to_dropbox() {
		dropbox_controler.upload_xml(note.getYear()+note.getMonth()+note.getDate()+".xml");
		dropbox_controler.upload_png(note);
	}
	private void deletePngFile() {
		File pngFile = new File(dirPath+"/image",note.getNoteDiaries().get(index).getSCanvasFileName());
		if(pngFile.exists()){
			pngFile.delete();
		}
	}
	 OnClickListener menuBtnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent;
				int btn_id = v.getId();
				if(btn_id==btn_monthly.getId()){
					intent = new Intent(Daily.this, CalendarView.class);
					startActivity(intent);
					finish();
				}else if(btn_id==btn_weekly.getId()){
					intent = new Intent(Daily.this, Weekly.class);
					startActivity(intent);
					finish();
				}else if(btn_id==btn_setting.getId()){
					intent = new Intent(Daily.this, SettingView.class);
					startActivity(intent);
					finish();
				}else if(btn_id==btn_freenote.getId()){
					intent = new Intent(Daily.this, NoteList.class);
					startActivity(intent);
					finish();
				}
			}
		};
		
	private boolean checkRecordFile(){
		
		File file = new File(dirPath+"/record", note.getYear()+note.getMonth()+note.getDate()+String.valueOf(index)+".mp4");
		
		if(file.exists())
			return true;
		
		return false;
	}
	private void playStart() {

		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}

		try {
			player = new MediaPlayer();
			FileInputStream fs = new FileInputStream(dirPath+"/record/"+note.getYear()+note.getMonth()+note.getDate()+String.valueOf(index)+".mp4");
			FileDescriptor fd = fs.getFD();
			player.setDataSource(fd);
			player.prepare();
			player.start();
			fs.close();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Toast.makeText(getApplicationContext(), "재생 시작", Toast.LENGTH_LONG)
				.show();
		
	}
	public void parser() {

		try {
			URL url = new URL(
					"http://www.kma.go.kr/XML/weather/sfc_web_map.xml");
			XmlPullParserFactory parserFactory = XmlPullParserFactory
					.newInstance();
			XmlPullParser parser = parserFactory.newPullParser();
			parser.setInput(url.openStream(), "utf-8");

			int parserEvent = parser.getEventType();
			String tag = "ready..!";

			String stnid = "";
			String desc = "";
			String ta = "";
			String region = "";

			while (parserEvent != XmlPullParser.END_DOCUMENT) {

				switch (parserEvent) {
				case XmlPullParser.START_DOCUMENT:
					Log.d("parserTest", "Parser Start..!");
					break;
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (tag.equals("local")) {
						stnid = parser.getAttributeValue(null, "stn_id");
						desc = parser.getAttributeValue(null, "desc");
						ta = parser.getAttributeValue(null, "ta");
					}
					break;
				case XmlPullParser.TEXT:
					if (tag.equals("local")) {
						region = parser.getText();
					}
					break;

				case XmlPullParser.END_TAG:
					if (tag.equals("local")) {
						if (region.equals("울산"))
							setWeatherImage(region, desc, ta);
					}
					tag = "nothing";
					break;
				}
				parserEvent = parser.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.d("parserTest", "error");
		}
	}
	public void setWeatherImage(String region, String desc, String ta) {

		tv_daily_temperature.setText(ta);

		Drawable weatherImage = null;

		if (desc.equals("맑음")) {
			weatherImage = getResources().getDrawable(R.drawable.w_sunny);
		} else if (desc.equals("비")) {
			weatherImage = getResources().getDrawable(R.drawable.w_rain);
		} else if (desc.equals("구름조금")) {
			weatherImage = getResources()
					.getDrawable(R.drawable.w_cloud_little);
		} else if (desc.equals("구름많이")) {
			weatherImage = getResources().getDrawable(R.drawable.w_cloud_much);
		} else if (desc.equals("흐림")) {
			weatherImage = getResources().getDrawable(R.drawable.w_cloud);
		} else if (desc.equals("박무")) {
			weatherImage = getResources().getDrawable(R.drawable.w_thin_mist);
		} else if (desc.equals("안개")) {
			weatherImage = getResources().getDrawable(R.drawable.w_mist);
		} else if (desc.equals("연무")) {
			weatherImage = getResources().getDrawable(R.drawable.w_smog);
		} else if (desc.equals("황사")) {
			weatherImage = getResources().getDrawable(R.drawable.w_yellow_dust);
		} else if (desc.equals("천둥번개")) {
			weatherImage = getResources().getDrawable(R.drawable.w_thunder);
		} else if (desc.equals("눈")) {
			weatherImage = getResources().getDrawable(R.drawable.w_snow);
		} else if (desc.equals("소나기")) {
			weatherImage = getResources().getDrawable(R.drawable.w_shower);
		} else if (desc.equals("비 또는 눈")) {
			weatherImage = getResources().getDrawable(R.drawable.w_rainorsnow);
		} else if (desc.equals("-")) {
			weatherImage = getResources().getDrawable(R.drawable.w_sunny);
		} else if (desc.equals("눈 또는 비")) {
			weatherImage = getResources().getDrawable(R.drawable.w_snoworrain);
		}
		iv_daily_weather.setImageDrawable(weatherImage);
		if (isEmpty) {
			note.getNoteDiaries().get(index).setTemperature(ta);
			note.getNoteDiaries().get(index).setWeatherImg(desc);
		}
	}
	private void deleteRecord(String recordfilename) {
		File file = new File(dirPath + "/record", recordfilename);
		if (file.exists()) {
			file.delete();
		}
		recordfilename = "null";
		note.getNoteDiaries().get(index).setRecordFileName(recordfilename);
	}
	protected void updateNoteFile(File file) {
		// TODO Auto-generated method stub

		try {
			file.createNewFile();
			// ////
			FileOutputStream fos = openFileOutput(note.getYear()+note.getMonth()+note.getDate()+".xml",
					Context.MODE_PRIVATE);
			fos.write(NoteControler.updateNoteXml(note, this)
					.toString().getBytes());

			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private CustomAdapter setAdapterSave(){
	     Vector<CData> vector = new Vector<CData>();
	        sapi.setFacebookAccessToken(session);
	   
	        for (int i = 0; i < savefeedlist.size(); i++) {
				if (savefeedlist.get(i).getText() != null) {
					CData cData;
					if (savefeedlist.get(i).getFeedImg() != null) {
						cData = new CData(sapi.getUser("facebook", savefeedlist.get(i).getUserId()).getName(),savefeedlist.get(i).getText(),downloadBitmap(sapi.getUser("facebook",
								savefeedlist.get(i).getUserId()).getUserProfileUrl()),downloadBitmap(savefeedlist.get(i).getFeedImg()),savefeedlist.get(i).getFeedDate().replace("T", " ").replace("+0000", " "),savefeedlist.get(i).getFeedId());
					} else {
						cData = new CData(sapi.getUser("facebook", savefeedlist.get(i).getUserId()).getName(),savefeedlist.get(i).getText(),downloadBitmap(sapi.getUser("facebook",
								savefeedlist.get(i).getUserId()).getUserProfileUrl()),null,savefeedlist.get(i).getFeedDate().replace("T", " ").replace("+0000", " "),savefeedlist.get(i).getFeedId());
					}
					vector.add(cData);
				}
			}
	 
	     return new CustomAdapter(this, R.layout.myitem, vector, this);
	    }
	
	
	public Bitmap downloadBitmap(String urlname) {
		Log.d("URl", "url " + urlname);
		URL url = null;
		Bitmap Result = null;
		try {
			url = new URL(urlname);
			Result = BitmapFactory.decodeStream(url.openStream());
			Bitmap.createScaledBitmap(Result, 10, 10, false);
			Log.d("URL", "herecome?" + Result);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Result = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Result = null;
		}
		return Result;
	}
	
}
