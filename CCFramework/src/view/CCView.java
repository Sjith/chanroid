package view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class CCView extends LinearLayout {

	private View contentView;

	public CCView(Context context) {
		this(context, null);
	}

	public CCView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	public CCView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate();
	}

	private void inflate() {
		contentView = inflate(getContext(), getLayoutId(), null);
		addView(contentView, getLayoutParam());
		allocViews();
	}

	public LayoutParams getLayoutParam() {
		return new LayoutParams(-1, -1);
	}

	public View getContentView() {
		return contentView;
	}

	public void changeFont(String fontFileName, TextView... tv) {
		for (TextView view : tv) {
			if (view != null)
				view.setTypeface(Typeface.createFromAsset(getContext()
						.getAssets(), fontFileName));
		}
	}

	public abstract int getLayoutId();

	public abstract void allocViews();

}
