package extend;

import iface.OnMatchSMSlistener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;

public abstract class SMSReceiver extends BroadcastReceiver {

	/**
	 * 자바 정규표현식 사용
	 * 
	 * @param pattern
	 */
	public SMSReceiver(Context ctx, String pattern) {
		regex = pattern;
		context = ctx;
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		context.registerReceiver(this, filter);
	}

	private Context context;
	private String regex;

	private OnMatchSMSlistener onMatchSMSlistener;

	/**
	 * 분석할 문자메시지로 인식할 조건을 설정
	 * 
	 * @return
	 */
	public abstract boolean checkCondition();

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if ("android.provider.Telephony.SMS_RECEIVED".equalsIgnoreCase(intent
				.getAction())) {
			Bundle bundle = intent.getExtras();
			Object[] pdus = (Object[]) bundle.get("pdus");
			SmsMessage[] msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				String message = msgs[i].getMessageBody();
				if (checkCondition()) {
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(message);
					if (matcher.find()) {
						if (onMatchSMSlistener != null)
							onMatchSMSlistener.onMatchSMS(matcher.group());
					}
				}
			}
			context.unregisterReceiver(this);
		}
	}

	public void setOnMatchSMSlistener(OnMatchSMSlistener onMatchSMSlistener) {
		this.onMatchSMSlistener = onMatchSMSlistener;
	}

}
