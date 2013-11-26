package com.pslab.snsdiary.freenote;

import com.pslab.snsdiary.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableRow;

public class Freenote_memo extends View{
	private String body;
	private String date;
	private Paint body_paint;
	private Paint date_paint;
	private boolean isEmpty=true;
	private boolean isDelete;
	Context context;
	public Freenote_memo(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	protected void init(){
		body = "";
		date = "";
		body_paint = new Paint();
		date_paint = new Paint();
		body_paint.setAntiAlias(true);
		date_paint.setAntiAlias(true);
		body_paint.setTextSize(20);
		date_paint.setTextSize(15);
		body_paint.setColor(Color.BLACK);
		date_paint.setColor(Color.BLACK);
		isDelete=false;
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("onDraw", "onDraw");
		this.setBackgroundResource(R.drawable.background_memo);
		if(isDelete){
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_memo_delete), null, new Rect(150, 150, 300, 300), new Paint());
		}
		canvas.drawText(date, 0, 20, date_paint);
		canvas.drawText(body, 0, 40, body_paint);
	}
	

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
