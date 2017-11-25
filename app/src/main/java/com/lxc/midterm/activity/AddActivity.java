package com.lxc.midterm.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lxc.midterm.Const;
import com.lxc.midterm.R;
import com.lxc.midterm.domain.Person;
import com.lxc.midterm.domain.SimpleResponse;
import com.lxc.midterm.tool.PersonTool;
import com.lxc.midterm.utils.PhotoUtils;

import java.io.File;
import java.util.UUID;

public class AddActivity extends AppCompatActivity {

    private ImageView addHead;
    private TextView addBack;
    private TextView addSave;
    private TextInputLayout addName;
    private TextInputLayout addSecondName;
    private RadioGroup addSex;
    private String sex_str = "男";
    private RadioGroup addCountry;
    private String country_str = "魏";
    private TextInputLayout addPersonDate;
    private TextInputLayout addHometown;
    private TextInputLayout addDescription;
    protected static final int TAKE_PICTURE = 1;
    protected static final int CHOOSE_PICTURE = 0;
    private static final int CROP_SMALL_PICTURE = 2;
    private static final int PERMISSION_REQUEST_CODE = 0X00000060;
    private String head_path = null;
    private Person person;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x2){
                SimpleResponse simpleResponse = (SimpleResponse) msg.obj;
                if(simpleResponse != null && simpleResponse.getErr() == null){
                    //添加成功后
                    Toast.makeText(AddActivity.this,simpleResponse.getSuccess(),Toast.LENGTH_SHORT).show();
                    Intent addIntent = new Intent(AddActivity.this, MainActivity.class);
                    addIntent.putExtra("add",true);
                    addIntent.putExtra("add_person", person);
                    setResult(2, addIntent);//成功
                    //finish();
                }else {
                    Toast.makeText(AddActivity.this,simpleResponse.getErr(),Toast.LENGTH_SHORT).show();
                    Intent addIntent = new Intent(AddActivity.this, MainActivity.class);
                    addIntent.putExtra("add",true);
                    setResult(1, addIntent);//失败
                   //finish();
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        PhotoUtils.imagePath = null;
        addHead = findViewById(R.id.add_head);
        addBack = findViewById(R.id.add_back);
        addSave = findViewById(R.id.add_save);
        addName = findViewById(R.id.add_name_input);
        addSecondName = findViewById(R.id.add_second_name_input);
        addSex = findViewById(R.id.add_sex_input);
        addCountry = findViewById(R.id.add_country_input);
        addPersonDate = findViewById(R.id.add_person_date_input);
        addHometown = findViewById(R.id.add_hometown_input);
        addDescription = findViewById(R.id.add_description_input);

        addName.setHint("姓名");
        addSecondName.setHint("字");
        //addSex.setHint("性别");
        //addCountry.setHint("国家势力");
        addPersonDate.setHint("生卒年月");
        addHometown.setHint("籍贯");
        addDescription.setHint("简介");

        //对必须输入的加限制
        final EditText editName = findViewById(R.id.add_name);
        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!hasFocus) && (editName.getText().toString() == null || editName.getText().equals(""))) {
                    addName.setError("姓名不能为空");
                } else {
                    addName.setErrorEnabled(false);
                }
            }
        });

        addSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                person = new Person();
                person.setName(addName.getEditText().getText().toString());
                person.setSecond_name(addSecondName.getEditText().getText().toString());
                person.setCountry(country_str);
                person.setSex(sex_str);
                person.setPerson_date(addPersonDate.getEditText().getText().toString());
                person.setHometown(addHometown.getEditText().getText().toString());
                person.setDescription(addDescription.getEditText().getText().toString());
                //必须提供一个uuid给服务器
                String uuid = UUID.randomUUID().toString();
                person.setUuid(uuid);
                //设置头像url
                String head_url = null;
                if(head_path!= null && head_path.endsWith("png")){
                    head_url = Const.IP + "head/" + person.getUuid() + ".png";
                }else if(head_path!= null &&head_path.endsWith("jpg")){
                    head_url = Const.IP + "head/" + person.getUuid() + ".jpg";
                }
                person.setHead_url(head_url);

                if(person.getName() != null && person.getSex() != null){
                    //开始上传到服务器
                    if(head_path != null){
                        File file = new File(head_path);
                        if(file.exists()){
                            PersonTool.addUpdatePerson(handler,person,file,null);
                        }else {
                            PersonTool.addUpdatePerson(handler,person,null,null);
                        }
                    }else {
                        PersonTool.addUpdatePerson(handler,person,null,null);
                    }
                }
            }
        });

        addBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //添加头像监听
        addHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoUtils.showChoosePicDialog(AddActivity.this);
            }
        });
        //两个单选框的监听
        addCountry.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                country_str = radioButton.getText().toString();
            }
        });

        addSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                sex_str = radioButton.getText().toString();
            }
        });
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
                        head_path = PhotoUtils.setImageToView(data, addHead); // 让刚才选择裁剪得到的图片显示在界面上
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
