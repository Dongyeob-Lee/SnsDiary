package com.pslab.snsdiary.weekly;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.TableRow;

public class WeekIntro extends View{
	private String yearMonth;
	private String week;
	private Paint pt_ym;
	private Paint pt_w;
	private Paint pt_week;
	public WeekIntro(Context context) {
		super(context);
		init();
	}
	protected void init(){
		pt_ym = new Paint();
		pt_w = new Paint();
		pt_week = new Paint();
		pt_ym.setAntiAlias(true);
		pt_w.setAntiAlias(true);
		pt_week.setAntiAlias(true);
		pt_ym.setColor(Color.GRAY);
		pt_w.setColor(Color.GRAY);
		pt_week.setColor(Color.GRAY);
		pt_ym.setTextSize(45);
		pt_w.setTextSize(90);
		pt_week.setTextSize(40);
		this.setLayoutParams(new TableRow.LayoutParams(224, 320));
	}
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("weekIntro", "onDraw");
		canvas.drawText(yearMonth, 40, 50, pt_ym);
		canvas.drawText(week, 85, 200, pt_w);
		canvas.drawText("Week", 60, 300, pt_week);
	}

	public String getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}
	
}
