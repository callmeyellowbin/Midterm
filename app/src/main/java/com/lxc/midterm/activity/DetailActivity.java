package com.lxc.midterm.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lxc.midterm.Const;
import com.lxc.midterm.R;
import com.lxc.midterm.domain.Person;
import com.lxc.midterm.domain.SimpleResponse;
import com.lxc.midterm.tool.PersonTool;
import com.lxc.midterm.utils.PhotoUtils;

import java.io.File;

public class DetailActivity extends AppCompatActivity {

    private EditText name;
    private ImageView head;
    private EditText person_date;
    private EditText sex;
    private EditText country;
    private EditText hometown;
    private EditText description;
    private TextView description_tv;
    private EditText second_name;
    private Person person;
    private LinearLayout first_bar;
    private LinearLayout second_bar;
    private boolean isDelete = false;
    private boolean isEdit = false;
    private String originName;
    private String originSecondName;
    private String originDate;
    private String originSex;
    private String originCountry;
    private String originHometown;
    private String originDescription;
    private InputMethodManager imm; //管理软键盘

    protected static final int TAKE_PICTURE = 1;
    protected static final int CHOOSE_PICTURE = 0;
    private static final int CROP_SMALL_PICTURE = 2;
    private static final int PERMISSION_REQUEST_CODE = 0X00000060;
    private String head_path = null;
    private Button btn_good; //点赞按钮

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x2 || msg.what == 0x5){
                SimpleResponse simpleResponse = (SimpleResponse) msg.obj;
                if(simpleResponse.getErr() == null){
                    //修改成功
                    Toast.makeText(DetailActivity.this,simpleResponse.getSuccess(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_detail);
        imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        person = (Person) getIntent().getSerializableExtra("person");
        PhotoUtils.imagePath = null;
        initLayout();
        InitListener();
        InitProperty(person);
    }

    private void initLayout() {
        name = findViewById(R.id.name);
        second_name = findViewById(R.id.second_name);
        head = findViewById(R.id.head);
        person_date = findViewById(R.id.person_date);
        sex = findViewById(R.id.sex);
        country = findViewById(R.id.country);
        hometown = findViewById(R.id.hometown);
        description = findViewById(R.id.description);
        first_bar = findViewById(R.id.first_bar);
        second_bar = findViewById(R.id.second_bar);
        btn_good = findViewById(R.id.btn_good);
        description_tv = findViewById(R.id.description_tv);
        //未按编辑键不可编辑
        name.setEnabled(false);
        second_name.setEnabled(false);
        person_date.setEnabled(false);
        sex.setEnabled(false);
        country.setEnabled(false);
        hometown.setEnabled(false);
        description.setEnabled(false);
        head.setEnabled(false);
        //使TextView的Description可以滚动起来
        description_tv.setMovementMethod(new ScrollingMovementMethod());
    }

    private void InitProperty(Person person) {
        name.setText(person.getName());
        second_name.setText(person.getSecond_name());
        person_date.setText(person.getPerson_date());
        sex.setText(person.getSex());
        country.setText(person.getCountry());
        hometown.setText(person.getHometown());
        description_tv.setText(person.getDescription());
        description.setText(description_tv.getText().toString());
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
                name.setEnabled(true);
                second_name.setEnabled(true);
                person_date.setEnabled(true);
                sex.setEnabled(true);
                country.setEnabled(true);
                hometown.setEnabled(true);
                description.setEnabled(true);
                head.setEnabled(true);
                description.setVisibility(View.VISIBLE);
                description_tv.setVisibility(View.GONE);
                //弹出软键盘
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
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
                        //更新本地全部数据，上传到服务器
                        person.setName(name.getText().toString());
                        person.setSecond_name(second_name.getText().toString());
                        person.setSex(sex.getText().toString());
                        person.setPerson_date(person_date.getText().toString());
                        person.setCountry(country.getText().toString());
                        person.setHometown(hometown.getText().toString());
                        person.setDescription(description.getText().toString());

                        if(head_path == null){
                            PersonTool.addUpdatePerson(handler,person,null,null);
                        }
                        else{
                            //设置头像url
                            String head_url = null;
                            if(head_path!= null &&head_path.endsWith("png")){
                                head_url = Const.IP + "head/" + person.getUuid() + ".png";
                            }else if(head_path!= null &&head_path.endsWith("jpg")){
                                head_url = Const.IP + "head/" + person.getUuid() + ".jpg";
                            }
                            person.setHead_url(head_url);
                            File file = new File(head_path);
                            if(file.exists()){
                                PersonTool.addUpdatePerson(handler,person,file,null);
                            }
                        }
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
                        //显示回TextView
                        description_tv.setText(originDescription);
                        description.setText(originDescription);
                        description.setVisibility(View.GONE);
                        description_tv.setVisibility(View.VISIBLE);
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

        //设置头像功能
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoUtils.showChoosePicDialog(DetailActivity.this);
            }
        });

        //点赞功能
        btn_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonTool.addGood(handler,person.getPerson_id());
                btn_good.setBackgroundResource(R.drawable.like);
            }
        });
    }


    public void clearFocus() {
        //不可点击了
        name.setEnabled(false);
        second_name.setEnabled(false);
        person_date.setEnabled(false);
        sex.setEnabled(false);
        country.setEnabled(false);
        hometown.setEnabled(false);
        head.setEnabled(false);
        //显示回TextView
        description_tv.setText(originDescription);
        description.setVisibility(View.GONE);
        description_tv.setVisibility(View.VISIBLE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    PhotoUtils.startPhotoZoom(PhotoUtils.tempUri, this); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    PhotoUtils.startPhotoZoom(data.getData(), this); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        head_path = PhotoUtils.setImageToView(data, head); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /**
     * 处理权限申请的回调。
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，加载图片。
                PhotoUtils.loadImageForSDCard(this);
            } else {
                //拒绝权限，弹出提示框。
                PhotoUtils.showExceptionDialog(this);
            }
        }
    }
}
