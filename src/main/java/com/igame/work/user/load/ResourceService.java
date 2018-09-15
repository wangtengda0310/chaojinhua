package com.igame.work.user.load;

import com.google.common.collect.Lists;
import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.event.EventService;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.PlayerEvents;
import com.igame.work.fight.FightDataManager;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.draw.DrawLevelTemplate;
import com.igame.work.fight.data.GodsdataTemplate;
import com.igame.work.user.data.ItemTemplate;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.core.log.GoldLog;
import com.igame.dto.IDFactory;
import com.igame.core.handler.RetVO;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.dto.Gods;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.quest.service.QuestService;
import com.igame.work.shop.service.ShopService;
import com.igame.work.turntable.service.TurntableService;
import com.igame.work.user.HeadConstants;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.Team;
import com.igame.work.user.service.HeadService;
import com.igame.work.vip.VIPService;

import java.util.List;
import java.util.Map;

/**
 *玩家资源操作类
 * @author Marcus.Z
 *
 */
public class ResourceService extends EventService implements ISFSModule {
	@Inject private QuestService questService;
	@Inject private PlayerService playerService;
	@Inject private MonsterService monsterService;
	@Inject private TurntableService turntableService;
	@Inject private ShopService shopService;
	@Inject private HeadService headService;
	@Inject private IDFactory idFactory;
	@Inject private VIPService vipService;

	
	//1 - 金币 2-钻石 3-体力 4-扫荡券 5-同化经验 6-同化点 7-星能 8-无尽积分 9-斗技积分 10-起源积分 11-部落积分 12-远征积分 13-悬赏积分 14-充值金额
    private static final int GOLD = 1;
    private static final int Diamond = 2;
    private static final int Physical = 3;
    private static final int Sao = 4;
    public static final int TONGHUAEXP = 5;
    private static final int TONGHUAPOINT= 6;
    private static final int XING = 7;

    public static final int WUJIN = 8;
    private static final int DOUJI = 9;
    private static final int QIYUAN = 10;
    private static final int BULUO = 11;
    public static final int YUANZHENG = 12;
    public static final int XUANSHANG = 13;

    private static final int MONEY = 14;


	public static String getRewardString(RewardDto reward){

    	StringBuffer items = new StringBuffer();
		if(reward.getGold() >0 ){
			items.append("1,1,").append(reward.getGold()).append(";");
		}
		if(reward.getDiamond() > 0){
			items.append("1,2,").append(reward.getDiamond()).append(";");
		}
		if(reward.getPhysical() > 0){
			items.append("1,3,").append(reward.getPhysical()).append(";");
		}
		if(reward.getTongExp() > 0){
			items.append("1,5,").append(reward.getTongExp()).append(";");
		}
		if(!reward.getItems().isEmpty()){
			for(Map.Entry<Integer, Integer> m : reward.getItems().entrySet()){
				items.append("3,").append(m.getKey()).append(",").append(m.getValue()).append(";");
			}
		}
		if(!reward.getMonsters().isEmpty()){
			for(Map.Entry<Integer, Integer> m : reward.getMonsters().entrySet()){
				items.append("2,").append(m.getKey()).append(",").append(m.getValue()).append(";");
			}
		}
		
		String rr = items.toString();
		if(rr.lastIndexOf(";") != -1){
			rr = rr.substring(0,rr.lastIndexOf(";"));
		}
		return rr;
    	
    }
    

	public void addRewarToPlayer(Player player,RewardDto reward){

		// ADDREWARD
		if(reward.getGold() >0 ){
			addGold(player, reward.getGold());
		}
		if(reward.getDiamond() > 0){
			addDiamond(player, reward.getDiamond());
		}
		if(reward.getPhysical() > 0){
			addPhysica(player, reward.getPhysical());
		}
		if(reward.getTongExp() > 0){
			addTongExp(player, reward.getTongExp());
		}
		if(!reward.getItems().isEmpty()){
			for(Map.Entry<Integer, Integer> m : reward.getItems().entrySet()){
				addItem(player, m.getKey(), m.getValue(), true);
			}
		}
		if(!reward.getMonsters().isEmpty()){
			for(Map.Entry<Integer, Integer> m : reward.getMonsters().entrySet()){
				addMonster(player, m.getKey(), m.getValue(), true);
			}
		}
		
	}
    

