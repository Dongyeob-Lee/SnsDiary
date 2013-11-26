package com.pslab.calendar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import com.pslab.snsdiary.note.NoteControler;
import com.pslab.snsdiary.note.NoteDiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

public class SubjectControler {
	private SharedPreferences prefs;
	public Handler handler;
	public static final String subjectXml = "subject.xml";
	Context mContext;
	
	
	public static boolean checkSubjectXmlExist(Context context){
		String path = context.getFilesDir().getAbsolutePath();
		File file = new File(path,subjectXml);
		if(file.exists()){
			//if subejctXml is exist just load
			Log.d("diary", "there is subject.xml");
			return true;
		}
		//if not,, must load subjectXml file from dropbox.
		//but if there is no file, must create subjectXml file.
		return false;
	}
	
	public static boolean makeSubjectXml(ArrayList<String> subjectlist, Context context){
		
		StringBuffer stringBuffer = new StringBuffer();
		File file = new File(context.getFilesDir().getAbsolutePath(), subjectXml);
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		stringBuffer.append("<item>\n");
		stringBuffer.append("\t<data>\n");
		for(int i=0; i<subjectlist.size(); i++){
			stringBuffer.append("\t\t<subject>"+subjectlist.get(i)+"</subject>\n");
			Log.d("input sub", subjectlist.get(i).toString());
		}
		stringBuffer.append("\t</data>\n");
		stringBuffer.append("</item>");
		try {
			file.createNewFile();
			// ////
			FileOutputStream fos = context.openFileOutput(subjectXml,
					Context.MODE_PRIVATE);
			fos.write(stringBuffer.toString().getBytes());

			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
		
	}
	
	public static ArrayList<String> getSubjectList(Context context){
		ArrayList<String> subjectList=new ArrayList<String>();
		String path = context.getFilesDir().getAbsolutePath();
		File file = new File(path,subjectXml);
		FileInputStream fis;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		if(file.exists()){
			try {
				fis = context.openFileInput(subjectXml);
				DocumentBuilder builder = factory.newDocumentBuilder();

				Document dom = builder.parse(fis);
				Element root = dom.getDocumentElement();
				NodeList items = root.getElementsByTagName("data");
				for (int r = 0; r < items.getLength(); r++) {
					Node item = items.item(r);
					NodeList datas = item.getChildNodes();
					for (int i = 0; i < datas.getLength(); i++) {
						Node data = datas.item(i);
						String nodename = data.getNodeName();
						if (nodename.equals("#text")) {
							continue;
						}
						if (nodename.equalsIgnoreCase("subject")){
							Log.d("sub", data.getFirstChild().getNodeValue());
							subjectList.add(data.getFirstChild().getNodeValue());
						}
					}
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
		}
		return subjectList;
	}
	public SubjectControler(Context context) {
		super();
		this.mContext = context;
	}
}
