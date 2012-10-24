package kr.co.drdesign.util;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;



public class ScrollAbleImageView extends FrameLayout implements View.OnTouchListener {



	private ScrollAbleImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private HorizontalScrollView Horizontal;
	private ScrollView Vertical;
	private ImageView iv;
	private LayoutParams params;
	private Drawable d;
	
	protected int currentX = 0;
	protected int currentY = 0;
	
	private Context ctx;
	
	

	public ScrollAbleImageView(Context context, Drawable d) {
		super(context);
		ctx = context;
		this.d = d;
		setView();
		// TODO Auto-generated constructor stub
	}
	
	private void setView() {
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		Horizontal = new HorizontalScrollView(ctx);
		Vertical = new ScrollView(ctx);
		Horizontal.setLayoutParams(params);
		Vertical.setLayoutParams(params);
		iv = new ImageView(ctx);
		iv.setImageDrawable(d);
		Vertical.setOnTouchListener(this);
		Horizontal.setOnTouchListener(this);
		
		iv.setLayoutParams(params);
		
		Horizontal.addView(iv);
		Vertical.addView(Horizontal);
		this.addView(Vertical);
	}
	
	public void setImageDrawable(Drawable d) {
		this.d = d;
		iv.setImageDrawable(d);
	}
	
	public void setScaleType(ScaleType scale) {
		iv.setScaleType(scale);
	}
	
	public void setImageMatrix(Matrix m) {
		iv.setImageMatrix(m);
	}
	
	public void scrollBy(int x, int y)
    {
		Horizontal.scrollBy(x, 0);
		Vertical.scrollBy(0, y);    
    }
	
	public Drawable getDrawable() {
		return d;
	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.i("Gruvice", "FixAbleImageView onTouch()");
		switch (event.getAction()) {    
		case MotionEvent.ACTION_DOWN :
			this.currentX = (int)event.getRawX();
			this.currentY = (int)event.getRawY(); 
			break;
		case MotionEvent.ACTION_MOVE :
			int x2 = (int)event.getRawX();
			int y2 = (int)event.getRawY();
			this.scrollBy(currentX-x2, currentY-y2);
			this.currentX = x2;
			this.currentY = y2;
			break;
		case MotionEvent.ACTION_UP :
			break;
		default :
			this.currentX = (int)event.getRawX();
			this.currentY = (int)event.getRawY();  
			break;    
		}
			this.currentX = (int)event.getRawX();
			this.currentY = (int)event.getRawY();
			scrollBy(currentX, currentY);
			return false;		
	}
	
}