    public RewardDto getResRewardDto(String val,int minuts,int maxMinuts){
    	
    	int timeCount = minuts;
    	if(timeCount > maxMinuts){
    		timeCount = maxMinuts;
    	}
    	int count = timeCount/60;//总数
    	RewardDto dto = new RewardDto();
		if(!MyUtil.isNullOrEmpty(val)){
			String[] tels = val.split(";");
			for(String tel : tels){
				String[] temp  = tel.split(",");
				switch (temp[0]){
					case "1":
						if(Integer.parseInt(temp[1]) == 1){//gold
							dto.setGold(dto.getGold() + (long)(Double.parseDouble(temp[2]) * count));
							break;
						} else if(Integer.parseInt(temp[1]) == 2){//Diamond
							dto.setDiamond(dto.getDiamond() + (int)(Double.parseDouble(temp[2]) * count));
							break;
						}else if(Integer.parseInt(temp[1]) == 3){//Physical
							dto.setPhysical(dto.getPhysical() + (int)(Double.parseDouble(temp[2]) * count));
							break;
						}else if(Integer.parseInt(temp[1]) == 5){//TONGHUAEXP
							dto.setTongExp(dto.getTongExp() + (int)(Double.parseDouble(temp[2]) * count));
							break;
						}
						else{
							break;
						}
					case "2"://mon
						dto.addMonster(Integer.parseInt(temp[1]), (int)(Double.parseDouble(temp[2]) * count));
						break;
					case "3"://item
						dto.addItem(Integer.parseInt(temp[1]), (int)(Double.parseDouble(temp[2]) * count));
						break;
					case "4"://monster exp item
						int totalExp = (int)(Double.parseDouble(temp[2]) * count);
						ItemTemplate max1 = PlayerDataManager.ItemData.getTemplate(200003);
						ItemTemplate max2 = PlayerDataManager.ItemData.getTemplate(200002);
						ItemTemplate max3 = PlayerDataManager.ItemData.getTemplate(200001);
						if(max1 != null){
							if(totalExp > (int)max1.getValue()){//大
								int cl = totalExp/(int)max1.getValue();
								dto.addItem(200003, cl);
								totalExp -= cl * (int)max1.getValue();
							}
						}
						if(max2 != null){
							if(totalExp > (int)max2.getValue()){//中
								int cl = totalExp/(int)max2.getValue();
								dto.addItem(200002, cl);
								totalExp -= cl * (int)max2.getValue();
							}
						}
						if(max3 != null){
							if(totalExp > (int)max3.getValue()){//小
								int cl = totalExp/(int)max3.getValue();
								dto.addItem(200001, cl);
								totalExp -= cl * (int)max3.getValue();
							}
						}
						break;
					default:
						break;

				}
			}
		}
		return dto;
    }
    
    
	/**
	 * 
	 * @param rate 概率 0-100
	 */
    public RewardDto getRewardDto(String val,String rate){
    	
    	RewardDto dto = new RewardDto();
		if(!MyUtil.isNullOrEmpty(val)){
			String[] tels = val.split(";");
			String[] rateVal = rate.split(";");
			if("100".equals(rate)){
				rateVal = new String[tels.length];
				for(int i = 0;i < tels.length;i++){
					rateVal[i] = "100";
				}
			}
			int i = 0;
			for(String tel : tels){
				if(GameMath.hitRate(Integer.parseInt(rateVal[i]) * 100)){
					String[] temp  = tel.split(",");
					switch (temp[0]){
						case "1":
							if(Integer.parseInt(temp[1]) == 1){//gold
								dto.setGold(dto.getGold() + Long.parseLong(temp[2]));
								break;
							} else if(Integer.parseInt(temp[1]) == 2){//Diamond
								dto.setDiamond(dto.getDiamond() + Integer.parseInt(temp[2]));
								break;
							}else if(Integer.parseInt(temp[1]) == 3){//Physical
								dto.setPhysical(dto.getPhysical() + Integer.parseInt(temp[2]));
								break;
							}else if(Integer.parseInt(temp[1]) == 5){//tongRes
								dto.setTongExp(dto.getTongExp() + Integer.parseInt(temp[2]));
								break;
							}
							else{
								break;
							}
						case "2"://mon
							dto.addMonster(Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
							break;
						case "3"://item
							dto.addItem(Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
							break;
						default:
							break;

					}
				}
				i++;
			}
		}
		return dto;
    }
    
    

    public RewardDto getTotalRewardDto(RewardDto total,RewardDto add){
    	
    	total.addGold(add.getGold());
    	total.addDiamond(add.getDiamond());
    	total.addExp(add.getExp());
    	total.addPhysical(add.getPhysical());
    	total.addTongExp(add.getTongExp());
    	for(Map.Entry<Integer, Integer> item : add.getItems().entrySet()){
    		total.addItem(item.getKey(), item.getValue());
    	}
    	for(Map.Entry<Integer, Integer> mon : add.getMonsters().entrySet()){
    		total.addMonster(mon.getKey(), mon.getValue());
    	}
    	
    	return total;
    }
    
    /**
     * 增加经验
     */
    public int addExp(Player player,int expAdd){

    	int exp = player.getExp() + expAdd;
		int tempLevel = player.getPlayerLevel();//当前等级
		int maxLevel = PlayerDataManager.PlayerLvData.getMaxLevel();//最大等级
		int currLevel = player.getPlayerLevel();
		int nextLevelNeedExp = PlayerDataManager.PlayerLvData.getTemplate(currLevel).getPlayerExp();//升到下一级所需经验
		while (currLevel < maxLevel && exp >= nextLevelNeedExp) {	// 满级后经验值不可以获得经验
			currLevel++;
			exp-=nextLevelNeedExp;
			nextLevelNeedExp = PlayerDataManager.PlayerLvData.getTemplate(currLevel).getPlayerExp();
		}
		if(currLevel>=maxLevel){	// 满级后经验值卡在0 不能增加
			exp = 0;
		}
		player.setExp(exp);
		if (currLevel != tempLevel) {
			player.setPlayerLevel(currLevel);
			upgradePlayer(player,tempLevel, currLevel);
		}
		RetVO vo = new RetVO();
		vo.addData("playerLevel", player.getPlayerLevel());
		vo.addData("exp", player.getExp());
		MessageUtil.sendMessageToPlayer(player, MProtrol.PLAYER_LEVEL_UP, vo);
		return expAdd;
    }
    
    
    /**
     * 增加怪物经验
     */
    public int addMonsterExp(Player player,long objectId,int expAdd,boolean send){
    	int ret = 0;
    	Monster mm = player.getMonsters().get(objectId);
    	if(mm == null){
    		ret = ErrorCode.MONSTER_NOT;
    		return ret;
    	}
    	if(mm.getLevel() >= MonsterDataManager.MonsterLvData.getMaxLevel()){//最大等级
			if (mm.getExp() != 0) {	// 经验值卡在0 不能增加
				mm.setExp(0);
			}
    		ret = ErrorCode.LEVEL_MAX;
    		return ret;
    	}
    	int ml = PlayerDataManager.PlayerLvData.getTemplate(player.getPlayerLevel()).getMonsterLv();//不能超过人物等级
    	if(mm.getLevel() >= ml){//最大等级
			if (mm.getExp() != 0) {	// 经验值卡在0 不能增加
				mm.setExp(0);
			}
    		ret = ErrorCode.LEVEL_NOT_PLAYER;
    		return ret;
    	}
    	int exp = mm.getExp() + expAdd;
		int tempLevel = mm.getLevel();//当前等级
		int maxLevel = MonsterDataManager.MonsterLvData.getMaxLevel();//最大等级
		int currLevel = mm.getLevel();
		int nextLevelNeedExp = MonsterDataManager.MonsterLvData.getTemplate(currLevel).getExp();//升到下一级所需经验

		while (currLevel < maxLevel && currLevel < ml && exp >= nextLevelNeedExp) {
			currLevel++;
			exp-=nextLevelNeedExp;
			nextLevelNeedExp = MonsterDataManager.MonsterLvData.getTemplate(currLevel).getExp();
		}
		if(currLevel>=maxLevel || currLevel >= ml){	// 满级或达到玩家等级后经验值卡在0 不能增加
			exp = 0;
		}
		mm.setExp(exp);
		if(currLevel >=  MonsterDataManager.MonsterLvData.getMaxLevel() || currLevel >= ml){
//			mm.setExp(0);
		}
		if (currLevel != tempLevel) {
			mm.setLevel(currLevel);
			mm.setTeamEquip(player.getTeams().values());
			mm.reCalculate(player,true);
			String[] equs = mm.getEquip().split(",");
			boolean change = false;
			if(mm.getLevel() >= 5 && "-1".equals(equs[0])){
				change = true;
				equs[0] = "0";
			}
			if(mm.getLevel() >= 15 && "-1".equals(equs[1])){
				change = true;
				equs[1] = "0";
			}
			if(mm.getLevel() >= 22 && "-1".equals(equs[2])){
				change = true;
				equs[2] = "0";
			}
			if(mm.getLevel() >= 30 && "-1".equals(equs[3])){
				change = true;
				equs[3] = "0";
			}
			if(change){
				mm.setEquip(MyUtil.toString(equs, ","));
			}
			questService.processTask(player, 1, currLevel - tempLevel);
		}


		mm.setDtate(2);
		if(send){
			RetVO vo = new RetVO();
    		List<Monster> ll = Lists.newArrayList();
    		ll.add(mm);
    		vo.addData("monsters", ll);
    		MessageUtil.sendMessageToPlayer(player, MProtrol.MONSTER_UPDATE, vo);
		}

		return ret;
    }
    
    /**
     * 增加金币
     */
    public void addGold(Player player,long value){
    	player.addGold(value);
		RetVO vo = new RetVO();
		vo.addData("stype", GOLD);
		vo.addData("count", player.getGold());
    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
    			+"#act:addRes"+"#stype:" + GOLD + "#count:"+value);
		MessageUtil.sendMessageToPlayer(player, MProtrol.GOLD_UPDATE, vo);

		if (value < 0) {
			fireEvent(player, PlayerEvents.CONSUME_GOLD, new Object[]{System.currentTimeMillis(), value});
		}
    }
    
    
    /**
     * 增加体力
     */
    public void addPhysica(Player player,int value){
    	player.addPhysical(value);
		RetVO vo = new RetVO();
		vo.addData("stype", Physical);
		vo.addData("count", player.getPhysical());
//    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
//    			+"#act:addRes"+"#stype:" + Physical + "#count:"+value+"#Date:"+System.currentTimeMillis());
		MessageUtil.sendMessageToPlayer(player, MProtrol.GOLD_UPDATE, vo);

    }
    
    /**
     * 增加钻石
     */
    public void addDiamond(Player player,int value){
    	player.addDiamond(value);
		RetVO vo = new RetVO();
		vo.addData("stype", Diamond);
		vo.addData("count", player.getDiamond());
    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
    			+"#act:addRes"+"#stype:" + Diamond + "#count:"+value);
		MessageUtil.sendMessageToPlayer(player, MProtrol.GOLD_UPDATE, vo);

		if (value < 0) {
			fireEvent(player, PlayerEvents.CONSUME_DIAMOND, new Object[]{System.currentTimeMillis(),value});
		}
		if (value > 0) {
			fireEvent(player, PlayerEvents.RECHARGE, new Object[]{System.currentTimeMillis(),value});
		}
    }
    
    
    /**
     * 玩家升级之后的事情
     */
    public void upgradePlayer(Player player,int tempLevel,int currLevel){
    	fireEvent(player, PlayerEvents.LEVEL_UP, new Object[]{tempLevel, currLevel});
    	int old = player.getBattleSpace();
    	if(currLevel>= 2 && currLevel<10){
    		player.setBattleSpace(2);
    	}else if(currLevel>= 10 && currLevel<19){
    		player.setBattleSpace(3);
    	}else if(currLevel>= 19 && currLevel<23){
    		player.setBattleSpace(4);
    	}else if(currLevel>= 23){
    		player.setBattleSpace(5);
    	}
    	if(tempLevel < 30 && currLevel >= 30){
        	if(player.getTonghua() == null){
        		player.setTonghua(playerService.getRandomTongHuaDto());
        		player.getTonghua().setStartRefTime(System.currentTimeMillis());
        	}
    	}
    	if(player.getBattleSpace() != old){	//开放阵位
			for (Team team : player.getTeams().values()) {
				for (int i = 0; i < player.getBattleSpace(); i++) {
					if (team.getTeamMonster()[i] == -1){
						team.getTeamMonster()[i] = 0;
					}
				}
//				MessageUtil.notifyTeamChange(player,team);
			}
			RetVO vo = new RetVO();
    		vo.addData("battleSpace", player.getBattleSpace());
    		MessageUtil.sendMessageToPlayer(player, MProtrol.PLAYER_INDE_UP, vo);
    	}
    	List<Gods> ll = Lists.newArrayList();
    	for(Integer type : FightDataManager.GodsData.getSets()){
    		GodsdataTemplate gt = FightDataManager.GodsData.getTemplate(type+"_0");
    		if(gt != null && player.getPlayerLevel() >= gt.getUnlockLv() && player.getGods().get(type) == null){   			
    			Gods gods = new Gods(player.getPlayerId(), type, 0, 1);
    			ll.add(gods);
    			player.getGods().put(gods.getGodsType(), gods);
    		}
    	}
    	MessageUtil.notifyGodsChange(player, ll);
    	playerService.checkDrawData(player, true);//检测造物台数据
    	questService.onLevelUp(player);//任务开放

		//判断是否解锁神秘商店
		shopService.initMysticalShop(player);
		//判断是否解锁幸运大转盘
		turntableService.initTurntable(player);
		//触发解锁头像
		headService.unlockHead(player, HeadConstants.HEAD_TOUCH_LV);
		//触发解锁头像框
		headService.unlockHeadFrame(player, HeadConstants.HEAD_TOUCH_LV);

    }
    
