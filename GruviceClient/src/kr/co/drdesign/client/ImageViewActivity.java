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
 * 목록에 있는 이미지들을 가져와서 보여주는 액티비티.
 * (현재 테스트 단계. 이미지 스크롤과 플리킹이 동시에 되지 않는 문제점 있음.
 *
 * 2011-07-06
 * 사진이 많아지면 메모리 릭이나 OOM이 생길 소지가 있다.
 * 사진을 한번에 다 띄우지 말고, 순차적으로 띄운다.
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
		// 멀티터치 핀치줌잉 구현예정.
		// 2011-07-04 구현은 되었으나 일단 보류. 터치이벤트 충돌

	int getDistance(MotionEvent event) {
		int dx = (int)(event.getX(0) - event.getX(1));
		int dy = (int)(event.getY(0) - event.getY(1));
		return (int)(Math.sqrt(dx * dx + dy * dy));
	}
	


    @SuppressWarnings("unused")
	private Matrix matrixTurning(Matrix matrix, ImageView view){
        // 매트릭스 값

        Matrix savedMatrix2 = new Matrix();
        final int WIDTH = 0;
        final int HEIGHT = 1;
    	
 	   Log.i("Gruvice", "Matrix Tunning");
        float[] value = new float[9];
        matrix.getValues(value); // 원래꺼
        
        float[] savedValue = new float[9];
        savedMatrix2.getValues(savedValue); // 바꿀꺼

       // 뷰 크기
        int width = view.getWidth();
        int height = view.getHeight();
        
        // 이미지 크기
        Drawable d = view.getDrawable();
        if (d == null)  return matrix;
        int imageWidth = d.getIntrinsicWidth();
        int imageHeight = d.getIntrinsicHeight();
        // drawable 의 본래 크기.
        int scaleWidth = (int) (imageWidth * value[0]);
        int scaleHeight = (int) (imageHeight * value[4]);
        
        // 이미지가 바깥으로 나가지 않도록.
        if (value[2] < width - scaleWidth)    value[2] = width - scaleWidth;
        if (value[5] < height - scaleHeight)   value[5] = height - scaleHeight;
        
//        if (value[2] > 0.0f)   value[2] = 0.0f;
//        if (value[5] > 0.0f)   value[5] = 0.0f;
        
        // 10배 이상 확대 하지 않도록
        if (value[0] > 10.0f || value[4] > 10.0f){
        	// 편집이고 자시고 걍 리턴.
            return matrix;
        }
        
        // 화면보다 작게 축소 하지 않도록
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
        
        // 원래부터 작은 얘들은 본래 크기보다 작게 하지 않도록
        else{
            if (value[0] < 1.0f)   value[0] = 1;
            if (value[4] < 1.0f)   value[4] = 1;
        }
        
        // 그리고 가운데 위치하도록 한다.
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


	// 2011-06-15 플리킹 기능 구현.
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
				else Toast.makeText(getApplicationContext(), "마지막 사진입니다.", Toast.LENGTH_SHORT).show();
				// viewFilpper 는 기본적으로 뷰 한개가 안에 있기때문에 addView를 하면 계속 뒤로 밀려남.
				// 마지막 뷰를 하나 지워주고 처리해야 한다.
			}
			 //left to right swipe
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				imgVf.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_in));
				imgVf.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out));
				if (imgVf.getDisplayedChild() != 0) imgVf.showPrevious();
				else Toast.makeText(getApplicationContext(), "첫번째 사진입니다.", Toast.LENGTH_SHORT).show();
			}   
			} catch (NullPointerException npe) {
				npe.getMessage();
			}
			// 플리킹시 애니메이션 구현예정.
			return true;
		}

	}
	
	
	protected class PinchZoomListener implements View.OnTouchListener {
	    // 디버깅 정보
	    
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
	     * 이미지 핏
	     */
	    public void setImagePit(){
	        
	        // 매트릭스 값
	        float[] value = new float[9];
	        matrix.getValues(value);
	        
	        // 뷰 크기
	        int width = imageView.getWidth();
	        int height = imageView.getHeight();
	        
	        
	        // 이미지 크기
	        Drawable d = imageView.getDrawable();
	        if (d == null)  return;
	       int imageWidth = d.getIntrinsicWidth();
	        int imageHeight = d.getIntrinsicHeight();
	        int scaleWidth = (int) (imageWidth * value[0]);
	        int scaleHeight = (int) (imageHeight * value[4]);
	        
	       // 이미지가 바깥으로 나가지 않도록.

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
	        
	        // 그리고 가운데 위치하도록 한다.
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

	       // 매트릭스 값 튜닝.
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
	        // 매트릭스 값
	 	   Log.i("Gruvice", "Matrix Tunning");
	        float[] value = new float[9];
	        matrix.getValues(value);
	        float[] savedValue = new float[9];
	        savedMatrix2.getValues(savedValue);
	       // 뷰 크기
	        int width = view.getWidth();
	        int height = view.getHeight();
	        
	        // 이미지 크기
	        Drawable d = view.getDrawable();
	        if (d == null)  return;
	        int imageWidth = d.getIntrinsicWidth();
	        int imageHeight = d.getIntrinsicHeight();
	        int scaleWidth = (int) (imageWidth * value[0]);
	        int scaleHeight = (int) (imageHeight * value[4]);
	        
	        // 이미지가 바깥으로 나가지 않도록.
	        if (value[2] < width - scaleWidth)   value[2] = width - scaleWidth;
	        if (value[5] < height - scaleHeight)   value[5] = height - scaleHeight;
	        if (value[2] > 0)   value[2] = 0;
	        if (value[5] > 0)   value[5] = 0;
	        
	        // 10배 이상 확대 하지 않도록
//	        if (value[0] > 10 || value[4] > 10){
//	            value[0] = savedValue[0];
//	            value[4] = savedValue[4];
//	            value[2] = savedValue[2];
//	            value[5] = savedValue[5];
//	        }
	        
	        // 화면보다 작게 축소 하지 않도록
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
	        
	        // 원래부터 작은 얘들은 본래 크기보다 작게 하지 않도록
	        else{
	            if (value[0] < 1)   value[0] = 1;
	            if (value[4] < 1)   value[4] = 1;
	        }
	        
	        // 그리고 가운데 위치하도록 한다.
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
