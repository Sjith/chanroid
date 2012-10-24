package kr.co.chunhoshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.chunhoshop.util.ListParser;
import kr.co.chunhoshop.util.ParserTag;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FAQActivity extends Activity implements ParserTag,
		OnClickListener, OnItemClickListener {

	ArrayList<Map<String, CharSequence>> faqArray = new ArrayList<Map<String, CharSequence>>();
	ListParser parser = new ListParser();
	FrameLayout desc;
	LinearLayout detail;

	ProgressBar loading;
	ListView faqList;

	FAQThread fThread = new FAQThread();
	FAQHandler fHandler = new FAQHandler();
	FAQAdapter fAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.faq);
		loading = (ProgressBar) findViewById(R.id.faqprogress);
		desc = (FrameLayout) findViewById(R.id.faqdesc);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		detail = (LinearLayout) inflater.inflate(R.layout.faq_detail, null);
		desc.addView(detail);

		faqList = (ListView) findViewById(R.id.faqlist);
		faqList.setOnItemClickListener(this);
		faqList.setDividerHeight(3);
	}

	void loading() {
		loading.setVisibility(View.VISIBLE);
		fThread = new FAQThread();
		fThread.start();
	}

	void loadComplete() {
		loading.setVisibility(View.GONE);
		faqArray = parser.get();
		
		if (faqArray.size() == 0) {
			loading();
			return;
		}		
		
		fAdapter = new FAQAdapter(this, faqArray, R.layout.faq_item, null, null);
		faqList.setAdapter(fAdapter);
	}

	@Override
	public void onResume() {

		super.onResume();
		if (desc.getVisibility() != View.GONE) {
			detail.setVisibility(View.GONE);
//			desc.setVisibility(View.GONE);
			findViewById(R.id.linearLayout1).setVisibility(View.VISIBLE);
		}
		if (faqArray.size() > 0) {
			faqArray.clear();
			fAdapter.notifyDataSetChanged();
		}

		loading();
	}

	class FAQThread extends Thread {
		@Override
		public void run() {
			parser.load(FAQPAGE);
			fHandler.sendEmptyMessage(0);
		}
	}

	class FAQHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				loadComplete();
				break;
			}
		}
	}

	class FAQAdapter extends SimpleAdapter {

		public FAQAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.SimpleAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.faq_item, null);
			}

			if (position % 2 == 0)
				view.setBackgroundColor(Color.WHITE);

			TextView title = (TextView) view.findViewById(R.id.faqitemtitle);
			title.setText(faqArray.get(position).get(FAQTITLE));

			return view;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		desc.setVisibility(View.VISIBLE);
		findViewById(R.id.linearLayout1).setVisibility(View.GONE);
		faqList.setVisibility(View.GONE);
		TextView title = (TextView) desc.findViewById(R.id.faqtitle);
		WebView content = (WebView) desc.findViewById(R.id.faqcontent);
		content.getSettings().setSupportZoom(true);
		content.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
		content.getSettings().setBuiltInZoomControls(true);
		title.setText(faqArray.get(arg2).get(FAQTITLE));
		content.loadDataWithBaseURL("a", faqArray.get(arg2).get(FAQCONTENT)
				.toString(), "text/html", CHARACTER_SET, null);
	}

	@Override
	public void onBackPressed() {
		if (desc.getVisibility() == View.VISIBLE) {
			desc.setVisibility(View.GONE);
			findViewById(R.id.linearLayout1).setVisibility(View.VISIBLE);
			faqList.setVisibility(View.VISIBLE);
		} else
			super.onBackPressed();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.smsbtn:
			String strnum = "01027930125";
//			String strbody = "천호쇼핑/";
			Uri smsuri = Uri.parse("sms:" + strnum);
			Intent sendIntent = new Intent(Intent.ACTION_SENDTO, smsuri);
//			sendIntent.putExtra("sms_body", strbody);
			startActivity(sendIntent);
			break;
		case R.id.callbtn:
			
			AlertDialog.Builder dialog1 = new AlertDialog.Builder(getParent().getParent());
			dialog1.setTitle("전화연결")
					.setMessage("누를수록 건강해지는 번호\n[080-607-1005]\n전화 연결 하시겠습니까?")
					.setNegativeButton("취소", null)
					.setPositiveButton(R.string.done,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent i = new Intent(Intent.ACTION_CALL);
									i.setData(Uri.parse("tel:0806071005"));
									startActivity(i);
									dialog.dismiss();
									// 전화걸기
								}
							}).show();

			break;
		case R.id.mailbtn:
			Intent sendIntent1 = new Intent(Intent.ACTION_SEND);
			sendIntent1.putExtra(Intent.EXTRA_SUBJECT, "");
			sendIntent1.setType("text/csv");
			sendIntent1.putExtra(Intent.EXTRA_EMAIL, new String[]{ "ch1005@chunhoshop.com" });
			sendIntent1.putExtra(Intent.EXTRA_TEXT, "");
			startActivity(Intent.createChooser(sendIntent1, "Send email"));
			break;
		}
	}

}
