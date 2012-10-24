package kr.co.drdesign.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.drdesign.client.controller.GroupController;
import kr.co.drdesign.client.service.GrpReceiver;
import kr.co.drdesign.util.GruviceUtillity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


// 2011-06-24 ����/Ż��, ����/���� �̷��κ� �� ������ ó�� �������.
public class GroupSettingActivity extends Activity {


	private ArrayList<Map<String,String>> grpList;
	private ArrayList<Map<String,String>> grpOrgList;

	private GrpReceiver grpReceive;
	private GruviceUtillity gUtil;
	private GroupController grpCtrl;

	private GroupHandler grpHandler;
	private GroupThread grpThread;

	private ListAdapter adapter;
	private ListView grpListView;
	private ProgressDialog grpdlg;
	
	private Drawable sBackGround;
	private String skinType;
	
	private Button grpCommitBtn;
	private Button grpAddBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grpmanage_list);
		
		grpCtrl = GroupController.getInstance(getApplicationContext());
		grpCommitBtn = (Button) findViewById(R.id.gmBtnDeleteGrp);
		grpAddBtn = (Button) findViewById(R.id.gmBtnAddGrp);
		grpOrgList = grpCtrl.getGroupList();
		gUtil = GruviceUtillity.getInstance(getApplicationContext());
		grpListView = (ListView) findViewById(R.id.gmLvGroups);
		grpReceive = new GrpReceiver(getApplicationContext(), gUtil.getClientId(), gUtil.getPassword());
		
		String[] SKIN_TYPES = GruviceUtillity.getInstance(getApplicationContext()).getSKIN_TYPES();
		skinType = GruviceUtillity.getInstance(getApplicationContext()).getSkinType();
		
		if( skinType.equalsIgnoreCase(SKIN_TYPES[0]) == true)
		{
			sBackGround = getResources().getDrawable(R.drawable.v1_ml_bg);
		}else  if( skinType.equalsIgnoreCase(SKIN_TYPES[1]) == true) 
		{
			sBackGround = getResources().getDrawable(R.drawable.v2_ml_bg);
		}
		grpListView.setBackgroundDrawable(sBackGround);
		
		grpAddBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				final Dialog dialog = new Dialog(GroupSettingActivity.this);
				dialog.setContentView(R.layout.gl_add_dialog);
				dialog.setTitle(R.string.gmBtnAddGrp);

				final EditText addGroupName = (EditText)dialog.findViewById(R.id.glAddGroupName);
				addGroupName.setHint("�����̸�");
				final EditText addGroupKName = (EditText)dialog.findViewById(R.id.glAddGroupKName);
				addGroupKName.setHint("�ѱ��̸�");
				final EditText addGroupDesc = (EditText)dialog.findViewById(R.id.glAddGroupDesc);
				addGroupDesc.setHint("�׷켳��");

				Button btnEditOK = (Button)dialog.findViewById(R.id.glBtnAddOK);
				btnEditOK.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						// �׷� ���� �ڵ� �� �ڸ�
						
						Map<String,String> map = new HashMap<String,String>();
						map.put(GroupController.GROUP_NAME, addGroupName.getText().toString());
						map.put(GroupController.GROUP_KNAME, addGroupKName.getText().toString());
						map.put(GroupController.GROUP_DESC, addGroupDesc.getText().toString());
						
						String resultText;
						
						if ( addGroupName.getText().toString().length() < 2 ||
								addGroupKName.getText().toString().length() < 2) {
							resultText = "�׷� �ѱ۸�� �������� �ʼ� �Է»����Դϴ�.\n2�ڸ� �̻� �Է����ּ���.";
						} else {
							if (addGroupDesc.getText().toString().length() < 2) {
								map.put(GroupController.GROUP_DESC, addGroupKName.getText().toString() + " �׷�");
								// �׷� ������ �Է����� ������ �⺻������ �׷� �̸�+ �׷��� description ���� �Էµȴ�.
							}
							boolean result = grpReceive.addGroup(map);
							if (result) {
								resultText = "�׷� ���� ��û�� �����Ǿ����ϴ�. ����ڰ� Ȯ�� �� �����ϸ� �߰��˴ϴ�.";
								// ��(��) ����ũ�� �̹� ����������Ӹ�
							} else {
								resultText = "�׷� ���� ��û�� �����Ͽ����ϴ�. �ٽ� �õ��� �ּ���.";
							}
						}
						
						
						
						
						// �׷� ���� �ڵ� ������ �ڸ�
						AlertDialog.Builder d = new AlertDialog.Builder(GroupSettingActivity.this);
						d.setTitle(R.string.gmBtnAddGrp);
						d.setMessage(resultText);
						d.setPositiveButton(R.string.txtDone, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog1, int which) {
								// TODO Auto-generated method stub
								dialog1.dismiss();
//								dialog.dismiss();
							}
						});
						d.show();
					}
				});

				Button btnEditCancel  = (Button)dialog.findViewById(R.id.glBtnAddCancel);
				btnEditCancel.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						dialog.dismiss();
					}
				});
				dialog.show();	
			}
		});
		grpCommitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		grpThread = new GroupThread();
		grpHandler = new GroupHandler();
		
		showLoadingDialog();
		grpThread.start();
	}

	// 2011-06-01 �׷����� �ε� ���̾�α�
	public void showLoadingDialog() {
		grpdlg = null;
		grpdlg = new ProgressDialog(this);
		grpdlg.setCancelable(true);
		grpdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		grpdlg.setMessage("������ ������ ��ٸ��� ���Դϴ�.");
		// �̺κ��� int���� ���ڰ��� �޴� �޼��尡 ����. �ϴ� int������ ���� ��ü�� ��������.
		grpdlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				grpHandler.sendEmptyMessage(0);
			}
		});
		grpdlg.show();
	}
	
	class GroupThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				grpList.clear();
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				grpList = new ArrayList<Map<String,String>>();
			}
			grpList = grpReceive.getAllGrplist(); // ���� ������� �и��Ұ�.
			grpHandler.sendEmptyMessage(0);
		}
		
		
	}
	
	class GroupHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0 : 
				grpdlg.dismiss();
				grpThread.interrupt();
				fillList();
				break;
			case 1 :
				break;
			default :
				break;
			}
		}
	}
	
	private void fillList()
	{
		Log.i("Gruvice", "Fill List.");

		adapter = new GroupListAdapter(
				getApplicationContext(),
				grpList,
				R.layout.grpmanage_item,
				new String[]{
						GroupController.GROUP_ID,
						GroupController.GROUP_NAME,
						GroupController.GROUP_KNAME
				},
				new int[]{ R.id.giCtvID, R.id.giTvName }
		);

		grpListView.setAdapter(adapter);
		grpListView.setOnItemClickListener(new ItemListener());
		grpListView.setOnItemLongClickListener(new ItemLongListener());
	}
	

	private boolean result;
	
	public boolean delGrpMem(final int gid) {
//		showLoadingDialog();
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0 : 
					Toast.makeText(getApplicationContext(), "�׷� Ż�� �����Ͽ����ϴ�.", Toast.LENGTH_SHORT);
					grpCtrl.delete(new String[] { Integer.toString(gid) });
					result = true;
					showLoadingDialog();
					grpThread.interrupt();
					grpThread = new GroupThread();
					grpThread.start();
					break;
				case 1 : 
					Toast.makeText(getApplicationContext(), "�׷� Ż�� �����Ͽ����ϴ�.", Toast.LENGTH_SHORT);
					result = false;
					break;
				}
			}
		};
		
		// ������ ����
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (grpReceive.delGrpMem(gid)) { // mid �� �������� �� ������ �ش�.
					handler.sendEmptyMessage(0);
					grpReceive.getGrplist();
				} else {
					handler.sendEmptyMessage(1);
				}
			}
		}).start();
		// �̰͵� ������� �и��ؾ� ��.
		// �ڵ�޽����� ������ �׷����, ���н� �� ����.
		// �۾� �Ϸ��� ����� �ڵ鷯�� �佺Ʈ �޽��� ���.
			
		return result;
	}
	
	public boolean addGrpMem(final int gid) {
//		showLoadingDialog();
		
		
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0 : 
					Toast.makeText(getApplicationContext(), "�׷� ���Կ� �����Ͽ����ϴ�.", Toast.LENGTH_SHORT).show();
					result = true;
					showLoadingDialog();
					grpThread.interrupt();
					grpThread = new GroupThread();
					grpThread.start();
					break;
				case 1 : 
					Toast.makeText(getApplicationContext(), "�׷� ���Կ� �����Ͽ����ϴ�.", Toast.LENGTH_SHORT).show();
					result = false;
					break;
				}
			}
		};
		
		// ������ ����
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (grpReceive.addGrpMem(gid)) { // mid �� �������� �� ������ �ش�.
					handler.sendEmptyMessage(0);
					grpReceive.getGrplist();
				} else {
					handler.sendEmptyMessage(1);
				}
			}
		}).start();
		return result;
	}

	class ItemLongListener implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view,
				final int position, long arg3) {
			// TODO Auto-generated method stub
			// ���Ե� �׷������� ���� �ľ�
			if (grpList.get(position).get("Registed") == "0") {
				// ���Ե� �׷��̸� �׷� ���� Ȯ��â ���
				AlertDialog.Builder dialog = new AlertDialog.Builder(GroupSettingActivity.this);
				dialog.setTitle("�׷� ����");
				dialog.setMessage(grpList.get(position).get(GroupController.GROUP_KNAME) + 
						"\n�ش� �׷��� �������� �����˴ϴ�. ����Ͻðڽ��ϱ�?");
				dialog.setPositiveButton(R.string.txtYES, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String resultText;
						if (grpReceive.removeGroup
								(Integer.valueOf(grpList.get(position)
										.get(GroupController.GROUP_ID)))) {
							resultText = "�׷� ������ �����߽��ϴ�.";
						} else {
							resultText = "�׷� ������ �����߽��ϴ�.";
						}
						
						AlertDialog.Builder d = new AlertDialog.Builder(GroupSettingActivity.this);
						d.setTitle("�׷� ����");
						d.setMessage(resultText);
						d.setPositiveButton(R.string.txtDone, null);
						d.show();						
					}
				});
				dialog.setNegativeButton(R.string.txtNO, null);
				dialog.show();
			}
			
			
			// Ȯ���� ������ �׷��� �����ϴ� ô �ϰ� ������ ���ο�û �帳�� ħ.
			return true;
		}
		
	}
	
	class ItemListener implements OnItemClickListener {
		TextView tvName;
		TextView tvDesc;		
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, final int position,
				long arg3) {
				// TODO Auto-generated method stub
			String registed;
			if (grpList.get(position).get("Registed") == "0") 
				registed = grpList.get(position).get(GroupController.GROUP_KNAME)
				+ "\n�̹� ���Ե� �׷��Դϴ�. Ż���Ͻðڽ��ϱ�?";
			else 
				registed = grpList.get(position).get(GroupController.GROUP_KNAME)
				+ "\n�� �׷쿡 �����Ͻðڽ��ϱ�?";
			

			tvName = (TextView) view.findViewById(R.id.gmTvName);
			tvDesc = (TextView) view.findViewById(R.id.gmTvDesc);
			
			Log.i("Gruvice", "position : " + Integer.toString(position));
			AlertDialog.Builder dialog = new AlertDialog.Builder(GroupSettingActivity.this)
			.setTitle(R.string.mnTxtNotice)
			.setMessage(registed)
			.setPositiveButton(R.string.txtOK, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if (grpList.get(position).get("Registed") == "0") {
						Log.i("Gruvice", "delGrpMem() : " + grpList.get(position).get(GroupController.GROUP_ID));
						if (delGrpMem(Integer.parseInt(grpList.get(position).get(GroupController.GROUP_ID)))) {
							Log.i("Gruvice", "position : " + Integer.toString(position));
							Log.i("Gruvice", "!!!!!!!");
							tvName.setTextColor(Color.GRAY);
							tvDesc.setTextColor(Color.GRAY);
							grpList.get(position).put("Registed", "1");
						}
					} // �׷� Ż��
					else {
						if (addGrpMem(Integer.parseInt(grpList.get(position).get(GroupController.GROUP_ID)))) {
							Log.i("Gruvice", "position : " + Integer.toString(position));
							Log.i("Gruvice", "!!!!!!!");
							tvName.setTextColor(Color.WHITE);
							tvDesc.setTextColor(Color.WHITE);
							grpList.get(position).put("Registed", "0");
						}
						Log.i("Gruvice", "addGrpMem() : " + grpList.get(position).get(GroupController.GROUP_ID));
					} // �׷� ����
				}
			})
			.setNegativeButton(R.string.txtNO, null);
			dialog.show();
		}
	}
	
	public class GroupListAdapter extends SimpleAdapter {
		
		RelativeLayout rl;
		TextView tvName;
		TextView tvDesc;
		
		public GroupListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
						String[] from, int[] to) 
		{
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			
			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.grpmanage_item, null);
			}

			rl = (RelativeLayout) view.findViewById(R.id.gmRL01);
			tvName = (TextView) view.findViewById(R.id.gmTvName);
			tvDesc = (TextView) view.findViewById(R.id.gmTvDesc);
			// ������ ��� �׷� ����� ������
			// ������ ��ϰ� ���Ե� ����� ��
			// ���ԵǾ��ִ� ���� ���ԵǾ����� ���� ��� �ؽ�Ʈ �Ӽ��� �ٸ��� �����Ѵ�.
			
			if (grpList == null) return null;
			for (int i = 0; i < grpOrgList.size(); i++) {
				if ( grpList.get(position).get(GroupController.GROUP_ID).equals(grpOrgList.get(i).get(GroupController.GROUP_ID))) {
					Log.i("Gruvice", "Already joined Group.");
					Map<String,String> map = grpList.get(position);
					tvName.setTextColor(Color.WHITE);
					tvDesc.setTextColor(Color.WHITE);
					map.put("Registed", "0");
					grpList.set(position, map);
					// add(index, object) �� index�� ��ü�� ������ �� �ڿ� ���ϹǷ� �뵵�� ����ġ ����.
					break; // �����׷��� ã���� for������ ��������.
				} else {
					Map<String,String> map = grpList.get(position);
					tvName.setTextColor(Color.GRAY);
					tvDesc.setTextColor(Color.GRAY);
					map.put("Registed", "1");
					grpList.set(position, map);
				}
			}
			Log.i("Gruvice", "locale : " + getResources().getConfiguration().locale.getDisplayLanguage());
			if (getResources().getConfiguration().locale.getDisplayLanguage().contains("��") || 
					getResources().getConfiguration().locale.getDisplayLanguage().contains("Korea")) {
				tvName.setText(grpList.get(position).get( GroupController.GROUP_KNAME ));
			} else {
				tvName.setText(grpList.get(position).get( GroupController.GROUP_NAME ));
			}
			tvDesc.setText(grpList.get(position).get( GroupController.GROUP_DESC ));
			
			// ������ �Ǿ� �ִ� �׷��̸� Ż��Ȯ��, ������ �Ǿ����� ���� �׷��̸� ����Ȯ��â ǥ��.
			// �ؽ�Ʈ �Ӽ��� �����Ѵ�.
			
			return view;
		}

	}
}