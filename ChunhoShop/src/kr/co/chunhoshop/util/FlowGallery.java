package kr.co.chunhoshop.util;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

public class FlowGallery extends Gallery {
	private static Camera mCamera; 
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
		 setSpacing(space);  // child view 의 간격을 줄여 겹치는 듯한 효과를 준다
		 setLayoutParams(new LayoutParams(
				 LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT));
	}

	@Override
	public void setSpacing(int space) {
		this.space = space;
		super.setSpacing(space);
	}
	
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		
		final int mCenter =(getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
		final int childCenter = child.getLeft() + child.getWidth() / 2;
		final int childWidth = child.getWidth();
		
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);

		float rate = Math.abs((float)(mCenter - childCenter)/ childWidth);
		
		mCamera.save();
		final Matrix matrix = t.getMatrix();

		float zoomAmount = (float) (rate * 700.0); // 뒤에 깔리는 뷰들의 확대 배율을 지정한다.
		mCamera.translate(0.0f, 0.0f, zoomAmount); // 이 값을 건들면 전체적으로 child들의 위치가 변경된다.      
		
		
		mCamera.getMatrix(matrix);    
//		matrix.preTranslate(-(childWidth/2), -(childWidth/2));
		matrix.preTranslate(-(childWidth/2), -(childWidth/2));
		matrix.postTranslate((childWidth/2), (childWidth/2));
		mCamera.restore();
		
		return true;
    }
	
}
