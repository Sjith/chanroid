package extend;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RemoteViews.RemoteView;

@RemoteView
public class AnimatedImageView extends ImageView {
	private AnimationDrawable mAnim;
	private boolean mAttached;

	/**
	 * 기본 생성자
	 * 
	 * @param context
	 *            컨텍스트
	 */
	public AnimatedImageView(Context context) {
		super(context);
	}

	/**
	 * 기본 생성자
	 * 
	 * @param context
	 *            생성자
	 * @param attrs
	 *            속성들
	 */
	public AnimatedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 애니메이션을 변경하고 시작합니다.
	 */
	private void updateAnim() {
		Drawable drawable = getDrawable();
		if (mAttached && mAnim != null) {
			mAnim.stop();
		}
		if (drawable instanceof AnimationDrawable) {
			mAnim = (AnimationDrawable) drawable;
			if (mAttached) {
				mAnim.start();
			}
		} else {
			mAnim = null;
		}
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		updateAnim();
	}

	@Override
	public void setImageResource(int resid) {
		super.setImageDrawable(null);
		super.setImageResource(resid);
		updateAnim();
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mAnim != null) {
			mAnim.start();
		}
		mAttached = true;
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mAnim != null) {
			mAnim.stop();
		}
		mAttached = false;
	}

	@Override
	protected void finalize() throws Throwable {
		clearAnimation();
		super.finalize();
	}
}
