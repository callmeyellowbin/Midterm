package com.lxc.midterm.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LaiXiancheng on 2017/11/26.
 * Email: lxc.sysu@qq.com
 */

public class Name2Pinyin {
	static Map<String, String> name2pinyin = new HashMap<String, String>();
	static boolean isInit = false;
	static public void initData(){
		if (isInit)
			return;
		isInit = true;
		name2pinyin.put("曹操","caocao");
		name2pinyin.put("曹丕","caopi");
		name2pinyin.put("曹仁","caoren");
		name2pinyin.put("曹植","caozhi");
		name2pinyin.put("大乔","daqiao");
		name2pinyin.put("小乔","xiaoqiao");
		name2pinyin.put("法正","fazheng");
		name2pinyin.put("典韦","dianwei");
		name2pinyin.put("貂蝉","diaochan");
		name2pinyin.put("董卓","dongzhuo");
		name2pinyin.put("甘宁","ganning");
		name2pinyin.put("关羽","guanyu");
		name2pinyin.put("郭嘉","guojia");
		name2pinyin.put("黄盖","huanggai");
		name2pinyin.put("黄月英","huangyueying");
		name2pinyin.put("黄忠","huangzhong");
		name2pinyin.put("华佗","huatuo");
		name2pinyin.put("华雄","huaxiong");
		name2pinyin.put("刘备","liubei");
		name2pinyin.put("鲁肃","lusu");
		name2pinyin.put("陆逊","luxun");
		name2pinyin.put("吕布","lvbu");
		name2pinyin.put("吕蒙","lvmeng");
		name2pinyin.put("马超","machao");
		name2pinyin.put("马谡","masu");
		name2pinyin.put("孟获","menghuo");
		name2pinyin.put("庞德","pangde");
		name2pinyin.put("庞统","pangtong");
		name2pinyin.put("司马懿","simayi");
		name2pinyin.put("孙坚","sunjian");
		name2pinyin.put("孙权","sunquan");
		name2pinyin.put("孙尚香","sunshangxiang");
		name2pinyin.put("太史慈","taishici");
		name2pinyin.put("夏侯惇","xiahoudun");
		name2pinyin.put("夏侯渊","xiahouyuan");
		name2pinyin.put("许褚","xuchu");
		name2pinyin.put("徐晃","xuhuang");
		name2pinyin.put("荀彧","xunyu");
		name2pinyin.put("徐庶","xushu");
		name2pinyin.put("袁绍","yuanshao");
		name2pinyin.put("袁术","yuanshu");
		name2pinyin.put("于吉","yuji");
		name2pinyin.put("张飞","zhangfei");
		name2pinyin.put("张角","zhangjiao");
		name2pinyin.put("张辽","zhangliao");
		name2pinyin.put("赵云","zhaoyun");
		name2pinyin.put("甄姬","zhenji");
		name2pinyin.put("周泰","zhoutai");
		name2pinyin.put("周瑜","zhouyu");
		name2pinyin.put("诸葛亮","zhugeliang");

	}
	static public String getPinyin(String name){
		initData();
		return name2pinyin.get(name);
	}
}
