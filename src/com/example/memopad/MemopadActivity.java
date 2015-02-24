package com.example.memopad;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		EditText et = (EditText)findViewById(R.id.editText1);
		int id = item.getItemId();
		if (id == R.id.menu_save) {
			saveMemo();
			return true;
		}else if(id == R.id.menu_open){
			Intent i = new Intent(this, MemoList.class);
			startActivityForResult(i, 0);
			return true;
		}else if(id == R.id.menu_new){
			et.setText("");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == 0){
			EditText et = (EditText)findViewById(R.id.editText1);
			et.setText(data.getStringExtra("text"));
			return;
		}
	}

	void saveMemo(){
		EditText et = (EditText)this.findViewById(R.id.editText1);
		String title;
		String memo = et.getText().toString();
		
		if(memo.trim().length()>0){
			if(memo.indexOf("\n") == -1){
				title = memo.substring(0, Math.min(memo.length(), 20));
			}else{
				title = memo.substring(0, Math.min(memo.indexOf("\n"), 20));
			}
			String ts =DateFormat.getDateTimeInstance().format(new Date());
			MemoDBHelper memos = new MemoDBHelper(this);
			SQLiteDatabase db = memos.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("title", title+"\n"+ts);
			values.put("memo", memo);
			db.insertOrThrow("memoDB", null, values);
			memos.close();
		}
	}
}
