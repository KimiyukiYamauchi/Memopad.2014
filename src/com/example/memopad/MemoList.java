package com.example.memopad;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

public class MemoList extends ListActivity {
	static final String[] cols =
		{"title", "memo", android.provider.BaseColumns._ID,};
	MemoDBHelper memos;

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		memos = new MemoDBHelper(this);
		SQLiteDatabase db = memos.getWritableDatabase();
		Cursor cursor = db.query
				("memoDB", cols, "_ID="+String.valueOf(id)
				, null, null, null, null);
		int idx = cursor.getColumnIndex("memo");
		cursor.moveToFirst();
		Intent i = new Intent();
		i.putExtra("text", cursor.getString(idx));
		setResult(RESULT_OK, i);
		memos.close();
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.memolist);
		showMemos(getMemos());
	}
	
	private Cursor getMemos(){
		memos =  new MemoDBHelper(this);
		SQLiteDatabase db = memos.getReadableDatabase();
		Cursor cursor  = db.query("memoDB", cols, 
				null, null, null, null, null);
		return cursor;
	}
	
	private void showMemos(Cursor cursor){
		if(cursor!=null){
			String [] from = {"title"};
			int[] to = {android.R.id.text1};
			SimpleCursorAdapter adapter =
					new SimpleCursorAdapter(
							this, android.R.layout.simple_list_item_1,
							cursor, from, to, 0
							);
			setListAdapter(adapter);
		}
		memos.close();
	}
	
	

}
