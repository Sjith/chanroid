package extend;

import java.util.ArrayList;

import utils.DisplayUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

/**
 * how to use
 * 
 * 1. Segment 배열 생성(ArrayList) (들어갈 뷰, 식별값 스트링)
 * 
 * 2. setSegments에 1을 넣는다.
 * 
 * 3. setOnSegmentChangedListener
 * 
 * 4. 탭처럼 쓴다.
 * 
 * 
 * @author CINEPOX
 * 
 */
public class SegmentLayout extends LinearLayout {

	private OnSegmentChangedListener segmentCallback;

	private int segmentMargin = 0;
	
	private OnClickListener emptyListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public SegmentLayout(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SegmentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}

	public SegmentLayout(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public void setOnSegmentChangedListener(OnSegmentChangedListener l) {
		segmentCallback = l;
	}

	private void init() {
		setOrientation(HORIZONTAL);
		setOnClickListener(emptyListener);
	}

	private void initSelectedState() {
		for (int i = 0; i < getChildCount(); i++) {
			((CheckedTextView) getChildAt(i)).setChecked(false);
		}
	}

	public void setCurrentSegment(int index) {
		initSelectedState();
		if (index > -1)
			((CheckedTextView) getChildAt(index)).setChecked(true);
	}

	/**
	 * 배열 초기화시 인덱스, 리스너 자동할당 (기존 뷰에 정의된 클릭 리스너는 해제됨)
	 * 
	 * @param segments
	 */
	public void setSegments(ArrayList<Segment> segments) {
		removeAllViewsInLayout();
		for (int i = 0; i < segments.size(); i++) {
			Segment s = segments.get(i);
			s.getView()
					.setOnClickListener(new SegmentClickListener(s.getTag()));
			if (s.getLayoutParams() != null)
				addView(s.getView(), s.getLayoutParams());
			else
				addView(s.getView());
		}

		if (segmentCallback != null)
			segmentCallback.onSegmentChanged(this, segments.get(0).getTag());
		initSelectedState();
		setCurrentSegment(0);
		setSegmentsMargin(segmentMargin);
	}

	public void setSegmentsMargin(int margin) {
		segmentMargin = margin;
		View v = null;
		LinearLayout.LayoutParams param = null;
		for (int i = 0; i < getChildCount(); i++) {
			v = getChildAt(i);
			param = (LayoutParams) v.getLayoutParams();
			param.rightMargin = (int) DisplayUtils.applyDimension(margin,
					getContext());
			v.setLayoutParams(param);
		}

		if (param != null && v != null) {
			param.rightMargin = 0;
			v.setLayoutParams(param);
		}
	}

	private class SegmentClickListener implements OnClickListener {

		private String tag;

		private SegmentClickListener(String tag) {
			this.tag = tag;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (segmentCallback != null)
				segmentCallback.onSegmentChanged(SegmentLayout.this, tag);
			initSelectedState();
			((CheckedTextView) v).setChecked(true);
		}

	}

	public static class Segment {

		private CheckedTextView view;
		private String tag;
		private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				-2, -2);

		public CheckedTextView getView() {
			return view;
		}

		private String getTag() {
			return tag;
		}

		public void show() {
			view.setVisibility(VISIBLE);
		}

		public void hide() {
			view.setVisibility(GONE);
		}

		public LayoutParams getLayoutParams() {
			return params;
		}

		public Segment setLayoutParams(LayoutParams params) {
			this.params = params;
			return this;
		}

		private Segment(CheckedTextView v, String tag) {
			view = v;
			this.tag = tag;
		}

		public static Segment newSegment(CheckedTextView v, String tag) {
			return new Segment(v, tag);
		}
	}

	public interface OnSegmentChangedListener {
		public void onSegmentChanged(SegmentLayout segment, String tag);
	}

}
