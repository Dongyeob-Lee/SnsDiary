package com.pslab.snsdiary.spentools;

import android.graphics.drawable.Drawable;

import com.pslab.snsdiary.R;

public class SubjectSelect {
	public int[] idlist;

	public SubjectSelect() {
		super();
		idlist = new int[25];
		
		idlist[0] = R.drawable.e0;
		idlist[1] = R.drawable.e1;
		idlist[2] = R.drawable.e2;
		idlist[3] = R.drawable.e3;
		idlist[4] = R.drawable.e4;
		idlist[5] = R.drawable.e5;
		idlist[6] = R.drawable.e6;
		idlist[7] = R.drawable.e7;
		idlist[8] = R.drawable.e8;
		idlist[9] = R.drawable.e9;
		idlist[10] = R.drawable.e10;
		idlist[11] = R.drawable.e11;
		idlist[12] = R.drawable.e12;
		idlist[13] = R.drawable.e13;
		idlist[14] = R.drawable.e14;
		idlist[15] = R.drawable.e15;
		idlist[16] = R.drawable.e16;
		idlist[17] = R.drawable.e17;
		idlist[18] = R.drawable.e18;
		idlist[19] = R.drawable.e19;
		idlist[20] = R.drawable.e20;
		idlist[21] = R.drawable.e21;
		idlist[22] = R.drawable.e22;
		idlist[23] = R.drawable.e23;
		idlist[24] = R.drawable.e24;
	}
	public int getDrawable(int i){
		return idlist[i];
	}
}
