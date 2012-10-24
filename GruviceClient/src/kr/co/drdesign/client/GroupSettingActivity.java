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


// 2011-06-24 가입/탈퇴, 생성/삭제 이런부분 다 스레드 처리 해줘야함.
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
				addGroupName.setHint("영문이름");
				final EditText addGroupKName = (EditText)dialog.findViewById(R.id.glAddGroupKName);
				addGroupKName.setHint("한글이름");
				final EditText addGroupDesc = (EditText)dialog.findViewById(R.id.glAddGroupDesc);
				addGroupDesc.setHint("그룹설명");

				Button btnEditOK = (Button)dialog.findViewById(R.id.glBtnAddOK);
				btnEditOK.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						// 그룹 생성 코드 들어갈 자리
						
						Map<String,String> map = new HashMap<String,String>();
						map.put(GroupController.GROUP_NAME, addGroupName.getText().toString());
						map.put(GroupController.GROUP_KNAME, addGroupKName.getText().toString());
						map.put(GroupController.GROUP_DESC, addGroupDesc.getText().toString());
						
						String resultText;
						
						if ( addGroupName.getText().toString().length() < 2 ||
								addGroupKName.getText().toString().length() < 2) {
							resultText = "그룹 한글명과 영문명은 필수 입력사항입니다.\n2자리 이상 입력해주세요.";
						} else {
							if (addGroupDesc.getText().toString().length() < 2) {
								map.put(GroupController.GROUP_DESC, addGroupKName.getText().toString() + " 그룹");
								// 그룹 설명을 입력하지 않을시 기본값으로 그룹 이름+ 그룹이 description 으로 입력된다.
							}
							boolean result = grpReceive.addGroup(map);
							if (result) {
								resultText = "그룹 생성 요청이 접수되었습니다. 담당자가 확인 후 승인하면 추가됩니다.";
								// 는(은) 훼이크고 이미 만들어졌어임마
							} else {
								resultText = "그룹 생성 요청이 실패하였습니다. 다시 시도해 주세요.";
							}
						}
						
						
						
						
						// 그룹 생성 코드 끝나는 자리
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

	// 2011-06-01 그룹정보 로딩 다이얼로그
	public void showLoadingDialog() {
		grpdlg = null;
		grpdlg = new ProgressDialog(this);
		grpdlg.setCancelable(true);
		grpdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		grpdlg.setMessage("서버의 응답을 기다리는 중입니다.");
		// 이부분은 int형의 인자값을 받는 메서드가 없음. 일단 int값으로 문자 객체를 만들어야함.
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
			grpList = grpReceive.getAllGrplist(); // 차후 스레드로 분리할것.
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
					Toast.makeText(getApplicationContext(), "그룹 탈퇴에 성공하였습니다.", Toast.LENGTH_SHORT);
					grpCtrl.delete(new String[] { Integer.toString(gid) });
					result = true;
					showLoadingDialog();
					grpThread.interrupt();
					grpThread = new GroupThread();
					grpThread.start();
					break;
				case 1 : 
					Toast.makeText(getApplicationContext(), "그룹 탈퇴에 실패하였습니다.", Toast.LENGTH_SHORT);
					result = false;
					break;
				}
			}
		};
		
		// 스레드 시작
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (grpReceive.delGrpMem(gid)) { // mid 는 서버에서 잘 생성해 준다.
					handler.sendEmptyMessage(0);
					grpReceive.getGrplist();
				} else {
					handler.sendEmptyMessage(1);
				}
			}
		}).start();
		// 이것두 스레드로 분리해야 함.
		// 핸들메시지로 성공시 그룹삭제, 실패시 걍 냅둠.
		// 작업 완료후 결과는 핸들러로 토스트 메시지 출력.
			
		return result;
	}
	
	public boolean addGrpMem(final int gid) {
//		showLoadingDialog();
		
		
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0 : 
					Toast.makeText(getApplicationContext(), "그룹 가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
					result = true;
					showLoadingDialog();
					grpThread.interrupt();
					grpThread = new GroupThread();
					grpThread.start();
					break;
				case 1 : 
					Toast.makeText(getApplicationContext(), "그룹 가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
					result = false;
					break;
				}
			}
		};
		
		// 스레드 시작
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (grpReceive.addGrpMem(gid)) { // mid 는 서버에서 잘 생성해 준다.
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
			// 가입된 그룹인지의 여부 파악
			if (grpList.get(position).get("Registed") == "0") {
				// 가입된 그룹이면 그룹 삭제 확인창 출력
				AlertDialog.Builder dialog = new AlertDialog.Builder(GroupSettingActivity.this);
				dialog.setTitle("그룹 삭제");
				dialog.setMessage(grpList.get(position).get(GroupController.GROUP_KNAME) + 
						"\n해당 그룹이 서버에서 삭제됩니다. 계속하시겠습니까?");
				dialog.setPositiveButton(R.string.txtYES, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String resultText;
						if (grpReceive.removeGroup
								(Integer.valueOf(grpList.get(position)
										.get(GroupController.GROUP_ID)))) {
							resultText = "그룹 삭제에 성공했습니다.";
						} else {
							resultText = "그룹 삭제에 실패했습니다.";
						}
						
						AlertDialog.Builder d = new AlertDialog.Builder(GroupSettingActivity.this);
						d.setTitle("그룹 삭제");
						d.setMessage(resultText);
						d.setPositiveButton(R.string.txtDone, null);
						d.show();						
					}
				});
				dialog.setNegativeButton(R.string.txtNO, null);
				dialog.show();
			}
			
			
			// 확인을 누르면 그룹을 삭제하는 척 하고 관리자 승인요청 드립을 침.
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
				+ "\n이미 가입된 그룹입니다. 탈퇴하시겠습니까?";
			else 
				registed = grpList.get(position).get(GroupController.GROUP_KNAME)
				+ "\n이 그룹에 가입하시겠습니까?";
			

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
					} // 그룹 탈퇴
					else {
						if (addGrpMem(Integer.parseInt(grpList.get(position).get(GroupController.GROUP_ID)))) {
							Log.i("Gruvice", "position : " + Integer.toString(position));
							Log.i("Gruvice", "!!!!!!!");
							tvName.setTextColor(Color.WHITE);
							tvDesc.setTextColor(Color.WHITE);
							grpList.get(position).put("Registed", "0");
						}
						Log.i("Gruvice", "addGrpMem() : " + grpList.get(position).get(GroupController.GROUP_ID));
					} // 그룹 가입
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
			// 서버의 모든 그룹 목록을 가져옴
			// 가져온 목록과 가입된 목록을 비교
			// 가입되어있는 경우와 가입되어있지 않은 경우 텍스트 속성을 다르게 설정한다.
			
			if (grpList == null) return null;
			for (int i = 0; i < grpOrgList.size(); i++) {
				if ( grpList.get(position).get(GroupController.GROUP_ID).equals(grpOrgList.get(i).get(GroupController.GROUP_ID))) {
					Log.i("Gruvice", "Already joined Group.");
					Map<String,String> map = grpList.get(position);
					tvName.setTextColor(Color.WHITE);
					tvDesc.setTextColor(Color.WHITE);
					map.put("Registed", "0");
					grpList.set(position, map);
					// add(index, object) 는 index에 객체가 있으면 그 뒤에 더하므로 용도가 적절치 않음.
					break; // 같은그룹을 찾으면 for문에서 빠져나옴.
				} else {
					Map<String,String> map = grpList.get(position);
					tvName.setTextColor(Color.GRAY);
					tvDesc.setTextColor(Color.GRAY);
					map.put("Registed", "1");
					grpList.set(position, map);
				}
			}
			Log.i("Gruvice", "locale : " + getResources().getConfiguration().locale.getDisplayLanguage());
			if (getResources().getConfiguration().locale.getDisplayLanguage().contains("한") || 
					getResources().getConfiguration().locale.getDisplayLanguage().contains("Korea")) {
				tvName.setText(grpList.get(position).get( GroupController.GROUP_KNAME ));
			} else {
				tvName.setText(grpList.get(position).get( GroupController.GROUP_NAME ));
			}
			tvDesc.setText(grpList.get(position).get( GroupController.GROUP_DESC ));
			
			// 가입이 되어 있는 그룹이면 탈퇴확인, 가입이 되어있지 않은 그룹이면 가입확인창 표시.
			// 텍스트 속성을 변경한다.
			
			return view;
		}

	}
}