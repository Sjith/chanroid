package kr.co.chunhoshop;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.chunhoshop.util.OrderParser;
import kr.co.chunhoshop.util.ParserTag;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BuyListActivity extends Activity implements ParserTag,
		OnGroupClickListener {

	ExpandableListView buyList;
	ArrayList<Map<String, String>> map = new ArrayList<Map<String, String>>();
	ArrayList<ArrayList<Map<String, String>>> products = new ArrayList<ArrayList<Map<String, String>>>();

	OrderParser oParser = new OrderParser();
	BuyThread bThread = new BuyThread();
	BuyHandler bHandler = new BuyHandler();
	BuyAdapter bAdapter;

	SharedPreferences mPref;

	ProgressBar loading;

	boolean loaded = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buy_list);
		loading = (ProgressBar) findViewById(R.id.buylistdialog);
		buyList = (ExpandableListView) findViewById(R.id.expandableListView1);
		buyList.setDividerHeight(3);
		buyList.setOnGroupClickListener(this);
		mPref = getSharedPreferences(AAUTO, MODE_PRIVATE);
		loading();
	}

	@Override
	public void onResume() {
		if (loaded) {
			map.clear();
			products.clear();
			try {
				bAdapter.notifyDataSetChanged();
			} catch (Exception e) {
			}
			loading();
		}
		super.onResume();
	}

	void loading() {

		 if (mPref.getString(ANAME, "").equalsIgnoreCase("")) {
			 loading.setVisibility(View.GONE);
			 
			 AlertDialog.Builder builder = new AlertDialog.Builder(getParent().getParent());
			 builder.setTitle(R.string.alert);
			 builder.setMessage("�������� ��ȣ �������� ��ȸ���� ���ų����� õȣ���� �����Ϳ� �����Ͻø� �ڼ��� Ȯ���Ͻ� �� �ֽ��ϴ�.");
			 builder.setPositiveButton("������ ����", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					AlertDialog.Builder dialog1 = new AlertDialog.Builder(BuyListActivity.this.getParent().getParent());
					dialog1.setTitle("��ȭ����")
					.setMessage("�������� �ǰ������� ��ȣ\n[080-607-1005]\n��ȭ ���� �Ͻðڽ��ϱ�?")
					.setNegativeButton("���", null)
					.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent i = new Intent(Intent.ACTION_CALL);
							i.setData(Uri.parse("tel:0806071005"));
							startActivity(i);
						}
					}).show();
				}
			});
			builder.setNegativeButton("Ȯ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			}).show();
			 return;
		 }

		if (loading.getVisibility() != View.VISIBLE)
			loading.setVisibility(View.VISIBLE);
		bThread = new BuyThread();
		bThread.start();
	}

	void loadComplete() {
		if (loading.getVisibility() == View.VISIBLE)
			loading.setVisibility(View.GONE);
		products = oParser.getProducts();
		map = oParser.getOrders();
		loaded = true;
		if (map.size() < 1) {
			Toast.makeText(this, "�ֹ� ������ �����ϴ�.", Toast.LENGTH_SHORT).show();
			return;
		}

		bAdapter = new BuyAdapter(this, map, R.layout.buy_list_group, null,
				null, products, R.layout.buy_list_child, null, null);
		buyList.setAdapter(bAdapter);
	}

	void reload() {
		if (loading.getVisibility() == View.VISIBLE)
			loading.setVisibility(View.GONE);
		products = oParser.getProducts();
		map = oParser.getOrders();

		if (map.size() < 1) {
			Toast.makeText(this, "�ֹ� ������ �����ϴ�.", Toast.LENGTH_SHORT).show();
			return;
		}

		// bAdapter.notifyDataSetChanged();
		bAdapter = new BuyAdapter(this, map, R.layout.buy_list_group, null,
				null, products, R.layout.buy_list_child, null, null);
		buyList.setAdapter(bAdapter);
	}

	class BuyThread extends Thread {
		@Override
		public void run() {
			if (mPref.getString(ATEL, "").equalsIgnoreCase("")) {
				bHandler.sendEmptyMessage(0);
				return;
			}
			oParser.load(ORDERLIST + "u_id=" + mPref.getString("id", "") + "&"
					+ "u_mobile=" + mPref.getString(ATEL, ""));
			// ChunhoUtil.getPhoneNum(BuyListActivity.this)));
			bHandler.sendEmptyMessage(0);
		}
	}

	class BuyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (!loaded) {
				loadComplete();
			} else {
				reload();
			}
		}
	}

	class BuyAdapter extends SimpleExpandableListAdapter {

		public BuyAdapter(Context context,
				List<? extends Map<String, ?>> groupData, int groupLayout,
				String[] groupFrom, int[] groupTo,
				List<? extends List<? extends Map<String, ?>>> childData,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, groupData, groupLayout, groupFrom, groupTo,
					childData, childLayout, childFrom, childTo);
			// TODO Auto-generated constructor stub
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.SimpleExpandableListAdapter#getChildrenCount(int)
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return products.get(groupPosition).size();
		}

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
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.buy_list_child, null);
			}

			TextView title = (TextView) view.findViewById(R.id.buylisttitle);
			TextView price = (TextView) view.findViewById(R.id.buylistprice);
			TextView count = (TextView) view.findViewById(R.id.buylistcount);

			LinearLayout flagbody = (LinearLayout) view
					.findViewById(R.id.buygiftchildbody);
			TextView text = (TextView) view.findViewById(R.id.buychildgifttext);
			ImageView optflag = (ImageView) view
					.findViewById(R.id.buychildgiftflag);
			ImageView healthflag = (ImageView) view
					.findViewById(R.id.buychildhealthflag);

			if (products.get(groupPosition).get(childPosition).get(POPTIONNAME) == null
					|| products.get(groupPosition).get(childPosition)
							.get(POPTIONNAME).equalsIgnoreCase("")) {
				flagbody.setVisibility(View.GONE);
				text.setVisibility(View.GONE);
				optflag.setVisibility(View.GONE);
				healthflag.setVisibility(View.GONE);
				if (products.get(groupPosition).get(childPosition).get("p_option_flag")
						.equalsIgnoreCase("Y")) {
					flagbody.setVisibility(View.VISIBLE);
					healthflag.setVisibility(View.VISIBLE);
				}
			} else {
				flagbody.setVisibility(View.VISIBLE);
				text.setVisibility(View.VISIBLE);
				optflag.setVisibility(View.VISIBLE);
				healthflag.setVisibility(View.GONE);	
				text.setText(products.get(groupPosition).get(childPosition)
						.get(POPTIONNAME));
				if (products.get(groupPosition).get(childPosition)
						.get(POPTIONNAME).contains("�ݾ״뺰")) {
					flagbody.setVisibility(View.GONE);
					text.setVisibility(View.GONE);
					optflag.setVisibility(View.GONE);
					healthflag.setVisibility(View.GONE);					
				}
			}

			DecimalFormat df = new DecimalFormat("#,##0");

			title.setText(products.get(groupPosition).get(childPosition)
					.get(PNAME));
			count.setText("���� : "
					+ products.get(groupPosition).get(childPosition)
							.get(PCOUNT) + "��");
			price.setText("���� : "
					+ df.format(Integer.valueOf(products.get(groupPosition)
							.get(childPosition).get(PPRICE))) + "��");

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
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.buy_list_group, null);
			}

			TextView title = (TextView) view.findViewById(R.id.buygroupname);
			TextView price = (TextView) view.findViewById(R.id.buygroupprice);
			ImageView state = (ImageView) view.findViewById(R.id.buygroupstate);
			TextView date = (TextView) view.findViewById(R.id.buygroupdate);

			DecimalFormat df = new DecimalFormat("#,##0");

			LinearLayout body = (LinearLayout) view
					.findViewById(R.id.buygiftbody);
			TextView text = (TextView) view.findViewById(R.id.buygifttext);

			if (map.get(groupPosition).get(OOPTION) == null
					|| map.get(groupPosition).get(OOPTION).equalsIgnoreCase("")) {
				body.setVisibility(View.GONE);
				text.setVisibility(View.GONE);
			} else {
				body.setVisibility(View.VISIBLE);
				text.setVisibility(View.VISIBLE);
				text.setText(map.get(groupPosition).get(OOPTION));
			}

			title.setText("�ֹ���ȣ : " + map.get(groupPosition).get(ONUM));
			price.setText("�� �ݾ� : "
					+ df.format(Integer.valueOf(map.get(groupPosition).get(
							OPRICE))) + "��");
			date.setText(map.get(groupPosition).get(ODATE));

			switch (Integer.parseInt(map.get(groupPosition).get(OSTATE))) {
			case 1:
				state.setVisibility(View.VISIBLE);
				state.setImageResource(R.drawable.order_btn_01);
				break;
			case 2:
				state.setVisibility(View.VISIBLE);
				state.setImageResource(R.drawable.order_btn_02);
				break;
			case 3:
				state.setVisibility(View.VISIBLE);
				state.setImageResource(R.drawable.order_btn_03);
				break;
			case 4:
				state.setVisibility(View.VISIBLE);
				state.setImageResource(R.drawable.order_btn_04);
				break;
			case 5:
				state.setVisibility(View.VISIBLE);
				state.setImageResource(R.drawable.order_btn_05);
				break;
			case 6:
				state.setVisibility(View.VISIBLE);
				state.setImageResource(R.drawable.order_btn_06);
				break;
			default:
				state.setVisibility(View.GONE);
			}
			return view;
		}

	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		// TODO Auto-generated method stub
		if (buyList.collapseGroup(groupPosition)) {
			return true;
		}

		for (int i = 0; i < map.size(); i++) {
			buyList.collapseGroup(i);
		}

		if (!buyList.expandGroup(groupPosition)) {
			buyList.collapseGroup(groupPosition);
		}
		return true;
	}
}
