package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public abstract class CCBaseView extends LinearLayout {
	
	private View contentView;
	
	public CCBaseView(Context context) {
		super(context);
		inflate();
	}
	
	public CCBaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inflate();
	}

	public CCBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate();
	}

	private void inflate() {
		contentView = inflate(getContext(), getLayoutId(), null);
		addView(contentView, getLayoutParams());
		allocViews();
	}

	public LayoutParams getLayoutParams() {
		return new LayoutParams(-1, -1);
	}
	
	public View getContentView() {
		return contentView;
	}

	public abstract int getLayoutId();

	public abstract void allocViews();

}
