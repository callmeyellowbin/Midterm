package com.lxc.midterm.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lxc.midterm.Client;
import com.lxc.midterm.Const;
import com.lxc.midterm.R;
import com.lxc.midterm.domain.Game;
import com.lxc.midterm.domain.GameRequest;
import com.lxc.midterm.domain.GameResponse;
import com.lxc.midterm.domain.Person;
import com.lxc.midterm.utils.Name2Pinyin;
import com.lxc.midterm.view.VersusRoleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.drafts.Draft_6455;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

	final int REFUSE_SHOW_CARD = 1;
	final int ALLOW_SHOW_CARD = 2;
	ConstraintLayout plays_layout1;
	ConstraintLayout plays_layout2;
	LinearLayout rootView;
	ImageView ivWait;
	TextView tvHint;
	ObjectAnimator waitAnimator;
	VersusRoleView role1, role2, role3, role4, role5;
	VersusRoleView player1, player2;
	Button btStop;
	ImageView ivMyScore, ivOpponentScore;
	ImageView ivResult, ivAnimation;
	ImageView ivHelper;

	private Client client;
	private Game game;
	MediaPlayer mp = new MediaPlayer();

	int state = 1;
	int myScore = 0;//计分
	int opponentScore = 0;
	Collection<Person> personCollection;
	int round = 0;//第round轮
	enum resultEnum {Win, Lose, Tied}
	enum playerEnum {Myself, Opponent}
	final int scoreIds[] = {R.drawable.num_0, R.drawable.num_1, R.drawable.num_2, R.drawable.num_3};
	final int versusIds[] = {R.drawable.sword, R.drawable.knife, R.drawable.bow, R.drawable.gun};
	boolean isStoped = false;//已经关闭了游戏
	final int versusTime = 2000;//对战动画效果显示时长
	boolean isFinish = false;//已经判定了游戏结束（但可能还没真正关闭游戏）
	boolean isNeedClose = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		plays_layout1 =  findViewById(R.id.plays1);
		plays_layout2 =  findViewById(R.id.plays2);
		//注册eventbus监听
		EventBus.getDefault().register(this);//订阅事件
		//连接服务器
		connectServer();
		tvHint = findViewById(R.id.tv_wait);
		ivWait = findViewById(R.id.iv_wait);
		ivMyScore = findViewById(R.id.iv_my_score);
		ivOpponentScore = findViewById(R.id.iv_opponent_score);
		ivResult = findViewById(R.id.iv_result);
		ivAnimation = findViewById(R.id.iv_animation);
		ivHelper = findViewById(R.id.helper);
		ivHelper.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showHelpDialog();
			}
		});
		btStop = findViewById(R.id.bt_stop);
		btStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showStopDialog();
			}
		});

		role1 = findViewById(R.id.role_1);
		role2 = findViewById(R.id.role_2);
		role3 = findViewById(R.id.role_3);
		role4 = findViewById(R.id.role_4);
		role5 = findViewById(R.id.role_5);
		player1 = findViewById(R.id.player1);
		player2 = findViewById(R.id.player2);
		role1.setOnClickListener(this);
		role2.setOnClickListener(this);
		role3.setOnClickListener(this);
		role4.setOnClickListener(this);
		role5.setOnClickListener(this);

	}

	private void showHelpDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
		builder.setTitle("游戏规则");
		builder.setMessage(R.string.rules);
		builder.setPositiveButton("确定", null);
		builder.show();
	}

	private void showStopDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
		builder.setMessage("确定结束游戏？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				stopGame();finish();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		//等待图标的旋转效果
		waitAnimator = ObjectAnimator.ofFloat(ivWait, "rotation", 0f, 360f);
		waitAnimator.setDuration(1000);
		waitAnimator.setInterpolator(new LinearInterpolator());
		waitAnimator.setRepeatCount(ValueAnimator.INFINITE);
		waitAnimator.setRepeatMode(ValueAnimator.RESTART);
		waitAnimator.start();



	}

	public void connectServer() {
		try {
			client = new Client(new URI(Const.WEBSOCKET_URI), new Draft_6455());
			client.connect();
			Toast.makeText(GameActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
		} catch (URISyntaxException e) {
			Toast.makeText(GameActivity.this, "连接失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onGetGameResponse(GameResponse response) {
		//接收到服务器返回的消息对象GameResponse
		System.out.println("response.getCode()" + response.getCode());
		switch (response.getCode()) {
			case 0: {
				//游戏开始
				game = response.getGame();//游戏的初始数据
				int cur_role = 1;
				personCollection = game.getPerson_map().values();
				for (Person p : personCollection) {
					VersusRoleView roleView = cur_role < 4 ?
							(VersusRoleView) plays_layout1.getChildAt(cur_role - 1) :
							(VersusRoleView) plays_layout2.getChildAt(cur_role - 4);
					roleView.setPerson(p);
					cur_role++;
				}
				//切换状态和界面
				state = ALLOW_SHOW_CARD;
				waitAnimator.cancel();
				ivWait.setVisibility(View.GONE);
				tvHint.setText(R.string.first_send_card_hint);
			}
			break;
			//1输了，2赢了，5平了，一起处理
			case 1:
			case 5:
			case 2: {
				Integer person_id = response.getPerson_id();//如果这一轮中是对方先手的话，同时返回别人这一轮所出武将，否则为null
				Person oppoPerson = new Person();
				//找到person_id对应的person
				for (Person p : personCollection) {
					if (p.getPerson_id().equals(person_id)) {
						oppoPerson = p;
						break;
					}
				}
				//将对手的牌显示出来
				player2.setVisibility(View.VISIBLE);
				ivAnimation.setVisibility(View.VISIBLE);
				player2.setPerson(oppoPerson);
				resultEnum res = response.getCode() == 2 ? resultEnum.Win : resultEnum.Lose;
				res = response.getCode() == 5 ? resultEnum.Tied : res;

				//播放攻击音效
				mp.reset();
				mp = MediaPlayer.create(this, R.raw.attack);
				mp.start();
				tvHint.setText("");

				ValueAnimator valueAnimator = ValueAnimator.ofInt(0,versusTime);
				valueAnimator.setDuration(versusTime);
				valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					int cnt = 0;
					int imageCnt = 0;
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						cnt++;
						if (cnt % 10 != 0)
							return;
						ivAnimation.setImageResource(versusIds[imageCnt % 4]);
						imageCnt++;
					}
				});
				final resultEnum finalRes = res;
				valueAnimator.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						super.onAnimationEnd(animation);
						mp.reset();//关闭攻击音效
						showResult(finalRes);//展示结果
					}
				});
				valueAnimator.start();

			}
			break;

			case 3: {
				//不需要自己发送关闭请求了
				isNeedClose = false;
				if (!isFinish)
					showFinishDialog();
			}
			break;

			case 4: {
				//游戏未开始，等待其他玩家加入
			}
			break;

			default:
				break;
		}
	}

	/**
	 * 更新界面的分数
	 * @param player 我方或是对方
	 */
	private void increaseScore(playerEnum player){
		if (player == playerEnum.Myself){
			myScore++;
			ivMyScore.setImageResource(scoreIds[myScore]);
		}
		else{
			opponentScore++;
			ivOpponentScore.setImageResource(scoreIds[opponentScore]);
		}
	}
	/**
	 * 选择图片以及计分，然后展示结果
	 */
	private void showResult(resultEnum res) {
		round++;//轮数增加

		int resId = R.drawable.win;
		int rawId = R.raw.caocao_win;
		Person myPerson = ((VersusRoleView) player1).getPerson();
		Resources resources = getResources();
		String name_pinyin = Name2Pinyin.getPinyin(myPerson.getName());

		switch (res) {
			case Win:
				resId = R.drawable.win;
				rawId = resources.getIdentifier(getPackageName()+":raw/"+name_pinyin+"_win",
						null, null);
				increaseScore(playerEnum.Myself);
				break;
			case Lose:
				resId = R.drawable.lose;
				rawId = resources.getIdentifier(getPackageName()+":raw/"+name_pinyin+"_lose",
						null, null);
				increaseScore(playerEnum.Opponent);
				break;
			case Tied:
				rawId = R.raw.tie;
				resId = R.drawable.tied;
				break;
		}

		//读取成功
		if (rawId > 0){
			mp = MediaPlayer.create(this, rawId);
			mp.start();
		}
		//动画展示结果 缓慢出现然后缓慢消失
		ivResult.setVisibility(View.VISIBLE);
		//ivResult.setAlpha(0.01f);
		ivResult.setImageResource(resId);
		ObjectAnimator animator = ObjectAnimator.ofFloat(ivResult, "alpha", 0f, 1f, 0f);
		animator.setDuration(2000);
		animator.setInterpolator(new LinearInterpolator());

		//结果展示完之后，隐藏擂台上的player,切换状态。以及判定是否整局已经结束
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				ivResult.setAlpha(0f);
				ivResult.setVisibility(View.INVISIBLE);
				player1.setVisibility(View.INVISIBLE);
				player2.setVisibility(View.INVISIBLE);
				ivAnimation.setVisibility(View.INVISIBLE);
				state = ALLOW_SHOW_CARD;
				tvHint.setText(R.string.send_card_hint);
				int stringId = R.string.win;
				if (round>=5 || myScore >= 3 || opponentScore >= 3){
					if (myScore > opponentScore)
						stringId = R.string.win;
					else if (myScore == opponentScore)
						stringId = R.string.tie;
					else
						stringId = R.string.lose;
					isFinish = true;
				}
				if (isFinish){
					final AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
					builder.setTitle(stringId);
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							stopGame();finish();
						}
					});
					builder.show();
				}

			}
		});
		animator.start();

	}

	/**
	 * 发牌到服务器
	 *
	 * @param p 要发送的武将
	 */
	private void sendCard(Person p) {
		GameRequest gameRequest = new GameRequest();
		Log.d("sendCard", game.getMy_id());
		gameRequest.setMy_id(game.getMy_id());
		gameRequest.setOpposite_id(game.getOpposite_id());
		gameRequest.setGame_id(game.getGame_id());
		gameRequest.setCode(0);
		gameRequest.setPerson_id(p.getPerson_id());
		client.send(JSON.toJSONString(gameRequest));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);//取消订阅
		//关掉连接
		stopGame();
		Toast.makeText(GameActivity.this, "连接关闭", Toast.LENGTH_SHORT).show();
	}

	private void showFinishDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("对方掉线了或者结束了游戏");
		builder.setPositiveButton("结束游戏", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.show();

	}

	private void stopGame(){
		if (isStoped)
			return;
		isStoped = true;
		if (game != null && isNeedClose){
			Log.d("stopGame", "stopGame: ");
			GameRequest gameRequest = new GameRequest();
			gameRequest.setMy_id(game.getMy_id());
			gameRequest.setOpposite_id(game.getOpposite_id());
			gameRequest.setGame_id(game.getGame_id());
			gameRequest.setCode(1);
			client.send(JSON.toJSONString(gameRequest));
		}

		if(client != null && client.isOpen()){
			client.close();
		}
	}

	private void setPlayerVisibility(int visibility) {
		player1.setVisibility(visibility);
		player2.setVisibility(visibility);
	}

	@Override
	public void onClick(View v) {
		//当前不能出牌
		if (state == REFUSE_SHOW_CARD)
			return;

		state = REFUSE_SHOW_CARD;
		//ViewGroup parent = (ViewGroup) v.getParent();
		player1.setVisibility(View.VISIBLE);//我方显示
		player1.setPerson(((VersusRoleView) v).getPerson());//设置到擂台
		sendCard(player1.getPerson());//发牌到服务器
		v.setVisibility(View.INVISIBLE);//抽掉下方的牌
		tvHint.setText(R.string.wait_hint);
	}

	@Override
	public void onBackPressed() {
		showStopDialog();
	}
}
