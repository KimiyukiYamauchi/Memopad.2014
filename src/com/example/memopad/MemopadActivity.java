package com.example.memopad;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MemopadActivity extends Activity {
	
	boolean memoChanged = false;
	String fn;
	String encode = "UTF-8";
	
	String readFile(){
		String str ="";
		String l = null;
		
		if(fn!=null){
			BufferedReader br = null;
			try {
				br = new BufferedReader(
						new InputStreamReader(
								new FileInputStream(fn)));
				do{
					l = br.readLine();
					if(l != null){
						str = str + l + "\n";
					}
				}while(l!=null);
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	@Override
	protected void onStop() {
		super.onStop();
		EditText et = (EditText)findViewById(R.id.editText1);
		SharedPreferences pref
			= getSharedPreferences("MemoPrefs", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("memo", et.getText().toString());
		editor.putInt("cursor", Selection.getSelectionStart(et.getText()));
		editor.putBoolean("memoChanged", memoChanged);
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
		memoChanged = pref.getBoolean("memoChanaged", false);
		
		TextWatcher tw =new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				memoChanged = true;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		};
		et.addTextChangedListener(tw);
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
			if(memoChanged){
				saveMemo();
			}
			Intent i = new Intent(this, MemoList.class);
			startActivityForResult(i, 0);
			return true;
		}else if(id == R.id.menu_new){
			if(memoChanged){
				saveMemo();
			}
			et.setText("");
			return true;
		}else if(id == R.id.menu_import){
			if(Environment.MEDIA_MOUNTED.
					equals(Environment.getExternalStorageState())){
				if(memoChanged){
					saveMemo();
				}
				memoChanged = false;
				Intent i = new Intent(this, FilePicker.class);
				startActivityForResult(i, 1);
			}else{
				Toast toast = Toast.
					makeText(this, R.string.toast_no_external_storage, 1000);
				toast.show();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.v("OnActivityResult","" +requestCode);

		if(resultCode == RESULT_OK){
			if(requestCode == 0){
				EditText et = (EditText)findViewById(R.id.editText1);
				et.setText(data.getStringExtra("text"));
				memoChanged = false;
			}else if(requestCode == 1){
				fn = data.getStringExtra("fn");
				if(fn.length()>0){
					EditText et = (EditText)findViewById(R.id.editText1);
					et.setText(readFile());
					memoChanged = false;
				}
			}
		}
		return;
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
		memoChanged = false;
	}
}
