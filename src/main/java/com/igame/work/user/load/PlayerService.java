package com.igame.work.user.load;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.ISFSModule;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.data.StrengthenplaceTemplate;
import com.igame.work.monster.data.StrengthenrewardTemplate;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.DrawdataTemplate;
import com.igame.work.user.data.DrawrewardTemplate;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.TongHuaDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PlayerService implements ISFSModule {
	private ResourceService resourceService;

	public TongHuaDto getRandomTongHuaDto(){
		
		TongHuaDto td = new TongHuaDto();
		int id = MonsterDataManager.StrengthenplaceData.getSet().get(GameMath.getRandInt(MonsterDataManager.StrengthenplaceData.getSet().size()));
		td.setId(id);
		List<StrengthenplaceTemplate> ls = MonsterDataManager.StrengthenplaceData.getRandom(id);
		int total = 11;
		td.setSid(1);
		if(ls.get(0).getTotal() > 0){
			total = ls.get(0).getTotal();
		}
		List<String> points = Lists.newArrayList();
		for(StrengthenplaceTemplate st : ls){
			td.setName(st.getName());
			Map<Integer,Integer> maps = Maps.newHashMap();
			String[] counts = st.getValue().split(",");
			String[] rates = st.getRate().split(",");
			for(int i = 0; i < counts.length;i++){
				maps.put(Integer.parseInt(counts[i]), Integer.parseInt(rates[i]));
			}
			int rcount = GameMath.getAIRate100(maps);//个数
			StrengthenrewardTemplate sw = MonsterDataManager.StrengthenrewardData.getTemplate(st.getStrengthen_type());
			
			for(int i = 0;i < rcount;i++){
				String temp = String.valueOf(st.getStrengthen_type());
				if(st.getStrengthen_type() == 1){//怪物
					temp += ",-1";
					temp += ",1";
					temp += "," + MonsterDataManager.StrengthenmonsterData.getAll().get(GameMath.getRandInt(MonsterDataManager.StrengthenmonsterData.getAll().size())).getNum();
					temp += ",1";
				}else{
					temp += ",-1";
					temp += ","+3;
					Map<Integer,Integer> map1 = Maps.newHashMap();
					String[] r1 = sw.getStrengthen_reward().split(",");
					String[] v1 = sw.getValue().split(",");
					String[] rate1 = sw.getRate().split(",");
					for(int j = 0;j < r1.length;j++){
						map1.put(Integer.parseInt(r1[j]), Integer.parseInt(rate1[j]));
					}
					int itemId= GameMath.getAIRate100(map1);
					temp += ","+itemId;
					int index = 0;
					for(String ii :r1){
						if(ii.equals(String.valueOf(itemId))){
							break;
						}
						index++;
					}
					temp += "," +GameMath.getRandomCount(Integer.parseInt(v1[index].split("-")[0]), Integer.parseInt(v1[index].split("-")[1]));
					
				}
				points.add(temp);
				if(points.size() >= total){
					break;
				}
			}

			if(points.size() >= total){
				break;
			}
		}
		
		int normal = total - points.size();
		if(normal > 0){
			StrengthenrewardTemplate sw = MonsterDataManager.StrengthenrewardData.getTemplate(0);
			String[] r1 = sw.getStrengthen_reward().split(",");
			String[] v1 = sw.getValue().split(",");
			String[] rate1 = sw.getRate().split(",");
			for(int i = 0;i < normal;i++){
				String temp = "0";
				if(GameMath.hitRate(5000)){//同化经验
					temp += ",-1";
					temp += ","+5;
					temp += ","+5;
					temp += "," +GameMath.getRandomCount(Integer.parseInt(sw.getStrengthen_exp().split("-")[0]), Integer.parseInt(sw.getStrengthen_exp().split("-")[1]));
					
				}else{//道具
					temp += ",-1";
					temp += ","+3;
					Map<Integer,Integer> map1 = Maps.newHashMap();
					for(int j = 0;j < r1.length;j++){
						map1.put(Integer.parseInt(r1[j]), Integer.parseInt(rate1[j]));
					}
					int itemId= GameMath.getAIRate100(map1);
					temp += ","+itemId;
					int index = 0;
					for(String ii :r1){
						if(ii.equals(String.valueOf(itemId))){
							break;
						}
						index++;
					}
					temp += "," +GameMath.getRandomCount(Integer.parseInt(v1[index].split("-")[0]), Integer.parseInt(v1[index].split("-")[1]));
					
				}
				points.add(temp);
			}
		}
		Collections.shuffle(points);
		
		String first = points.get(0);
		String[] ff = first.split(",");
		ff[1] = "0";
		first = MyUtil.toString(ff, ",");
		points.set(0, first);
		td.setTongStr(MyUtil.toString(points, ";"));
		
		
		return td;
		
	}
	
	/**
	 * 检测玩家的造物台数据
	 */
	public void checkDrawData(Player player,boolean notify){
		

		String tempDraw = player.getDraw().getDrawList();
		
		for(DrawdataTemplate dt : PlayerDataManager.DrawdataData.getAll()){
			if(dt.getLimit().equals("-1") && !player.hasCheckPointDraw(String.valueOf(dt.getDrawType()))){
				player.getDraw().setDrawList(player.getDraw().getDrawList() + ","+String.valueOf(dt.getDrawType()));
			}
			if(dt.getLimit().startsWith("1") && player.getDraw().getDrawLv() >= Integer.parseInt(dt.getLimit().split(",")[1]) && !player.hasCheckPointDraw(String.valueOf(dt.getDrawType()))){
				player.getDraw().setDrawList(player.getDraw().getDrawList() + ","+String.valueOf(dt.getDrawType()));
			}
			if(dt.getLimit().startsWith("2") && player.getPlayerLevel() >= Integer.parseInt(dt.getLimit().split(",")[1]) && !player.hasCheckPointDraw(String.valueOf(dt.getDrawType()))){
				player.getDraw().setDrawList(player.getDraw().getDrawList() + ","+String.valueOf(dt.getDrawType()));
			}
			if(dt.getLimit().startsWith("3") && player.hasCheckPoint(dt.getLimit().split(",")[1]) && !player.hasCheckPointDraw(String.valueOf(dt.getDrawType()))){
				player.getDraw().setDrawList(player.getDraw().getDrawList() + ","+String.valueOf(dt.getDrawType()));
			}
		}
		if(player.getDraw().getDrawList().startsWith(",")){
			player.getDraw().setDrawList(player.getDraw().getDrawList().substring(1));
		}
		if(notify && !tempDraw.equals(player.getDraw().getDrawList())){
			MessageUtil.notifyDrawData(player);
		}
		
	}
	
	

	public List<RewardDto> couKa(int rewardId,int count){
		
		List<RewardDto> rt = Lists.newArrayList();
		DrawrewardTemplate dt = PlayerDataManager.DrawrewardData.getTemplate(rewardId);
		if(dt != null){
			Map<Integer,Integer> rates = Maps.newHashMap();//概率
			Map<Integer,Integer> rateInc = Maps.newHashMap();//概率提升
			Map<Integer,Integer> counts = Maps.newHashMap();//已抽取次数
			Map<Integer,Integer> maxCounts = Maps.newHashMap();//最大次数
			Map<Integer,String> items = Maps.newHashMap();//道具列表
			if(!MyUtil.isNullOrEmpty(dt.getItem1())){
				rates.put(1, Integer.parseInt(dt.getRate1().split(",")[0]));
				rateInc.put(1, Integer.parseInt(dt.getRate1().split(",")[1]));
				counts.put(1, 0);
				maxCounts.put(1, dt.getMax1());
				items.put(1, dt.getItem1());
			}
			if(!MyUtil.isNullOrEmpty(dt.getItem2())){
				rates.put(2, Integer.parseInt(dt.getRate2().split(",")[0]));
				rateInc.put(2, Integer.parseInt(dt.getRate2().split(",")[1]));
				counts.put(2, 0);
				maxCounts.put(2, dt.getMax2());
				items.put(2, dt.getItem2());
			}
			if(!MyUtil.isNullOrEmpty(dt.getItem3())){
				rates.put(3, Integer.parseInt(dt.getRate3().split(",")[0]));
				rateInc.put(3, Integer.parseInt(dt.getRate3().split(",")[1]));
				counts.put(3, 0);
				maxCounts.put(3, dt.getMax3());
				items.put(3, dt.getItem3());
			}
			if(!MyUtil.isNullOrEmpty(dt.getItem4())){
				rates.put(4, Integer.parseInt(dt.getRate4().split(",")[0]));
				rateInc.put(4, Integer.parseInt(dt.getRate4().split(",")[1]));
				counts.put(4, 0);
				maxCounts.put(4, dt.getMax4());
				items.put(4, dt.getItem4());
			}
			if(!MyUtil.isNullOrEmpty(dt.getItem5())){
				rates.put(5, Integer.parseInt(dt.getRate5().split(",")[0]));
				rateInc.put(5, Integer.parseInt(dt.getRate5().split(",")[1]));
				counts.put(5, 0);
				maxCounts.put(5, dt.getMax5());
				items.put(5, dt.getItem5());
			}
			if(!MyUtil.isNullOrEmpty(dt.getItem6())){
				rates.put(6, Integer.parseInt(dt.getRate6().split(",")[0]));
				rateInc.put(6, Integer.parseInt(dt.getRate6().split(",")[1]));
				counts.put(6, 0);
				maxCounts.put(6, dt.getMax6());
				items.put(6, dt.getItem6());
			}
			
			for(int i = 1;i <= count;i++){
				int key = GameMath.getRate(rates);//随机到的key
				if(key == 0){
					break;
				}
				counts.put(key, counts.get(key) + 1);//已抽次数+1
				for(Integer index : rates.keySet()){//未抽到的加概率
					if(index != key){
						rates.put(index, rates.get(index) + rateInc.get(index));
					}
				}
				String[] itemStr = items.get(key).split(";");
				RewardDto temp = resourceService.getRewardDto(itemStr[GameMath.getRandInt(itemStr.length)], "100");
//				resourceService.getTotalRewardDto(rt, temp);
				rt.add(temp);
				if(counts.get(key) >= maxCounts.get(key)){
					rates.remove(key);
				}
				if(rates.isEmpty()){
					break;
				}
			}
		}
		return rt;
	}
	

}
