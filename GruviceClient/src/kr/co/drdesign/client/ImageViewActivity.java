package kr.co.drdesign.client;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;
/**
 * 
 * @author brake-_-
 * 2011-06-15
 * ��Ͽ� �ִ� �̹������� �����ͼ� �����ִ� ��Ƽ��Ƽ.
 * (���� �׽�Ʈ �ܰ�. �̹��� ��ũ�Ѱ� �ø�ŷ�� ���ÿ� ���� �ʴ� ������ ����.
 *
 * 2011-07-06
 * ������ �������� �޸� ���̳� OOM�� ���� ������ �ִ�.
 * ������ �ѹ��� �� ����� ����, ���������� ����.
 *
 */
public class ImageViewActivity extends Activity {
	
	private LinearLayout ll01;
	private ViewFlipper imgVf;
	private GestureDetector mGesture;
	private ImageView iv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_view);
		ll01 = (LinearLayout) findViewById(R.id.imgll01);
		mGesture = new GestureDetector(getApplicationContext(), new LinearGestureListener());
		
		Intent intent = getIntent();
		imgVf = new ViewFlipper(this);
		imgVf.setScrollBarStyle(ScrollView.SCROLLBARS_INSIDE_OVERLAY);
		imgVf.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		if (intent != null) {
			for (int i = 0; i < intent.getExtras().size(); i++) {
//				BitmapDrawable b = (BitmapDrawable) BitmapDrawable.createFromPath(intent.getStringExtra("image" + (i+1)));
				Drawable d = Drawable.createFromPath(intent.getStringExtra("image" + (i+1)));
				iv = new ImageView(this);
				iv.setImageDrawable(d);
				iv.setScaleType(ScaleType.CENTER);
//				Matrix m = iv.getImageMatrix();
//				iv.setImageMatrix(m);
				
				imgVf.addView(iv);
			}
		}
		imgVf.removeViewAt(imgVf.getChildCount()-1);
		ll01.addView(imgVf);
		imgVf.setDisplayedChild(intent.getIntExtra("index", 0));
	}
	

	final static float STEP = 200;
	float mRatio = 1.0f;
	int mBaseDist;
	float mBaseRatio;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		if (event.getPointerCount() == 2) {
//			int action = event.getAction();
//			int pureaction = action & MotionEvent.ACTION_MASK;
//			if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
//				mBaseDist = getDistance(event);
//				mBaseRatio = mRatio;
//			} else {
//				float delta = (getDistance(event) - mBaseDist) / STEP;
//				float multi = (float)Math.pow(2, delta);
//				mRatio = Math.min(1024.0f, Math.max(0.1f, mBaseRatio * multi));
//
//				ImageView iv = (ImageView) imgVf.getCurrentView();
//				iv.setScaleType(ScaleType.MATRIX);
//				
//				Matrix m = new Matrix();
//				m.postScale(mRatio, mRatio);
//				iv.setImageMatrix(matrixTurning(m, iv));
//		        Log.i("Gruvice", "getWidth : " + iv.getWidth());
//			}
//			return true;
//		} else 
			if (mGesture.onTouchEvent(event)) {
				Log.i("Gruvice", "fliking");
				return true;
			}
			return false;
	
	}
		// ��Ƽ��ġ ��ġ���� ��������.
		// 2011-07-04 ������ �Ǿ����� �ϴ� ����. ��ġ�̺�Ʈ �浹

	int getDistance(MotionEvent event) {
		int dx = (int)(event.getX(0) - event.getX(1));
		int dy = (int)(event.getY(0) - event.getY(1));
		return (int)(Math.sqrt(dx * dx + dy * dy));
	}
	


    @SuppressWarnings("unused")
	private Matrix matrixTurning(Matrix matrix, ImageView view){
        // ��Ʈ���� ��

        Matrix savedMatrix2 = new Matrix();
        final int WIDTH = 0;
        final int HEIGHT = 1;
    	
 	   Log.i("Gruvice", "Matrix Tunning");
        float[] value = new float[9];
        matrix.getValues(value); // ������
        
        float[] savedValue = new float[9];
        savedMatrix2.getValues(savedValue); // �ٲܲ�

       // �� ũ��
        int width = view.getWidth();
        int height = view.getHeight();
        
        // �̹��� ũ��
        Drawable d = view.getDrawable();
        if (d == null)  return matrix;
        int imageWidth = d.getIntrinsicWidth();
        int imageHeight = d.getIntrinsicHeight();
        // drawable �� ���� ũ��.
        int scaleWidth = (int) (imageWidth * value[0]);
        int scaleHeight = (int) (imageHeight * value[4]);
        
        // �̹����� �ٱ����� ������ �ʵ���.
        if (value[2] < width - scaleWidth)    value[2] = width - scaleWidth;
        if (value[5] < height - scaleHeight)   value[5] = height - scaleHeight;
        
//        if (value[2] > 0.0f)   value[2] = 0.0f;
//        if (value[5] > 0.0f)   value[5] = 0.0f;
        
        // 10�� �̻� Ȯ�� ���� �ʵ���
        if (value[0] > 10.0f || value[4] > 10.0f){
        	// �����̰� �ڽð� �� ����.
            return matrix;
        }
        
        // ȭ�麸�� �۰� ��� ���� �ʵ���
        if (imageWidth > width || imageHeight > height){
            if (scaleWidth < width && scaleHeight < height){
                int target = WIDTH;
                if (imageWidth < imageHeight) target = HEIGHT;
                
                if (target == WIDTH) value[0] = value[4] = (float)width / imageWidth;
                if (target == HEIGHT) value[0] = value[4] = (float)height / imageHeight;
                
                scaleWidth = (int) (imageWidth * value[0]);
                scaleHeight = (int) (imageHeight * value[4]);
                
                if (scaleWidth > width) value[0] = value[4] = (float)width / imageWidth;
                if (scaleHeight > height) value[0] = value[4] = (float)height / imageHeight;
//            	return matrix;
            }
        }
        
        // �������� ���� ����� ���� ũ�⺸�� �۰� ���� �ʵ���
        else{
            if (value[0] < 1.0f)   value[0] = 1;
            if (value[4] < 1.0f)   value[4] = 1;
        }
        
        // �׸��� ��� ��ġ�ϵ��� �Ѵ�.
        scaleWidth = (int) (imageWidth * value[0]);
        scaleHeight = (int) (imageHeight * value[4]);
        if (scaleWidth < width){
            value[2] = (float) width / 2 - (float)scaleWidth / 2;
        }
        if (scaleHeight < height){
            value[5] = (float) height / 2 - (float)scaleHeight / 2;
        }

        for (int i = 0; i < value.length; i++) {
        	Log.i("Gruvice", "matrix value : " + Float.toString(value[i]));
        }
        matrix.setValues(value);
        savedMatrix2.set(matrix);
        return savedMatrix2;
    }


	// 2011-06-15 �ø�ŷ ��� ����.
	protected class LinearGestureListener extends GestureDetector.SimpleOnGestureListener {
		private static final int SWIPE_MIN_DISTANCE = 100;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.e("onFling", "onFling()");
			
			try {

			 //right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				imgVf.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_in));
				imgVf.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_out));
				if (imgVf.getDisplayedChild() != (imgVf.getChildCount() - 1)) imgVf.showNext();
				else Toast.makeText(getApplicationContext(), "������ �����Դϴ�.", Toast.LENGTH_SHORT).show();
				// viewFilpper �� �⺻������ �� �Ѱ��� �ȿ� �ֱ⶧���� addView�� �ϸ� ��� �ڷ� �з���.
				// ������ �並 �ϳ� �����ְ� ó���ؾ� �Ѵ�.
			}
			 //left to right swipe
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				imgVf.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_in));
				imgVf.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out));
				if (imgVf.getDisplayedChild() != 0) imgVf.showPrevious();
				else Toast.makeText(getApplicationContext(), "ù��° �����Դϴ�.", Toast.LENGTH_SHORT).show();
			}   
			} catch (NullPointerException npe) {
				npe.getMessage();
			}
			// �ø�ŷ�� �ִϸ��̼� ��������.
			return true;
		}

	}
	
	
	protected class PinchZoomListener implements View.OnTouchListener {
	    // ����� ����
	    
	    private Matrix matrix = new Matrix();
	    private Matrix savedMatrix = new Matrix();
	    private Matrix savedMatrix2 = new Matrix();
	    
	    private static final int NONE = 0;
	    private static final int DRAG = 1;
	    private static final int ZOOM = 2;
	    private int mode = NONE;
	    
	    private PointF start = new PointF();
	    private PointF mid = new PointF();
	    private float oldDist = 1f;
	    
	    private static final int WIDTH = 0;
	    private static final int HEIGHT = 1;
	    
	    
	    private ImageView imageView;
	   
	    public PinchZoomListener(View v) {
	    	imageView = (ImageView) v;
	    }
	    

	   /**
	     * �̹��� ��
	     */
	    public void setImagePit(){
	        
	        // ��Ʈ���� ��
	        float[] value = new float[9];
	        matrix.getValues(value);
	        
	        // �� ũ��
	        int width = imageView.getWidth();
	        int height = imageView.getHeight();
	        
	        
	        // �̹��� ũ��
	        Drawable d = imageView.getDrawable();
	        if (d == null)  return;
	       int imageWidth = d.getIntrinsicWidth();
	        int imageHeight = d.getIntrinsicHeight();
	        int scaleWidth = (int) (imageWidth * value[0]);
	        int scaleHeight = (int) (imageHeight * value[4]);
	        
	       // �̹����� �ٱ����� ������ �ʵ���.

	       value[2] = 0;
	       value[5] = 0;
	        
	        if (imageWidth > width || imageHeight > height){
	            int target = WIDTH;
	            if (imageWidth < imageHeight) target = HEIGHT;
	            
	            if (target == WIDTH) value[0] = value[4] = (float)width / imageWidth;
	            if (target == HEIGHT) value[0] = value[4] = (float)height / imageHeight;
	            
	            scaleWidth = (int) (imageWidth * value[0]);
	            scaleHeight = (int) (imageHeight * value[4]);
	            
	            if (scaleWidth > width) value[0] = value[4] = (float)width / imageWidth;
	            if (scaleHeight > height) value[0] = value[4] = (float)height / imageHeight;
	        }
	        
	        // �׸��� ��� ��ġ�ϵ��� �Ѵ�.
	        scaleWidth = (int) (imageWidth * value[0]);
	        scaleHeight = (int) (imageHeight * value[4]);
	        if (scaleWidth < width){
	            value[2] = (float) width / 2 - (float)scaleWidth / 2;
	        }
	        if (scaleHeight < height){
	            value[5] = (float) height / 2 - (float)scaleHeight / 2;
	        }
	        
	        matrix.setValues(value);
	        
	        imageView.setImageMatrix(matrix);
	    }
	    
	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
	        ImageView view = (ImageView) v;
	        Log.i("Gruvice", "onTouch()");
	       switch (event.getAction() & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN:
	        	Log.i("Gruvice", "ACTION_DOWN");
	           savedMatrix.set(matrix);
	           start.set(event.getX(), event.getY());
	           mode = DRAG;
	           break;
	        case MotionEvent.ACTION_POINTER_DOWN:
	        	Log.i("Gruvice", "ACTION_POINTER_DOWN");
	           oldDist = spacing(event);
	           if (oldDist > 10f) {
	              savedMatrix.set(matrix);
	              midPoint(mid, event);
	              mode = ZOOM;
	           }
	           break;
	       case MotionEvent.ACTION_UP:

	       case MotionEvent.ACTION_POINTER_UP:
	           mode = NONE;
	           break;
	        case MotionEvent.ACTION_MOVE:
	     	   Log.i("Gruvice", "ACTION_MOVE");
	           if (mode == DRAG) {
	        	   Log.i("Gruvice", "DRAG MODE");
	              matrix.set(savedMatrix);
	             matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);

	          }
	           else if (mode == ZOOM) {
	        	   Log.i("Gruvice", "ZOOM MODE");
	              float newDist = spacing(event);
	              if (newDist > 10f) {
	                 matrix.set(savedMatrix);
	                 float scale = newDist / oldDist;
	                 matrix.postScale(scale, scale, mid.x, mid.y);
	              }
	           }
	           break;
	        }

	       // ��Ʈ���� �� Ʃ��.
	        matrixTurning(matrix, view);
	        
	        view.setImageMatrix(matrix);
	        
	        return true;
	    }
	    
	    private float spacing(MotionEvent event) {
	       float x = event.getX(0) - event.getX(1);
	       float y = event.getY(0) - event.getY(1);
	       return FloatMath.sqrt(x * x + y * y);
	    }

	   private void midPoint(PointF point, MotionEvent event) {
	       float x = event.getX(0) + event.getX(1);
	       float y = event.getY(0) + event.getY(1);
	       point.set(x / 2, y / 2);
	    }
	    
	    private void matrixTurning(Matrix matrix, ImageView view){
	        // ��Ʈ���� ��
	 	   Log.i("Gruvice", "Matrix Tunning");
	        float[] value = new float[9];
	        matrix.getValues(value);
	        float[] savedValue = new float[9];
	        savedMatrix2.getValues(savedValue);
	       // �� ũ��
	        int width = view.getWidth();
	        int height = view.getHeight();
	        
	        // �̹��� ũ��
	        Drawable d = view.getDrawable();
	        if (d == null)  return;
	        int imageWidth = d.getIntrinsicWidth();
	        int imageHeight = d.getIntrinsicHeight();
	        int scaleWidth = (int) (imageWidth * value[0]);
	        int scaleHeight = (int) (imageHeight * value[4]);
	        
	        // �̹����� �ٱ����� ������ �ʵ���.
	        if (value[2] < width - scaleWidth)   value[2] = width - scaleWidth;
	        if (value[5] < height - scaleHeight)   value[5] = height - scaleHeight;
	        if (value[2] > 0)   value[2] = 0;
	        if (value[5] > 0)   value[5] = 0;
	        
	        // 10�� �̻� Ȯ�� ���� �ʵ���
//	        if (value[0] > 10 || value[4] > 10){
//	            value[0] = savedValue[0];
//	            value[4] = savedValue[4];
//	            value[2] = savedValue[2];
//	            value[5] = savedValue[5];
//	        }
	        
	        // ȭ�麸�� �۰� ��� ���� �ʵ���
	        if (imageWidth > width || imageHeight > height){
	            if (scaleWidth < width && scaleHeight < height){
	                int target = WIDTH;
	                if (imageWidth < imageHeight) target = HEIGHT;
	                
	                if (target == WIDTH) value[0] = value[4] = (float)width / imageWidth;
	                if (target == HEIGHT) value[0] = value[4] = (float)height / imageHeight;
	                
	                scaleWidth = (int) (imageWidth * value[0]);
	                scaleHeight = (int) (imageHeight * value[4]);
	                
	                if (scaleWidth > width) value[0] = value[4] = (float)width / imageWidth;
	                if (scaleHeight > height) value[0] = value[4] = (float)height / imageHeight;
	            }
	        }
	        
	        // �������� ���� ����� ���� ũ�⺸�� �۰� ���� �ʵ���
	        else{
	            if (value[0] < 1)   value[0] = 1;
	            if (value[4] < 1)   value[4] = 1;
	        }
	        
	        // �׸��� ��� ��ġ�ϵ��� �Ѵ�.
	        scaleWidth = (int) (imageWidth * value[0]);
	        scaleHeight = (int) (imageHeight * value[4]);
	        if (scaleWidth < width){
	            value[2] = (float) width / 2 - (float)scaleWidth / 2;
	        }
	        if (scaleHeight < height){
	            value[5] = (float) height / 2 - (float)scaleHeight / 2;
	        }
	        
	        matrix.setValues(value);
	        savedMatrix2.set(matrix);
	    }
	}
	
}
