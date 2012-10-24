package kr.co.vipsapp.util;

import java.net.URL;
import java.util.ArrayList;

import kr.co.vipsapp.util.vo.AODProgramVO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class XmlAODProgram {

	// final String AOD_PROGRAM =
	// "http://www.befm.or.kr/app/LinkageAction.do?cmd=aodPrgm";
	final String AOD_PROGRAM = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=aodPrgm";

	private ArrayList<AODProgramVO> prgmList = new ArrayList<AODProgramVO>();

	public XmlAODProgram() {

	}

	public void parsingAODProgram() {
		try {

			URL url = new URL(AOD_PROGRAM);
			XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
			XmlPullParser parser = xpp.newPullParser();
			parser.setInput(url.openStream(), "UTF-8");
			
			String tag = "", text = "", id = "", name = "";
			
			AODProgramVO vo = null;
			
			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG :
					tag = parser.getName();
					if (tag.equalsIgnoreCase("program")) {
						id = parser.getAttributeValue(0);
					}
					break;
				case XmlPullParser.TEXT	:
					text = parser.getText();
					if (tag.equalsIgnoreCase("program")) {
						name = text;
					}
					break;
				case XmlPullParser.END_TAG :
					if (tag.equalsIgnoreCase("program")) {
						vo = new AODProgramVO(id, name);
						prgmList.add(vo);
					}
					tag = ""; text = "";
					break;
				}
			}

//			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//			DocumentBuilder db = dbf.newDocumentBuilder();
//			Document doc = db.parse(new InputSource(url.openStream()));

//			NodeList nodeList = doc.getElementsByTagName("program");
//			Log.i("INFO", "Length: " + nodeList.getLength());
//
//			AODProgramVO vo = null;
//			for (int j = 0; j < nodeList.getLength(); j++) {
//
//				Node pnode = nodeList.item(j);
//				Element pElement = (Element) pnode;
//				String id = pElement.getAttribute("id");
//
//				NodeList sList = pElement.getChildNodes();
//				String name = ((Node) sList.item(0)).getNodeValue();
//
//				Log.i("INFO", "id: " + id + " , name:" + name);
//
//				vo = new AODProgramVO(id, name);
//				prgmList.add(vo);
//			}

		} catch (Exception e) {

		}

	}

	public ArrayList<AODProgramVO> getPrgmList() {
		return prgmList;
	}

	public void setPrgmList(ArrayList<AODProgramVO> prgmList) {
		this.prgmList = prgmList;
	}

}
