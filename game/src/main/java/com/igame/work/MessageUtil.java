package com.igame.work;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igame.core.handler.RetVO;
import com.igame.core.log.ExceptionLog;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.mingyunZhiMen.FateDto;
import com.igame.work.checkpoint.xinmo.XingMoDto;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.item.dto.Item;
import com.igame.work.mail.Mail;
import com.igame.work.monster.dto.Gods;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.dto.WuEffect;
import com.igame.work.quest.dto.TaskDayInfo;
import com.igame.work.user.dto.MessageCache;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.Team;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 消息发送类
 * @author Marcus.Z
 *
 */
public class MessageUtil {

	private static Map<Long,AtomicInteger> proIndex = new ConcurrentHashMap<>();//发送的消息ID

	/**
	 * 推送一条消息给玩家
	 */
	public static void sendMessageToPlayer(Player player,int protrol,RetVO vo){
		// TODO 跟GameHandler类的sendClient和ReconnectedHandler类的cacheResponse方法很像

		if (player == null){
			return;
		}
		
		vo.setIsPush(1);
		vo.setIndex(proIndex.computeIfAbsent(player.getPlayerId(),pid->new AtomicInteger(0)).incrementAndGet());
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
				List<Integer> key = player.getProTuiMap().keySet().stream()
						.sorted(Comparator.comparingInt(h -> h))
						.collect(Collectors.toList());
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

	public static void notifyTimeResToPlayer(Player player, int chapterId, RewardDto reward){
		
		RetVO vo = new RetVO();
		vo.addData("reward", ResourceService.getRewardString(reward));
    	vo.addData("chapterId", chapterId);
    	MessageUtil.sendMessageToPlayer(player, MProtrol.CHECKPOINT_RES_UPDATE, vo);
		
	}

	public static void notifyItemChange(Player player, List<Item> items){
		
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
	public static void notifyMonsterChange(Player player, List<Monster> mons){
		
		if(!mons.isEmpty()){
			RetVO vo = new RetVO();		
	    	vo.addData("monsters", mons);
			MessageUtil.sendMessageToPlayer(player, MProtrol.MONSTER_UPDATE, vo);
		}
	}
	
	/**
	 * 推送怪兽列表
	 * @param player 玩家
	 * @param mons 怪兽list
	 */
	public static void notifyMonsterList(Player player, int total, int index, List<Monster> mons){
		
		if(!mons.isEmpty()){
			RetVO vo = new RetVO();	
			vo.addData("total", total);
			vo.addData("index", index);
	    	vo.addData("monsters", mons);
			MessageUtil.sendMessageToPlayer(player, MProtrol.MONSTER_LSIT, vo);
		}
	}

	public static void notifyGodsChange(Player player, List<Gods> gods){
		
		if(!gods.isEmpty()){
			RetVO vo = new RetVO();		
	    	vo.addData("gods", gods);
			MessageUtil.sendMessageToPlayer(player, MProtrol.Gods_TUI, vo);
		}
	}
	
	/**
	 * 心魔列表更新
	 */
	public static void notifyXingMoChange(Player player, String removeId, List<XingMoDto> ls){
		
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
	 */
	public static void notifyTongHuaDtoChange(Player player){
		
		RetVO vo = new RetVO();	
		player.getTonghua().calLeftTime();
    	vo.addData("tongInfo", player.getTonghua());
		MessageUtil.sendMessageToPlayer(player, MProtrol.TONGHUA_UPDATE, vo);

	}
	
	
	/**
	 * 同化属性更新
	 */
	public static void notifyTongHuaAddChange(Player player){
		
		RetVO vo = new RetVO();	
    	vo.addData("tongAdd", player.getTongAdd());
		MessageUtil.sendMessageToPlayer(player, MProtrol.TONGHUAINFO_UPDATE, vo);

	}
	
	/**
	 * 新邮件通知
	 */
	public static void notifyNewMail(Player player, Mail mail){
		
		RetVO vo = new RetVO();	
    	vo.addData("mail", mail);
		MessageUtil.sendMessageToPlayer(player, MProtrol.MAIL_TUI, vo);

	}
	
	
	/**
	 * 新邮件通知
	 */
	public static void notifyMeetM(Player player){
		
		RetVO vo = new RetVO();	
    	vo.addData("meetM", MyUtil.toString(player.getMeetM(), ","));
		MessageUtil.sendMessageToPlayer(player, MProtrol.MEET_UPDATE, vo);

	}
	
	/**
	 * 造物台更新
	 */
	public static void notifyDrawData(Player player){
		
		RetVO vo = new RetVO();	
    	vo.addData("draw", player.getDraw());
		MessageUtil.sendMessageToPlayer(player, MProtrol.DRAW_UPDATE, vo);

	}
	
	/**
	 * 匹配结束
	 */
	public static void notifyMatchEnd(FightBase fb){

//		RetVO vo = new RetVO();
//    	vo.addData("i", fb.getFightB().getPlayer().getPlayerId());
//    	vo.addData("n", fb.getFightB().getPlayer().getUsername());
//    	vo.addData("l", fb.getFightB().getPlayer().getPlayerLevel());
//    	List<MatchMonsterDto> la = Lists.newArrayList();
//    	for(Monster m : fb.getFightB().getMonsters().values()){
//    		MatchMonsterDto mto = new MatchMonsterDto(m);
//    		la.add(mto);
//    	}
//    	vo.addData("m", la);
//		MessageUtil.sendMessageToPlayer(fb.getFightA().getPlayer(), MProtrol.F_P_E, vo);
//
//
//		RetVO vo2 = new RetVO();
//    	vo2.addData("i", fb.getFightA().getPlayer().getPlayerId());
//    	vo2.addData("n", fb.getFightA().getPlayer().getUsername());
//    	vo2.addData("l", fb.getFightA().getPlayer().getPlayerLevel());
//    	List<MatchMonsterDto> lb = Lists.newArrayList();
//    	for(Monster m : fb.getFightA().getMonsters().values()){
//    		MatchMonsterDto mto = new MatchMonsterDto(m);
//    		lb.add(mto);
//    	}
//    	vo2.addData("m", lb);
//		MessageUtil.sendMessageToPlayer(fb.getFightB().getPlayer(), MProtrol.F_P_E, vo2);
		


	}
	
	
	/**
	 * 开始加载游戏
	 */
	public static void notifyMatchLoad(FightBase fb){
		
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
	

	public static void notifyWinner(Player player,long winner){

		RetVO vo = new RetVO();
    	vo.addData("winner", winner);
		MessageUtil.sendMessageToPlayer(player, 400, vo);	// 400 现在定义的事心跳协议?

	}

	
	
	/**
	 * 任务更新
	 */
	public static void notifyQuestChange(Player player, List<TaskDayInfo> qList){
		
		if(!qList.isEmpty()){
			RetVO vo = new RetVO();		
	    	vo.addData("quest", qList);
			MessageUtil.sendMessageToPlayer(player, MProtrol.QUEST_UPDATE, vo);
		}
	}
	
	
	/**
	 * 星河之眼更新
	 */
	public static void notifyTrialChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("towerId", player.getTowerId());
    	vo.addData("oreCount", player.getOreCount());
		MessageUtil.sendMessageToPlayer(player, MProtrol.TRIAL_UPDATE, vo);
		
	}
	
	
	/**
	 * 无尽之森关卡更新
	 */
	public static void notifyWuChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("wuMap", player.getWuMap().values());
		MessageUtil.sendMessageToPlayer(player, MProtrol.WU_UPDATE, vo);
		
	}
	
	/**
	 * 无尽无尽之森自己怪物阵容更新
	 */
	public static void notifyWuZhengChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("wuZheng", CheckPointService.parsePlayer(player));
		MessageUtil.sendMessageToPlayer(player, MProtrol.WUZHENG_UPDATE, vo);
		
	}
	
	/**
	 * 推送无尽之森奶更新
	 */
	public static void notifyWuNaiChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("wuNai", player.getWuNai());
		MessageUtil.sendMessageToPlayer(player, MProtrol.WUNAI_UPDATE, vo);
		
	}
	
