package view;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

/**
 * ViewHolder와 Struct를 활용하여 설계가 용이하도록 재구성한 어댑터
 * 
 * @author CINEPOX
 * 
 * @param <T>
 */
public abstract class CCAdapter<T> extends SimpleAdapter {

	private Context mContext;
	private int mLayoutId;
	private List<T> mData;

	private CCAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		mContext = context;
		mLayoutId = resource;
		// TODO Auto-generated constructor stub
	}

	public CCAdapter(Context context, int resource, List<T> data) {
		this(context, null, resource, null, null);
		mData = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null)
			convertView = inflateItem();
		convertView = initItem(getItemData(position), convertView);
		return convertView;
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int getCount() {
		return mData.size();
	};

	private T getItemData(int location) {
		return mData.get(location);
	}

	private View inflateItem() {
		return View.inflate(getContext(), mLayoutId, null);
	}

	public Context getContext() {
		return mContext;
	}

	/**
	 * 뷰 내용 초기화 널체크 필요없이 뷰 선언하고 데이터 세팅만 해 주면 됨
	 * 뷰홀더 생성 이런건 상속받은 클래스에서 알아서..
	 * 
	 * @param data
	 * @param convertView
	 * @return
	 */
	public abstract View initItem(T data, View convertView);
	
}
