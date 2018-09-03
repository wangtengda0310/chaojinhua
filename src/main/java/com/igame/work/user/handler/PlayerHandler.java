package com.igame.work.user.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.dto.IDFactory;
import com.igame.util.DateUtil;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.PlayerEvents;
import com.igame.work.activity.ActivityService;
import com.igame.work.chat.dao.PlayerMessageDAO;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.guanqia.GuanQiaDataManager;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.guanqia.data.CheckPointTemplate;
import com.igame.work.checkpoint.mingyunZhiMen.FateDto;
import com.igame.work.checkpoint.worldEvent.WordEventDAO;
import com.igame.work.checkpoint.xinmo.XingMoDto;
import com.igame.work.fight.FightDataManager;
import com.igame.work.fight.data.GodsdataTemplate;
import com.igame.work.fight.service.ArenaService;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.friend.service.FriendService;
import com.igame.work.item.service.ItemService;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.dao.GodsDAO;
import com.igame.work.monster.dao.MonsterDAO;
import com.igame.work.monster.dto.Gods;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.quest.dto.TaskDayInfo;
import com.igame.work.quest.service.QuestService;
import com.igame.work.serverList.ServerListHandler;
import com.igame.work.shop.service.ShopService;
import com.igame.work.turntable.service.TurntableService;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.*;
import com.igame.work.user.load.PlayerService;
import com.igame.work.user.load.ResourceService;
import com.igame.work.user.service.HeadService;
import com.igame.work.user.service.MailService;
import com.igame.work.user.service.PlayerCacheService;
import com.igame.work.user.service.VIPService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;