	/**
	 * 无尽无尽之森自己怪物阵容更新
	 */
	public static void notifyWuBufferChange(Player player, List<WuEffect> ls){
	
		RetVO vo = new RetVO();		
    	vo.addData("wuEffect", ls);
		MessageUtil.sendMessageToPlayer(player, MProtrol.WUBUFFER_UPDATE, vo);
		
	}
	
	
	/**
	 * 无尽之森已用免费重置次数更新
	 */
	public static void notifyWuResetChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("wuReset", player.getWuReset());
		MessageUtil.sendMessageToPlayer(player, MProtrol.WU_RESET, vo);
		
	}
	
	
	/**
	 * 推送命运之门信息更新
	 */
	public static void notifyDeInfoChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("desInfo", FateDto.creatFateDto(player.getFateData()));
		MessageUtil.sendMessageToPlayer(player, MProtrol.DE_UPDATE, vo);
		
	}
	
	
	/**
	 * 推送新门更新
	 */
	public static void notifyGateChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("gateInfo", player.getFateData().getGate());
		MessageUtil.sendMessageToPlayer(player, MProtrol.GATE_UPDATE, vo);
		
	}
	
	
	/**
	 * 推送竞技场已用挑战次数
	 */
	public static void notifyAreaCountChange(Player player){
	
		RetVO vo = new RetVO();		
    	vo.addData("areaCount", player.getAreaCount());
		MessageUtil.sendMessageToPlayer(player, MProtrol.AREA_COUNT, vo);
		
	}


	/**
	 * 推送阵容更新
	 */
	public static void notifyTeamChange(Player player, Team team){

		RetVO vo = new RetVO();
    	vo.addData("team", team);
		MessageUtil.sendMessageToPlayer(player, MProtrol.TEAM_UPDATE, vo);

	}
	


}
