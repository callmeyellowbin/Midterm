package com.lxc.midterm.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.lxc.midterm.Client;
import com.lxc.midterm.Const;
import com.lxc.midterm.R;
import com.lxc.midterm.domain.Game;
import com.lxc.midterm.domain.GameResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.drafts.Draft_6455;

import java.net.URI;
import java.net.URISyntaxException;

public class GameActivity extends AppCompatActivity {

    private Client client;
    private Game game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //注册eventbus监听
        EventBus.getDefault().register(this);//订阅事件
        //连接服务器
        connectServer();

        //出牌示例
/*        GameRequest gameRequest = new GameRequest();
        gameRequest.setMy_id(new Integer(game.getMy_id()));
        gameRequest.setMy_id(new Integer(game.getOpposite_id()));
        gameRequest.setGame_id(game.getGame_id());
        gameRequest.setCode(0);
        gameRequest.setPerson_id(1);
        client.send(JSON.toJSONString(gameRequest));*/

        //结束游戏示例
/*        GameRequest gameRequest2 = new GameRequest();
        gameRequest.setMy_id(new Integer(game.getMy_id()));
        gameRequest.setMy_id(new Integer(game.getOpposite_id()));
        gameRequest.setGame_id(game.getGame_id());
        gameRequest.setCode(1);
        client.send(JSON.toJSONString(gameRequest2));
        if(client != null && client.isConnecting()){
            client.close();
        }*/

    }

    public void connectServer(){
        try {
            client = new Client(new URI(Const.WEBSOCKET_URI),new Draft_6455());
            client.connect();
            Toast.makeText(GameActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
        } catch (URISyntaxException e) {
            Toast.makeText(GameActivity.this,"连接失败" + e.getMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetGameResponse(GameResponse response) {
        //接收到服务器返回的消息对象GameResponse
        System.out.println("response.getCode()"+ response.getCode());
        switch (response.getCode()){
            case 0:{
                //游戏开始
                game = response.getGame();//游戏的初始数据
            }
            break;

            case 1:{
                //当前回合中：你的武将输了
                Integer person_id = response.getPerson_id();//如果这一轮中是对方先手的话，同时返回别人这一轮所出武将，否则为null
                //计分...
            }
            break;

            case 2:{
                //当前回合中：你的武将赢了
                Integer person_id = response.getPerson_id();//如果这一轮中是对方先手的话，同时返回别人这一轮所出武将，否则为null
                //计分...
            }
            break;

            case 3:{
                //对方掉线了或者结束了游戏
                //不用发送结束游戏请求
            }
            break;

            case 4:{
                //游戏未开始，等待其他玩家加入
            }
            break;

            case 5:{
                //当前回合中：你的武将平局了
                Integer person_id = response.getPerson_id();//如果这一轮中是对方先手的话，同时返回别人这一轮所出武将
            }
            break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消订阅
        //关掉连接
        if(client != null && client.isConnecting()){
            client.close();
        }
        Toast.makeText(GameActivity.this,"连接关闭",Toast.LENGTH_SHORT).show();
    }
}
