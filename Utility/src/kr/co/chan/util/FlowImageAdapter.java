package kr.co.chan.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class FlowImageAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private Context mContext;

	private List<Bitmap> mImages = new ArrayList<Bitmap>();

	public FlowImageAdapter(Context c, List<Bitmap> list) {
		mContext = c;
		mImages = list;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ImageView iv = new ImageView(mContext);
		iv.setImageBitmap(mImages.get(position));
		iv.setScaleType(ScaleType.FIT_XY);
		
		return iv;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mImages.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mImages.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

}