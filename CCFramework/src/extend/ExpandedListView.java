package extend;

import iface.OnItemClickListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * 스크롤뷰 안에선 리스트뷰 스크롤이 안되니 리스트뷰를 펼친것 처럼 걍 만들어 놓은것
 * 
 * @author CINEPOX
 * 
 */
public class ExpandedListView extends LinearLayout {
	private BaseAdapter adapter;
	private int viewPosition;
	private boolean isExpanded;
	private OnItemClickListener callback;

	public ExpandedListView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public ExpandedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}

	public ExpandedListView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public void setAdapter(BaseAdapter adapter) {
		removeAllViewsInLayout();
		this.adapter = adapter;
		if (this.adapter != null) {
			for (int i = 0; i < this.adapter.getCount(); i++) {
				View item = this.adapter.getView(i, null, this);
				item.setClickable(true);
				item.setOnClickListener(new OnExpandItemClickListener(i));
				addView(item);
			}
		}
	}

	public void setOnItemClickListener(OnItemClickListener l) {
		this.callback = l;
	}

	private void init() {
		setOrientation(VERTICAL);
	}
	
	public void collapse(int position) {
		viewPosition = position;
		for (int i = position; i < adapter.getCount(); i++) {
			getChildAt(i).setVisibility(GONE);
		}
		isExpanded = false;
	}

	public void expand() {
		for (int i = viewPosition; i < adapter.getCount(); i++) {
			getChildAt(i).setVisibility(VISIBLE);
		}
		isExpanded = true;
	}

	private class OnExpandItemClickListener implements OnClickListener {

		private int position;

		public OnExpandItemClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (callback == null)
				return;

			callback.OnItemClick(ExpandedListView.this, v, position);
		}

	}

}
