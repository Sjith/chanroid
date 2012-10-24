/**
** 라이센스 따위 없지만 소스는 공개하지 않을테다 캬캬
**/

package kr.co.chunhoshop.util;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;


public class RotateView extends ImageView {

     GestureDetector mGestureDetector;
     OnDegreesChangeListener listen;
     OnClickListener click;
     Context ctx;
     float currentDegrees = 0.0f;
     float currentPoint = 0.0f;
     float decelator = 3.0f;
     float celldegrees = 45.0f;
     int count;
     int currentCount;
     boolean scrolling;
     boolean directon;
     boolean fleaking = false;

     public RotateView(Context context) {
          super(context);
          ctx = context;
          mGestureDetector = new GestureDetector(ctx, new RotateGestureListener());
          // TODO Auto-generated constructor stub
     }

     public RotateView(Context context, AttributeSet attrs) {
          super(context, attrs);
          ctx = context;
          mGestureDetector = new GestureDetector(ctx, new RotateGestureListener());
     }

     public RotateView(Context context, AttributeSet attrs, int defStyle) {
          super(context, attrs, defStyle);
          ctx = context;
          mGestureDetector = new GestureDetector(ctx, new RotateGestureListener());
     }

     /**
      * 각도가 변경되면 호출될 리스너 설정.
      *
      * @param listener
      */
     public void setOnDegreesChangedListener(OnDegreesChangeListener listener) {
          if (listener != null)
               listen = listener;
     }
    
     public void setOnClickListener(OnClickListener listen) {
          if (listen != null)
               this.click = listen;         
     }
    
     public void onClick(View v) {
          if (click != null)
               click.onClick(this);
     }

     /**
      * 낮을수록 스크롤시 회전 속도가 빨라진다.
      *
      * @param decelator
      */
     public void setAccelator(float decelator) {
          this.decelator = decelator;
     }
     
     public void setFleaking(boolean fleak) {
    	 fleaking = fleak;
     }

     /**
      * 회전판을 몇등분 할 것인지 결정
      *
      * @param count
      */
     public void setItemCount(int count) {
          if (count != 0) {
               this.count = count;
               celldegrees = 360 / count;
          }
     }

     public int getItemCount() {
          return (int) (360 / celldegrees);
     }

     public int getCurrentCount() {
          if (currentDegrees == 0)
               return 0;
          currentCount = (int) (currentDegrees / celldegrees);
          if (currentCount > count - 1) currentCount = currentCount % count;
          if (currentCount < 0) currentCount = currentCount + count;
          return currentCount;
     }

     public void setCurrentDegrees(float degrees) {
          // 인터페이스 호출
          currentDegrees = degrees;
          if (currentDegrees >= 0) {
               if (currentDegrees % celldegrees < celldegrees / 2) {
                    currentDegrees = currentDegrees
                              - (currentDegrees % celldegrees);
               } else if (currentDegrees % celldegrees > celldegrees / 2) {
                    currentDegrees = currentDegrees
                              + (celldegrees - (currentDegrees % celldegrees));
               }
          } else if (currentDegrees < 0) {
               if (currentDegrees % celldegrees < -(celldegrees / 2)) {
                    currentDegrees = currentDegrees
                              + (-celldegrees - (currentDegrees % celldegrees));
               } else if (currentDegrees % celldegrees > -(celldegrees / 2)) {
                    currentDegrees = currentDegrees
                              - (currentDegrees % celldegrees);
               }
          }
          fitRotate(false);
          if (listen != null) {
               listen.onDegreesChanged(currentDegrees);
          }
     }

     public float getCurrentDegrees() {
          return currentDegrees;
     }

     /*
      * (non-Javadoc)
      *
      * @see android.view.View#onTouchEvent(android.view.MotionEvent)
      */
     @Override
     public boolean onTouchEvent(MotionEvent event) {
          // TODO Auto-generated method stub
          if (mGestureDetector.onTouchEvent(event)) {
               return true;
          }
          if (event.getAction() == MotionEvent.ACTION_UP) {
               if (scrolling) {
                    fitRotate(false);
                    scrolling = false;
                    return true;
               } else
                    onClick(this);
                    return true;
          }
          return true;
     }

     class RotateGestureListener extends SimpleOnGestureListener {

