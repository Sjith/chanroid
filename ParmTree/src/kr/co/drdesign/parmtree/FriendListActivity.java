package kr.co.drdesign.parmtree;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

public class FriendListActivity extends Activity {

	Button addBtn;
	ListView friendList;
	ArrayList<Map<String,String>> friendArray;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list);
		addBtn = (Button) findViewById(R.id.friendbutton1);
		friendList = (ListView) findViewById(R.id.friendlistView1);
		friendArray = new ArrayList<Map<String,String>>();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		this.getParent().onBackPressed();
	}

}
