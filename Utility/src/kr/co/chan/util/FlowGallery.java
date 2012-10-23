package kr.co.chan.util;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
public class FlowGallery extends Gallery {
	private Camera mCamera;
	private int space = -20;

	public FlowGallery(Context context) {
		this(context, null);
	}

	public FlowGallery(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FlowGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mCamera = new Camera();
		setSpacing(space);
		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
	}

	@Override
	public void setSpacing(int space) {
		this.space = space;
		super.setSpacing(space);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int position = getSelectedItemPosition();
		int SWIPE_MIN_DISTANCE = 50;
		int SWIPE_THRESHOLD_VELOCITY = 150;
		if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			if (position < getCount() - 1)
				setSelection(position + 1, true);
		} else if (e1.getX() > e2.getX()) {
			if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				if (position > 0)
					setSelection(position - 1, true);
		}
		return true;
	}

	protected boolean getChildStaticTransformation(View child, Transformation t) {

		final int mCenter = (getWidth() - getPaddingLeft() - getPaddingRight())
				/ 2 + getPaddingLeft();
		final int childCenter = child.getLeft() + child.getWidth() / 2;
		final int childWidth = child.getWidth();

		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);

		float rate = Math.abs((float) (mCenter - childCenter) / childWidth);

		mCamera.save();
		final Matrix matrix = t.getMatrix();

		float zoomAmount = (float) (rate * 700.0);
		mCamera.translate(0.0f, 0.0f, zoomAmount);

		mCamera.getMatrix(matrix);
		matrix.preTranslate(-(childWidth / 2), -(childWidth / 2));
		matrix.postTranslate((childWidth / 2), (childWidth / 2));
		mCamera.restore();

		return true;
	}

}
