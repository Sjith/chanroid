package kr.co.chunhoshop;

import kr.co.chunhoshop.util.ParserTag;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TwitterLogin extends Activity implements ParserTag {
	// INTENT
	Intent mIntent;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_login);
		WebView webView = (WebView) findViewById(R.id.webView);

		// ȭ�� ��ȯ�� WebView���� ȭ�� ��ȯ�ϵ����Ѵ�.
		// �̷������� ������ ǥ�� �������� ���� ������.
		webView.setWebViewClient(new WebViewClient() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.webkit.WebViewClient#onPageStarted(android.webkit.WebView
			 * , java.lang.String, android.graphics.Bitmap)
			 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				// �α׾ƿ��� ó�����Ŀ��� �ٷ� Activity�� �����Ų��.
				// if (url != null && url.equals("http://mobile.twitter.com/"))
				// {
				// finish();
				// }
				// �α����� �����ϸ�
				// else
				if (url != null && url.startsWith(TWITTER_CALLBACK_URL)) {
					String[] urlParameters = url.split("\\?")[1].split("&");
					String oauthToken = "";
					String oauthVerifier = "";

					try {
						if (urlParameters[0].startsWith("oauth_token")) {
							oauthToken = urlParameters[0].split("=")[1];
						} else if (urlParameters[1].startsWith("oauth_token")) {
							oauthToken = urlParameters[1].split("=")[1];
						}

						if (urlParameters[0].startsWith("oauth_verifier")) {
							oauthVerifier = urlParameters[0].split("=")[1];
						} else if (urlParameters[1]
								.startsWith("oauth_verifier")) {
							oauthVerifier = urlParameters[1].split("=")[1];
						}

						mIntent.putExtra("oauth_token", oauthToken);
						mIntent.putExtra("oauth_verifier", oauthVerifier);

						setResult(RESULT_OK, mIntent);
						finish();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			public void onPageFinished(WebView view, String url) {
				// page �������� �Ϸ�Ǹ� ȣ���.
				super.onPageFinished(view, url);
			}
		});

		mIntent = getIntent();
		String url1 = mIntent.getStringExtra("auth_url");
		webView.loadUrl(url1);
	}
}