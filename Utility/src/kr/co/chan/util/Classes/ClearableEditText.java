package kr.co.chan.util.Classes;

import kr.co.chan.util.R;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class ClearableEditText extends RelativeLayout implements TextWatcher, OnClickListener {
	
	LayoutInflater mInflater;
	public EditText textBox;
	public Button clearBtn;

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
		mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.claearable_edittext, this, true);
		textBox = (EditText) findViewById(R.id.editText1);
		textBox.addTextChangedListener(this);
		clearBtn = (Button) findViewById(R.id.button1);
		clearBtn.setOnClickListener(this);
	}
	
	public void clear() {
		textBox.setText("");
		clearBtn.setVisibility(View.INVISIBLE);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
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
	
	public void setPrivateImeOptions(String option) {
		textBox.setPrivateImeOptions(option);
	}
	
	public void setImeOptions(int option) {
		textBox.setImeOptions(option);
	}


}
