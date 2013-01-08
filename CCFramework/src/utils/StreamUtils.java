package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.NameValuePair;

public class StreamUtils {

	/**
	 * 지정된 url의 스트림을 가져옴
	 * 
	 * @param url
	 *            스트림을 가져올 url
	 * @param params
	 *            url에 보낼 post data
	 * @return 지정된 url의 스트림
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static InputStream inStreamFromURL(String url,
			ArrayList<NameValuePair> params) throws IllegalStateException,
			IOException {

		StringBuilder paramBuilder = new StringBuilder();
		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		conn.setDoInput(true);
		conn.setRequestMethod("GET");

		if (params != null) {
			conn.setDoOutput(true); // 이거 호출하면 자동으로 POST로 변경됨
			conn.setRequestMethod("POST");
			for (NameValuePair p : params)
				paramBuilder.append(p.getName() + "=" + p.getValue() + "&");

			OutputStream paramWriter = conn.getOutputStream();
			paramWriter.write(paramBuilder.toString().getBytes());
			paramWriter.flush();
			paramWriter.close();
		}


		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
			return conn.getInputStream();
		else
			return null;

	}

}
