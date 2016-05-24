package ljt.myebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class DownLoadActivity extends Activity{
	private WebView myWebView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.downloadtxt);
		myWebView=(WebView)findViewById(R.id.mywebview1);
		
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setAppCacheEnabled(false);
		
		
		Intent intent1=getIntent();
		String uri=intent1.getStringExtra("uri");
		myWebView.loadUrl(uri);
		
	}
	

}
