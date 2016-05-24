package ljt.myebook;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class MyDialog extends Dialog{
	private Context context;
	private RadioButton qidianRB;
	private RadioButton xiaoxiangRB;
	private RadioButton hongxiuRB;
	private RadioButton jinjiangRB;
	private Button okBT;
	private Button cancelBT;
	private String addressUri="http://3g.qidian.com";
	public MyDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	public MyDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.setContentView(R.layout.mydialog);
		super.onCreate(savedInstanceState);
		findView();
		okBT.setOnClickListener(myOnClickListener);
		cancelBT.setOnClickListener(myOnClickListener);
		
	}
	private void findView(){
		qidianRB=(RadioButton)findViewById(R.id.qidianRB);
		xiaoxiangRB=(RadioButton)findViewById(R.id.xiaoxiangRB);
		hongxiuRB=(RadioButton)findViewById(R.id.hongxiuRB);
		jinjiangRB=(RadioButton)findViewById(R.id.jinjiangRB);
		okBT=(Button)findViewById(R.id.dialog_button_ok);
		cancelBT=(Button)findViewById(R.id.dialog_button_cancel);
	}
	
	private android.view.View.OnClickListener myOnClickListener=new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.dialog_button_ok:{
				addressUri=getAddressUri();
				System.out.println(addressUri);
				Intent intent=new Intent(context,DownLoadActivity.class);
				intent.putExtra("uri", addressUri);
				context.startActivity(intent);
				MyDialog.this.dismiss();
				break;
			}
			case R.id.dialog_button_cancel:{
				System.out.println("dialog has cancel");
				MyDialog.this.dismiss();
				break;
			}
			}
			
		}
	};
	private String getAddressUri(){
		String uri="";
		if(qidianRB.isChecked()){
			uri="http://3g.qidian.com";
		}else if(xiaoxiangRB.isChecked()){
			uri="http://wap.feiku.com";
		}else if(hongxiuRB.isChecked()){
			uri="http://wap.hongxiu.com";
		}else if(jinjiangRB.isChecked()){
			uri="http://m.jjwxc.com";
		}
		return uri;
	}

}
