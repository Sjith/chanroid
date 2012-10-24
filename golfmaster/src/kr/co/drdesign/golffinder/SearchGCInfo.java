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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

abstract public class SearchGCInfo
{
	public static final String LATITUDE		= "LATITUDE";	//위도 
	public static final String LONGITUDE 	= "LONGITUDE";	//경도
	public static final String NAME			= "NAME";
	public static final String ADDRESS 		= "ADDRESS"; 
	public static final String TELEPHONE 	= "TELEPHONE";
	public static final String HOMEPAGE 	= "HOME_PAGE";
	public static final String DETAIL_VIEW	= "DETAIL_VIEW";
	public static final String PREVIEW		= "PREVIEW";
	public static final String DISTANCE		= "DISTANCE";

	public static final int radius = 20; // 검색 범위

	//public static final String QUERY = "category:스크린골프장";
	//public static final String QUERY = "부산역";
	public String QUERY = "category:골프장";
	public String CATEGORY = "골프장";
	public Context context;

	public static List<Map<String,String>> sortList( List<Map<String, String>> list, String key )
	{
		Log.i("DR", "sort");
		int index = 0;
		ArrayList<Map<String,String>> sortedList = new ArrayList<Map<String,String>>();
		while (index < list.size())
		{
			String sTemp = list.get(index).get(key);
			Integer comp1 = Integer.parseInt(sTemp);
			Integer comp2;
			for( int i = 0 ; i < index ; i++)
			{
				sortedList.add( list.get(i) );
			}
			for( int i = index+1; i < list.size() ; i++ )
			{
				comp2 = Integer.parseInt(list.get(i).get(key)); 
				if( comp1.compareTo(comp2) > 0)
				{
					sortedList.add(list.get(i));
				}
			}
			sortedList.add( list.get(index) );
			for( int i = index+1; i < list.size() ; i++ )
			{
				comp2 = Integer.parseInt(list.get(i).get(key)); 

				if( comp1.compareTo(comp2) <= 0)
				{
					sortedList.add(list.get(i));
				}
			}
			if( sortedList.get(index).get(key).equals(sTemp) ) index++;

			list = sortedList;
			sortedList = new ArrayList<Map<String,String>>();
		}

		return list;
	}

	public SearchGCInfo(Context context, String QUERY, String CATEGORY)
	{
		this.context = context.getApplicationContext();
		this.QUERY = QUERY;
		this.CATEGORY = CATEGORY;
	}

	public ArrayList<Map<String,String>> getGCList(GeoPoint geoPoint) 
	throws IOException, URISyntaxException
	{
		double latitude 	= geoPoint.getLatitudeE6() / 1E6 ;
		double longitude 	= geoPoint.getLongitudeE6() / 1E6 ;
		return getGCList( latitude, longitude, radius);
	}

	public ArrayList<Map<String,String>> getGCList(Location location) 
	throws IOException, URISyntaxException
	{
		double latitude 	= location.getLatitude();
		double longitude 	= location.getLongitude();
		return getGCList( latitude, longitude, radius);
	}


	//Thread로 변경해야 속도가 빨라진다.
	public ArrayList<Map<String,String>> getGCList(double latitude, double longitude, int radius) 
	throws IOException, URISyntaxException
	{
		String location 	= latitude + "," + longitude;
		ArrayList<Map<String, String>> list = new ArrayList<Map<String,String>>();
		ArrayList<Map<String, String>> temp;
		for( int i = 0 ; i < 4 ; i++ )
		{
			temp = getGCList(location, radius, i*20);
			if( temp == null ) 
				break;

			list.addAll(temp);

			if( temp.size() < 20 )
			{
				break;
			}
		}
		return list;
	}

	public ArrayList<Map<String,String>> getGCList(String address, String query, int radius)
	throws IOException, URISyntaxException
	{
		ArrayList<Map<String, String>> list = new ArrayList<Map<String,String>>();
		ArrayList<Map<String, String>> temp;
		for( int i = 0 ; i < 4 ; i++ )
		{
			temp = getGCList(address, "intitle:\"" + query + "\" " + CATEGORY, radius, i*20);
			if( temp == null ) 
				break;

			list.addAll(temp);

			if( temp.size() < 20 )
			{
				break;
			}
		}
		return list;
	}
	public ArrayList<Map<String,String>> getGCList(Location location, int radius, int page)
	throws IOException, URISyntaxException
	{
		if ( location == null ) return new ArrayList<Map<String,String>>();
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		String address = location2Address(location); 
		if( address == null )
			address = location.getLatitude()+","+location.getLongitude();
		else
			params.add(new BasicNameValuePair("near", address ));

		return getGCList( address, radius, page);
	}

	public ArrayList<Map<String,String>> getGCList(Location location, String query, int radius, int page)
	throws IOException, URISyntaxException
	{
		if ( location == null ) return new ArrayList<Map<String,String>>();
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		String address = location2Address(location); 
		if( address == null )
			address = location.getLatitude()+","+location.getLongitude();
		else
			params.add(new BasicNameValuePair("near", address ));

		return getGCList( address, query, radius, page);
	}

