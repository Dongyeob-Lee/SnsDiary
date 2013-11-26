package com.pslab.snsdiary.weekly;

import com.pslab.snsdiary.R;
import com.pslab.snsdiary.note.Note;
import com.pslab.snsdiary.note.NoteControler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableRow;

public class DayOfWeek extends View{

	private int year;
	private int month;
	private int day;
	private String title;
	private String canvasname;
	private String date;
	private int subject;
	private Paint bgTitlePaint;
	private Paint textTitlePaint;
	private Paint textDatePaint;
	private Boolean isEmpty;
	private boolean mSelected;
	Context context;
	public DayOfWeek(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context= context;
		init();
		
	}
	private void init()
	{
		bgTitlePaint = new Paint();
		bgTitlePaint.setColor(Color.GRAY);
		bgTitlePaint.setAlpha(150);
		textTitlePaint = new Paint();
		textTitlePaint.setColor(Color.BLACK);
		textTitlePaint.setAntiAlias(true);
		textTitlePaint.setTextSize(40);
		textDatePaint=new Paint();
		textDatePaint.setColor(Color.BLACK);
		textDatePaint.setAntiAlias(true);
		textDatePaint.setTextSize(40);
		this.setLayoutParams(new TableRow.LayoutParams(224, 320));
		isEmpty = true;
		mSelected=false;
	}
	
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if(mSelected){
//			mSelected = false;
//		}else{
//			mSelected = true;
//		}
//		invalidate();
//		return super.onTouchEvent(event);
//	}
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("dayOfWeek", "onDraw");
		if(canvasname.equals("pleasenewdiary")){
//			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),com.pslab.snsdiary.R.drawable.pleasenewdiary), null, new Rect(0,0,224,320), new Paint());
//			this.setBackgroundResource(R.drawable.pleasenewdiary);
		}else{
//			Drawable d = new BitmapDrawable(getResources(), loadThumbnail(canvasname));
//			this.setBackground(d);
			canvas.drawBitmap(loadThumbnail(canvasname), null, new Rect(0,0,224,320), new Paint());
			canvas.drawRect(new Rect(0, 0, 224, 60), bgTitlePaint);
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), subject), null, new Rect(5, 5, 50,50), new Paint());
			canvas.drawText(title, 55, 45, textTitlePaint);
		}
		 if(mSelected){
			System.out.println("adifjai;efjwo;efjaiowe;fawf");
	        	canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),com.pslab.snsdiary.R.drawable.img_seleted_week), null, new Rect(0,0,224,320), new Paint());
//	            canvas.drawPaint(bgSelectedDayPaint);
	        }
		canvas.drawText(date, 8, 305, textDatePaint);
		
	}
	
	public boolean ismSelected() {
		return mSelected;
	}
	public void setmSelected(boolean mSelected) {
		this.mSelected = mSelected;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public Boolean getIsEmpty() {
		return isEmpty;
	}
	public void setIsEmpty(Boolean isEmpty) {
		this.isEmpty = isEmpty;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCanvasname() {
		return canvasname;
	}
	public void setCanvasname(String canvasname) {
		this.canvasname = canvasname;
	}
	public int getSubject() {
		return subject;
	}
	public void setSubject(int subject) {
		this.subject = subject;
	}
	public Bitmap loadThumbnail(String filename){
    	String imgPath = context.getFilesDir().getAbsolutePath()+"/"+filename;
		return BitmapFactory.decodeFile(imgPath);
    	
    }
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public void setTextDateColor(int Color) {
		this.textDatePaint.setColor(Color);
	}
	
}
