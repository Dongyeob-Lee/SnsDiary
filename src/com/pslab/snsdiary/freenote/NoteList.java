package com.pslab.snsdiary.freenote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.pslab.calendar.CalendarView;
import com.pslab.snsdiary.R;
import com.pslab.snsdiary.SettingView;
import com.pslab.snsdiary.daily.Daily;
import com.pslab.snsdiary.note.Note;
import com.pslab.snsdiary.note.NoteActivity;
import com.pslab.snsdiary.note.NoteControler;
import com.pslab.snsdiary.spentools.SPenSDKUtils;
import com.pslab.snsdiary.weekly.Weekly;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class NoteList extends Activity {
	
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int DELETE_ID = Menu.FIRST;
    private ImageView btn_freenote_monthly;
    private ImageView btn_freenote_weekly;
    private ImageView btn_freenote_daily;
    private ImageView btn_freenote_setting;
    private ImageView btn_new_memo;
    private ImageView btn_delete_memo;
	public static String curDate = "";
	private EditText et_memodialog_body;
	private TextView tv_memodialog_date;
	LinearLayout layout;
	File file;
	private String dirpath;
	private String memofile;
	int selectedid;
	Boolean delete=false;
	ArrayList<Freenote_memo> freenote_memos = new ArrayList<Freenote_memo>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.notelist);
		
		btn_freenote_monthly = (ImageView)findViewById(R.id.btn_freenote_monthly);
		btn_freenote_weekly = (ImageView)findViewById(R.id.btn_freenote_weekly);
		btn_freenote_daily = (ImageView)findViewById(R.id.btn_freenote_daily);
		btn_freenote_setting = (ImageView)findViewById(R.id.btn_freenote_setting);
		btn_freenote_monthly.setOnClickListener(mbtnClickListener);
		btn_freenote_weekly.setOnClickListener(mbtnClickListener);
		btn_freenote_daily.setOnClickListener(mbtnClickListener);
		btn_freenote_setting.setOnClickListener(mbtnClickListener);
		
		dirpath = getFilesDir().getAbsolutePath();
		memofile = "memos.xml";
		file = new File(dirpath, memofile);
		
		fillData();
		
		
        long msTime = System.currentTimeMillis();  
        Date curDateTime = new Date(msTime);
 	
        SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y");  
        curDate = formatter.format(curDateTime);      
        
		btn_new_memo = (ImageView)findViewById(R.id.btn_freenote_new_memo);
		btn_new_memo.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {

				createNote();
				}
		});
		btn_delete_memo = (ImageView)findViewById(R.id.btn_freenote_delete_memo);
		btn_delete_memo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				invalidateDelete();
//				makeNotes(freenote_memos, true);
			}
		});
		
	}
	
	private void createNote() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this);
		LayoutInflater layoutInflater;
		layoutInflater = (LayoutInflater) this
				.getSystemService(this.LAYOUT_INFLATER_SERVICE);
		layout = (LinearLayout) layoutInflater.inflate(R.layout.memo_dialog,
				(ViewGroup) findViewById(R.id.new_diary_layout));
		tv_memodialog_date = (TextView)layout.findViewById(R.id.tv_memodialog_date);
		tv_memodialog_date.setText(curDate);
		et_memodialog_body = (EditText) layout
				.findViewById(R.id.et_memodialog_body);
//		builder.setTitle("새로운 다이어리 작성");
		builder.setView(layout);
