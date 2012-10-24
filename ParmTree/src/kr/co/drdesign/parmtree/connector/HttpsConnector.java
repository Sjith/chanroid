package kr.co.drdesign.parmtree.connector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import kr.co.drdesign.parmtree.aria.Base64Coder;

public class HttpsConnector extends WebConnector {


	public CharSequence readFromURL( String URL, String id, String password, Map<String, String> post) throws IOException 
	{
		StringBuilder content = new StringBuilder();

		try{
			BufferedReader br = getBufferedReaderFromURL(URL, id, password, post);

			String line;
			while ((line = br.readLine()) != null)
			{
				content.append(line).append("\n");
			}

			br.close();
			return content;
		}catch( IOException ioe )
		{
			throw ioe;
		}
	}

	public CharSequence decodeFromURL( String URL, String id, String password, Map<String, String> post) throws IOException 
	{
		StringBuilder content = new StringBuilder();

		try{
			BufferedReader br = getBufferedReaderFromURL(URL, id, password, post);

			String line;
			byte[] lineGetBytes = null;
			while ((line = br.readLine()) != null)
			{
				//100803 halftale
				//lineGetBytes = Base64.decode(line.getBytes(), Base64.DEFAULT);
				lineGetBytes = Base64Coder.decode(line);
				content.append(ARIADecode(lineGetBytes)).append("\n");
			}

			br.close();
			return content;
		}catch( IOException ioe )
		{
			
			throw ioe;
		}
	}

	public BufferedReader getBufferedReaderFromURL( String URL, String id, String password, Map<String, String> post ) throws IOException{
		try
		{
			URL url = new URL(URL);

			HttpURLConnection http = null;

			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();
				HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);

				http = https;
			} else {
				http = (HttpURLConnection) url.openConnection();
			}

			http.setDoOutput(true);
			http.setDoInput( true );
			// Make this URL connection available for input and output

			// Log in by base64 encrypt
			// Property Example Login
			// Authorization: Basic TXlVc2VySUQ6TXlQYXNzd29yZA==
			String temp = id + ":" + password;
			String loginBybase64 = "Basic " + Base64Coder.encodeString(temp);			
			http.setRequestProperty( "Authorization", loginBybase64 );

			if( post != null && post.size() > 0){
				PrintWriter pw = new PrintWriter( new OutputStreamWriter(http.getOutputStream(), CHARACTER_SET));

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
			//String data = "userid=???&password=???";
			//wr.write(data);
			//wr.flush();
			http.connect();
			return new BufferedReader(new InputStreamReader(http.getInputStream(), CHARACTER_SET));
		}catch( IOException ioe )
		{
			
			throw ioe;
		}
	}


	private void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] chain,
					String authType)
			throws java.security.cert.CertificateException {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] chain,
					String authType)
			throws java.security.cert.CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
			.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	@Override
	public BufferedInputStream getBufferedInputStreamFromURL(
			String URL,
			String id, 
			String password, 
			Map<String, String> post) 
	throws IOException {
		try
		{
			URL url = new URL(URL);

			HttpURLConnection http = null;

			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();
				HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);

				http = https;
			} else {
				http = (HttpURLConnection) url.openConnection();
			}

			http.setDoOutput(true);
			http.setDoInput( true );
			// Make this URL connection available for input and output

			// Log in by base64 encrypt
			// Property Example Login
			// Authorization: Basic TXlVc2VySUQ6TXlQYXNzd29yZA==
			String temp = id + ":" + password;
			String loginBybase64 = "Basic " + Base64Coder.encodeString(temp);			
			http.setRequestProperty( "Authorization", loginBybase64 );

			if( post != null && post.size() > 0){
				PrintWriter pw = new PrintWriter( new OutputStreamWriter(http.getOutputStream(), CHARACTER_SET));

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
			//			String data = "userid=???&password=???";
			//			wr.write(data);
			//			wr.flush();
			http.connect();
			return new BufferedInputStream(http.getInputStream());
		}catch( IOException ioe )
		{
			
			throw ioe;
		}
	}

	@Override
	public BufferedInputStream getBufferedInputStreamFromURLwithoutSession(
			String URL, String id, String password, Map<String, String> post)
	throws IOException {
		try
		{
			URL url = new URL(URL);

			HttpURLConnection http = null;

			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();
				HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);

				http = https;
			} else {
				http = (HttpURLConnection) url.openConnection();
			}

			http.setDoOutput(true);
			http.setDoInput( true );
			// Make this URL connection available for input and output

			// Log in by base64 encrypt
			// Property Example Login
			// Authorization: Basic TXlVc2VySUQ6TXlQYXNzd29yZA==
			String temp = id + ":" + password;
			String loginBybase64 = "Basic " + Base64Coder.encodeString(temp);			
			http.setRequestProperty( "Authorization", loginBybase64 );

			if( post != null && post.size() > 0){
				PrintWriter pw = new PrintWriter( new OutputStreamWriter(http.getOutputStream(), CHARACTER_SET));

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
			//String data = "userid=???&password=???";
			//wr.write(data);
			//wr.flush();
			http.connect();
			return new BufferedInputStream(http.getInputStream());
		}catch( IOException ioe )
		{
			
			throw ioe;
		}
	}
}
