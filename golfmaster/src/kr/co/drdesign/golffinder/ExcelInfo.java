package kr.co.drdesign.golffinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class ExcelInfo  {
	public static final String LATITUDE		= SearchGCInfo.LATITUDE; 
	public static final String LONGITUDE 	= SearchGCInfo.LONGITUDE;
	public static final String PREVIEW		= SearchGCInfo.PREVIEW;

	public static final String NO 					= "no";
	public static final String CLASSIFICATION 		= "����";
	public static final String FEE_MEMBER_WEEKDAY 	= "ȸ��-����";
	public static final String FEE_MEMBER_WEEKEND 	= "ȸ��-�ָ�";
	public static final String FEE_WEMEMBER_WEEKDAY = "����ȸ��-����";
	public static final String FEE_WEMEMBER_WEEKEND = "����ȸ��-�ָ�";
	public static final String FEE_NORMAL_WEEKDAY 	= "��ȸ��-����";
	public static final String FEE_NORMAL_WEEKEND 	= "��ȸ��-�ָ�";
	public static final String ADDRESS 				= "������ �ּ�";
	public static final String PARENT_ADDRESS 		= "����";
	public static final String RESTDAY 				= "������";
	public static final String BOOKING_TEAM 		= "��ü�� �̿�";
	public static final String HOMEPAGE 			= "Ȩ������ �ּ�";
	public static final String BOOKING_WEKKDAY		= "���� ����";//����
	public static final String BOOKING_WEKKEND		= "�ָ� ����";//����
	public static final String FAX					= "������ �ѽ���ȣ";
	public static final String FEE_CADDY			= "ĳ����";
	public static final String FEE_CART				= "īƮ��";
	public static final String DAY_OF_MEMBER		= "ȸ���� ��";
	public static final String NAME_OF_GOLFCLUB		= "�������";
	public static final String NUMBER				= "������ ��ȭ��ȣ";
	public static final String ETC					= "��Ÿ";
	public static final String HOLE_NUMBER			= "������ Ȧ �Ը�";

	public Context context;
	public static ArrayList<Map<String,String>> al;
	private static Map<String,String[]> colfCoordinateMap;

	public ExcelInfo(Context context)
	{
		this.context = context.getApplicationContext();
		if( colfCoordinateMap == null ) colfCoordinateMap = getCoordinateList();
		al = getList();
	}

	public String[] getCoordinate( String name )
	{
		return colfCoordinateMap.get(name);
	}
	
	public ArrayList<Map<String,String>> getList()
	{
		if( al != null && al.size() > 5 ) return al;

		XmlPullParser xpp = context.getResources().getXml(R.xml.golf); 

		al = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String,String>();
		String type = "";
		int eventType;
		try {
			eventType = xpp.getEventType();
			StringBuilder sb = new StringBuilder();
			while (eventType != XmlPullParser.END_DOCUMENT) 
			{ 

				switch( eventType ){
				case XmlPullParser.START_DOCUMENT :
				case XmlPullParser.END_DOCUMENT :
					break;
				case XmlPullParser.START_TAG :
					type = xpp.getName().replace('_', ' ');
					break;
				case XmlPullParser.TEXT :
					map.put( type, xpp.getText());
					break;
				case XmlPullParser.END_TAG :
					type = xpp.getName();
					if(type.equals("golfinfo"))
					{
						al.add(map);
						map = new HashMap<String,String>();
					}
					break;
				default :
					break;
				}
				eventType = xpp.next(); 
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return al;
	}

	public ArrayList<Map<String,String>> getGCList(String address, String query, int radius, int page)
	throws IOException, URISyntaxException
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("near", address ));
		//params.add(new BasicNameValuePair("near", "�λ걤���� �ʷ���"));
		params.add(new BasicNameValuePair("q", query));
		params.add(new BasicNameValuePair("radius", 0.621371192 * radius + "")); //���Ϸ� ǥ�õ� �ݰ�
		// 1km = 0.621371192 mile
		params.add(new BasicNameValuePair("output", "kml")); //������� - xml
		params.add(new BasicNameValuePair("mrt", "yp")); //�˻�����. ��ü
		params.add(new BasicNameValuePair("start", 0 + "")); //�˻������ ó���� ������ ����������
		params.add(new BasicNameValuePair("num", "2")); //�˻������ ó���� ������ ����������

		URI uri = URIUtils.createURI("http", "maps.google.co.kr", -1, "", 
				URLEncodedUtils.format(params, "UTF-8"), null);

		Log.i("GF", "URL = " + uri.toURL());

		InputStream is = uri.toURL().openStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String str ;
		StringBuilder sb = new StringBuilder();
		while( (str = br.readLine()) != null )
		{
			str.replace("&nbsp", " ");
			sb.append( str );
			sb.append("\n");
		}
		return parsingMsgList( new StringReader(sb.toString()) );
	}

	public Address address2Location(String locationName) throws IOException
	{
		try{
			Geocoder goecoder = new Geocoder(context, Locale.getDefault());
			List<Address> addList = goecoder.getFromLocationName(locationName, 1);
			if( addList != null && addList.size() > 0 )
			{
				return addList.get(0);
			}
		}catch( IndexOutOfBoundsException e){
			Log.e("GF", e.getMessage());
		}
		return null;
	}

	protected ArrayList<Map<String, String>> parsingMsgList( Reader reader ){
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> item = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true); 
			XmlPullParser xpp = factory.newPullParser(); 

			xpp.setInput(reader); 

			String tag=null, text = null;
			boolean placemark = false;
			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				switch( eventType ){
				case XmlPullParser.START_DOCUMENT :
				case XmlPullParser.END_DOCUMENT :
					break;
				case XmlPullParser.START_TAG :
					tag = xpp.getName();
					if( tag.equalsIgnoreCase("Placemark"))
					{
						item = new HashMap<String,String>();
						placemark = true;
					}
					if( placemark && tag.equalsIgnoreCase("text"))
					{
						int cnt = xpp.getAttributeCount();
						for( int i = 0; i < cnt ; i++ )
						{
							//Log.i("DR", "AttributeName : " + xpp.getAttributeName(i));
							//Log.i("DR", "AttributeValue : " + xpp.getAttributeValue(i));
							//Log.i("DR", "AttributeType : " + xpp.getAttributeType(i));
						}
					}
					break;
				case XmlPullParser.TEXT :
					//Log.i("DR", "TEXT = " + xpp.getText());
					text = xpp.getText();
					break;
				case XmlPullParser.END_TAG :
					//Log.i("DR", "END_TAG = " + xpp.getName());
					tag = xpp.getName();
					if( tag.equalsIgnoreCase("Placemark"))
					{
						placemark = false;
						list.add(item);
					}else if( placemark && tag.equalsIgnoreCase("name"))
					{
						item.put(SearchGCInfo.NAME, text);
					}else if( placemark && tag.equalsIgnoreCase("Address"))
					{
						item.put(SearchGCInfo.ADDRESS, text);
					}else if( placemark && tag.equalsIgnoreCase("coordinates"))
					{
						String[] coordinates = text.split(",");
						item.put(SearchGCInfo.LONGITUDE, coordinates[0]);
						item.put(SearchGCInfo.LATITUDE, coordinates[1]);
					}else if( placemark && tag.equalsIgnoreCase("text"))
					{
						String[] datas = parseCDATA(text);
						item.put(SearchGCInfo.DETAIL_VIEW, 	datas[0]);
						if( datas[1] != null )
							item.put(SearchGCInfo.HOMEPAGE, 	datas[1]);
						item.put(SearchGCInfo.TELEPHONE, 	datas[2]);
						if( datas[3] != null )
							item.put(SearchGCInfo.PREVIEW, 	datas[3]);
					}
					break;
				default :
					break;

				}
				eventType = xpp.next(); 
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		//			for( Map<String,String> map : list)
		//			{
		//				Iterator<String> it = map.keySet().iterator();
		//				while(it.hasNext())
		//				{
		//					String s  = it.next();
		//					
		//					Log.i("DR", s +" = " + map.get(s));
		//				}
		//			}
		return list;
	}

	private String[] parseCDATA(String text)
	{
		StringTokenizer st = new StringTokenizer(text, ">");
		String token;
		String[] data = new String[4];
		int i = 0;
		while(st.hasMoreTokens())
		{
			token = st.nextToken();

			if(token.length() > 6){
				if( token.contains("<a href"))
				{
					//�ڼ�������
					data[0] = "maps.google.co.kr/m" + token.substring(15, token.length()-1);
				}else if( token.contains("</a") && token.contains("$[name]") == false ) 
				{
					//Ȩ������
					data[1] = token.substring(0, token.length() - 3);
				}else if( token.contains("<br/"))
				{
					//��ȭ��ȣ
					data[2] = token.substring(0, token.length() - 4);
				}else if( token.contains("img"))
				{
					//�̸����� �̹���
					data[3] = token.substring(10, token.length() - 2);;
				}
			}
		}
		return data;
	}

	public ArrayList<Map<String,String>> getGCListByCondition( String condition )
	{
		ArrayList<Map<String,String>> conditionList = new ArrayList<Map<String,String>> ();
		for( Map<String,String> item : al )
		{
			if( item.get(NAME_OF_GOLFCLUB).toUpperCase().contains(condition.toUpperCase()) )
			{
				conditionList.add(item);
			}
		}
		return conditionList;
	}

	public Map<String,String> getItemByNO( String no )
	{
		String number;
		for( Map<String, String>item : al )
		{
			number = item.get(NO);
			if( number != null && number.equals(no))
			{
				return item;
			}
		}
		Log.i("GF", "No = " + no + " null");
		return null;
	}

	public ArrayList<Map<String,String>> getGCListByLocationCategory( String locationCategory )
	{
		if( locationCategory == null || locationCategory.length() == 0 ) 
			return al;  
		ArrayList<Map<String,String>> conditionList = new ArrayList<Map<String,String>> ();
		for( Map<String,String> item : al )
		{
			if( item.get(PARENT_ADDRESS).contains(locationCategory))
			{
				conditionList.add(item);
			}
		}
		return conditionList;
	}
	public GeoPoint getGeoPoint(Location location) {
		if (location == null) {
			return null;
		}
		Double lat = location.getLatitude() * 1E6;
		Double lng = location.getLongitude() * 1E6;
		return new GeoPoint(lat.intValue(), lng.intValue());
	}

	public GeoPoint getGeoPoint(Double lat, Double lng) {
		lat = lat * 1E6;
		lng = lng * 1E6;
		return new GeoPoint(lat.intValue(), lng.intValue());
	}


	public Map<String, String[]> getCoordinateList()
	{
		Map<String, String[]> colfCoordinateMap = new HashMap<String,String[]>();
		try {
			XmlResourceParser xpp =
				context.getResources().getXml(R.xml.golf_coordinates);


			String tag=null, name = null, text = null;
			boolean placemark = false;
			String[] coordinates = null;

			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				switch( eventType ){
				case XmlPullParser.START_DOCUMENT :
				case XmlPullParser.END_DOCUMENT :
					break;
				case XmlPullParser.START_TAG :
					tag = xpp.getName();
					if( tag.equalsIgnoreCase("Placemark"))
					{
						placemark = true;
					}
					break;
				case XmlPullParser.TEXT :
					//Log.i("DR", "TEXT = " + xpp.getText());
					text = xpp.getText();
					break;
				case XmlPullParser.END_TAG :
					//Log.i("DR", "END_TAG = " + xpp.getName());
					tag = xpp.getName();
					if( tag.equalsIgnoreCase("Placemark"))
					{
						placemark = false;
						colfCoordinateMap.put(name, coordinates);
					}else if( placemark && tag.equalsIgnoreCase("name"))
					{
						name = text.replace("_", " ");
					}else if( placemark && tag.equalsIgnoreCase("coordinates"))
					{
						coordinates = text.split(",");
					}
					break;
				default :
					break;

				}
				eventType = xpp.next(); 
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return colfCoordinateMap;
	}
}