import java.util.*;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PlayerHandler extends BaseHandler{

	private ActivityService activityService;
	private ArenaService robotService;

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
			sendClient(MProtrol.PLAYER_ENTER,error(ErrorCode.SERVER_NOT),user);
			return;
		}
		
		Player player = PlayerDAO.ins().getPlayerByUserId(serverId, userId);
		if(player == null){//不存在就默认创建一个
			try {
				player = newPlayer(userId, serverId);
				if(player.get_id() == null){
					sendClient(MProtrol.PLAYER_ENTER,error(ErrorCode.NEWPLAYER_ERROR),user);
					return;
				}
			} catch (Exception e) {
				this.getLogger().warn("PlayerHandler save error", e);
				e.printStackTrace();
				sendClient(MProtrol.PLAYER_ENTER,error(ErrorCode.NEWPLAYER_ERROR),user);
				return;
			}
		}else{
			loadPlayer(player,serverId);//载入玩家数据
		}

		try {
			player.setUser(user);
			SessionManager.ins().addSession(player);
			afterPlayerLogin(player);
		} catch (Exception e) {
			this.getLogger().warn("PlayerHandler load error", e);
			e.printStackTrace();
			sendClient(MProtrol.PLAYER_ENTER,error(ErrorCode.NEWPLAYER_ERROR),user);
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
		vo.addData("showActivities", activityService.clientData(player));
		vo.addData("dateTime", DateUtil.formatClientDateTime(new Date()));
		try {
			json = mapper.writeValueAsString(vo);
		} catch (JsonProcessingException e) {
			this.getLogger().warn("PlayerHandler RetVO error", e);
		}
		
		res.putUtfString("infor", json);
		send(MProtrol.toStringProtrol(MProtrol.PLAYER_ENTER), res, user);
		GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.LOGIN,"");
	}

	private Player newPlayer(long userId, int serverId) throws Exception {

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
		FriendService.ins().newPlayer(player);

		//初始化会员特权
		VIPService.ins().initPrivileges(player.getVipPrivileges());

		//初始化角色挑战次数上限
		player.setPlayerTop(new PlayerTop().init());

		//初始化角色剩余挑战次数
		BeanUtils.copyProperties(player.getPlayerCount(),player.getPlayerTop());

		return PlayerDAO.ins().savePlayer(serverId, player);
	}

	private void loadPlayer(Player player, int serverId){
		ItemService.ins().loadPlayer(player, serverId);

		player.setGods(GodsDAO.ins().getByPlayer(serverId, player.getPlayerId()));

		MonsterService.loadPlayer(player,serverId);

		player.setWordEvent(WordEventDAO.ins().getByPlayer(serverId, player.getPlayerId()));

		MailService.ins().loadPlayer(player, serverId);
		ShopService.ins().loadPlayer(player, serverId);
		FriendService.ins().loadPlayer(player);

		player.setPrivateMessages(PlayerMessageDAO.ins().getMessageByPlayerId(player.getSeverId(),player.getPlayerId()).getMessages());
		player.initMessageBoard();

		QuestService.loadPlayer(player, serverId);

		PlayerCacheService.remove(player);

	}

	/**
	 * 玩家登录成功后的操作
	 */
	private void afterPlayerLogin(Player player) throws Exception {

		if (player.getPrivateMessages().size() <= 0)
			VIPService.ins().initPrivileges(player.getVipPrivileges());

		//初始化商店或者刷新
		if (player.getShopInfo() == null)
			ShopService.ins().initShop(player);
		else
			ShopService.ins().reloadAll(player.getShopInfo());

		//初始化头像和头像框
		if (player.getUnlockHead().size() == 0)
			HeadService.ins().initHead(player);
		if (player.getUnlockFrame().size() == 0)
			HeadService.ins().initFrame(player);


		//初始化角色挑战次数上限与剩余挑战次数
		if (player.getPlayerTop() == null){
			player.setPlayerTop(new PlayerTop().init());
			BeanUtils.copyProperties(player.getPlayerCount(),player.getPlayerTop());
		}

		if(player.getTimeResCheck() == null){
			player.setTimeResCheck(Maps.newHashMap());
		}
		if(player.getPlayerLevel() >= 30 && player.getTonghua() == null){
			player.setTonghua(PlayerService.getRandomTongHuaDto());
			player.getTonghua().setStartRefTime(System.currentTimeMillis());
		}
		for(int i = 3;i<=7;i++){
			if( i == 5){
				continue;
			}
			player.getResMintues().putIfAbsent(i, 0);
		}
		for(Integer type : FightDataManager.GodsData.getSets()){
			GodsdataTemplate gt = FightDataManager.GodsData.getTemplate(type+"_0");
			if(gt != null && player.getPlayerLevel() >= gt.getUnlockLv() && player.getGods().get(type) == null){
				Gods gods = new Gods(player.getPlayerId(), type, 0, 1);
				player.getGods().put(gods.getGodsType(), gods);
			}
		}
		player.setLoginTime(new Date());

		if(!MyUtil.isNullOrEmpty(player.getCheckPoint())){
			for(String cc :player.getCheckPoint().split(",")){//已过关卡有，但是资源关卡时间计数器没有添加
				Integer cid = Integer.parseInt(cc);
				CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(cid);
				if(ct != null && ct.getChapterType() == 2 && !MyUtil.isNullOrEmpty(ct.getDropPoint())
						&& !player.getTimeResCheck().containsKey(cid)){
					player.getTimeResCheck().put(cid, ct.getMaxTime() * 60);
				}
			}
			for(Integer id : player.getTimeResCheck().keySet()){//已过关卡里没有的，资源关卡时间计数器有的删除
				if(!player.hasCheckPoint(String.valueOf(id))){
					player.getTimeResCheck().remove(id);
				}
			}
		}

		fireEvent(player, PlayerEvents.RESET_ONCE, null);

		calPlayerTimeRes(player);//计算金币关卡的获得数量 和心魔 以及各货币资源的定时更新
		player.calLeftTime();//算每个心魔剩余时间
		MonsterService.reCalMonsterExtPre(player,true);//计算图鉴增加属性
		ComputeFightService.ins().computePlayerFight(player);
		//player.reCalFightValue();//计算战斗力
		PlayerService.checkDrawData(player, false);//检测造物台数据
		QuestService.checkPlayerQuest(player);//检测玩家任务
		if(player.getWuMap().isEmpty()){
			CheckPointService.refEndlessRef(player);
		}

		player.getFateData().setTodayFateLevel(1);
		player.getFateData().setTodayBoxCount(0);
		player.getFateData().setTempBoxCount(-1);
		player.getFateData().setTempSpecialCount(0);
		player.getFateData().setAddRate(0);

		if(player.getTeams().get(6) == null){
			long id1 = -1;
			long id2 = -1;
			int count = 0;
			for(Monster mm : player.getMonsters().values()){
				if(count == 0){
					id1 = mm.getObjectId();
				}else{
					id2 = mm.getObjectId();
				}
				count++;
				if(count >= 2){
					break;
				}
			}
			player.getTeams().put(6,new Team(6,"竞技场防守阵容",id1,id2));
		}

		if (player.getTurntable() != null && TurntableService.ins().needRealod(player.getTurntable().getLastUpdate()))
			TurntableService.ins().reloadTurntable(player);

		if(player.getLastNickname()!=null && !"".equals(player.getLastNickname())) {
			player.setModifiedName(1);
		}
		ActivityService.loadPlayer(player);
	}

	private void calPlayerTimeRes(Player player){
		synchronized (player.getTimeLock()) {
			calRes(player);
			if(!player.getTimeResCheck().isEmpty()){
				long now = System.currentTimeMillis();
				for(Map.Entry<Integer, Integer> m : player.getTimeResCheck().entrySet()){
					CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(m.getKey());
					if(ct != null && !MyUtil.isNullOrEmpty(ct.getDropPoint())){
						int total = m.getValue();//原有分钟数
						if(player.getLoginoutTime() != null){//计算新的
							int timeAdd = (int)((now - player.getLoginoutTime().getTime())/60000);
							total += timeAdd;
							if(total > ct.getMaxTime() * 60){
								total = ct.getMaxTime()  * 60;
							}
							player.getTimeResCheck().put(m.getKey(), total);
						}
						RewardDto dto = ResourceService.ins().getResRewardDto(ct.getDropPoint(), total, ct.getMaxTime() * 60);
//    					MessageUtil.notifyTimeResToPlayer(player, m.getKey(), dto);
						player.getResC().put(ct.getChapterId(),new ResCdto(ct.getChapterId(), ResourceService.ins().getRewardString(dto)));

					}
				}
			}
			initXinMo(player);

		}

	}

	//登录时候初始化
	private void initXinMo(Player player){

		if(!MyUtil.isNullOrEmpty(player.getCheckPoint()) && player.getCheckPoint().split(",").length >= 30){

			long now = System.currentTimeMillis();
			//先是删掉24小时没打的
			List<Integer> remove = Lists.newArrayList();
			for(XingMoDto xin :player.getXinMo().values()){
				if(xin.getStatTime() == 0 || now - xin.getStatTime()  >= 24 * 3600 * 1000){
					remove.add(xin.getCheckPiontId());
				}
			}
			if(!remove.isEmpty()){
				for(Integer id :remove){
					player.getXinMo().remove(id);
					GoldLog.info("remove xinMo:" + id);
				}
			}

			//判断是否刷新新的
			if(player.getLoginoutTime() != null){
				int timeAdd = (int)((now - player.getLoginoutTime().getTime())/60000);//分钟数
				int total = player.getXinMoMinuts() +  timeAdd;
				int count  = total /60; //几个小时-可刷几个
				if(count > 0){
					String[] ccs =  player.getCheckPoint().split(",");
					List<Integer> ls = Collections.synchronizedList(Lists.newArrayList());
					for(String cc : ccs){
						if(!player.getXinMo().containsKey(Integer.parseInt(cc)) && GuanQiaDataManager.CheckPointData.getTemplate(Integer.parseInt(cc)).getChapterType() != 2 && Integer.parseInt(cc) <=140){//有心魔的关卡不在生成列表中
							ls.add(Integer.parseInt(cc));
						}
					}
					if(!ls.isEmpty()){
						if(count > 25-player.getXinMo().size()){
							count = 25-player.getXinMo().size();
						}
						while(!ls.isEmpty() && count > 0){
							int index = GameMath.getRandInt(ls.size());
							Integer cid = ls.remove(index);
							XingMoDto xx = new XingMoDto();
							xx.setCheckPiontId(cid);
							Map<Long, RobotDto> robs = robotService.getRobot().get(player.getSeverId());
							if(!robs.isEmpty()){
								RobotDto rb = new ArrayList<>(robs.values()).get(GameMath.getRandInt(robs.size()));
								xx.setPlayerId(rb.getPlayerId());
								xx.setMid(rb.getName());
								xx.setPlayerFrameId(rb.getPlayerFrameId());
								xx.setPlayerHeadId(rb.getPlayerHeadId());
							}else{
								xx.setMid("");//待改变和生成具体机器人数据
							}
							xx.setStatTime(now);
							player.getXinMo().put(cid, xx);
							count--;
							GoldLog.info("add xinMo:" + cid);
							if(player.getXinMo().size() >= 25){
								break;
							}
						}

					}
					player.setXinMoMinuts(total % 60);//保存剩余分钟数
				}else{
					player.setXinMoMinuts(total);//保存剩余分钟数
				}

			}
		}
	}

	private void calRes(Player player){
		if(player.getLoginoutTime() != null){
			long now = System.currentTimeMillis();
			int timeAdd = (int)((now - player.getLoginoutTime().getTime())/60000);
			for(Map.Entry<Integer, Integer> m : player.getResMintues().entrySet()){
				player.getResMintues().put(m.getKey(), m.getValue() + timeAdd);
			}
			if(player.getResMintues().get(3) != null){
				if(player.getResMintues().get(3) >=6){
					ResourceService.ins().addPhysica(player, player.getResMintues().get(3)/6);
					player.getResMintues().put(3, player.getResMintues().get(3)%6);
				}
			}
			if(player.getResMintues().get(4) != null){
				if(player.getResMintues().get(4) >=60){
					ResourceService.ins().addSao(player, player.getResMintues().get(4)/60);
					player.getResMintues().put(4, player.getResMintues().get(4)%60);
				}

			}
			if(player.getResMintues().get(6) != null){
				if(player.getResMintues().get(6) >=120 && player.getTongRes() < 15){
					int add = player.getResMintues().get(6)/120;
					if(add > 15 - player.getTongRes()){
						add = 15 - player.getTongRes();
					}
					ResourceService.ins().addTongRes(player, add);
					player.getResMintues().put(6, player.getResMintues().get(6)%120);
				}

			}
			if(player.getResMintues().get(7) != null){
				if(player.getResMintues().get(7) >=120 && player.getXing() < 10){
					int add = player.getResMintues().get(7)/120;
					if(add > 10 - player.getXing()){
						add = 10 - player.getXing();
					}
					ResourceService.ins().addXing(player, add);
					player.getResMintues().put(7, player.getResMintues().get(7)%120);
				}

			}
		}

	}

}
