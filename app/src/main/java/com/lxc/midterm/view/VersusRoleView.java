package com.lxc.midterm.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lxc.midterm.R;
import com.lxc.midterm.domain.Person;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 对战界面的人物卡片界面
 * Created by LaiXiancheng on 2017/11/23.
 * Email: lxc.sysu@qq.com
 */

public class VersusRoleView extends FrameLayout{
	TextView tvName;
	TextView tvFiled;
	TextView tvAbility;
	CircleImageView ivAvatar;
	//String name, filed, ability;
	Person person;
	Context mContext;
	String fileds[] = {"土","水","火","金","木"};


	public VersusRoleView(Context context) {
		this(context,null);
	}

	public VersusRoleView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.role_in_versus, this);
		tvName= (TextView) findViewById(R.id.role_name);
		tvFiled= (TextView) findViewById(R.id.role_filed);
		tvAbility= (TextView) findViewById(R.id.role_ability);
		ivAvatar = findViewById(R.id.iv_avatar);

	}


	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
		this.tvName.setText(person.getName());
		this.tvFiled.setText("五行:"+fileds[person.getPerson_field()-1]);
		this.tvAbility.setText("武力: "+person.getAbility());
		Glide.with(mContext).load(person.getHead_url()).into(ivAvatar);
	}

}
