package com.igame.work.monster.dto;

import com.google.common.collect.Lists;
import com.igame.util.GameMath;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class JiyinType {

	public static final String TYPE_001 =	"1";//攻击+30
	public static final String TYPE_002 =	"2";//体力+90
	
	public static final String TYPE_003 =	"3";//霸气+20
	public static final String TYPE_004 =	"4";//移速+20
	
	public static final String TYPE_005 =	"5";//攻击+40 体力-30
	public static final String TYPE_006 =	"6";//体力+120 攻击-10
	
	public static final String TYPE_007 =	"7";//霸气+10 移速+10
	public static final String TYPE_008 =	"8";//攻击+15 霸气+10
	
	public static final String TYPE_009 =	"9";//攻击+15 移速+10
	public static final String TYPE_010 =	"10";//体力+45 霸气+10
	
	public static final String TYPE_011 =	"11";//体力+45 移速+10
	public static final String TYPE_012 =	"12";//颜值增加了！帅呆啦(无属性增益效果)
	
	
	//以下为大基因类型
	
	public static final String TYPE_013 =	"13";//获得经验+20%
	public static final String TYPE_014 =	"14";//等级上限+4
	
	public static final String TYPE_015 =	"15";//普通纹章效果+10%
	public static final String TYPE_016 =	"16";//体型+15%
	
	public static final String TYPE_017 =	"17";//射程+15%(对近战怪兽无效，近战怪兽也无法随机到这个基因)
	public static final String TYPE_018 =	"18";//体型-10%
	
	public static final String TYPE_019 =	"19";//血量高于75%攻击力+200
	public static final String TYPE_020 =	"20";//血量低于45%攻击力+200
	
	public static List<String> small = Lists.newArrayList();
	public static List<String> big = Lists.newArrayList();
	public static List<String> bigNotJing = Lists.newArrayList();
	static{
		small.add(TYPE_001);
		small.add(TYPE_002);
		small.add(TYPE_003);
		small.add(TYPE_004);
		small.add(TYPE_005);
		small.add(TYPE_006);
		small.add(TYPE_007);
		small.add(TYPE_008);
		small.add(TYPE_009);
		small.add(TYPE_010);
		small.add(TYPE_011);
		small.add(TYPE_012);
		
		big.add(TYPE_001);
		big.add(TYPE_002);
		big.add(TYPE_003);
		big.add(TYPE_004);
		big.add(TYPE_005);
		big.add(TYPE_006);
		big.add(TYPE_007);
		big.add(TYPE_008);
		big.add(TYPE_009);
		big.add(TYPE_010);
		big.add(TYPE_011);
		big.add(TYPE_012);
		big.add(TYPE_013);
		big.add(TYPE_014);
		big.add(TYPE_015);
		big.add(TYPE_016);
		big.add(TYPE_017);
		big.add(TYPE_018);
		big.add(TYPE_019);
		big.add(TYPE_020);		
		
		bigNotJing.add(TYPE_001);
		bigNotJing.add(TYPE_002);
		bigNotJing.add(TYPE_003);
		bigNotJing.add(TYPE_004);
		bigNotJing.add(TYPE_005);
		bigNotJing.add(TYPE_006);
		bigNotJing.add(TYPE_007);
		bigNotJing.add(TYPE_008);
		bigNotJing.add(TYPE_009);
		bigNotJing.add(TYPE_010);
		bigNotJing.add(TYPE_011);
		bigNotJing.add(TYPE_012);
		bigNotJing.add(TYPE_013);
		bigNotJing.add(TYPE_014);
		bigNotJing.add(TYPE_015);
		bigNotJing.add(TYPE_016);
		bigNotJing.add(TYPE_018);
		bigNotJing.add(TYPE_019);
		bigNotJing.add(TYPE_020);
	}
	
	
	/**
	 * 随机一种基因
	 */
	public static String getRandType(int rank,boolean jingzhan){
		
		if(rank%5 == 0 && rank >=5){//大基因
			if(jingzhan){
				return bigNotJing.get(GameMath.getRandInt(bigNotJing.size()));
			}else{
				return big.get(GameMath.getRandInt(big.size()));
			}			
		}else{
			return small.get(GameMath.getRandInt(small.size()));
		}
		
	}
	


}
