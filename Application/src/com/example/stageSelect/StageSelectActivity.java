package com.example.stageSelect;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.pigdig.PigDigActivity;
import com.example.pigdig.R;

public class StageSelectActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stage_select);
		
		/*
		 * TODO test用
		 */
		SharedPreferences preferences = getSharedPreferences("CONFIG", MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("STAGE", "Stage1");
		
		Intent intent = new Intent(getApplicationContext(), PigDigActivity.class);
//		intent.putExtra("STAGE", stage);
		startActivity(intent);
		/**
		 * TODO ここまで
		 */
		
		//ステージセレクトの取得
		ListView stageListView = (ListView)findViewById(R.id.stageListView);
		stageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//選択されたステージの取得
				String stage = (String)parent.getItemAtPosition(position);
				//選択されたステージ情報をプリファレンスに保存して，PigDigをスタート
				SharedPreferences preferences = getSharedPreferences("CONFIG", MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("STAGE", stage);
				
				Intent intent = new Intent(getApplicationContext(), PigDigActivity.class);
//				intent.putExtra("STAGE", stage);
				startActivity(intent);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
}
