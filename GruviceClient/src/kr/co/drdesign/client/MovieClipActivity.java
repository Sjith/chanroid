package kr.co.drdesign.client;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class MovieClipActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie_clip);
		
		// 2011-06-08 �����信�� ��Ʃ�� �������� �ٷ� ����� �� �����Ŷ� �����߳�?
		// �̰� ���ְ� �Ͻ��� ����Ʈ�� �������.
		Intent i = getIntent();
		String url = null;
		if( i != null ){
			url = i.getStringExtra("URL");

			VideoView vv = (VideoView)findViewById(R.id.VideoView01);
			MediaController mc = new MediaController(this);  

			Uri video = Uri.parse(url);  

			vv.setMediaController(mc);  
			vv.setVideoURI(video);
			
			if(!vv.isPlaying())vv.start();
		}
		else{
			finish();
		}
	}
}
