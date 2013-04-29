package extend;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.ccframework.R;

public class ClearableEditText extends RelativeLayout implements TextWatcher,
		OnClickListener {

	private EditText textBox;
	private ImageButton clearBtn;

	public ClearableEditText(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public ClearableEditText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}

	void init() {
		LayoutInflater mInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.claearable_edittext, this, true);
		clearBtn = (ImageButton) findViewById(R.id.btn_cleartext_clear);
		clearBtn.setOnClickListener(this);
		textBox = (EditText) findViewById(R.id.edit_cleartext_text);
		textBox.addTextChangedListener(this);
		textBox.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI
				| EditorInfo.IME_ACTION_SEARCH);
		textBox.setTextColor(Color.BLACK);
	}

	public void clear() {
		textBox.setText("");
		clearBtn.setVisibility(View.INVISIBLE);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if (!isEnabled())
			return;
		if (textBox.getText().length() > 0) {
			clearBtn.setVisibility(View.VISIBLE);
		} else {
			clearBtn.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		clear();
	}

	public void setTextSize(float size) {
		textBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
	}

	public void setMaxLength(int length) {

	}

	public void setFilters(InputFilter[] filters) {
		textBox.setFilters(filters);
	}

	public void setOnKeyListener(OnKeyListener l) {
		textBox.setOnKeyListener(l);
	}

	public void setTextColor(int color) {
		textBox.setTextColor(color);
	}

	public void addTextChangedListener(TextWatcher listener) {
		textBox.addTextChangedListener(listener);
	}

	public void setPassword(boolean flag) {
		if (flag)
			textBox.setTransformationMethod(new PasswordTransformationMethod());
	}

	public void setPrivateImeOptions(String option) {
		textBox.setPrivateImeOptions(option);
	}

	public void setImeOptions(int option) {
		textBox.setImeOptions(option);
	}

	public void setText(CharSequence cs) {
		textBox.setText(cs);
	}

	public void setClearIcon(int resId) {
		clearBtn.setBackgroundResource(resId);
	}

	public Editable getText() {
		return textBox.getText();
	}

	public void setBackgroundColor(int color) {
		textBox.setBackgroundColor(color);
	}

	public void setBackgroundResource(int resid) {
		textBox.setBackgroundResource(resid);
	}

	public void setCursorVisible(boolean visible) {
		textBox.setCursorVisible(visible);
	}

	public void setEms(int ems) {
		textBox.setEms(ems);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		textBox.setEnabled(enabled);
		clearBtn.setVisibility(enabled ? VISIBLE : GONE);
	}

	public void setGravity(int gravity) {
		textBox.setGravity(gravity);
	}

	public final void setHint(CharSequence hint) {
		textBox.setHint(hint);
	}

	public final void setHint(int resid) {
		textBox.setHint(resid);
	}

	public final void setHintTextColor(int color) {
		textBox.setHintTextColor(color);
	}

	public void setInputType(int type) {
		textBox.setInputType(type);
	}

	public void setMaxEms(int maxems) {
		textBox.setMaxEms(maxems);
	}

	public void setMaxLines(int maxlines) {
		textBox.setMaxLines(maxlines);
	}

	public void setSingleLine() {
		textBox.setSingleLine();
	}

	public void setSingleLine(boolean singleLine) {
		textBox.setSingleLine(singleLine);
	}

}
