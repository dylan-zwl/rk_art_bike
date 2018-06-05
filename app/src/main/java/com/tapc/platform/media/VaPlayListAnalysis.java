package com.tapc.platform.media;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class VaPlayListAnalysis {
	/**
	 * ��XML�ļ��ж�ȡ����
	 * 
	 * @param xml
	 *            XML�ļ�������
	 */
	public PlayEntity getvaInfor(InputStream xml) throws Exception {
		PlayEntity mvaPlayList = new PlayEntity();
		mvaPlayList.evtList = new ArrayList<String>();
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(xml, "UTF-8");
		int event = pullParser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			String nodeName = pullParser.getName();
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if ("name".equals(nodeName)) {
					mvaPlayList.name = pullParser.nextText();
				} else if ("description".equals(nodeName)) {
					mvaPlayList.description = pullParser.nextText();
				} else if ("location".equals(nodeName)) {
					mvaPlayList.location = pullParser.nextText();
				} else if ("still".equals(nodeName)) {
					mvaPlayList.still = pullParser.nextText().replace("\\", "/");
				} else if ("uniqueid".equals(nodeName)) {
					mvaPlayList.uniqueid = pullParser.nextText();
				} else if ("video".equals(nodeName)) {
					mvaPlayList.evtList.add(pullParser.nextText().replace("\\", "/"));
				}
				break;
			case XmlPullParser.END_TAG:
				break;
			}
			event = pullParser.next();
		}
		return mvaPlayList;
	}
}
