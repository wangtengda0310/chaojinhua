package com.igame.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;




/**
 * 游戏战斗算法类，游戏中的算法都在此类中
 * 
 * @author zhh
 * 
 */
public class GameMath {

	
	public static final Random Rnd = new Random();
	
	public static final DecimalFormat df = new DecimalFormat("0.00");
	
	public static String formatNumber(double value){
		return df.format(value);
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static int getRandInt(int value){
		return Rnd.nextInt(value);
	}
	
	/**
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
    public static int getRandomInt(int min, int max) {
        return getRandInt(max - min) + min;
    }
	
	/**
	 * @return
	 */
	public static int getInventoryCellValidPrice( byte costType, int size, int slot){
		int price = 0;
		switch(costType){
		case 0:
			break;
		case 1:
			break;
		default:
			price = -1;
		}
		return price;
	}
	
	/**
	 * 二进制输出
	 * @param x
	 */
	public static void IntToBin(String str, int x)
	{
		for(int i = 31;i >= 0;i--)
		{
		}
		System.out.println();
	}

	/**
	 * 概率命中运算
	 * @param rate
	 * @return
	 */
	public static boolean hitRate(int rate){
		return (Rnd.nextInt(10000) + 1) <= rate;
	}
	
	/**
	 * 概率命中运算
	 * @param rate
	 * @return
	 */
	public static boolean hitRate100(int rate){
		return (Rnd.nextInt(100) + 1) <= rate;
	}

	/**
	 * 概率命中运算
	 * @param rate
	 * @return
	 */
	public static boolean hitRate1000(int rate){
		return (Rnd.nextInt(1000) + 1) <= rate;
	}

	/**
	 */
	public static int getRandomCount(int min,int max){
		return Rnd.nextInt(max - min + 1) + min;
	}
	
	public static double getRandomDouble(double min,double max){
		return min + ((max - min) * Rnd.nextDouble());
	}
	
	public static float getRandomFloat(float min,float max){
		return min + ((max - min) * Rnd.nextFloat());
	}

	/**
	 * 互斥概率
	 * @param map
	 * @return
	 */
	public static Integer getAIRate(Map<Integer,Integer>  map){

		int rate = Rnd.nextInt(10000) + 1;
		int start = 1;
		int ret = 0;
		for(Map.Entry<Integer,Integer> m : map.entrySet()){
			if(start<= rate &&  rate< (start + m.getValue())){
				ret = m.getKey();
			}
			start += m.getValue();
		}

		return ret;
	}
	
	/**
	 * 互斥概率
	 * @param map
	 * @return
	 */
	public static Integer getAIRate100(Map<Integer,Integer>  map){

		int rate = Rnd.nextInt(100) + 1;
		int start = 1;
		int ret = 0;
		for(Map.Entry<Integer,Integer> m : map.entrySet()){
			if(start<= rate &&  rate< (start + m.getValue())){
				ret = m.getKey();
			}
			start += m.getValue();
		}

		return ret;
	}
	
	
	/**
	 * 互斥概率
	 * @param map
	 * @return
	 */
	public static Integer getRate(Map<Integer,Integer>  map){
		
		int total = 0;
		for(Integer value : map.values()){
			total += value;
		}
		int rate = Rnd.nextInt(total) + 1;
		int start = 1;
		int ret = 0;
		for(Map.Entry<Integer,Integer> m : map.entrySet()){
			if(start<= rate &&  rate< (start + m.getValue())){
				ret = m.getKey();
			}
			start += m.getValue();
		}

		return ret;
	}


	/**
	 *
	 * 互斥概率
	 *
	 * 概率和可以不为1，概率支持浮点数
	 *
	 * 抽奖操作如下：
	 * 1.输入抽奖概率集合，【抽奖概率集合为{10.0, 20.0, 30.0}】
	 * 2.生成随机数，          【生成方法为 random.nextFloat() * total】
	 * 3.判断随机数在哪个区间内，返回该区间的index【生成了随机数12.001，则它属于(10.0, 30.0]，返回 index = 1】
	 *
	 * @param rates 概率list
	 * @return 概率list集合中的下标，-1 表示 未找到，原因可能为概率集合设置不合理
	 *
	 */
	public static int getRate(List<Float> rates){


		//计算基数
		float total = 0;
		for(Float rate : rates){
			total += rate;
		}

		//校验基数
		int ret = -1;
		if(total <= 0){
			return ret;
		}

		//生成0-1间的随机数
		float ranRate = Rnd.nextFloat() * total;
		while (ranRate == 0){
			ranRate = Rnd.nextFloat() * total;
		}

		//判断随机数在哪个区间内
		float start = 0;
		for (int i = 0; i < rates.size(); i++) {
			if(start <= ranRate && ranRate < (start + rates.get(i))){
				ret = i;
			}
			start += rates.get(i);
		}

		return ret;
	}
	
	/**
	 * @param last
	 * @param type 
	 * @return
	 */
	public static int getChoukaCoolDown(Timestamp last,int type){
		if(type == 1){
			if(last == null){
				return 0;
			}else{
				long left = last.getTime() + 10 * 60 * 1000 - System.currentTimeMillis();
				return left <=0 ? 0 : (int)(left/1000);
			}
			
		}else if(type == 2){
			if(last == null){
				return 0;
			}else{
				long left = last.getTime() + 24 * 3600 * 1000 - System.currentTimeMillis();
				return left <=0 ? 0 : (int)(left/1000);
			}
		}
		return Integer.MAX_VALUE;
	}
	
	


	public static void main(String[] args){

		//String s = "12.5;12.5;12.5;12.5;12.5;12.5;12.5;12.5";
		String s = "0;0;0";
		List<Float> rates = new ArrayList<>();
		for (String s1 : s.split(";")) {
			rates.add(Float.parseFloat(s1));
		}

		System.out.println(getRate(rates));
	}


}
