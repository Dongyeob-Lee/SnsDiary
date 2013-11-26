package com.pslab.snsdiary.note;

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

public class NoteControler {

	public static boolean checkNoteExist(String path, String notename) {

		File file = new File(path, notename);

		if (file.exists()) {
			return true;
		}
		return false;
	}

	public static String intDateToStringDate(int year, int month, int day) {
		String totalString = String.valueOf(year) + doubleString(month + 1)
				+ doubleString(day);
		return totalString + ".xml";
	}

	protected static String doubleString(int value) {
		String temp;

		if (value < 10) {
			temp = "0" + String.valueOf(value);

		} else {
			temp = String.valueOf(value);
		}
		return temp;
	}
	public static Bitmap inputBitmapToSObjectImage(Context context, String imagename) {
		String path = context.getFilesDir().getAbsolutePath() + "/image/";

		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inPreferredConfig = Config.RGB_565;
		option.inSampleSize = 8;
		Bitmap bitmap = BitmapFactory.decodeFile(path + imagename);
		return bitmap;
	}

	public static Note getXmlStringDate(String path, String year, String month,
			String day, Context mContext) {
		String notename = year + month + day + ".xml";
		File file = new File(path, notename);
		FileInputStream fis;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Note note = new Note(year, month, day);
		NoteDiary diary; 
		if (file.exists()) {
			try {
				fis = mContext.openFileInput(notename);
				DocumentBuilder builder = factory.newDocumentBuilder();

				Document dom = builder.parse(fis);
				Element root = dom.getDocumentElement();
				
				NodeList items = root.getElementsByTagName("data");
				for (int r = 0; r < items.getLength(); r++) {
					diary = new NoteDiary();
					Node item = items.item(r);
					NodeList datas = item.getChildNodes();
					ArrayList<String> f = new ArrayList<String>();
					for (int i = 0; i < datas.getLength(); i++) {
						Node data = datas.item(i);
						String nodename = data.getNodeName();
						if (nodename.equals("#text")) {
							continue;
						}
						if(nodename.equalsIgnoreCase("feed")){
							System.out.println("feed");
							f.add(data.getFirstChild().getNodeValue());
						}
						if (nodename.equalsIgnoreCase("title")) {
							diary.setTitle(data.getFirstChild().getNodeValue());
						} else if (nodename.equalsIgnoreCase("scanvasfilename")) {
							diary.setSCanvasFileName(data.getFirstChild().getNodeValue());
						} else if(nodename.equalsIgnoreCase("temperature")){
							diary.setTemperature(data.getFirstChild().getNodeValue());
						} else if (nodename.equalsIgnoreCase("weather")) {
							diary.setWeatherImg(data.getFirstChild().getNodeValue());
						}
						else if (nodename.equalsIgnoreCase("subject")) {
							diary.setSubject(Integer.parseInt(data.getFirstChild().getNodeValue()));
						}else if(nodename.equalsIgnoreCase("record")){
							
							diary.setRecordFileName(data.getFirstChild().getNodeValue());
						}else if(notename.equalsIgnoreCase("feed")){
							
						}
					}
					diary.setFeedlist(f);
					note.getNoteDiaries().add(diary);
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
		return note;
	}
	public static StringBuffer updateNoteXml(Note note, Context mContext){
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		stringBuffer.append("<item>\n");
		for (int r = 0; r < note.getNoteDiaries().size(); r++) {
			stringBuffer.append("\t<data>\n");
			if (note.getNoteDiaries().get(r).getTitle() == null) {
				stringBuffer.append("\t\t<title>제목을 입력해 주세요!</title>\n");
			} else {
				stringBuffer.append("\t\t<title>"
						+ note.getNoteDiaries().get(r).getTitle() + "</title>\n");
			}
			stringBuffer.append("\t\t<scanvasfilename>"+note.getNoteDiaries().get(r).getSCanvasFileName()+"</scanvasfilename>\n");
			stringBuffer.append("\t\t<temperature>"+note.getNoteDiaries().get(r).getTemperature()+"</temperature>\n");
			stringBuffer.append("\t\t<weather>"+note.getNoteDiaries().get(r).getWeatherImg()+"</weather>\n");
			stringBuffer.append("\t\t<subject>"+note.getNoteDiaries().get(r).getSubject()+"</subject>\n");
			stringBuffer.append("\t\t<record>"+note.getNoteDiaries().get(r).getRecordFileName()+"</record>\n");
			for(int i=0; i<note.getNoteDiaries().get(r).getFeedlist().size(); i++){
				stringBuffer.append("\t\t<feed>"+note.getNoteDiaries().get(r).getFeedlist().get(i)+"</feed>\n");
			}
			stringBuffer.append("\t</data>\n");
		}

		stringBuffer.append("</item>");

		return stringBuffer;
	}
	public static StringBuffer makeNoteXml(Note note,SCanvasView mSCanvas, Context mContext) {

		StringBuffer stringBuffer = new StringBuffer();
		String imgPath = mContext.getFilesDir().getAbsolutePath();
		String imgDir = "image";
		File pathFile = new File(imgPath, imgDir);
		if (!pathFile.exists()) {
			pathFile.mkdir();
		}
		imgPath = imgPath + "/image/";
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		stringBuffer.append("<item>\n");
		for (int r = 0; r < note.getNoteDiaries().size(); r++) {
			stringBuffer.append("\t<data>\n");
			if (note.getNoteDiaries().get(r).getTitle() == null) {
				stringBuffer.append("\t\t<title>제목을 입력해 주세요!</title>\n");
			} else {
				stringBuffer.append("\t\t<title>"
						+ note.getNoteDiaries().get(r).getTitle() + "</title>\n");
			}
			stringBuffer.append("\t\t<scanvasfilename>"+note.getNoteDiaries().get(r).getSCanvasFileName()+"</scanvasfilename>\n");
			stringBuffer.append("\t\t<temperature>"+note.getNoteDiaries().get(r).getTemperature()+"</temperature>\n");
			stringBuffer.append("\t\t<weather>"+note.getNoteDiaries().get(r).getWeatherImg()+"</weather>\n");
			stringBuffer.append("\t\t<subject>"+note.getNoteDiaries().get(r).getSubject()+"</subject>\n");
			stringBuffer.append("\t\t<record>"+note.getNoteDiaries().get(r).getRecordFileName()+"</record>\n");
			for(int i=0; i<note.getNoteDiaries().get(r).getFeedlist().size(); i++){
				stringBuffer.append("\t\t<feed>"+note.getNoteDiaries().get(r).getFeedlist().get(i)+"</feed>\n");
			}
			stringBuffer.append("\t</data>\n");
		}

		stringBuffer.append("</item>");

		return stringBuffer;
	}
	
}
