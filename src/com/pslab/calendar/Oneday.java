package com.pslab.calendar;

import java.util.Calendar;

import com.pslab.snsdiary.spentools.SubjectSelect;

import android.R;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;


	// 원본 : http://funpython.com/blog/59?category=2
	// 수정 : 한수댁
 
public class Oneday extends View {
 
    private int year;
    private int month;
    private int day;
    private int dayOfWeek;
     
    private String textDay;
    private String textActCnt;
 
    private Paint bgDayPaint;
    private Paint bgSelectedDayPaint;
    private Paint bgActcntPaint;
    private Paint bgTodayPaint;
    private Paint textDayPaint;
    private Paint textActcntPaint;
 
    private int textDayTopPadding;
    private int textDayLeftPadding;
    private int textActcntTopPadding;
    private int textActcntLeftPadding;
    private int subject =-1;
    private Paint mPaint;
 
    private boolean mSelected;
    public boolean isToday = false;
    private boolean isEmpty=true;
 
    public Oneday(Context context , android.util.AttributeSet attrs) {
        super(context, attrs);
        init();
    }
 
    public Oneday(Context context) {
        super(context);
        init();
 
    }
 
    private void init()
    {
        bgDayPaint = new Paint();
        bgSelectedDayPaint = new Paint();
        bgActcntPaint = new Paint();
        textDayPaint = new Paint();
        textActcntPaint = new Paint();
        bgTodayPaint = new Paint();
//        bgDayPaint.setColor(Color.WHITE);
        bgActcntPaint.setColor(Color.YELLOW);
        textDayPaint.setColor(Color.WHITE);
        textDayPaint.setAntiAlias(true);
        textActcntPaint.setColor(Color.WHITE);
        textActcntPaint.setAntiAlias(true);
        bgTodayPaint.setColor(Color.GREEN);
        rect = new RectF();
 
        setTextDayTopPadding(0);
        setTextDayLeftPadding(0);
 
        setTextActcntTopPadding(0);
        setTextActcntLeftPadding(0);
 
        mPaint = new Paint();
 
        mSelected = false;
    }
 
    RectF rect;
    @Override
    protected void onDraw(Canvas canvas) {
        if(mSelected){
        	canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),com.pslab.snsdiary.R.drawable.img_seleted_day), null, new Rect(0,0,93,120), new Paint());
