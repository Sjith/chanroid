package kr.co.chan.util;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * 편의상 각 item들의 크기가 모두 같다고 가정
 * 각 item들의 크기가 다를 경우 엉뚱한 곳으로 스크롤 될 수 있음
 * @author chan-woo park
 *
 */
public class FastGallery extends HorizontalScrollView {

	OnPositionChangedListener callback;
	@SuppressWarnings("deprecation")
	GestureDetector gesture = new GestureDetector(new LinearGestureListener());
	LinearLayout layout;
	int space;
	int position;
	int scrollX;

	int itemCount;
	int width;
	int itemWidth;
	
	public FastGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setLayout(context);
		setSmoothScrollingEnabled(true);
		// TODO Auto-generated constructor stub
	}

	public FastGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		setLayout(context);
		setSmoothScrollingEnabled(true);
		// TODO Auto-generated constructor stub
	}

	public FastGallery(Context context) {
		super(context);
		setLayout(context);
		setSmoothScrollingEnabled(true);
		// TODO Auto-generated constructor stub
	}
	
	public void setOnPositionChangerListener(OnPositionChangedListener listen) {
		this.callback = listen;
	}
	
	public OnPositionChangedListener getOnPositionChangedListener() {
		return callback;
	}

	private void setLayout(Context ctx) {
		layout = new LinearLayout(ctx);
		layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		layout.setOrientation(LinearLayout.HORIZONTAL);
		super.addView(layout);
	}

	public LinearLayout getLayout() {
		return layout;
	}
	
	public int getCurrentPosition() {
		return position;
	}
	
	public int getItemCounts() {
		return layout.getChildCount();
	}
	
	/**
	 * 반드시 뷰를 다 세팅하고 나서 호출할것
	 * 중간에 동적으로 뷰를 추가해야 할 시
	 * 뷰를 추가한 후 다시 호출하는 것을 권장
	 * @param space
	 */
	public void setSpacing(int space) {
		this.space = space;
		for (int i = 1; i < layout.getChildCount() - 1; i++) {
			layout.getChildAt(i).setPadding(space, layout.getChildAt(i).getPaddingTop(), 
					space, layout.getChildAt(i).getPaddingBottom());
		}
	}

	public void addItem(View view) {
		layout.addView(view);
	}

	public void removeItem(View view) {
		layout.removeView(view);
	}

	public void removeItem(int index) {
		layout.removeViewAt(index);
	}

	public void removeItem(int start, int count) {
		layout.removeViews(start, count);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		itemCount = layout.getChildCount();
		width = getWidth() * itemCount;
		itemWidth = width / itemCount;
		
		if (gesture.onTouchEvent(ev)) {
			call();
			return true;
		} else {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_UP:
				fitScroll();
				call();
				return true;
			}
		}
		
		return super.onTouchEvent(ev);
	}
	
	private void call() {
		position = scrollX / itemWidth;
		callback.onPositionChanged(this, position);
	}
	
	public final boolean toNext() {
		if (position >= getItemCounts() - 1) return false;
		int nextPoint = itemWidth * (position + 1);
		if (smoothScrollToX(nextPoint) == 0) return false;
		return true;
	}
	
	public final boolean toPrev() {
		if (position <= 0) return false;
		int prevPoint = itemWidth * (position - 1);
		if (smoothScrollToX(prevPoint) == 0) return false;
		return true;
	}

	private void fitScroll() {
		
		if (getScrollX() < (itemWidth / 2)) {
			// 현재 좌표가 첫번째 아이템의 가운데보다 왼쪽에 있으면 맨 왼쪽으로 스크롤
			smoothScrollToX(0);
			return;
		} else if (getScrollX() > width - (itemWidth * 1.5)) {
			// 현재 좌표가 마지막 바로 전 아이템의 가운데보다 오른쪽에 있으면 맨 오른쪽으로 스크롤
			smoothScrollToX(width);
			return;
		}

		for (int i = 1; i < itemCount; i++) {
			// 현재 좌표를 가지고 돌면서 어디서 제일 가까운지 확인
			int toNext = getScrollX() - (itemWidth * i);
			// 현재 비교중인 자리에서 현재 좌표까지의 거리
			if (Math.abs(toNext) < itemWidth / 2) {
				// item 절반의 넓이보다 거리가 짧을때
				smoothScrollToX(itemWidth * i-1);
				return;
			}
		}
	}
	
	public final int smoothScrollToX(int x) {
		scrollX = x;
		
		final int movement = -(getScrollX() - x);
		
		final int interval = 20;
		// 핸들러 타이머 간격
		
		final int speed = 5;
		final int cell = (movement / interval) * speed;
		
		if (cell == 0) return 0;
		
		final Handler h = new Handler();
		Runnable r = new Runnable() {
			int count = 0;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				if (movement == 0) return;
				if (movement > 0 && count >= movement) return;
				if (movement < 0 && count <= movement) return;
				
				// 거리 좌표가 양수일때
				if (movement > 0 && count < movement) {
					if (count + cell <= movement) {
						scrollBy(cell, 0);
						h.postDelayed(this, interval);
						count = count + cell;
					} else {
						scrollBy(movement - count, 0);					
					}
				} else if (movement < 0 && count > movement) {
					// 거리 좌표가 음수일때
					if (count + cell >= movement) {
						scrollBy(cell, 0);
						h.postDelayed(this, interval);
						count = count + cell;						
					} else {
						scrollBy(movement - count, 0);
					}
				}
			}
		};
		h.post(r);
		setPosition(x);
		return movement;
	}
	
	private void setPosition(int x) {
		position = x / itemWidth;
	}
	
	public interface OnPositionChangedListener {
		public void onPositionChanged(FastGallery g, int position);
	}
	
	protected class LinearGestureListener extends GestureDetector.SimpleOnGestureListener {
		private final int SWIPE_MIN_DISTANCE = 50;
		private final int SWIPE_THRESHOLD_VELOCITY = 150;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if (position == layout.getChildCount() - 1) return false;
				return toNext();
			}
			// left to right swipe
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if (position == 0) return false;
				return toPrev();
			}
			return false;
		}
	}

}
