package com.igame.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.igame.core.log.ExceptionLog;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.mingyunZhiMen.FateDto;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.xinmo.XingMoDto;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.dto.FightProp;
import com.igame.work.monster.dto.Gods;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.dto.WuEffect;
import com.igame.work.quest.dto.TaskDayInfo;
import com.igame.work.user.dto.Mail;
import com.igame.work.user.dto.MessageCache;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.Team;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息发送类
 * @author Marcus.Z
 *
 */
public class MessageUtil {
	
	/**
	 * 推送一条消息给玩家
	 * @param player
	 * @param protrol
	 * @param vo
	 */
	public static void sendMessageToPlayer(Player player,int protrol,RetVO vo){

		if (player == null){
			return;
		}
		
		vo.setIsPush(1);
		vo.setIndex(player.getProIndex().incrementAndGet());
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		ISFSObject res = new SFSObject();
		try {
			json = mapper.writeValueAsString(vo);
		} catch (JsonProcessingException e) {
			ExceptionLog.error("MessageUtil sendMessageToPlayer error:" + e);
		}
		res.putUtfString("infor", json);
		synchronized (player.getProPushLock()) {
			player.getProTuiMap().put(vo.getIndex(), new MessageCache(String.valueOf(protrol), res));
			int size = player.getProTuiMap().size();
			if(size > 20){
				int left = size - 20;
				List<Integer> key = player.getProTuiMap().keySet().stream().collect(Collectors.toList());
				key.sort((h1, h2) -> h1-h2);
				if(key.size() < left){
					left = key.size();
				}
				for(int i = 0;i < left;i++){
					player.getProTuiMap().remove(key.get(i));
				}

			}
		}
//	    if(protrol != 1006){
			player.getUser().getZone().getExtension().send(MProtrol.toStringProtrol(protrol), res, player.getUser());
//	    }
			

		
	}
	
	
	public static void sendMessageToPlayerNoCache(Player player,MessageCache mc){
		
		if(player != null){
			player.getUser().getZone().getExtension().send(mc.getCmdName(), mc.getiSFSObject(), player.getUser());
		}

		
	}
	
	/**
	 * 
	 * @param player
	 * @param chapterId
	 */
	public static void notiyTimeResToPlayer(Player player,int chapterId,RewardDto reward){
		
		RetVO vo = new RetVO();	
    	vo.addData("chapterId", chapterId);
    	vo.addData("reward", ResourceService.ins().getRewardString(reward));
    	MessageUtil.sendMessageToPlayer(player, MProtrol.CHECKPOINT_RES_UPDATE, vo);
		
	}
	
	/**
	 * 
	 * @param player
	 * @param items
	 */
	public static void notiyItemChange(Player player,List<Item> items){
		
		if(!items.isEmpty()){
			RetVO vo = new RetVO();
    		vo.addData("items", items);
    		MessageUtil.sendMessageToPlayer(player, MProtrol.ITEM_UPDATE, vo);
		}
		
	}

	/**
	 * 推送怪兽更新
	 * @param player 玩家
	 * @param mons 怪兽list
	 */
	public static void notiyMonsterChange(Player player,List<Monster> mons){
		
		if(!mons.isEmpty()){
			RetVO vo = new RetVO();		
	    	vo.addData("monsters", mons);
			MessageUtil.sendMessageToPlayer(player, MProtrol.MONSTER_UPDATE, vo);
		}
	}
	
	/**
	 * 
	 * @param player
	 */
	public static void notiyMonsterPropChange(Player player,List<FightProp> props){
		
		if(!props.isEmpty()){
			RetVO vo = new RetVO();		
	    	vo.addData("props", props);
			MessageUtil.sendMessageToPlayer(player, MProtrol.TEST, vo);
		}
	}
	
	
	/**
	 * 推送怪兽列表
	 * @param player 玩家
	 * @param mons 怪兽list
	 */
	public static void notiyMonsterList(Player player,int total,int index,List<Monster> mons){
		
		if(!mons.isEmpty()){
			RetVO vo = new RetVO();	
			vo.addData("total", total);
			vo.addData("index", index);
	    	vo.addData("monsters", mons);
			MessageUtil.sendMessageToPlayer(player, MProtrol.MONSTER_LSIT, vo);
		}
	}
	
	/**
	 * 
	 * @param player
	 */
	public static void notiyGodsChange(Player player,List<Gods> gods){
		
		if(!gods.isEmpty()){
			RetVO vo = new RetVO();		
	    	vo.addData("gods", gods);
			MessageUtil.sendMessageToPlayer(player, MProtrol.Gods_TUI, vo);
		}
	}
	
