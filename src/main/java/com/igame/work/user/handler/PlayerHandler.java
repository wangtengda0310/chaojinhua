package com.igame.work.user.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.dto.IDFactory;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.dto.FateDto;
import com.igame.work.checkpoint.service.CheckPointService;
import com.igame.work.friend.service.FriendService;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.dao.MonsterDAO;
import com.igame.work.monster.dto.Monster;
import com.igame.work.quest.dto.TaskDayInfo;
import com.igame.work.shop.service.ShopService;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.PlayerTop;
import com.igame.work.user.dto.Team;
import com.igame.work.user.load.PlayerLoad;
import com.igame.work.user.service.HeadService;
import com.igame.work.user.service.VIPService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PlayerHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		
		long userId = jsonObject.getLong("userId");
		int serverId = jsonObject.getInt("serverId");
		
		trace("进入游戏---- serverId:" + serverId + "," +"userId:" + userId);

		RetVO vo = new RetVO();
		int index = params.getInt("index");
		vo.setIndex(index);
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		ISFSObject res = new SFSObject();
		if(!ServerListHandler.servers.containsKey(serverId)){//不存在的服务器ID
			sendError(ErrorCode.SERVER_NOT,MProtrol.toStringProtrol(MProtrol.PLAYER_ENTER), vo, user);
			return;
		}
		
		Player player = PlayerDAO.ins().getPlayerByUserId(serverId, userId);
		if(player == null){//不存在就默认创建一个
			try {
				player = initPlayer(userId, serverId);
				if(player.get_id() == null){
					sendError(ErrorCode.NEWPLAYER_ERROR,MProtrol.toStringProtrol(MProtrol.PLAYER_ENTER), vo, user);
					return;
				}
			} catch (Exception e) {
				this.getLogger().warn("PlayerHandler save error", e);
				e.printStackTrace();
				sendError(ErrorCode.NEWPLAYER_ERROR,MProtrol.toStringProtrol(MProtrol.PLAYER_ENTER), vo, user);
				return;
			}
		}else{
			PlayerLoad.ins().loadPlayer(player,serverId, userId);//载入玩家数据
		}

		try {
			player.setUser(user);
			SessionManager.ins().addSession(player);
			PlayerLoad.ins().afterPlayerLogin(player);
		} catch (Exception e) {
			this.getLogger().warn("PlayerHandler load error", e);
			e.printStackTrace();
			sendError(ErrorCode.NEWPLAYER_ERROR,MProtrol.toStringProtrol(MProtrol.PLAYER_ENTER), vo, user);
		}

		List<TaskDayInfo> qs = Lists.newArrayList();
		for(TaskDayInfo td :player.getAchievement().values()){
			if(td.getDtate() != 3){
				qs.add(td);
			}
		}
//		for(TaskDayInfo td :player.getDayTask().values()){
//			if(td.getDtate() != 3){
//				qs.add(td);
//			}
//		}

		vo.addData("player", player);
		for (Monster monster : player.getMonsters().values()) {
			monster.setTeamEquip(player.getTeams().values());
		}
//		vo.addData("monsters", player.getMonsters().values());
		vo.addData("items", player.getItems().values());
		vo.addData("xinMo", player.getXinMo().values());
		vo.addData("tongAdd", player.getTongAdd());
		vo.addData("gods", player.getGods().values());
		vo.addData("mail", player.getMail().values());
		vo.addData("meetM", MyUtil.toString(player.getMeetM(), ","));
		vo.addData("resC", player.getResC().values());
		vo.addData("draw", player.getDraw());
		vo.addData("quest", qs);
		vo.addData("wuMap", player.getWuMap().values());
		vo.addData("wuZheng", CheckPointService.parsePlayer(player));
		vo.addData("wuEffect", player.getWuEffect());
		vo.addData("desInfo", FateDto.creatFateDto(player.getFateData()));
		vo.addData("friends", player.getFriends());
		vo.addData("teams",player.getTeams().values());
		vo.addData("vipPrivileges",player.getVipPrivileges());
		try {
			json = mapper.writeValueAsString(vo);
		} catch (JsonProcessingException e) {
			this.getLogger().warn("PlayerHandler RetVO error", e);
		}
		
		res.putUtfString("infor", json);
		send(MProtrol.toStringProtrol(MProtrol.PLAYER_ENTER), res, user);
		GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.LOGIN,"");
	}

	private Player initPlayer(long userId, int serverId) throws Exception {

		Player player = new Player();

		player.setUserId(userId);
		player.setPlayerId(IDFactory.ins().getNewIdByType(IDFactory.PL, serverId));
		player.setSeverId(serverId);
		player.setUsername("user_"+player.getPlayerId());
		player.setNickname("玩家_"+player.getPlayerId());
		player.setPlayerLevel(1);
		player.setGold(10000);
		player.setDiamond(100);
		player.setBagSpace(100);
		player.setBuildTime(new Date());
		player.setBattleSpace(2);
		player.setLoginTime(new Date());

		//创建默认的怪物
		Monster m1 = new Monster(player, IDFactory.ins().getNewIdMonster(serverId), player.getPlayerId(), MonsterDataManager.MONSTER_DATA.getMonsterTemplate(1001).getMonster_hp(), 0,1001);
		Monster m2 = new Monster(player,IDFactory.ins().getNewIdMonster(serverId), player.getPlayerId(),  MonsterDataManager.MONSTER_DATA.getMonsterTemplate(1002).getMonster_hp(), 0,1002);

		m1.reCalculate(player,true);
		m2.reCalculate(player,true);

		MonsterDAO.ins().saveNewMonster(serverId, m1);
		MonsterDAO.ins().saveNewMonster(serverId, m2);
		player.getMonsters().put(m1.getObjectId(),m1);
		player.getMonsters().put(m2.getObjectId(),m2);

		//初始化阵容
		player.getTeams().put(1,new Team(1,"默认队伍1",m1.getObjectId(),m2.getObjectId()));
		player.getTeams().put(2,new Team(2,"默认队伍2",m1.getObjectId(),m2.getObjectId()));
		player.getTeams().put(3,new Team(3,"默认队伍3",m1.getObjectId(),m2.getObjectId()));
		player.getTeams().put(4,new Team(4,"默认队伍4",m1.getObjectId(),m2.getObjectId()));
		player.getTeams().put(5,new Team(5,"默认队伍5",m1.getObjectId(),m2.getObjectId()));
		player.getTeams().put(6,new Team(6,"竞技场防守阵容",m1.getObjectId(),m2.getObjectId()));
		//player.getTeams()[0] = m1.getObjectId()+","  + m2.getObjectId() +",-1,-1,-1";
		
		//初始化留言板
		player.initMessageBoard();

		//初始化商店
		ShopService.ins().initShop(player);

		//初始化头像和头像框
		HeadService.ins().initHead(player);
		HeadService.ins().initFrame(player);

		//初始化好友
		FriendService.ins().initFriend(player);

		//初始化会员特权
		VIPService.ins().initPrivileges(player.getVipPrivileges());

		//初始化角色挑战次数上限
		player.setPlayerTop(new PlayerTop().init());

		//初始化角色剩余挑战次数
		BeanUtils.copyProperties(player.getPlayerCount(),player.getPlayerTop());

		return PlayerDAO.ins().savePlayer(serverId, player);
	}

}
