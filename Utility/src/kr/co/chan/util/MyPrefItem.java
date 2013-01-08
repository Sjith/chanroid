package kr.co.chan.util;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyPrefItem extends RelativeLayout {

	/**
	 * @uml.property  name="title"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private TextView title;
	/**
	 * @uml.property  name="summary"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private TextView summary;
	/**
	 * @uml.property  name="arrow"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private ImageView arrow;
	
	private void create(Context context) {
		int padding = (int) Util.Views.applyDimension(15, context);
		setPadding(padding, padding/3, padding, padding/3);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.bottomMargin = (int) Util.Views.applyDimension(1, context);
		setClickable(true);
		setGravity(Gravity.CENTER_VERTICAL);
		setLayoutParams(params);
		setBackgroundResource(R.drawable.pref_item_selector);
		title = new TextView(context);
//		title.setTextSize(Util.Views.applyScale(9, context));
		title.setTextSize(18);
		title.setText("pref1");
		summary = new TextView(context);
		summary.setVisibility(View.GONE);
		summary.setTextColor(Color.parseColor("#0099ff"));
		arrow = new ImageView(context);
		arrow.setImageResource(R.drawable.arrow);
		arrow.setId(2);
		
		LinearLayout left = new LinearLayout(context);
		left.setOrientation(LinearLayout.VERTICAL);
		left.addView(title);
		left.addView(summary);
		
		RelativeLayout.LayoutParams arrowparams = new RelativeLayout.LayoutParams((int) Util.Views.applyDimension(20, context), (int) Util.Views.applyDimension(30, context));	
		arrowparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		arrowparams.addRule(RelativeLayout.CENTER_VERTICAL);
		arrowparams.leftMargin = (int) Util.Views.applyDimension(10, context);
		addView(arrow, arrowparams);

		RelativeLayout.LayoutParams leftparams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		leftparams.addRule(RelativeLayout.LEFT_OF, arrow.getId());
		leftparams.addRule(RelativeLayout.CENTER_VERTICAL);
		addView(left, leftparams);
		
		
	}
	
	public MyPrefItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		create(context);
		// TODO Auto-generated constructor stub
	}

	public MyPrefItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		create(context);
		// TODO Auto-generated constructor stub
	}

	public MyPrefItem(Context context) {
		super(context);
		create(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setTitle(String title) {
		this.title.setText(title);
	}
	
	public void setSummary(String summary) {
		this.summary.setText(summary);
		this.summary.setVisibility(View.VISIBLE);
	}
	
	public void setArrow(int visible) {
		this.arrow.setVisibility(visible);
	}
	
	public void setMarginBottom(int margin) {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
		params.bottomMargin = margin;
		setLayoutParams(params);
	}

}
