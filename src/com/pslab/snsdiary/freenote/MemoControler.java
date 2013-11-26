package com.pslab.snsdiary.freenote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.samsung.spensdk.SCanvasView;
import com.sileria.util.Log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

public class MemoControler {

	public static boolean checkNoteExist(String path, String notename) {

		File file = new File(path, notename);

		if (file.exists()) {
			return true;
		}
		return false;
	}

	public static ArrayList<Freenote_memo> getXmlStringDate(Context mContext) {
		String notename = "memos.xml";
		String path = mContext.getFilesDir().getAbsolutePath();
		File file = new File(path, notename);
		FileInputStream fis;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		ArrayList<Freenote_memo> memos = new ArrayList<Freenote_memo>();
		if (file.exists()) {
			try {
				fis = mContext.openFileInput(notename);
				DocumentBuilder builder = factory.newDocumentBuilder();

				Document dom = builder.parse(fis);
				Element root = dom.getDocumentElement();
				NodeList items = root.getElementsByTagName("data");
				for (int r = 0; r < items.getLength(); r++) {
					Freenote_memo memo = new Freenote_memo(mContext);
					Node item = items.item(r);
					NodeList datas = item.getChildNodes();
					for (int i = 0; i < datas.getLength(); i++) {
						Node data = datas.item(i);
						String nodename = data.getNodeName();
						if (nodename.equals("#text")) {
							continue;
						}
						if (nodename.equalsIgnoreCase("body")) {
							memo.setBody(data.getFirstChild().getNodeValue());
						} else if (nodename.equalsIgnoreCase("date")) {
							memo.setDate(data.getFirstChild().getNodeValue());
						} 
					}
					memos.add(memo);
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
		}
		return memos;
	}
	public static StringBuffer makeNoteXml(ArrayList<Freenote_memo> memos, Context mContext) {

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		stringBuffer.append("<item>\n");
		for (int r = 0; r < memos.size(); r++) {
			stringBuffer.append("\t<data>\n");
			stringBuffer.append("\t\t<body>"+memos.get(r).getBody()+"</body>\n");
			stringBuffer.append("\t\t<date>"+memos.get(r).getDate()+"</date>\n");
			stringBuffer.append("\t</data>\n");
		}

		stringBuffer.append("</item>");

		return stringBuffer;
	}
	
}
