package com.pslab.calendar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.pslab.snsdiary.R;
import com.pslab.snsdiary.note.Note;
import com.pslab.snsdiary.note.NoteActivity;
import com.pslab.snsdiary.note.NoteControler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
 
// ���� : http://funpython.com/blog/59?category=2
// ���� : �Ѽ���
public class CTCalendarView extends Activity implements OnClickListener {
     
    private Calendar rightNow;
    private GregorianCalendar gCal;
    protected int iYear = 0;
    protected int iMonth = 0;
 
    private int startDayOfweek = 0;
    private int maxDay = 0;
    private int oneday_width =0;
    private int oneday_height =0;
 
    ArrayList<String> daylist; //���� ����� ������ �ִ´�. 1,2,3,4,.... 28?30?31? 
    ArrayList<String> actlist; //���ڿ� �ش��ϴ� Ȱ�������� ������ �ִ´�.
 
    TextView title;
    private int dayCnt;
    private int mSelect = -1;
    private String dirpath;
    private LinearLayout linearLayout;
    TextView tv_cal_year;
    TextView tv_cal_month;
    protected void initialize(){
        setContentView(R.layout.calendarview);
        dirpath = getFilesDir().getAbsolutePath();
        rightNow = Calendar.getInstance();
        gCal = new GregorianCalendar();
        iYear = rightNow.get(Calendar.YEAR);
        iMonth = rightNow.get(Calendar.MONTH);
      
        ///////////////////////////////////////////////////////////////////////////////
        
        tv_cal_year = (TextView)findViewById(R.id.tv_cal_year);
        tv_cal_month = (TextView)findViewById(R.id.tv_cal_month);
        
        tv_cal_year.setText(String.valueOf(iYear));
        tv_cal_month.setText(String.valueOf(iMonth+1));
        makeCalendardata(iYear, iMonth);
    }
 
    protected void initialize(int year, int month){
        setContentView(R.layout.calendarview);
        dirpath = getFilesDir().getAbsolutePath();
        rightNow = Calendar.getInstance();
        gCal = new GregorianCalendar();
        iYear = year;
        iMonth = month;
      
        ///////////////////////////////////////////////////////////////////////////////
        
        tv_cal_year = (TextView)findViewById(R.id.tv_cal_year);
        tv_cal_month = (TextView)findViewById(R.id.tv_cal_month);
        
        tv_cal_year.setText(String.valueOf(iYear));
        tv_cal_month.setText(String.valueOf(iMonth+1));
        makeCalendardata(iYear, iMonth);
    }
  //�޷��� ���ڸ� ǥ���Ѵ�. 
    private void printDate(String thisYear, String thisMonth)
    {
 
        if(thisMonth.length() == 1) {
        }
        else{
        }
    }
 
  //�޷¿� ǥ���� ���ڸ� �迭�� �־� �����Ѵ�. 
    private void makeCalendardata(int thisYear, int thisMonth)
    {
        printDate(String.valueOf(thisYear),String.valueOf(thisMonth+1));
         
        rightNow.set(thisYear, thisMonth, 1);
        gCal.set(thisYear, thisMonth, 1);
        startDayOfweek = rightNow.get(Calendar.DAY_OF_WEEK);
 
        
        maxDay = gCal.getActualMaximum ((Calendar.DAY_OF_MONTH));
        if(daylist==null)daylist = new ArrayList<String>();
        daylist.clear();
 
        if(actlist==null)actlist = new ArrayList<String>();
        actlist.clear();
 
        daylist.add("SUN");actlist.add("");
        daylist.add("MON");actlist.add("");
        daylist.add("TUE");actlist.add("");
        daylist.add("WED");actlist.add("");
        daylist.add("THU");actlist.add("");
        daylist.add("FRI");actlist.add("");
        daylist.add("SAT");actlist.add("");
 
        if(startDayOfweek != 1) {
            gCal.set(thisYear, thisMonth-1, 1);
            int prevMonthMaximumDay = (gCal.getActualMaximum((Calendar.DAY_OF_MONTH))+2);
            for(int i=startDayOfweek;i>1;i--){
                daylist.add(Integer.toString(prevMonthMaximumDay-i));
                actlist.add("p");
            }
        }
 
        for(int i=1;i<=maxDay;i++) //���ڸ� �ִ´�.
        {
            daylist.add(Integer.toString(i));
            actlist.add("");
        }
 
 
        int dayDummy = (startDayOfweek-1)+maxDay;
        if(dayDummy >35)
        {
            dayDummy = 42 - dayDummy;
        }else{
            dayDummy = 35 - dayDummy;
        }
         
      //������..�׷��ϱ� ��ĭ�� �־� �޷� ����� �̻ڰ� ����� �ش�.
        if(dayDummy != 0)
        {
            for(int i=1;i<=dayDummy;i++) 
            {
                daylist.add(Integer.toString(i));
                actlist.add("n");
            }
        }
 
        makeCalendar();
       
    }
 
 
    
