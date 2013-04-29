package com.fedorvlasov.lazylist;

public interface ImageLoaderCallback {
	public void onLoad(AbsImageLoader loader);
	public void onStart(AbsImageLoader loader);
	public void onError(AbsImageLoader loader, String message);
}