//		builder.setMessage(year + "." + month + "." + date + "("
//				+ SelectedDay.getDayOfWeekKorean() + ")");
		builder.setPositiveButton("저장",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0,
							int arg1) {
						Freenote_memo freenote_memo = new Freenote_memo(NoteList.this);
						freenote_memo.setBody(et_memodialog_body.getText().toString());
						freenote_memo.setDate(tv_memodialog_date.getText().toString());
						freenote_memos.add(freenote_memo);
						saveMemo();
						fillData();
					}

				});
		builder.setNegativeButton("취소", null);
		builder.show();
    }
	
	private void saveMemo(){
		try {
			file.createNewFile();
			// ////
			FileOutputStream fos = openFileOutput(memofile,
					Context.MODE_PRIVATE);
			fos.write(MemoControler.makeNoteXml(freenote_memos,this)
					.toString().getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void fillData() {

		if(MemoControler.checkNoteExist(dirpath, memofile)){
			freenote_memos = MemoControler.getXmlStringDate(this);
		}
        makeNotes(freenote_memos);
    }
	private void editMemo(int id){
		for(int i=0; i<freenote_memos.size(); i++){
			if(id==freenote_memos.get(i).getId()){
				selectedid = i;
				AlertDialog.Builder builder = new AlertDialog.Builder(
						this);
				LayoutInflater layoutInflater;
				layoutInflater = (LayoutInflater) this
						.getSystemService(this.LAYOUT_INFLATER_SERVICE);
				layout = (LinearLayout) layoutInflater.inflate(R.layout.memo_dialog,
						(ViewGroup) findViewById(R.id.new_diary_layout));
				tv_memodialog_date=(TextView)layout.findViewById(R.id.tv_memodialog_date);
				tv_memodialog_date.setText(freenote_memos.get(i).getDate());
				et_memodialog_body = (EditText) layout
						.findViewById(R.id.et_memodialog_body);
				et_memodialog_body.setText(freenote_memos.get(i).getBody());
				builder.setView(layout);
				builder.setPositiveButton("저장",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0,
									int arg1) {
								freenote_memos.get(selectedid).setBody(et_memodialog_body.getText().toString());
								saveMemo();
								fillData();
							}

						});
				builder.setNegativeButton("취소", null);
				builder.show();
			}
		}
	}
	private void invalidateDelete(){
		if(!delete){
			for(int i=0; i<freenote_memos.size(); i++){
				freenote_memos.get(i).setDelete(true);
				freenote_memos.get(i).invalidate();
			}
			btn_new_memo.setVisibility(View.INVISIBLE);
			btn_delete_memo.setImageResource(R.drawable.icon_complete_memo);
			delete=true;
		}else{
			for(int i=0; i<freenote_memos.size(); i++){
				freenote_memos.get(i).setDelete(false);
				freenote_memos.get(i).invalidate();
			}
			btn_new_memo.setVisibility(View.VISIBLE);
			btn_delete_memo.setImageResource(R.drawable.icon_delete_memo);
			delete=false;
		}
		
	}
	private void invalidateofdelete(){
			for(int i=0; i<freenote_memos.size(); i++){
				freenote_memos.get(i).setDelete(true);
				freenote_memos.get(i).invalidate();
			}
	}
	private void deleteMemo(int Id){
		for(int i=0; i<freenote_memos.size(); i++){
			if(Id==freenote_memos.get(i).getId()){
				final int selected = i;
				AlertDialog.Builder builder = new AlertDialog.Builder(
						this);
				builder.setTitle("메모 삭제");
				builder.setMessage("삭제하시겠습니까?");
				builder.setPositiveButton("삭제",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0,
									int arg1) {
								freenote_memos.remove(selected);
								saveMemo();
								fillData();
								invalidateofdelete();
							}
						});
				builder.setNegativeButton("취소", null);
				builder.show();
				
			}
		}
	}
    private void makeNotes(ArrayList<Freenote_memo> memos){
    	TableLayout tableLayout = (TableLayout)findViewById(R.id.tl_freenote);
    	tableLayout.removeAllViews();
    	
    	int note_count = memos.size();
    	int maxRow = note_count/2+1;
    	int count;
    	for(int i=0; i<maxRow; i++){
    		
    		count=0;
    		TableRow tr = new TableRow(this);
    		tr.setLayoutParams(new TableRow.LayoutParams(android.widget.TableRow.LayoutParams.WRAP_CONTENT, android.widget.TableRow.LayoutParams.WRAP_CONTENT));
    		while(note_count>0){
    			
    			if(count==2){
    				break;
    			}
    			memos.get(note_count-1).setId(note_count-1);
    			memos.get(note_count-1).invalidate();
    			memos.get(note_count-1).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (delete) {
							deleteMemo(v.getId());
						}else{
							editMemo(v.getId());
						}
					}
				});
    			TableRow.LayoutParams params= new TableRow.LayoutParams(320, 290);
    			params.setMargins(0, 5, 5, 0);
    			tr.addView(memos.get(note_count-1),params);
    			
    			note_count--;
    			count++;
    		}
//    		if(count!=2){
//    			Freenote_memo freenote_memo = new Freenote_memo(this);
//    			freenote_memo.setBody("마지막 하나더");
//    			tr.addView(freenote_memo);
//    		}
    		tableLayout.addView(tr, new TableLayout.LayoutParams(android.widget.TableRow.LayoutParams.WRAP_CONTENT, android.widget.TableRow.LayoutParams.WRAP_CONTENT));
    		
    	}
    }
    
    OnClickListener mbtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final Calendar today = Calendar.getInstance();
	        String curYear = String.valueOf(today.get(Calendar.YEAR));
	        String curMonth = String.valueOf(doubleString(today.get(Calendar.MONTH)+1));
	        String curDay = String.valueOf(doubleString(today.get(Calendar.DAY_OF_MONTH)));
			if(v.getId()==btn_freenote_monthly.getId()){
				Intent intent = new Intent(NoteList.this, CalendarView.class);
				startActivity(intent);
				finish();
			}else if(v.getId()==btn_freenote_weekly.getId()){
				Intent intent = new Intent(NoteList.this, Weekly.class);
				startActivity(intent);
				finish();
			}else if(v.getId()==btn_freenote_daily.getId()){
				if(NoteControler.checkNoteExist(getFilesDir().getAbsolutePath(), curYear+curMonth+curDay+".xml")){
					Note note = NoteControler.getXmlStringDate(getFilesDir().getAbsolutePath(), curYear, curMonth, curDay, NoteList.this);
					Intent intent = new Intent(NoteList.this, Daily.class);
					intent.putExtra("note_object", note);
					intent.putExtra("completed", false);
					startActivity(intent);
					finish();
				}else{
					Note note = new Note(curYear, curMonth, curDay);
					Intent intent = new Intent(NoteList.this,Daily.class);
					intent.putExtra("note_object", note);
					intent.putExtra("isempty", true);
					startActivity(intent);
					finish();
				}
			}else if(v.getId()==btn_freenote_setting.getId()){
				Intent intent = new Intent(NoteList.this, SettingView.class);
				startActivity(intent);
				finish();
			}
		}
	};
    
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();        
    }   
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
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