    @SuppressWarnings("deprecation")
	private void makeCalendar()
    {
        final Oneday[] oneday = new Oneday[daylist.size()];
        final Calendar today = Calendar.getInstance();
        TableLayout tablelayout =(TableLayout)findViewById(R.id.tl_calendar_monthly);
        tablelayout.removeAllViews();
 
        Log.d("diary", "CTCalendarView : makeCalendar()");
        dayCnt = 0;
        int maxRow = ((daylist.size() > 42)? 7:6);
        int maxColumn = 7;
 
 
        oneday_width = getWindow().getWindowManager().getDefaultDisplay().getWidth()-105;
        oneday_height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        
        oneday_height =120; 
        		//((((oneday_height >= oneday_width)?oneday_height:oneday_width) - tl.getTop()) / (maxRow+1))-10;
        oneday_width = 651/7;
 
 
 
        int daylistsize =daylist.size()-1;
        for(int i=1;i<=maxRow;i++ )
        {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            for(int j=1;j<=maxColumn;j++)
            {
                //calender_oneday�� ������ ������ �ִ´�. 
                oneday[dayCnt] = new Oneday(getApplicationContext());
 
                //���Ϻ� ���� ���ϱ� 
                if((dayCnt % 7) == 0){
                    oneday[dayCnt].setTextDayColor(Color.parseColor("#D4145A"));
                } else if((dayCnt % 7) == 6){
                    oneday[dayCnt].setTextDayColor(Color.parseColor("#3C96EF"));
                } else {
                    oneday[dayCnt].setTextDayColor(Color.parseColor("#333333"));
                }
                 
                //���� ǥ���� ����
                if(dayCnt >= 0 && dayCnt < 7)
                {
                    oneday[dayCnt].setTextDayTopPadding(8); //����ǥ�� �Ҷ� top padding
                    if(dayCnt==0){
                    	oneday[dayCnt].setTextDayColor(Color.parseColor("#D4145A")); //������ �۾� ���� 
                    }
                    else if(dayCnt==6){
                    	oneday[dayCnt].setTextDayColor(Color.parseColor("#3C96EF")); //������ �۾� ���� 
                    }
                    else{
                    	oneday[dayCnt].setTextDayColor(Color.parseColor("#333333")); //������ �۾� ���� 
                    }
                    oneday[dayCnt].setTextDaySize(25); //������ �۾�ũ�� 
                    oneday[dayCnt].setLayoutParams(new LayoutParams(oneday_width,35)); //���� ��Ʈ�� ũ�� 
                    oneday[dayCnt].isToday = false;
                     
                }else{
                     
                    oneday[dayCnt].isToday = false;
                    oneday[dayCnt].setDayOfWeek(dayCnt%7 + 1);
                    oneday[dayCnt].setDay(Integer.valueOf(daylist.get(dayCnt)).intValue());
                    oneday[dayCnt].setTextActcntSize(14);
                    oneday[dayCnt].setTextActcntColor(Color.BLACK);
                    oneday[dayCnt].setTextActcntTopPadding(18);
                    oneday[dayCnt].setBgSelectedDayPaint(Color.rgb(0, 162, 232));
                    oneday[dayCnt].setBgTodayPaint(Color.LTGRAY);
                    oneday[dayCnt].setBgActcntPaint(Color.rgb(251, 247, 176));
                    oneday[dayCnt].setLayoutParams(new LayoutParams(oneday_width,oneday_height));
                     
                    //���� �� �� ǥ��
                    if(actlist.get(dayCnt).equals("p")){
                         oneday[dayCnt].setTextDaySize(20);
                         actlist.set(dayCnt, "");
                         oneday[dayCnt].setTextDayTopPadding(-4);
                          
                         if(iMonth - 1 < Calendar.JANUARY){
                             oneday[dayCnt].setMonth(Calendar.DECEMBER);
                             oneday[dayCnt].setYear(iYear - 1);
                         }  else {
                             oneday[dayCnt].setMonth(iMonth - 1);
                             oneday[dayCnt].setYear(iYear);
                         }
                     
                    // ���� �� �� ǥ��
                    } else if(actlist.get(dayCnt).equals("n")){
                        oneday[dayCnt].setTextDaySize(20);
                        actlist.set(dayCnt, "");
                        oneday[dayCnt].setTextDayTopPadding(-4);
                        if(iMonth + 1 > Calendar.DECEMBER){
                            oneday[dayCnt].setMonth(Calendar.JANUARY);
                            oneday[dayCnt].setYear(iYear + 1);
                        }  else {
                            oneday[dayCnt].setMonth(iMonth + 1);
                            oneday[dayCnt].setYear(iYear);
                        }
                    // ���� �� ��� ǥ��
                    }else{
                        oneday[dayCnt].setTextDaySize(27);
                        oneday[dayCnt].setYear(iYear);
                        oneday[dayCnt].setMonth(iMonth);
                         
                        //���� ǥ��
                        if(oneday[dayCnt].getDay() == today.get(Calendar.DAY_OF_MONTH)
                                && oneday[dayCnt].getMonth() == today.get(Calendar.MONTH)
                                && oneday[dayCnt].getYear() == today.get(Calendar.YEAR)){
                             
                            oneday[dayCnt].isToday = true;
                            oneday[dayCnt].invalidate();
                            mSelect = dayCnt;
                        }
                        /////////���⼭ ��¥�� �ش��ϴ� ������ �ִٸ� �� ������ ������ �޷¿� ǥ��!!
                        //������ �ִ��� üũ �ϴ¹�.
                        if(NoteControler.checkNoteExist(dirpath, intDateToNotename(iYear, iMonth,dayCntToRealDay(dayCnt)))){
                        	
                        	Note note = NoteControler.getXmlStringDate(dirpath, String.valueOf(iYear), doubleString(iMonth+1), doubleString(dayCntToRealDay(dayCnt)),CTCalendarView.this);
                        	
                        	for(int k=0; k<note.getNoteDiaries().size();k++){
                        		oneday[dayCnt].setEmpty(false);
                        		oneday[dayCnt].setSubject(note.getNoteDiaries().get(0).getSubject());
                        	}
                        }
                    }
                     
 
                    oneday[dayCnt].setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            //Toast.makeText(context, iYear+"-"+iMonth+"-"+oneday[v.getId()].getTextDay(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    });
 
                    oneday[dayCnt].setOnTouchListener(new OnTouchListener() {
 
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                             
                            if(oneday[v.getId()].getTextDay() != "" && event.getAction() == MotionEvent.ACTION_UP)
                            {
                                if(mSelect != -1){
                                    oneday[mSelect].setSelected(false);
                                    oneday[mSelect].invalidate();
                                }
                                oneday[v.getId()].setSelected(true);
                                oneday[v.getId()].invalidate();
                                mSelect = v.getId();
                                 
                                //Log.d("hahaha", oneday[mSelect].getMonth()+"-"+ oneday[mSelect].getDay());
                                 
                                onTouched(oneday[mSelect]);
                            }
                            return false;
                        }                        
                    });
                }
                 
                 
                oneday[dayCnt].setTextDay(daylist.get(dayCnt).toString()); //����,���� �ֱ� 
                oneday[dayCnt].setTextActCnt(actlist.get(dayCnt).toString());//Ȱ������ �ֱ� 
                oneday[dayCnt].setId(dayCnt); //������ ��ü�� �����Ҽ� �ִ� id�ֱ� 
                oneday[dayCnt].invalidate();
                tr.addView(oneday[dayCnt]);
 
                if(daylistsize != dayCnt)
                {
                    dayCnt++;
                }else{
                    break;
                }
            }
            tablelayout.addView(tr,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
 
        }
    }
     
  
    /**
     * ���ڸ� 2�ڸ� ���ڷ� ��ȯ, 2 -> 02
     * @param value
     * @return
     */
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
 
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
      
//        case R.id.btn_december:
//        	iMonth = 11;
//        	makeCalendardata(iYear, iMonth);
//        	break;
//        
        }
    }
     
    /**
     * ���� Ŭ�������� �������̵� �ؼ� ��ġ�� ��¥ �Է� �ޱ�
     * @param oneday
     */
    protected void onTouched(Oneday oneday){
         
    }
     
    /**
     * �ش� ���� ������ ���� �ȿ� �ִ��� �˻�
     * @param test �˻��� ��¥
     * @param basis ���� ��¥
     * @param during �Ⱓ(��)
     * @return
     */
    protected boolean isInside(Oneday test, Oneday basis, int during){
        Calendar calbasis = Calendar.getInstance();
        calbasis.set(basis.getYear(), basis.getMonth(), basis.getDay());
        calbasis.add(Calendar.DAY_OF_MONTH, during);
         
        Calendar caltest = Calendar.getInstance();
        caltest.set(test.getYear(), test.getMonth(), test.getDay());
         
        if(caltest.getTimeInMillis() < calbasis.getTimeInMillis()){
            return true;
        }
        return false;
    }
     
    /**
     *���� �޷����� �̵� 
     */
    public void gotoToday(){
        final Calendar today = Calendar.getInstance();
        iYear = today.get(Calendar.YEAR);
        iMonth = today.get(Calendar.MONTH);
        initialize();
        makeCalendardata(today.get(Calendar.YEAR),today.get(Calendar.MONTH));
    }
    
    public String intDateToNotename(int year, int month, int day){
		String totalString  = String.valueOf(year)+doubleString(month+1)+doubleString(day)+".xml";
		return totalString;
	}
    protected int dayCntToRealDay(int daycnt){
    	return Integer.valueOf(daylist.get(daycnt)).intValue();
    }
}