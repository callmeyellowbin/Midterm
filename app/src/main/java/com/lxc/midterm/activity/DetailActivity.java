package com.lxc.midterm.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lxc.midterm.Person;
import com.lxc.midterm.R;

public class DetailActivity extends AppCompatActivity {

    private EditText name;
    private ImageView head;
    private EditText person_date;
    private EditText sex;
    private EditText country;
    private EditText hometown;
    private EditText description;
    private EditText second_name;
    private Person person;
    private RelativeLayout first_bar;
    private RelativeLayout second_bar;
    private boolean isDelete = false;
    private boolean isEdit = false;
    private String originName;
    private String originSecondName;
    private String originDate;
    private String originSex;
    private String originCountry;
    private String originHometown;
    private String originDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        person = (Person) getIntent().getSerializableExtra("person");
        name = (EditText) findViewById(R.id.name);
        second_name = (EditText) findViewById(R.id.second_name);
        head = (ImageView) findViewById(R.id.head);
        person_date = (EditText) findViewById(R.id.person_date);
        sex = (EditText) findViewById(R.id.sex);
        country = (EditText) findViewById(R.id.country);
        hometown = (EditText) findViewById(R.id.hometown);
        description = (EditText) findViewById(R.id.description);
        first_bar = (RelativeLayout) findViewById(R.id.first_bar);
        second_bar = (RelativeLayout) findViewById(R.id.second_bar);
        //未按编辑键不可编辑
        name.setInputType(InputType.TYPE_NULL);
        second_name.setInputType(InputType.TYPE_NULL);
        person_date.setInputType(InputType.TYPE_NULL);
        sex.setInputType(InputType.TYPE_NULL);
        country.setInputType(InputType.TYPE_NULL);
        hometown.setInputType(InputType.TYPE_NULL);
        description.setInputType(InputType.TYPE_NULL);
        InitListener();
        InitProperty(person);
    }

    private void InitProperty(Person person) {
        name.setText(person.getName());
        second_name.setText(person.getSecond_name());
        person_date.setText(person.getPerson_date());
        sex.setText(person.getSex());
        country.setText(person.getCountry());
        hometown.setText(person.getHometown());
        description.setText(person.getDescription());
        Glide.with(DetailActivity.this).load(person.getHead_url()).into(head);
        setOrigin();
    }

    private void setOrigin() {
        originName = name.getText().toString();
        originSecondName = second_name.getText().toString();
        originDate = person_date.getText().toString();
        originSex = sex.getText().toString();
        originCountry = country.getText().toString();
        originHometown = hometown.getText().toString();
        originDescription = description.getText().toString();
    }
    private void InitListener() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回
                finish();
            }
        });
        findViewById(R.id.photo_bt_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除武将
                AlertDialog.Builder dialog = new AlertDialog.Builder(DetailActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("确定要删除这个武将吗?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isDelete = true;
                        finish();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
            }
        });
        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //可编辑
                first_bar.setVisibility(View.GONE);
                second_bar.setVisibility(View.VISIBLE);
                name.setInputType(InputType.TYPE_CLASS_TEXT);
                second_name.setInputType(InputType.TYPE_CLASS_TEXT);
                person_date.setInputType(InputType.TYPE_CLASS_TEXT);
                sex.setInputType(InputType.TYPE_CLASS_TEXT);
                country.setInputType(InputType.TYPE_CLASS_TEXT);
                hometown.setInputType(InputType.TYPE_CLASS_TEXT);
                description.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //编辑完成
                AlertDialog.Builder dialog = new AlertDialog.Builder(DetailActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("保存修改?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isEdit = true;
                        second_bar.setVisibility(View.GONE);
                        first_bar.setVisibility(View.VISIBLE);
                        setOrigin();
                        clearFocus();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            //取消编辑
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DetailActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("放弃修改?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        second_bar.setVisibility(View.GONE);
                        first_bar.setVisibility(View.VISIBLE);
                        name.setText(originName);
                        second_name.setText(originSecondName);
                        sex.setText(originSex);
                        person_date.setText(originDate);
                        country.setText(originCountry);
                        hometown.setText(originHometown);
                        description.setText(originDescription);
                        clearFocus();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
            }
        });
    }


    public void clearFocus() {
//        text.clearFocus();
        name.setInputType(InputType.TYPE_NULL);
        second_name.setInputType(InputType.TYPE_NULL);
        person_date.setInputType(InputType.TYPE_NULL);
        sex.setInputType(InputType.TYPE_NULL);
        country.setInputType(InputType.TYPE_NULL);
        hometown.setInputType(InputType.TYPE_NULL);
        description.setInputType(InputType.TYPE_NULL);
    }
    @Override
    public void finish() {
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        data.putExtra("person", person);
        data.putExtra("isDelete", isDelete);
        data.putExtra("isEdit", isEdit);
        setResult(RESULT_OK, data);
        super.finish();
    }
}