	public ArrayList<Map<String,String>> getGCList(String address, int radius, int page)
	throws IOException, URISyntaxException
	{
		if ( address == null || address.length() == 0) 
			return new ArrayList<Map<String,String>>();
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		if( address == null || address.equals(""))
			params.add(new BasicNameValuePair("near", address ));
		else
			params.add(new BasicNameValuePair("near", address ));
		//params.add(new BasicNameValuePair("near", "부산광역시 초량동"));
		params.add(new BasicNameValuePair("q", QUERY));
		params.add(new BasicNameValuePair("radius", 0.621371192 * radius + "")); //마일로 표시된 반경
		// 1km = 0.621371192 mile
		params.add(new BasicNameValuePair("output", "kml")); //결과포멧 - xml
		params.add(new BasicNameValuePair("mrt", "yp")); //검색오션. 업체
		params.add(new BasicNameValuePair("start", page + "")); //검색결과중 처음에 보여줄 시작페이지
		params.add(new BasicNameValuePair("num", "20")); //검색결과중 처음에 보여줄 시작페이지

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

	public ArrayList<Map<String,String>> getGCList(String address, String query, int radius, int page)
	throws IOException, URISyntaxException
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		if( address == null || address.equals(""))
			;
		else
			params.add(new BasicNameValuePair("near", address ));

		//params.add(new BasicNameValuePair("near", "부산광역시 초량동"));
		params.add(new BasicNameValuePair("q", query));
		params.add(new BasicNameValuePair("radius", 0.621371192 * radius + "")); //마일로 표시된 반경
		// 1km = 0.621371192 mile
		params.add(new BasicNameValuePair("output", "kml")); //결과포멧 - xml
		params.add(new BasicNameValuePair("mrt", "yp")); //검색오션. 업체
		params.add(new BasicNameValuePair("start", page + "")); //검색결과중 처음에 보여줄 시작페이지
		params.add(new BasicNameValuePair("num", "20")); //검색결과중 처음에 보여줄 시작페이지

		URI uri = URIUtils.createURI("http", "maps.google.co.kr", -1, "", 
				URLEncodedUtils.format(params, "UTF-8"), null);

		Log.i("GF", "URLs = " + uri.toURL());

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

	public String location2Address(Location location) throws IOException
	{
		if ( location == null ) return null;
		try{
			Geocoder goecoder = new Geocoder(context, Locale.getDefault());
			Address address = goecoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);

			StringBuilder strAddress = new StringBuilder();
			strAddress.append(address.getLocality()).append(' ');
			strAddress.append(address.getThoroughfare());
			//		.append(' ');
			//		strAddress.append(address.getFeatureName());
			return strAddress.toString();
		}catch( IndexOutOfBoundsException e){
			Log.e("GF", e.getMessage());
		}
		return null;
	}

	public void address2Location(String locationName) throws IOException
	{
		if ( locationName == null || locationName.length() == 0) return ; 

		try{
			Geocoder goecoder = new Geocoder(context, Locale.getDefault());
			List<Address> addList = goecoder.getFromLocationName(locationName, 1);
			if( addList != null && addList.size() > 0 )
			{
				Log.i("GF", "Latitude = " + addList.get(0).getLatitude());
				Log.i("GF", "Latitude = " + addList.get(0).getLongitude());
			}
		}catch( IndexOutOfBoundsException e){
			Log.e("GF", e.getMessage());
		}
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
						//int cnt = xpp.getAttributeCount();
						//for( int i = 0; i < cnt ; i++ )
						//{
						//	Log.i("DR", "AttributeName : " + xpp.getAttributeName(i));
						//	Log.i("DR", "AttributeValue : " + xpp.getAttributeValue(i));
						//	Log.i("DR", "AttributeType : " + xpp.getAttributeType(i));
						//}
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


		for(int i = 0; i < list.size() ; i++)
		{
			Map<String,String> map = list.get(i);
			if( map.get(TELEPHONE) == null )
			{
				list.remove(map);
				i--;
			}
		}
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
					//자세히보기
					data[0] = "maps.google.co.kr/m" + token.substring(15, token.length()-1);
				}else if( token.contains("</a") && token.contains("$[name]") == false ) 
				{
					//홈페이지
					data[1] = token.substring(0, token.length() - 3);
				}else if( token.contains("<br/"))
				{
					//전화번호
					data[2] = token.substring(0, token.length() - 4);
				}else if( token.contains("img"))
				{
					//미리보기 이미지
					data[3] = token.substring(10, token.length() - 2);;
				}
			}
		}
		return data;
	}

	public static class Call implements View.OnClickListener{

		private Context context;
		private String number;

		public Call(Context context, String number)
		{
			this.context = context;
			this.number = number;
		}
		@Override
		public void onClick(View v) {

			Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
			dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(dialIntent);
		}
	}
	public static class OpenLink implements View.OnClickListener{

		private Context context;
		private String link;
		private Map<String, String> item;

		public OpenLink(Context context, String link)
		{
			this.context = context;
			this.link = link;

		}
		public OpenLink(Context context, String link, Map<String, String> item)
		{
			this(context, link);
			this.item = item;
		}
		@Override
		public void onClick(View v) {
			if( item != null ) {
				boolean result = new FavoriteRecentWebDBHelper(context).addClub(item);
			}
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setData(Uri.parse("http://" + link));
			context.startActivity(i);
		}
	}
}