	/**
	 * 心魔列表更新
	 * @param player
	 */
	public static void notiyXingMoChange(Player player,String removeId,List<XingMoDto> ls){
		
		RetVO vo = new RetVO();	
		player.calLeftTime();
		vo.addData("removeId", removeId);
    	vo.addData("xinMo", ls);
    	long now = System.currentTimeMillis();
    	for(XingMoDto xd : ls){
    		xd.calLeftTime(now);
    	}
		MessageUtil.sendMessageToPlayer(player, MProtrol.XINGMO_UP, vo);

	}
	
	/**
	 * 同化对象更新
	 * @param player
	 */
	public static void notiyTongHuaDtoChange(Player player){
		
		RetVO vo = new RetVO();	
		player.getTonghua().calLeftTime();
    	vo.addData("tongInfo", player.getTonghua());
		MessageUtil.sendMessageToPlayer(player, MProtrol.TONGHUA_UPDATE, vo);

	}
	
	
	/**
	 * 同化属性更新
	 * @param player
	 */
	public static void notiyTongHuaAddChange(Player player){
		
		RetVO vo = new RetVO();	
    	vo.addData("tongAdd", player.getTongAdd());
		MessageUtil.sendMessageToPlayer(player, MProtrol.TONGHUAINFO_UPDATE, vo);

	}
	
	/**
	 * 新邮件通知
	 * @param player
	 */
	public static void notiyNewMail(Player player,Mail mail){
		
		RetVO vo = new RetVO();	
    	vo.addData("mail", mail);
		MessageUtil.sendMessageToPlayer(player, MProtrol.MAIL_TUI, vo);

	}
	
	
	/**
	 * 新邮件通知
	 * @param player
	 */
	public static void notiyMeetM(Player player){
		
		RetVO vo = new RetVO();	
    	vo.addData("meetM", MyUtil.toString(player.getMeetM(), ","));
		MessageUtil.sendMessageToPlayer(player, MProtrol.MEET_UPDATE, vo);

	}
	
	/**
	 * 造物台更新
	 * @param player
	 */
	public static void notiyDrawData(Player player){
		
		RetVO vo = new RetVO();	
    	vo.addData("draw", player.getDraw());
		MessageUtil.sendMessageToPlayer(player, MProtrol.DRAW_UPDATE, vo);

	}
	
	/**
	 * 匹配结束
	 */
	public static void notiyMatchEnd(FightBase fb){
		
		RetVO vo = new RetVO();			
    	vo.addData("i", fb.getFightB().getPlayer().getPlayerId());
    	vo.addData("n", fb.getFightB().getPlayer().getUsername());
    	vo.addData("l", fb.getFightB().getPlayer().getPlayerLevel());    	
    	List<MatchMonsterDto> la = Lists.newArrayList();
    	for(Monster m : fb.getFightB().getMonsters().values()){
    		MatchMonsterDto mto = new MatchMonsterDto(m);
    		la.add(mto);
    	}
    	vo.addData("m", la);
		MessageUtil.sendMessageToPlayer(fb.getFightA().getPlayer(), MProtrol.F_P_E, vo);
		
		
		RetVO vo2 = new RetVO();			
    	vo2.addData("i", fb.getFightA().getPlayer().getPlayerId());
    	vo2.addData("n", fb.getFightA().getPlayer().getUsername());
    	vo2.addData("l", fb.getFightA().getPlayer().getPlayerLevel());    	
    	List<MatchMonsterDto> lb = Lists.newArrayList();
    	for(Monster m : fb.getFightA().getMonsters().values()){
    		MatchMonsterDto mto = new MatchMonsterDto(m);
    		lb.add(mto);
    	}
    	vo2.addData("m", lb);
		MessageUtil.sendMessageToPlayer(fb.getFightB().getPlayer(), MProtrol.F_P_E, vo2);
		


	}
	
	
	/**
	 * 开始加载游戏
	 */
	public static void notiyMatchLoad(FightBase fb){
		
		RetVO vo = new RetVO();			
		MessageUtil.sendMessageToPlayer(fb.getFightA().getPlayer(), MProtrol.F_P_ENTER, vo);
		MessageUtil.sendMessageToPlayer(fb.getFightB().getPlayer(), MProtrol.F_P_ENTER, vo);

	}
	
	
	/**
	 * 开始加载游戏
	 */
	public static void gameStart(FightBase fb){
		
		RetVO vo = new RetVO();			
		MessageUtil.sendMessageToPlayer(fb.getFightA().getPlayer(), MProtrol.F_START, vo);
		MessageUtil.sendMessageToPlayer(fb.getFightB().getPlayer(), MProtrol.F_START, vo);

	}
	
	
	public static void notiyWinner(Player player,long winner){
		
		RetVO vo = new RetVO();	
    	vo.addData("winner", winner);
		MessageUtil.sendMessageToPlayer(player, 400, vo);

	}
	
	
	
