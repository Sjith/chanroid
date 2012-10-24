package kr.co.chunhoshop;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import kr.co.chunhoshop.util.ParserTag;
import kr.co.chunhoshop.util.ParserUtil;
import kr.co.chunhoshop.util.ParserUtil.FlushedInputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class ChunhoWayActivity extends Activity implements ParserTag {

	ProgressDialog dialog;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dialog = new ProgressDialog(getParent());
		dialog.setMessage("불러오는 중입니다...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loading();
	}
	
	void loading() {
		if (!dialog.isShowing()) dialog.show();
		new BonusSync().execute((Void)null);
	}
	
	String parse() {
		
		try {
			URL url = new URL("http://www.chunhoshop.com/xml/m_img_setting.asp?mode=way");
			XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
			XmlPullParser parser = xpp.newPullParser();
			parser.setInput(url.openStream(), CHARACTER_SET);
			
			String tag = "", text = "";
			
			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					break;
				case XmlPullParser.TEXT:
					text = parser.getText();
					if (tag.equalsIgnoreCase("se_url")) {
						return text;
					}
					break;
				case XmlPullParser.END_TAG:
					tag = "";
					text = "";
					break;
				}

				Log.i("Chunho", "tag : " + parser.getName() + ", text : " + parser.getText());
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}

	public Bitmap getImageURLAbsolutePath(String path) {
		try {
			URL url = new URL(path);
			URLConnection conn = url.openConnection();
			conn.connect();
			ParserUtil.FlushedInputStream bis = new FlushedInputStream(
					conn.getInputStream());
			Bitmap b = BitmapFactory.decodeStream(bis);
			bis.close();
			return b;
		} catch (Exception e) {
			return null;
		}
	}
	
	class BonusSync extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Bitmap b = getImageURLAbsolutePath(parse());
			return b;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			setContentView(R.layout.way);
			if (result == null) return;
			ImageView iv = (ImageView) findViewById(R.id.wayimg);
			iv.setImageBitmap(result);
		}

		
	}
}