//            canvas.drawPaint(bgSelectedDayPaint);
        }
        if(isToday){
        	canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),com.pslab.snsdiary.R.drawable.img_today), null, new Rect(this.getWidth()/2,3,this.getWidth()-3,37), new Paint());
        }
        SubjectSelect subjectSelect = new SubjectSelect();
        int width = this.getWidth();
        int height = this.getHeight();
 
        int textDaysize = (int)textDayPaint.measureText(getTextDay()) / 2;
        int textActsize = (int)textActcntPaint.measureText(getTextActCnt()) / 2;
        int dis = day/10;
        if(day==0){
        	width = width/2-20;
        }else if(dis>=1){
        	width = width/2+7;
        }else{
        	width = width/2+20;
        }
        canvas.drawText(getTextDay(), width,30, textDayPaint);
        if(!isEmpty){
        	canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),com.pslab.snsdiary.R.drawable.icon_content), null, new Rect(7,6,30,32), new Paint());
        }
        
        if(subject!=-1){
        	canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), subjectSelect.getDrawable(subject)), null, new Rect(10, 40,this.getWidth()-15 ,110), new Paint());
        }
        
        
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GRAY);
        canvas.drawLine(0, this.getHeight()-1, this.getWidth()-1, this.getHeight()-1, mPaint);
        canvas.drawLine(this.getWidth()-1, 0, this.getWidth()-1, this.getHeight()-1, mPaint);
    }
    
    public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public int getSubject() {
		return subject;
	}

	public void setSubject(int subject) {
		this.subject = subject;
	}

	public int getTextDayTopPadding() {
        return this.textDayTopPadding;
    }
    public int getTextDayLeftPadding() {
        return this.textDayLeftPadding;
    }
    public void setTextDayTopPadding(int top){
        this.textDayTopPadding = top;
    }
    public void setTextDayLeftPadding(int left){
        this.textDayLeftPadding = left;
    }
 
    public int getTextActcntTopPadding() {
        return this.textActcntTopPadding;
    }
    public int getTextActcntLeftPadding() {
        return this.textActcntLeftPadding;
    }
    public void setTextActcntTopPadding(int top){
        this.textActcntTopPadding = top;
    }
    public void setTextActcntLeftPadding(int left){
        this.textActcntLeftPadding = left;
    }
 
    public void setBgTodayPaint(int color){
        this.bgTodayPaint.setColor(color);
    }
    public void setBgDayPaint(int color){
        this.bgDayPaint.setColor(color);
    }
    public void setBgSelectedDayPaint(int color){
        this.bgSelectedDayPaint.setColor(color);
    }
    public void setBgActcntPaint(int color){
        this.bgActcntPaint.setColor(color);
    }
    public void setSelected(boolean selected){
        this.mSelected = selected;
    }
    public boolean getSelected() {
        return this.mSelected;
    }
 
    /**
     * 일자에 표시된 글 리턴
     * @return
     */
    public String getTextDay() {
        return this.textDay;
    }
    /**
     * 일자에 표시할 글 입력
     * @param string
     */
    public void setTextDay(String string) {
        this.textDay = string;
    }
 
    /**
     *  부가 설명에 표시된 글 리턴
     * @return
     */
    public String getTextActCnt(){
        return this.textActCnt;
    }
    /**
     * 부가 설명에 표시할 글 입력 
     * @param string
     */
    public void setTextActCnt(String string){
        this.textActCnt = string;
    }
    /**
     * 일자 글씨 색상
     * @param color
     */
    public void setTextDayColor(int color){
        this.textDayPaint.setColor(color);
    }
    /**
     * 일자 글씨 크기
     * @param size
     */
    public void setTextDaySize(int size){
        this.textDayPaint.setTextSize(size);
    }
 
    /**
     *  부가 설명 글자 색상
     * @param color
     */
    public void setTextActcntColor(int color){
        this.textActcntPaint.setColor(color);
    }
    /**
     * 부가 설명 글자 크기
     * @param size
     */
    public void setTextActcntSize(int size){
        this.textActcntPaint.setTextSize(size);
    }
     
    /**
     * 년도
     * @param _year
     */
    public void setYear(int _year){
        year = _year;
    }
    /**
     * @return 년도
     */
    public int getYear(){
        return year;
    }
 
    /**
     *  월
     *  
     * @param _month 0~11, Calendar.JANUARY ~ Calendar.DECEMBER
     */
    public void setMonth(int _month){
        month = Math.min(Calendar.DECEMBER, Math.max(Calendar.JANUARY, _month));
        month = _month;
    }
    /**
     * @return 월 0~11, Calendar.JANUARY ~ Calendar.DECEMBER
     */
    public int getMonth(){
        return month;
    }
     
    /**
     * 일 1~31
     */
    public void setDay(int _day){
        day = Math.min(31, Math.max(1, _day));
        day = _day;
    }
    /**
     * @return 일 1~31
     */
    public int getDay(){
        return day;
    }
    /**
     * 요일 1~7<br/>
     * Calendar.SUNDAY ~ Calendar.SATURDAY
     */
    public void setDayOfWeek(int _dayOfWeek){
        dayOfWeek = Math.min(Calendar.SATURDAY, Math.max(Calendar.SUNDAY, _dayOfWeek));
        dayOfWeek = _dayOfWeek;
    }
     
    /**
     * @return 요일 1~7, Calendar.SUNDAY ~ Calendar.SATURDAY
     */
    public int getDayOfWeek(){
        return dayOfWeek;
    }
     
    /**
     * 해당 요일을 한글로 리턴
     * @return "일", "월", "화", "수", "목", "금", "토"
     */
    public String getDayOfWeekKorean(){
        final String[]korean = {"오류", "일", "월", "화", "수", "목", "금", "토"};
        return korean[dayOfWeek];
    }
     
    /**
     * 해당 요일을 영어로 리턴
     * @return "Sun", "Mon", "Tues", "Wednes", "Thurs", "Fri", "Satur"
     */
    public String getDayOfWeekEnglish(){
        final String[]korean = {"E", "Sun", "Mon", "Tues", "Wednes", "Thurs", "Fri", "Satur"};
        return korean[dayOfWeek];
    }
     
    /**
     * 기본 정보 복사
     * @param srcDay
     */
    public void copyData(Oneday srcDay){
        setYear(srcDay.getYear());
        setMonth(srcDay.getMonth());
        setDay(srcDay.getDay());
        setDayOfWeek(srcDay.getDayOfWeek());
        setTextDay(srcDay.getTextDay());
        setTextActCnt(srcDay.getTextActCnt());
    }
     
}