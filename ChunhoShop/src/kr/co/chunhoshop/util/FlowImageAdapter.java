package kr.co.chunhoshop.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class FlowImageAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private Context mContext;
	private ImageView[] iv;

	// 어떻게든 이미지 배열만 받아오면 될듯
	private List<Bitmap> mImages = new ArrayList<Bitmap>();

	private int cnt;

	public FlowImageAdapter(Context c, List<Bitmap> list) {
		mContext = c;
		mImages = list;
		cnt = mImages.size();
		iv = new ImageView[cnt];

		for (int i = 0; i < cnt; i++) {
			iv[i] = new ImageView(mContext);
			iv[i].setImageBitmap(mImages.get(i));
			iv[i].setScaleType(ImageView.ScaleType.FIT_XY);
			// iv[i].setLayoutParams(
			// new Gallery.LayoutParams(
			// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			iv[i].setLayoutParams(new Gallery.LayoutParams(300, 300));

		}
	}

	public int getCount() {
		return cnt;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return iv[position];
	}

}