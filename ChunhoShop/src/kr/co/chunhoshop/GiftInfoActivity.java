package kr.co.chunhoshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.chunhoshop.util.ListParser;
import kr.co.chunhoshop.util.ParserTag;
import kr.co.chunhoshop.util.ParserUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class GiftInfoActivity extends Activity implements ParserTag,
		OnGroupClickListener {

	ArrayList<ArrayList<Map<String, CharSequence>>> map = new ArrayList<ArrayList<Map<String, CharSequence>>>();
	ArrayList<Map<String, String>> group = new ArrayList<Map<String, String>>();
	ListParser parser = new ListParser();
	GiftThread gThread = new GiftThread();
	GiftHandler gHandler = new GiftHandler();
	ProgressDialog loading;

	ExpandableListView gList;
	GiftAdapter gAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gift);
		gList = (ExpandableListView) findViewById(R.id.giftlistview);
		gList.setOnGroupClickListener(this);
		gList.setGroupIndicator(null);
		loading = new ProgressDialog(this);
		loading.setMessage("목록을 가져오는 중입니다.");
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loading();
	}

	void loading() {
		if (!loading.isShowing())
			loading.show();
		gThread = new GiftThread();
		gThread.start();
	}

	void loadComplete() {
		if (loading.isShowing())
			loading.dismiss();
		gAdapter = new GiftAdapter(this, group, R.layout.gift_group, null,
				null, map, R.layout.gift_child, null, null);
		gList.setAdapter(gAdapter);
	}

	class GiftThread extends Thread {
		@Override
		public void run() {
			for (int i = 1; i < 5; i++) {
				parser = new ListParser();
				parser.load(BONUSLIST + i);
				map.add(parser.get());
				Map<String, String> gr = new HashMap<String, String>();
				gr.put(i + "", i + "");
				group.add(gr);
			}
			gHandler.sendEmptyMessage(0);
		}
	}

	class GiftHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			loadComplete();
		}
	}

	class GiftAdapter extends SimpleExpandableListAdapter {

		public GiftAdapter(Context context,
				List<? extends Map<String, ?>> groupData, int groupLayout,
				String[] groupFrom, int[] groupTo,
				List<? extends List<? extends Map<String, ?>>> childData,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, groupData, groupLayout, groupFrom, groupTo,
					childData, childLayout, childFrom, childTo);
			// TODO Auto-generated constructor stub
		}

		Map<Integer, Map<Integer, Bitmap>> icons = new HashMap<Integer, Map<Integer, Bitmap>>();
		Map<Integer, Map<Integer, ImgSync>> syncs = new HashMap<Integer, Map<Integer, ImgSync>>();

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.SimpleExpandableListAdapter#getChildView(int,
		 * int, boolean, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				view = View.inflate(GiftInfoActivity.this, R.layout.gift_child,
						null);
			}

			ImageView icon = (ImageView) view.findViewById(R.id.giftchildicon);
			TextView name = (TextView) view.findViewById(R.id.giftchildtext);

			String[] title = map.get(groupPosition).get(childPosition)
					.get(GNAME).toString().split("]-");

			name.setText(title[title.length - 1]);

			if (icons.containsKey(groupPosition)) {
				if (icons.get(groupPosition).containsKey(childPosition)) {
					icon.setImageBitmap(icons.get(groupPosition).get(
							childPosition));
				} else {
					if (syncs.get(groupPosition).containsKey(childPosition)) {
						if (syncs.get(groupPosition).get(childPosition)
								.getStatus() == AsyncTask.Status.PENDING) {
							syncs.get(groupPosition).get(childPosition)
									.execute();
						} else if (syncs.get(groupPosition).get(childPosition)
								.getStatus() == AsyncTask.Status.RUNNING) {
							icon.setImageResource(R.drawable.icon);
						}
					} else {
						ImgSync sync = new ImgSync(groupPosition,
								childPosition, icon);
						syncs.get(groupPosition).put(childPosition, sync);
						syncs.get(groupPosition).get(childPosition).execute();
					}
				}
			} else {
				Map<Integer, ImgSync> map = new HashMap<Integer, ImgSync>();
				ImgSync sync = new ImgSync(groupPosition, childPosition, icon);
				map.put(childPosition, sync);
				syncs.put(groupPosition, map);
				syncs.get(groupPosition).get(childPosition).execute();
			}

			return view;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.SimpleExpandableListAdapter#getGroupView(int,
		 * boolean, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				view = View.inflate(GiftInfoActivity.this, R.layout.gift_group,
						null);
			}

			ImageView icon = (ImageView) view.findViewById(R.id.giftgroupimg);
			switch (groupPosition) {
			case 0:
				icon.setImageResource(R.drawable.gift_10_btn);
				break;
			case 1:
				icon.setImageResource(R.drawable.gift_20_btn);
				break;
			case 2:
				icon.setImageResource(R.drawable.gift_30_btn);
				break;
			case 3:
				icon.setImageResource(R.drawable.gift_40_btn);
				break;
			}

			return view;
		}

		class ImgSync extends AsyncTask<Void, Void, Bitmap> {

			ImageView iv;
			int gp;
			int cp;

			public ImgSync(int gposition, int cposition, ImageView v) {
				// TODO Auto-generated constructor stub
				iv = v;
				gp = gposition;
				cp = cposition;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(Bitmap result) {
				// TODO Auto-generated method stub
				iv.setImageBitmap(result);
				notifyDataSetChanged();
			}

			@Override
			protected Bitmap doInBackground(Void... params) {
				// TODO Auto-generated method stub

				Bitmap b = null;
				if (icons.containsKey(gp)) {
					b = icons.get(gp).get(cp);
				} else {
					Map<Integer, Bitmap> map = new HashMap<Integer, Bitmap>();
					icons.put(gp, map);
				}
				String url = (String) map.get(gp).get(cp).get(GIMG);

				if (b == null) {
					b = ParserUtil.getImageURL(url);
					icons.get(gp).put(cp, b);
				}

				return b;
			}
		}
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		// TODO Auto-generated method stub
		if (gList.collapseGroup(groupPosition)) {
			return true;
		}

		for (int i = 0; i < map.size(); i++) {
			gList.collapseGroup(i);
		}

		if (!gList.expandGroup(groupPosition)) {
			gList.collapseGroup(groupPosition);
		}
		return true;
	}

}
