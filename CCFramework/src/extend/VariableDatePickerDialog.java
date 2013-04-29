package extend;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;

public class VariableDatePickerDialog extends DatePickerDialog {

	private View yearPicker;
	private View monthPicker;
	private View dayPicker;

	public VariableDatePickerDialog(Context context,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		// TODO Auto-generated constructor stub
		try {
			Field yearField = DatePicker.class.getDeclaredField("mYearSpinner");
			yearField.setAccessible(true);
			yearPicker = (View) yearField.get(getDatePicker());

			Field monthField = DatePicker.class
					.getDeclaredField("mMonthSpinner");
			monthField.setAccessible(true);
			monthPicker = (View) monthField.get(getDatePicker());

			Field dayField = DatePicker.class.getDeclaredField("mDaySpinner");
			dayField.setAccessible(true);
			dayPicker = (View) dayField.get(getDatePicker());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		// TODO Auto-generated method stub
		getDatePicker().init(year, month, day, this);
	}

	public VariableDatePickerDialog(Context context, OnDateSetListener callback) {
		this(context, callback, Calendar.getInstance().get(Calendar.YEAR),
				Calendar.getInstance().get(Calendar.MONTH), Calendar
						.getInstance().get(Calendar.DAY_OF_MONTH));
	}

	public void setUseYear(boolean use) {
		yearPicker.setVisibility(use ? View.VISIBLE : View.GONE);
	}

	public void setUseMonth(boolean use) {
		monthPicker.setVisibility(use ? View.VISIBLE : View.GONE);
	}

	public void setUseDay(boolean use) {
		dayPicker.setVisibility(use ? View.VISIBLE : View.GONE);
	}

}
