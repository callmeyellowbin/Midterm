package com.lxc.midterm.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.lxc.midterm.Person;
import com.lxc.midterm.R;
import com.lxc.midterm.RoleItemAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

	private TextView search;
	private TextView edit;
	private TextView add;
	private RoleItemAdapter adapter;
	private RecyclerView recyclerView;
	private TextView beginGame;
	private List<Person> mPersons = new ArrayList<>();
	private List<Person> mSearchResult = new ArrayList<>();
	private InputMethodManager imm; //管理软键盘

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initItems();    //初始化任务列表
		imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

		adapter = new RoleItemAdapter(mSearchResult, this);
		adapter.setOnItemClickListener(new RoleItemAdapter.onItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				//先收起软键盘
				if(imm != null) {
					imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
							0);
				}
				// 传递序列化对象给详情页
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("person", mSearchResult.get(position));
                startActivityForResult(intent, position);
			}
		});
		recyclerView = findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(adapter);

		search = findViewById(R.id.home_search);
		edit = findViewById(R.id.home_edit);
		search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (edit.getVisibility() == View.INVISIBLE) {
					search.setBackgroundResource(R.drawable.delete);
					edit.setVisibility(View.VISIBLE);
					edit.requestFocus();    //获取焦点
					//弹出软键盘
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				} else {
					search.setBackgroundResource(R.drawable.search);
					edit.setText("");
					//收起软键盘
					if(imm != null) {
						imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
								0);
					}
					edit.setVisibility(View.INVISIBLE);
				}
			}
		});
		//对文本输入进行监听，一旦输入改变就进行搜索
		edit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String contain = edit.getText().toString();
				doSearch(contain);
			}
		});
		//失去焦点后或者触摸输入框之外需要收起软键盘以完善用户体验
		edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					//收起软键盘
					if(imm != null) {
						imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
								0);
					}
				}
			}
		});
		setupUI(findViewById(R.id.constraint_layout));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data.getBooleanExtra("isDelete", false)) {
			mSearchResult.remove(requestCode);
			mPersons.remove(requestCode);
			adapter.notifyDataSetChanged();
		}
		else if (data.getBooleanExtra("isEdit", false)) {
			Person p = (Person) data.getSerializableExtra("person");
			mSearchResult.set(requestCode, p);
			adapter.notifyDataSetChanged();
			for (int i = 0; i < mPersons.size(); i++) {
				if (mPersons.get(i).getName().equals(p.getName())) {
					mPersons.set(i, p);
					break;
				}
			}
		}
	}

	private void initItems() {

		for (int i = 0; i < 10; i++) {
			Person item = new Person();
			item.setHead_url("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=294736936,926228072&fm=27&gp=0.jpg");
			Log.d("Output i", Integer.toString(i));
			item.setName(""+Integer.toString(i));
			item.setSex("男");
			item.setPerson_date("?-?");
			mPersons.add(item);
			mSearchResult.add(item);
		}
	}

	private void doSearch(String str) {
		mSearchResult.clear();
		for(int i = 0; i < mPersons.size(); i++) {
			if (mPersons.get(i).getName().contains(str)) {
				mSearchResult.add(mPersons.get(i));
			}
		}
		adapter.notifyDataSetChanged();
	}

	//给所有非输入框控件设置触摸监听，以收起软键盘
	private void setupUI(View view) {
		//Set up touch listener for non-text box views to hide keyboard.
		if(!(view instanceof EditText)) {
			view.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					if(imm != null) {
						imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
								0);
					}
					return false;
				}
			});
		}

		//If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupUI(innerView);
			}
		}
	}
}
