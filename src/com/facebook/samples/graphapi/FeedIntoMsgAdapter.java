package com.facebook.samples.graphapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import com.facebook.Session;
import com.pslab.snsdiary.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.pslab.jaehwan.api.SAPI;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedIntoMsgAdapter extends ArrayAdapter<String>{

	private Vector items;
	private Activity activity;
	private int RsrcId;
	private Session session;

	SAPI sapi = new SAPI();
	
	public FeedIntoMsgAdapter(Context context, int RsrcId, Vector items, Activity activity ){
		super(context, RsrcId, RsrcId, items);
		this.items = items;
		this.activity = activity;
		this.RsrcId = RsrcId;
		this.session = Session.getActiveSession();
	}
	
	public CData getItems(int position){
		return (CData)items.get(position);
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		
		if(v == null){
			LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(RsrcId, null);
		}
		return searchRow(position,v);
	}
	
	public View searchRow(int position, View v){
		final CData p = (CData)items.get(position);
		sapi.setFacebookAccessToken(session);
		
		if(p!=null){
			TextView text1 = (TextView)v.findViewById(R.id.feed_into_list_user_id);
			TextView text2 = (TextView)v.findViewById(R.id.feed_into_list_content);
			TextView text3 = (TextView)v.findViewById(R.id.feed_into_list_item_time);
			ImageView userimage = (ImageView)v.findViewById(R.id.feed_into_list_user_pic);
		//	ImageView feedimage = (ImageView)v.findViewById(R.id.feed_list_content_img);
			
			text1.setText(p.getS_name());
			text2.setText(p.getS_message());
			text3.setText(p.getS_dadte());
			if(p.getS_profile()!=null)
				userimage.setImageBitmap(p.getS_profile());
			
		}
		return v;
	}

	
	
	public Bitmap downloadBitmap(String urlname){
		Log.d("URl", "url "+ urlname);
		URL url = null;
		Bitmap Result = null;
		
		try {
			url = new URL(urlname);

			Result = BitmapFactory.decodeStream(url.openStream());
			Bitmap.createScaledBitmap(Result, 10, 10, false);
			Log.d("URL","herecome?" + Result);
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
