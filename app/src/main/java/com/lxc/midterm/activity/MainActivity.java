package com.lxc.midterm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.lxc.midterm.R;
import com.lxc.midterm.RoleItemAdapter;
import com.lxc.midterm.domain.Person;
import com.lxc.midterm.tool.PersonTool;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

	private TextView search;
	private TextView edit;
	private TextView add;
	private TextView tv_to_good_rank;
	private RoleItemAdapter adapter;
	private RecyclerView recyclerView;
	private TextView beginGame;
	private int pull_times;		//记录上拉刷新的次数
	private List<Person> mPersons = new ArrayList<>();
	private InputMethodManager imm; //管理软键盘


	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 0x1:{
					List<Person>list = (List<Person>) msg.obj;
					mPersons.addAll(list);
					adapter.notifyDataSetChanged();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initItems();    //初始化任务列表
		imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

		adapter = new RoleItemAdapter(mPersons, this);
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
                intent.putExtra("person", mPersons.get(position));
                intent.putExtra("add",true);
                startActivityForResult(intent, position);
			}
		});


		recyclerView = findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(adapter);

		search = findViewById(R.id.home_search);
		edit = findViewById(R.id.home_edit);
		tv_to_good_rank = findViewById(R.id.tv_to_good_rank);
		tv_to_good_rank.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//进入排行榜
				startActivity(new Intent(MainActivity.this,RankActivity.class));
			}
		});

		beginGame = findViewById(R.id.begin_game);
		search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (edit.getVisibility() == View.INVISIBLE) {
					//隐藏排行榜
					tv_to_good_rank.setVisibility(View.INVISIBLE);
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
					tv_to_good_rank.setVisibility(View.VISIBLE);
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
		//下拉刷新
		pull_times  = 1;
		RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
		refreshLayout.setEnableRefresh(false);	//取消下拉刷新功能
		refreshLayout.setEnableAutoLoadmore(false);
		refreshLayout.setRefreshFooter(new ClassicsFooter(this)
												.setProgressResource(R.drawable.progress)
												.setArrowResource(R.drawable.arrow));
		refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
			@Override
			public void onLoadmore(RefreshLayout refreshlayout) {
				PersonTool.getTwentyPerson(handler,pull_times,null);
				pull_times++;
				refreshlayout.finishLoadmore();
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

		//开始游戏监听
		beginGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this,GameActivity.class));
			}
		});

		//添加人物监听
		add = findViewById(R.id.home_add);
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivityForResult(new Intent(MainActivity.this,AddActivity.class),1);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//删除、修改
		if(data != null){
			if (data.getBooleanExtra("isDelete",false)) {
				PersonTool.deletePerson(handler, mPersons.get(requestCode).getPerson_id());
				mPersons.clear();
				initItems();
				adapter.notifyDataSetChanged();
			} else if (data.getBooleanExtra("isEdit", false)) {
				Person p =(Person) data.getSerializableExtra("person");
				mPersons.set(requestCode, p);
				adapter.notifyDataSetChanged();
				// TODO: 2017/11/23 修改的方法还没给出

			}else if(data.getBooleanExtra("add", false)){
				if(resultCode == 2){
					//添加成功
					Person p =(Person) data.getSerializableExtra("add_person");
					mPersons.add(0,p);
					adapter.notifyDataSetChanged();
				}
			}
		}

	}

	private void initItems() {
		PersonTool.getTwentyPerson(handler,0,null);
	}

	private void doSearch(String str) {
		mPersons.clear();
		if(str == null || str.equals("")){
			//不带关键字的搜索
			PersonTool.getTwentyPerson(handler,0,null);
		}else {
			PersonTool.getTwentyPerson(handler,0,str);
		}
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
