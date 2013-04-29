package com.fedorvlasov.lazylist;

import android.content.Context;

public class ImageLoader extends AbsImageLoader {

	public ImageLoader(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getLoadAnimationResources() {
		// TODO Auto-generated method stub
		return R.anim.thumb_fade_in;
	}

	@Override
	public int getOutAnimationResources() {
		// TODO Auto-generated method stub
		return R.anim.thumb_fade_out;
	}
}
