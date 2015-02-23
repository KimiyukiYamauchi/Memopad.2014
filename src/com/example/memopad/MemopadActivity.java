package com.example.memopad;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Selection;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MemopadActivity extends Activity {

	@Override
	protected void onStop() {
		super.onStop();
		EditText et = (EditText)findViewById(R.id.editText1);
		SharedPreferences pref
			= getSharedPreferences("MemoPrefs", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("memo", et.getText().toString());
		editor.putInt("cursor", Selection.getSelectionStart(et.getText()));
		editor.commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		EditText et = (EditText)findViewById(R.id.editText1);
		SharedPreferences pref 
			= this.getSharedPreferences("MemoPrefs", MODE_PRIVATE);
		et.setText(pref.getString("memo", "no text"));
		et.setSelection(pref.getInt("cursor", 0));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.memopad, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	void saveMemo(){
		EditText et = (EditText)this.findViewById(R.id.editText1);
		String title;
		String memo = et.getText().toString();
		
		if(memo.trim().length()>0){
			if(memo.indexOf("\n") == -1){
				title = memo.substring(0, Math.min(memo.length(), 20));
			}else{
				title = memo.substring(0, Math.min(memo.length(), 20));
			}
			String ts =DateFormat.getDateTimeInstance().format(new Date());
			MemoDBHelper memos = new MemoDBHelper(this);
		}
	}
}
