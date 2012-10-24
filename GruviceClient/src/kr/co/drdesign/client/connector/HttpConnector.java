package kr.co.drdesign.client.connector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kr.co.drdesign.util.Base64Coder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class HttpConnector extends WebConnector {

	private static HttpClient HTTP_CLIENT = new DefaultHttpClient();
	
	// Waiting Time, this case is 5000ms
	
	public CharSequence readFromURL( String URL, String id, String password, Map<String, String> post) 
	throws IOException
	{
		StringBuilder buff = new StringBuilder();
		try {
			BufferedReader br = getBufferedReaderFromURL( URL, id, password, post);
			String line;

			// halftale 100806 tempolory Code
			if (br == null) return null;
			while((line = br.readLine()) != null ){
				buff.append(line);
			}
		}catch(IOException ioe){
			L('e', ioe.getMessage());
			throw ioe;
		}
		return buff;
	}
	public CharSequence decodeFromURL( String URL, String id, String password, Map<String, String> post)
	throws IOException, IllegalArgumentException
	{
		StringBuilder content = new StringBuilder();

		try{
			L("read buffer Start() : " + URL + "," + id + "," + password);
			BufferedReader br = getBufferedReaderFromURL(URL, id, password, post);

			String line;
			StringBuilder sb = new StringBuilder();
			byte[] lineGetBytes = null;
			
			while ((line = br.readLine()) != null)
			{
				sb.append(line + "\n");

				//메모리를 많이 잡아먹는 구문.( 절대로 String을 사용하면 안된다. )
				//100803 halftale
				//lineGetBytes = android.util.Base64.decode(line.getBytes(), android.util.Base64.DEFAULT);
				lineGetBytes = Base64Coder.decode(line.trim());
				content.append(ARIADecode(lineGetBytes)).append("\n");
			}
			
			
			br.close();
			return content;
		}catch( IOException ioe )
		{
			L('e', "HttpConnector line 87");
			L('e', ioe.getMessage());
			throw ioe;
		}catch( IllegalArgumentException iae)
		{
			L('e', "HttpConnector line 92");
			L('e', iae.getMessage());
			throw iae;
		}catch( OutOfMemoryError ooe) {
			L('e', "HttpConnector line 96");
			L('e', ooe.getMessage());
			throw ooe;
		}
	}
	public BufferedReader getBufferedReaderFromURL( String url, String id, String password, Map<String, String> post )
	throws IOException
	{
		try {
			InputStream is  = getBufferedInputStreamFromURL(url, id, password, post);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, CHARACTER_SET), 8);
			return reader;
		}catch(IOException ioe){
			L('e', "HttpConnector line 107");
			L('e', ioe.getMessage());
			throw ioe;
		}
	}

	@Override
	public BufferedInputStream getBufferedInputStreamFromURL(String url,
			String id, String password, Map<String, String> post) throws IOException {
		try {
			/** 연결 타입아웃내에 연결되는지 테스트, 5초 이내에 되지 않는다면 에러 */
			if (url.contains(" ")) url.replace(" ", "%20"); // 공백문자를 url에서 사용하는 문자로 변환
			Log.i("Gruvice", "URL Test : " + id + " + " + password + " + " + url);
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("clientid", id.trim()));
			Log.i("DR", "- Client id is " + id);
			Log.i("DR", "- getBufferedInputStreamFromURL " + id);
			nameValuePairs.add(new BasicNameValuePair("PW", password));

			if( post != null)
			{
				Set<String> keyset = post.keySet();
				if( keyset != null && keyset.size() !=0)
				{
					Iterator<String> it = keyset.iterator();
					String key;
					while( it.hasNext() )
					{
						key = it.next();
						nameValuePairs.add( new BasicNameValuePair( key, post.get(key)) );	
					}
				}
			}
			/** 네트웍 연결해서 데이타 받아오기 */
			HttpParams params = HTTP_CLIENT.getParams();
			HttpConnectionParams.setConnectionTimeout(params, TIME_OUT);
			HttpConnectionParams.setSoTimeout(params, TIME_OUT);

			HttpPost httppost = new HttpPost(url);
			UrlEncodedFormEntity entityRequest = 
				new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
			httppost.setEntity(entityRequest);

			HttpResponse response = HTTP_CLIENT.execute(httppost);

			HttpEntity entityResponse = response.getEntity();

			InputStream is = entityResponse.getContent();
			BufferedInputStream bis = new BufferedInputStream( is );
			return bis;
		}catch(IOException ioe){
			L('e', "HttpConnector line 156");
//			L('e', ioe.getMessage());
			throw ioe;
		}
		//			/** convert response to string */
		//			BufferedReader reader = new BufferedReader(new InputStreamReader(
		//					is, "iso-8859-1"), 8);
		//			StringBuilder sb = new StringBuilder();
		//			String line = null;
		//			while ((line = reader.readLine()) != null) {
		//				sb.append(line).append("\n");
		//			}
		//			is.close();
		//			result = sb.toString();
	}

	public BufferedInputStream getBufferedInputStreamFromURLwithoutSession(String URL,
			String id, String password, Map<String, String> post) throws IOException {
		try {
			URL url = new URL( URL );
			HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
			httpURLCon.setDefaultUseCaches(false);
			httpURLCon.setAllowUserInteraction(true);
			httpURLCon.setDoInput(true);
			httpURLCon.setDoOutput(true);
			httpURLCon.setRequestProperty("Connection","Keep-Alive"); 
			httpURLCon.setRequestProperty("content-type", "application/x-www-form-urlencoded");

			if( post != null && post.size() > 0){
				PrintWriter pw = new PrintWriter( new OutputStreamWriter(httpURLCon.getOutputStream(), CHARACTER_SET));

				Iterator<String> it = post.keySet().iterator();
				StringBuilder sb = new StringBuilder();
				String name, value;
				while( it.hasNext() ){
					name = it.next();
					value = post.get(name);
					sb.append(name).append("=").append(value).append("&");
				}
				pw.write(sb.toString());
				pw.flush();
				pw.close();
			}
			httpURLCon.connect();
			L('i', "httpURLCon.getInputStream()");

			return new BufferedInputStream(httpURLCon.getInputStream());
		}catch(IOException ioe){
			L('e', ioe.getMessage());
			throw ioe;
		}
	}
	public void EndConnection()
	{
		HTTP_CLIENT.getConnectionManager().shutdown();
	}
	//	@Override
	//	public BufferedInputStream getBufferedInputStreamFromURL(String URL,
	//			String id, String password, Map<String, String> post) throws IOException {
	//		try {
	//			URL url = new URL( URL );
	//			HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
	//			httpURLCon.setDefaultUseCaches(false);
	//			httpURLCon.setAllowUserInteraction(true);
	//			httpURLCon.setDoInput(true);
	//			httpURLCon.setDoOutput(true);
	//			httpURLCon.setRequestProperty("Connection","Keep-Alive"); 
	//			httpURLCon.setRequestProperty("content-type", "application/x-www-form-urlencoded");
	//			
	//			if( post != null && post.size() > 0){
	//				PrintWriter pw = new PrintWriter( new OutputStreamWriter(httpURLCon.getOutputStream(), CHARACTER_SET));
	//
	//				Iterator<String> it = post.keySet().iterator();
	//				StringBuilder sb = new StringBuilder();
	//				String name, value;
	//				while( it.hasNext() ){
	//					name = it.next();
	//					value = post.get(name);
	//					sb.append(name).append("=").append(value).append("&");
	//				}
	//				pw.write(sb.toString());
	//				pw.flush();
	//				pw.close();
	//			}
	//			httpURLCon.connect();
	//			L('i', "httpURLCon.getInputStream()");
	//			
	//			return new BufferedInputStream(httpURLCon.getInputStream());
	//		}catch(IOException ioe){
	//			L('e', ioe.getMessage());
	//			throw ioe;
	//		}
	//	}
}