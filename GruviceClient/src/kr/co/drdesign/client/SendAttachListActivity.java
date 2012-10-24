package kr.co.drdesign.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import kr.co.drdesign.client.controller.AttachController;
import kr.co.drdesign.util.GruviceUtillity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SendAttachListActivity extends Activity implements View.OnClickListener {

	
	
//	private Button salCommitBtn;
//	private Button salCancelBtn;
	
	private ArrayList<String> attList = new ArrayList<String>();
	private ListView salList;
	private Set<Integer> checkedSet = new TreeSet<Integer>();

	private Intent i;
	
	public static final String INDEX 			= "INDEX";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snm_attach_list);

		String skinType;
		String[] skinTypes;
		RelativeLayout snmRl02;
		
		// 스킨타입에 따라 배경 설정
		snmRl02 = (RelativeLayout) findViewById(R.id.snmRl02);
		skinTypes = GruviceUtillity.getInstance(getApplicationContext()).getSKIN_TYPES();
		skinType = GruviceUtillity.getInstance(getApplicationContext()).getSkinType();
		if( skinType.equalsIgnoreCase(skinTypes[0]) == true)
		{
			snmRl02.setBackgroundDrawable(getResources().getDrawable(R.drawable.v1_ml_bg));
		}else  
		{
			snmRl02.setBackgroundDrawable(getResources().getDrawable(R.drawable.v2_ml_bg));
		}
		

		salList = (ListView)findViewById(R.id.snmAttachListView);
		salList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		i = getIntent();
		try {
			setList();
		} catch(NullPointerException e) {
			AlertDialog.Builder d = new AlertDialog.Builder(SendAttachListActivity.this);
			d.setTitle(R.string.txtAlret);
			d.setMessage(R.string.altxtNoAttachFile);
			d.setPositiveButton(R.string.txtDone, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
				}
			});
		}

		for (int i = 0; i > attList.size(); i++) {
			Log.i("Gruvice", "############ : " + attList.get(i));
			if (attList.get(i).equals(AttachController.NO_CONTENTS)) {
				attList.remove(i);
			}
		}
		
		SimpleListAdapter attAdapter = new SimpleListAdapter(this);
		salList.setAdapter(attAdapter);
		
	}

	private void setList() throws NullPointerException {
		
		for (int a = 1; a < 11; a++) {
			try {
				if (!i.getStringExtra("image" + a).equals(AttachController.NO_CONTENTS)) {
					attList.add(i.getStringExtra("image" + a));
				}
			} catch(Exception e) {
//				Log.i("Gruvice", e.getMessage());
			}
		}
		
		if (!i.getStringExtra("file").equals(AttachController.NO_CONTENTS)) {
			attList.add(i.getStringExtra("file"));
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch ( v.getId() ) {
			case R.id.snmBtnAttachCommit :
				// 편집내용을 인텐트에 실어서 보냄.
				Intent i = new Intent();
				for (int j = 0; j < attList.size(); j++) {
					i.putExtra( "image" + (j+1) , attList.get(j));
				}
				if (this.i.getStringExtra("file") != null) {
					i.putExtra("file", attList.get(attList.size()-1));
				}
				setResult(RESULT_OK, i);
				finish();
			break;
			case R.id.snmBtnAttachCancel :
				setResult(RESULT_CANCELED);
				finish();
			break;
			case R.id.snmBtnAttachRemove :
				// 2011-05-13 강화된 for문. checkedSet에 있는 값들을 차례대로 j 에 대입시켜 돌린다.
				// 해당 인덱스의 값을 지운다.
				// 2011-07-06 목록 편집은 구현 완료. 받는 쪽을 구현해줘야 함.
				
				for (int j : checkedSet) {	
					if (!attList.isEmpty()) {
						Log.i("Gruvice", "attList.remove : " + j);
						if (attList.size() == j) {
							attList.remove(j-1);
						} else attList.remove(j);
					}
				}
				
				// 2011-05-13 동적으로 생성된 ArrayList를 편집/삭제하는 방법을 연구해봐야 한다.
				checkedSet = new TreeSet<Integer>();
				SimpleListAdapter attAdapter = new SimpleListAdapter(this);
				salList.setAdapter(attAdapter);
			break;
		}
	}

	// 2011-05-13 보내는 메시지 첨부파일 목록 어댑터.
	// 현재는 그냥 보는것만 가능하나, 차후 첨부파일 추가 또는 제거 기능을 구현해야 한다.
	class SimpleListAdapter extends ArrayAdapter<String> {
		
		private TextView salTv;
		private CheckedTextView salCtv;
		private ImageButton salIb;
		
		public SimpleListAdapter(Context context) {
			super(context, R.layout.snm_attach_item, attList);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.snm_attach_item, null);
			}

			salTv = (TextView) view.findViewById(R.id.snmAttName);
			salCtv = (CheckedTextView) view.findViewById(R.id.snmAttCtvItem);
			salIb = (ImageButton) view.findViewById(R.id.snmAttImage);
			

			final File file = new File(attList.get(position));
			
			MimeTypeMap mtm = MimeTypeMap.getSingleton();

			String fileExtension = file.getName().substring
			 	(file.getName().lastIndexOf(".") + 1, 
			 	file.getName().length()).toLowerCase();

			final String mimeType = mtm.getMimeTypeFromExtension(fileExtension); 

			if (mimeType.contains("image")) {
				salIb.setVisibility(View.VISIBLE);
				salIb.setBackgroundDrawable(Drawable.createFromPath(attList.get(position)));
			}
			
			salTv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// 2011-05-12 첨부파일 실행을 위한 리스너
					
					i = new Intent(getApplicationContext(), ImageViewActivity.class);
					for (int j = 0; j < attList.size(); j++) {
						i.putExtra("image" + (j+1), attList.get(j));
						i.putExtra("index", position);
					}
					try {
						startActivity(i);
					} catch (ActivityNotFoundException ane) {
						Toast.makeText(getApplicationContext(), 
								R.string.altxtNotSupportType, Toast.LENGTH_SHORT).show();
					}
				}
			});
			salIb.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// 2011-05-12 첨부파일 실행을 위한 리스너
					

					i = new Intent(getApplicationContext(), ImageViewActivity.class);
					for (int j = 0; j < attList.size(); j++) {
						i.putExtra("image" + (j+1), attList.get(j));
						i.putExtra("index", position);
					}
					try {
						startActivity(i);
					} catch (ActivityNotFoundException ane) {
						Toast.makeText(getApplicationContext(), 
								R.string.altxtNotSupportType, Toast.LENGTH_SHORT).show();
					}
				}
			});
			salCtv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					( (CheckedTextView)v ).toggle();
					if( ((CheckedTextView)v).isChecked() ){
						Log.i("Gruvice", "Checked : " + position);
						checkedSet.add(position);
					} else {
						Log.i("Gruvice", "UnChecked : " + position);
						checkedSet.remove(position);
					}
				}
			});
			
			String fileName[] = attList.get(position).split("/");
			salTv.setText(fileName[fileName.length-1]);
			return view;
		}
		
		
		
	}

}