	/**
	 * 任务更新
	 * @param player
	 */
	public static void notiyQuestChange(Player player,List<TaskDayInfo> qList){
		
		if(!qList.isEmpty()){
			RetVO vo = new RetVO();		
	    	vo.addData("quest", qList);
			MessageUtil.sendMessageToPlayer(player, MProtrol.QUEST_UPDATE, vo);
		}
	}
	
	
	/**
	 * 星河之眼更新
	 * @param player
	 */
	public static void notiyTrialChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("towerId", player.getTowerId());
    	vo.addData("oreCount", player.getOreCount());
		MessageUtil.sendMessageToPlayer(player, MProtrol.TRIAL_UPDATE, vo);
		
	}
	
	
	/**
	 * 无尽之森关卡更新
	 * @param player
	 */
	public static void notiyWuChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("wuMap", player.getWuMap().values());
		MessageUtil.sendMessageToPlayer(player, MProtrol.WU_UPDATE, vo);
		
	}
	
	/**
	 * 无尽无尽之森自己怪物阵容更新
	 * @param player
	 */
	public static void notiyWuZhengChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("wuZheng", CheckPointService.parsePlayer(player));
		MessageUtil.sendMessageToPlayer(player, MProtrol.WUZHENG_UPDATE, vo);
		
	}
	
	/**
	 * 推送无尽之森奶更新
	 * @param player
	 */
	public static void notiyWuNaiChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("wuNai", player.getWuNai());
		MessageUtil.sendMessageToPlayer(player, MProtrol.WUNAI_UPDATE, vo);
		
	}
	
	/**
	 * 无尽无尽之森自己怪物阵容更新
	 * @param player
	 */
	public static void notiyWuBufferChange(Player player,List<WuEffect> ls){
	
		RetVO vo = new RetVO();		
    	vo.addData("wuEffect", ls);
		MessageUtil.sendMessageToPlayer(player, MProtrol.WUBUFFER_UPDATE, vo);
		
	}
	
	
	/**
	 * 无尽之森已用免费重置次数更新
	 * @param player
	 */
	public static void notiyWuResetChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("wuReset", player.getWuReset());
		MessageUtil.sendMessageToPlayer(player, MProtrol.WU_RESET, vo);
		
	}
	
	
	/**
	 * 推送命运之门信息更新
	 * @param player
	 */
	public static void notiyDeInfoChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("desInfo", FateDto.creatFateDto(player.getFateData()));
		MessageUtil.sendMessageToPlayer(player, MProtrol.DE_UPDATE, vo);
		
	}
	
	
	/**
	 * 推送新门更新
	 * @param player
	 */
	public static void notiyGateChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("gateInfo", player.getFateData().getGate());
		MessageUtil.sendMessageToPlayer(player, MProtrol.GATE_UPDATE, vo);
		
	}
	
	
	/**
	 * 推送竞技场已用挑战次数
	 * @param player
	 */
	public static void notiyAreaCountChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("areaCount", player.getAreaCount());
		MessageUtil.sendMessageToPlayer(player, MProtrol.AREA_COUNT, vo);
		
	}


	/**
	 * 推送阵容更新
	 * @param player
	 */
	public static void notiyTeamChange(Player player, Team team){

		RetVO vo = new RetVO();
    	vo.addData("team", team);
		MessageUtil.sendMessageToPlayer(player, MProtrol.TEAM_UPDATE, vo);

	}
	
	
	/**
	 * 推送玩家倒计时开始
	 * @param player
	 */
	public static void notiyCDDown(Player player, int type){

		RetVO vo = new RetVO();
    	vo.addData("type", type);
		MessageUtil.sendMessageToPlayer(player, MProtrol.CD_DOWN, vo);

	}


	/**
	 * 推送玩家vip更新
	 * @param player
	 */
	public static void notiyVipPrivilegesChange(Player player) {

		//推送
		RetVO vo = new RetVO();
		vo.addData("vipLv", player.getVip());
		vo.addData("vipPrivileges", player.getVipPrivileges());

		MessageUtil.sendMessageToPlayer(player, MProtrol.VIP_UPDATE, vo);
	}


	/**
	 * 推送玩家大转盘更新
	 * @param player
	 */
	public static void notiyTurntableChange(Player player) {

		//推送
		RetVO vo = new RetVO();
		vo.addData("turntable", player.transTurntableVo());

		MessageUtil.sendMessageToPlayer(player, MProtrol.TURNTABLE_UPDATE, vo);
	}

	/**
	 * 推送玩家好友更新
	 * @param player
	 */
	public static void notiyFriendInfo(Player player) {

		//推送
		RetVO vo = new RetVO();

		vo.addData("friends",player.getFriends());
		MessageUtil.sendMessageToPlayer(player, MProtrol.FRIENDS_UPDATE, vo);

	}
}