          /*
           * (non-Javadoc)
           *
           * @see
           * android.view.GestureDetector.SimpleOnGestureListener#onScroll(android
           * .view.MotionEvent, android.view.MotionEvent, float, float)
           */
          @Override
          public boolean onScroll(MotionEvent e1, MotionEvent e2,
                    float distanceX, float distanceY) {

               scrolling = true;
               if (e2.getY() < getHeight() / 2) {
                    rotate(distanceX);
               } else if (e2.getY() >= getHeight() / 2) {
                    rotate(-distanceX);
               }
               if (e2.getX() < getWidth() / 2) {
                    rotate(-distanceY);
               } else if (e2.getX() >= getWidth() / 2) {
                    rotate(distanceY);
               }
               return true;
          }

          /*
           * (non-Javadoc)
           *
           * @see
           * android.view.GestureDetector.SimpleOnGestureListener#onFling(android
           * .view.MotionEvent, android.view.MotionEvent, float, float)
           */
          @Override
         public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                    float velocityY) {
//               int SWIPE_MIN_DISTANCE = 100;
//               int SWIPE_MAX_OFF_PATH = 250;
//               int SWIPE_THRESHOLD_VELOCITY = 300;
//
//               if (fleaking) {
//	               if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
//	                    return false;
//	               } else if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH) {
//	                    return false;
//	               }
//	
//	               if (e2.getY() >= getHeight() / 2) {
//	                    if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
//	                              && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//	                         fullRotate(-(celldegrees * 2)); // left to right
//	                         directon = true;
//	                    } else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
//	                              && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//	                         fullRotate(celldegrees * 2); // right to left
//	                         directon = false;
//	                    }
//	               } else if (e2.getY() < getHeight() / 2) {
//	                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
//	                              && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//	                         fullRotate(celldegrees * 2);
//	                         directon = true;
//	                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
//	                              && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//	                         fullRotate(-(celldegrees * 2));
//	                         directon = false;
//	                    }
//	               }
//	              
//	               if (Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY ||
//	                         Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//	                    if (e2.getX() < e1.getX() && e2.getY() > e1.getY() ) {
//	                         // case 1
//	                         // right to left
//	                         fullRotate(-(celldegrees * 2)); // left to right
//	                         directon = true;
//	                    } else if (e2.getX() > e1.getX() && e2.getY() > e1.getY()) {
//	                         // case 2
//	                         // left to right
//	                         fullRotate(celldegrees * 2); // right to left
//	                         directon = false;
//	                    } else if (e2.getX() < e1.getX() && e2.getY() < e1.getY()) {
//	                         // case 3
//	                         // right to left
//	                         fullRotate(-(celldegrees * 2)); // left to right
//	                         directon = true;
//	                    } else if (e2.getX() > e1.getX() && e2.getY() < e1.getY()) {
//	                         // case 4
//	                         // left to right
//	                         fullRotate(celldegrees * 2); // right to left
//	                         directon = false;
//	                    } else {
//	                         if (e2.getY() > e1.getY() && e1.getX() < getWidth() / 2) {
//	                              // case 5-1
//	                              // left to right
//	                              fullRotate(celldegrees * 2); // right to left
//	                              directon = false;
//	                         } else if (e2.getY() > e1.getY() && e1.getX() > getWidth() / 2) {
//	                              // case 5-2
//	                              // right to left
//	                              fullRotate(-(celldegrees * 2)); // left to right
//	                              directon = true;
//	                         } else if (e2.getX() < e1.getX() && e1.getY() > getHeight() / 2) {
//	                              // case 6-1
//	                              // right to left
//	                              fullRotate(-(celldegrees * 2)); // left to right
//	                              directon = true;
//	                         } else if (e2.getX() < e1.getX() && e1.getY() < getHeight() / 2) {
//	                              // case 6-2
//	                              // left to right
//	                              fullRotate(celldegrees * 2); // right to left
//	                              directon = false;
//	                         } else if (e2.getX() > e1.getX() && e1.getY() > getHeight() / 2) {
//	                              // case 7-1
//	                              // left to right
//	                              fullRotate(celldegrees * 2); // right to left
//	                              directon = false;
//	                         } else if (e2.getX() > e1.getX() && e1.getY() < getHeight() / 2){
//	                              // case 7-2
//	                              // right to left
//	                              fullRotate(-(celldegrees * 2)); // left to right
//	                              directon = true;
//	                         } else if (e2.getY() < e1.getY() && e1.getX() < getWidth() / 2) {
//	                              // case 8-1
//	                              // right to left
//	                              fullRotate(-(celldegrees * 2)); // left to right
//	                              directon = true;
//	                         } else if (e2.getY() < e1.getY() && e1.getX() > getWidth() / 2) {
//	                              // case 8-2
//	                              // left to right
//	                              fullRotate(celldegrees * 2); // right to left
//	                              directon = false;
//	                         }
//	                    }
//	               }
//               }
//
//               fitRotate(true);
//               scrolling = false;
//               return false;

          	return false;
//          }
          	
          }
     }

     public void fullRotate(float distance) {
          float degrees = -(distance / decelator);
          // 분모의 값을 낮추면 회전속도가 빨라진다.
          currentDegrees = currentDegrees + degrees;

          // 인터페이스 호출
          float fitdegrees = fixDegrees(currentDegrees);

          if (listen != null) {
               fitdegrees = Math.abs(fitdegrees % 360);
               listen.onDegreesChanged(fitdegrees);
          }
     }

     public void rotate(float distance) {

          int rotateWidth = getWidth() / 2;
          int rotateHeight = getHeight() / 2;

          float degrees = -(distance / decelator);
          // 분모의 값을 낮추면 회전속도가 빨라진다.
          currentDegrees = currentDegrees + degrees;

          Matrix m = new Matrix();
          m.setRotate(currentDegrees, rotateWidth, rotateHeight);
          setImageMatrix(m);

          // 인터페이스 호출
          float fitdegrees = fixDegrees(currentDegrees);
          if (listen != null) {
               listen.onDegreesChanged(fitdegrees);
          }
     }

     public void fitRotate(boolean isFling) {

          int rotateWidth = getWidth() / 2;
          int rotateHeight = getHeight() / 2;
          float fitdegrees = currentDegrees;
          float start = 0;
          float speed = count * 50;

          if (fitdegrees >= 0) {
               if (fitdegrees % celldegrees < celldegrees / 2) {
                    start = currentDegrees % celldegrees;
               } else if (fitdegrees % celldegrees > celldegrees / 2) {
                    start = -(celldegrees - currentDegrees % celldegrees);
               }
          } else if (fitdegrees < 0) {
               if (fitdegrees % celldegrees < -(celldegrees / 2)) {
                    start = -celldegrees - currentDegrees % celldegrees;
               } else if (fitdegrees % celldegrees > -(celldegrees / 2)) {
                    start = -(currentDegrees % celldegrees);
               }
          }
          fitdegrees = fixDegrees(fitdegrees);
          // 360도를 celldegrees도 구간으로 나누어서 가까운 쪽의 각도로 보정합니다.

          if (isFling) {
               speed = count * 200;
               if (start < 0) {
                    start = start - 360;
               } else if (start >= 0) {
                    start = start + 360;
               }

               if (directon) {
                    start = -Math.abs(start);
               } else {
                    start = Math.abs(start);
               }
          }

          RotateAnimation rotate = new RotateAnimation(-start, 0,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
          rotate.setDuration((int) speed);
          rotate.setInterpolator(AnimationUtils.loadInterpolator(ctx,
                    android.R.anim.decelerate_interpolator));
          startAnimation(rotate);

          Matrix m = new Matrix();
          m.setRotate(fitdegrees, rotateWidth, rotateHeight);
          setImageMatrix(m);
          // 실제로 보정된 각도로 이미지를 회전시킵니다.

          currentDegrees = fitdegrees;
          // 애니메이션이 끝나면 현재 각도에 보정된 각도를 대입합니다.
          if (listen != null) {
               fitdegrees = Math.abs(fitdegrees % 360);
               listen.onDegreesChanged(fitdegrees);
          }
     }
    
    

     float fixDegrees(float degrees) {
          if (degrees >= 0) {
               if (degrees % celldegrees < celldegrees / 2) {
                    degrees = currentDegrees - (currentDegrees % celldegrees);
               } else if (degrees % celldegrees > celldegrees / 2) {
                    degrees = currentDegrees
                              + (celldegrees - (currentDegrees % celldegrees));
               }
          } else if (degrees < 0) {
               if (degrees % celldegrees < -(celldegrees / 2)) {
                    degrees = currentDegrees
                              + (-celldegrees - (currentDegrees % celldegrees));
               } else if (degrees % celldegrees > -(celldegrees / 2)) {
                    degrees = currentDegrees - (currentDegrees % celldegrees);
               }
          }
          return degrees;
     }

     public interface OnDegreesChangeListener {
          public void onDegreesChanged(float degrees);
     }

}
