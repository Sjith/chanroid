package kr.co.chunhoshop;

import java.util.HashMap;
import java.util.Map;

import kr.co.chunhoshop.util.ItemParser;
import kr.co.chunhoshop.util.ParserTag;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NoticeDescActivity extends Activity implements ParserTag {

	ItemParser parser;
	ProgressBar loading;

	ProductThread pThread = new ProductThread();
	ProductHandler pHandler = new ProductHandler();
	Map<String, CharSequence> map = new HashMap<String, CharSequence>();
	Intent getIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_detail);
		getIntent = getIntent();
		parser = new ItemParser();
		loading = (ProgressBar) findViewById(R.id.noticedetaildialog);
		loading();
	}

	public void loading() {
		loading.setVisibility(View.VISIBLE);
		pThread = new ProductThread();
		pThread.start();
	}

	public void loadComplete() {
		loading.setVisibility(View.GONE);
		map = parser.get();
		// ºä Ç×¸ñ ¼ÂÆÃ
		RelativeLayout item = (RelativeLayout) View.inflate(this,
				R.layout.notice_item2, null);
		LinearLayout body = (LinearLayout) findViewById(R.id.noticebody);
		TextView title = (TextView) item.findViewById(R.id.noticelisttitle);
		title.setText(map.get(NSUBJECT));
		TextView date = (TextView) item.findViewById(R.id.noticelistdate);
		date.setText(map.get(NDATE));
		body.addView(item);
		ImageView arrow = (ImageView) item.findViewById(R.id.noticearrow);
		arrow.setVisibility(View.GONE);

		WebView content = new WebView(this);
		content.getSettings().setSupportZoom(true);
		content.getSettings().setBuiltInZoomControls(true);
		content.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
		content.getSettings().setLoadWithOverviewMode(true);
		content.setPadding(5, 5, 5, 5);
		content.loadDataWithBaseURL("a", map.get(NCONTENT).toString(), "text/html", CHARACTER_SET, null);
		body.addView(content);

	}
	
	class ProductThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			parser.load(NOTICEITEM + getIntent.getAction());
			pHandler.sendEmptyMessage(0);
		}
	}

	class ProductHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			loadComplete();
		}

	}

}