    /**
     * 添加道具
     */
    public Item addItem(Player player,int itemId,int count,boolean send){
    	int ret = 0;
    	Item item = player.getItems().get(itemId);
    	if(PlayerDataManager.ItemData.getTemplate(itemId) == null){
    		ret = 1;
    	}else{
        	if(item == null){
        		item = new Item(player.getPlayerId(), itemId, count, 1);
        	}else{
        		item.setCount(item.getCount() + count);
        		item.setDtate(2);
        	}
        	player.getItems().put(itemId, item);
			if(item.getCount() <= 0){
				item.setCount(0);
				item.setDtate(3);
				player.getRemoves().add(player.getItems().remove(item.getItemId()));
			}
        	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
        			+"#act:addItem" + "#itemId:" + itemId+"#count:"+count);
    	}

    	if(send){
    		RetVO vo = new RetVO();
    		vo.setState(ret);
    		vo.setErrCode(ret);
    		if(ret == 0){
        		List<Item> ll = Lists.newArrayList();
        		ll.add(item);
        		vo.addData("items", ll);
    		}
    		MessageUtil.sendMessageToPlayer(player, MProtrol.ITEM_UPDATE, vo);
    	}
    	return item;
    }
    
    
    /**
     * 添加怪物
     */
    public List<Monster> addMonster(Player player,int monster_id,int count,boolean send){
    	int ret = 0;
    	MonsterTemplate mt = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(monster_id);
    	List<Monster> ls = Lists.newArrayList();
    	if(mt == null){
    		ret = 1;
    	}else{
    		for(int i = 0;i < count;i++){
    			Monster m = new Monster(player,idFactory.getNewIdMonster(player.getSeverId()), player.getPlayerId(),  mt.getMonster_hp(), 1,mt.getMonster_id());
            	player.getMonsters().put(m.getObjectId(), m);
            	m.reCalculate(player, true);
            	ls.add(m);
            	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
            			+"#act:addMonster" + "#monster_id:" + monster_id+"#count:"+count);
    		}
    		if(player.getMeetM().contains(monster_id)){//把遇到的怪物删除
    			player.getMeetM().remove(monster_id);
    			MessageUtil.notifyMeetM(player);
    		}
    		//重新计算玩家怪物图鉴属性
    		monsterService.reCalMonsterExtPre(player,true);
    		questService.processTask(player, 16, count);
    		//触发解锁头像
			headService.unlockHead(player,HeadConstants.HEAD_TOUCH_MONSTER);
			fireEvent(player, PlayerEvents.GOT_MONSTER, monster_id);
    	}

    	if(send){
    		RetVO vo = new RetVO();
    		vo.setState(ret);
    		vo.setErrCode(ret);
    		if(ret == 0){
        		vo.addData("monsters", ls);
    		}
    		MessageUtil.sendMessageToPlayer(player, MProtrol.MONSTER_UPDATE, vo);
    	}
    	return ls;
    }
    
    /**
     * 使用道具
     */
    public int useItem(Player player,int itemId,int count,int targetType,long targetId){
    	
    	int ret = 0;
    	Item item = player.getItems().get(itemId);
    	if(item == null || item.getUsableCount(-1) <= 0 || count == 0){
    		ret = ErrorCode.ITEM_NOT_EXIT;//道具不存在
    		return ret;
    	}
    	ItemTemplate itemTemplate = PlayerDataManager.ItemData.getTemplate(itemId);
    	if(itemTemplate == null){
    		ret = ErrorCode.ITEM_NOT_EXIT;//道具不存在
    		return ret;
    	}
    	if(itemTemplate.getItemType() == 2 && itemTemplate.getSubtype() == 2){//消耗道具
    		if(itemTemplate.getEffect() == 143 && targetType == 2){//给怪物加经验的
    	    	Monster mm = player.getMonsters().get(targetId);
    	    	if(mm == null){
    	    		ret = ErrorCode.MONSTER_NOT;
    	    		return ret;
    	    	}
    	    	if(mm.getLevel() >= MonsterDataManager.MonsterLvData.getMaxLevel()){//最大等级
    	    		ret = ErrorCode.LEVEL_MAX;
    	    		return ret;
    	    	}
    	    	int ml = PlayerDataManager.PlayerLvData.getTemplate(player.getPlayerLevel()).getMonsterLv();//不能超过人物等级
    	    	if(mm.getLevel() >= ml){//最大等级
    	    		ret = ErrorCode.LEVEL_NOT_PLAYER;
    	    		return ret;
    	    	}
    			int left = item.getCount();//总共有的
    			int use = count;//使用的
    			while(left > 0 && use > 0 && addMonsterExp(player,targetId,CheckPointService.getTotalExp(mm, (int)itemTemplate.getValue()),false) == 0){
    				left--;
    				use--;
    			} 
    			if(left != item.getCount()){//至少使用一个道具成功
    				item.setCount(left);
    				item.setDtate(2);
    				if(left <= 0){//已经用光了
    					item.setCount(0);
    					item.setDtate(3);
    					player.getRemoves().add(player.getItems().remove(item.getItemId()));
    				}
					fireEvent(player, PlayerEvents.CONSUME_ITEM, new Object[]{itemId,item.getCount()-left});
    	    		RetVO vo = new RetVO();
	        		List<Item> ll = Lists.newArrayList();
	        		ll.add(item);
	        		vo.addData("items", ll);
	        		MessageUtil.sendMessageToPlayer(player, MProtrol.ITEM_UPDATE, vo);
	        		Monster temp = mm.clonew();
	        		temp.hp = mm.hpTemp;
	        		if(mm.hpTemp == 0){
	        			temp.hp = mm.hpInit;
	        		}
	        		temp.attack = mm.attackTemp;
	        		if(mm.attackTemp == 0){
	        			temp.attack = mm.attack;
	        		}
	        		temp.fightValue = mm.fightValueTemp;
	        		if(mm.fightValueTemp == 0){
	        			temp.fightValue = mm.fightValue;
	        		}
	        		List<Monster> mons = Lists.newArrayList();
	        		mons.add(temp);
	        		MessageUtil.notifyMonsterChange(player, mons);
    	    		
    			}else{
    				ret = ErrorCode.LEVEL_NOT_PLAYER;//一个道具也没使用超成功
    	    		return ret;
    			}
    		}
    		
    	}
    	return ret;
    }
    
    
    
    /**
     * 增加同调经验
     */
    public int addTongExp(Player player,int tongExpAdd){

    	int exp = player.getTongAdd().getTongExp() + tongExpAdd;
		int tempLevel = player.getTongAdd().getTongLevel();//当前等级
		int maxLevel = MonsterDataManager.TongHuaData.getAll().get(MonsterDataManager.TongHuaData.getAll().size() -1).getStrengthen_lv();
		int currLevel = player.getTongAdd().getTongLevel();
		int nextLevelNeedExp = MonsterDataManager.TongHuaData.getTemplate(currLevel).getExp();//升到下一级所需经验;
		while ((currLevel + 1) <= maxLevel && exp >= nextLevelNeedExp) {
			currLevel++;
			exp-=nextLevelNeedExp;
			nextLevelNeedExp = MonsterDataManager.TongHuaData.getTemplate(currLevel).getExp();
		}
		player.getTongAdd().setTongExp(exp);
		if (currLevel != tempLevel) {
			player.getTongAdd().setTongLevel(currLevel);
			questService.processTask(player, 19, 0);
		}
		MessageUtil.notifyTongHuaAddChange(player);
		return tongExpAdd;
    }
    
    /**
     * 增加同化资源
     */
    public void addTongRes(Player player,int value){
    	player.addTongRes(value);
		RetVO vo = new RetVO();
		vo.addData("stype", TONGHUAPOINT);
		vo.addData("count", player.getTongRes());
    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
    			+"#act:addRes"+"#stype:" + TONGHUAPOINT + "#count:"+value);
		MessageUtil.sendMessageToPlayer(player, MProtrol.GOLD_UPDATE, vo);

    }
    
    /**
     * 增加扫荡券
     */
    public void addSao(Player player,int value){
    	player.addSao(value);
		RetVO vo = new RetVO();
		vo.addData("stype", Sao);
		vo.addData("count", player.getSao());
    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
    			+"#act:addRes"+"#stype:" + Sao + "#count:"+value);
		MessageUtil.sendMessageToPlayer(player, MProtrol.GOLD_UPDATE, vo);

    }
    
    /**
     * 增加星能
     */
    public void addXing(Player player,int value){
    	player.addXing(value);
		RetVO vo = new RetVO();
		vo.addData("stype", XING);
		vo.addData("count", player.getXing());
    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
    			+"#act:addRes"+"#stype:" + XING + "#count:"+value);
		MessageUtil.sendMessageToPlayer(player, MProtrol.GOLD_UPDATE, vo);

    }

	/**
	 * 增加无尽积分
	 */
	public void addWuScore(Player player,int value){
    	player.addWuScore(value);
//		RetVO vo = new RetVO();
//		vo.addData("stype", XING);
//		vo.addData("count", player.getXing());
//    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
//    			+"#act:addRes"+"#stype:" + XING + "#count:"+value);
//		MessageUtil.sendMessageToPlayer(player, MProtrol.GOLD_UPDATE, vo);

    }
    
    
    /**
     * 增加造物台经验值
     */
    public void addDrawExp(Player player,int value){
    	
    	int tempExp = player.getDraw().getDrawExp();
    	int exp = player.getDraw().getDrawExp() + value;
		int tempLevel = player.getDraw().getDrawLv();//当前等级
		DrawLevelTemplate max = PlayerDataManager.DrawLevelData.getAll().get(PlayerDataManager.DrawLevelData.getAll().size() -1);
		int maxLevel = max.getDrawLevel();
		int currLevel = player.getDraw().getDrawLv();
		int nextLevelNeedExp = PlayerDataManager.DrawLevelData.getTemplate(currLevel).getDrawExp();//升到下一级所需经验
		int count = 0;
		while ((currLevel + 1) <= maxLevel && exp >= nextLevelNeedExp) {
			currLevel++;
			exp-=nextLevelNeedExp;
			nextLevelNeedExp = PlayerDataManager.DrawLevelData.getTemplate(currLevel).getDrawExp();
			count++;
//			if(count>=50){
//				break;
//			}
		}
		if(exp > max.getDrawExp()){
			exp =  max.getDrawExp();
		}
		player.getDraw().setDrawExp(exp);
		if (currLevel != tempLevel) {
			player.getDraw().setDrawLv(currLevel);
		}
		if (currLevel != tempLevel || tempExp != player.getDraw().getDrawExp()) {
			MessageUtil.notifyDrawData(player);
//	    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
//			+"#act:addRes"+"#stype:" + XING + "#count:"+value);
		}


    }


	/**
	 * 增加斗技场积分
	 */
	public void addDoujiScore(Player player, int value) {

		player.addDoujiScore(value);
		RetVO vo = new RetVO();
		vo.addData("stype", DOUJI);
		vo.addData("count", player.getDoujiScore());
		GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
				+"#act:addRes"+"#stype:" + DOUJI + "#count:"+value);
		MessageUtil.sendMessageToPlayer(player, MProtrol.GOLD_UPDATE, vo);
	}

	/**
	 * 增加起源殿积分
	 */
	public void addQiyuanScore(Player player, int value) {

		player.addQiyuanScore(value);
		RetVO vo = new RetVO();
		vo.addData("stype", QIYUAN);
		vo.addData("count", player.getQiyuanScore());
		GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
				+"#act:addRes"+"#stype:" + QIYUAN + "#count:"+value);
		MessageUtil.sendMessageToPlayer(player, MProtrol.GOLD_UPDATE, vo);
	}

	/**
	 * 增加部落积分
	 */
	public void addBuluoScore(Player player, int value) {

		player.addBuluoScore(value);
		RetVO vo = new RetVO();
		vo.addData("stype", BULUO);
		vo.addData("count", player.getBuluoScore());
		GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
				+"#act:addRes"+"#stype:" + BULUO + "#count:"+value);
		MessageUtil.sendMessageToPlayer(player, MProtrol.GOLD_UPDATE, vo);
	}
	
	/**
	 * 竞技场挑战次数
	 */
    public void addAreaCount(Player player,int value){
    	player.addAreaCount(value);
//    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
//    			+"#act:addRes"+"#stype:" + Sao + "#count:"+value);
		MessageUtil.notifyAreaCountChange(player);

    }

	/**
	 * 增加充值金额
	 */
	public void addMoney(Player player,long value){

		player.addMoney(value);
		GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
				+"#act:addRes"+"#stype:" + MONEY + "#count:"+value);

		boolean flag = vipService.addVipLv(player);
		if (flag){
			MessageUtil.notifyVipPrivilegesChange(player);
		}

	}
}
