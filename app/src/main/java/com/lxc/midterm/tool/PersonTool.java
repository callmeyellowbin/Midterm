package com.lxc.midterm.tool;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lxc.midterm.Const;
import com.lxc.midterm.domain.Person;
import com.lxc.midterm.domain.SimpleResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by road on 2017/11/19.
 */

public class PersonTool {

    /*分段加载的请求，page代表请求第几页（初始化的时候填0），keyword为搜索的关键字，非搜索的时候keyword填null*/
    /*第一次加载page填0，第二次填1，第三次填2。。*/
    /*返回List<Person>，如果size=0，说明接下来没有了*/
    public static void getTwentyPerson(final Handler handler, final Integer page, final String keyword){
        // 使用okhttp
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("page",page.toString());
                if (keyword != null && (!keyword.equals(""))) {
                    System.out.println("使用关键字搜索");
                    builder.add("keyword",keyword);
                }
                //设置参数
                Request request = new Request.Builder().post(builder.build()).url(Const.IP + "getTwentyPerson.action").build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //System.out.println(ans_str);
                        JSONArray jsonArray = JSON.parseArray(ans_str);
                        List<Person> list = jsonArray.toJavaList(Person.class);
                        Message msg = new Message();
                        msg.what = 0x1;
                        msg.obj = list;
                        handler.sendMessage(msg);
                    }
                });
                super.run();
            }
        }.start();
    }

    /*添加人物的请求,音频文件没有的话填null，如果返回的simpleResponse的err为空的话则添加成功*/
    /*音频上传目前暂未处理，填null*/
    /*返回SimpleResponse*/
    public static void addUpdatePerson(final Handler handler, final Person person, File head_file, File audio_file){
        //上传帖子图片
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("person",JSON.toJSONString(person));
        if(head_file != null && head_file.exists()){
            if(head_file.getName().endsWith("png"))
               builder.addFormDataPart("img",head_file.getName(), RequestBody.create(MediaType.parse("image/png"),head_file));
            else if(head_file.getName().endsWith("jpg"))
                builder.addFormDataPart("img",head_file.getName(), RequestBody.create(MediaType.parse("image/jpg"),head_file));
        }
        MultipartBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(Const.IP +"/addUpdatePerson.action").post(requestBody).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String ans_str = response.body().string();
                SimpleResponse simpleResponse = JSON.parseObject(ans_str,SimpleResponse.class);
                Message message = new Message();
                message.what = 0x2;
                message.obj=simpleResponse;
                handler.sendMessage(message);
            }
        });
    }

    /*删除对应person_id的人，如果返回的simpleResponse的err为空的话则删除成功*/
    /*返回SimpleResponse*/
    public static void deletePerson(final Handler handler, final Integer person_id){
        // 使用okhttp
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("person_id",person_id.toString());
                //设置参数
                Request request = new Request.Builder().post(builder.build()).url(Const.IP + "deletePerson.action").build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //System.out.println(ans_str);
                        SimpleResponse simpleResponse = JSON.parseObject(ans_str,SimpleResponse.class);
                        Message msg = new Message();
                        msg.what = 0x3;
                        msg.obj = simpleResponse;
                        handler.sendMessage(msg);
                    }
                });
                super.run();
            }
        }.start();
    }

    /*返回按照人气值排序的人物列表*/
    public static void getTenRankPerson(final Handler handler, final Integer page){
        // 使用okhttp
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("page",page.toString());
                //设置参数
                Request request = new Request.Builder().post(builder.build()).url(Const.IP + "getGoodRank.action").build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //System.out.println(ans_str);
                        JSONArray jsonArray = JSON.parseArray(ans_str);
                        List<Person> list = jsonArray.toJavaList(Person.class);
                        Message msg = new Message();
                        msg.what = 0x4;
                        msg.obj = list;
                        handler.sendMessage(msg);
                    }
                });
                super.run();
            }
        }.start();
    }

        /*对人物点赞*/
        public static void addGood(final Handler handler,final Integer person_id){
            // 使用okhttp
            new Thread(){
                @Override
                public void run() {
                    OkHttpClient mokHttpClient = new OkHttpClient();
                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("person_id", String.valueOf(person_id));
                    //设置参数
                    Request request = new Request.Builder().post(builder.build()).url(Const.IP + "addGood.action").build();
                    mokHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String ans_str = response.body().string();
                            //System.out.println(ans_str);
                            SimpleResponse simpleResponse = JSON.parseObject(ans_str,SimpleResponse.class);
                            Message msg = new Message();
                            msg.what = 0x5;
                            msg.obj = simpleResponse;
                            handler.sendMessage(msg);
                        }
                    });
                    super.run();
                }
            }.start();
        }

}
