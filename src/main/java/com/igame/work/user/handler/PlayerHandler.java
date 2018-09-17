package com.igame.work.user.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.dto.IDFactory;
import com.igame.util.DateUtil;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.PlayerEvents;
import com.igame.work.activity.ActivityService;
import com.igame.work.activity.denglu.DengluService;
import com.igame.work.activitylimit.ShopActivityService;
import com.igame.work.chat.dao.PlayerMessageDAO;
import com.igame.work.chat.service.MessageBoardService;
import com.igame.work.chat.service.PrivateMessageService;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.mingyunZhiMen.FateDto;
import com.igame.work.checkpoint.worldEvent.WordEventDAO;
import com.igame.work.checkpoint.wujinZhiSen.EndlessService;
import com.igame.work.fight.FightService;
import com.igame.work.fight.arena.ArenaService;
import com.igame.work.fight.data.GodsdataTemplate;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.dto.FriendInfo;
import com.igame.work.friend.service.FriendService;
import com.igame.work.item.service.ItemService;
import com.igame.work.mail.MailService;
import com.igame.work.monster.dao.GodsDAO;
import com.igame.work.monster.dao.MonsterDAO;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.work.monster.dto.Gods;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.quest.dto.TaskDayInfo;
import com.igame.work.quest.service.QuestService;
import com.igame.work.serverList.ServerListHandler;
import com.igame.work.shop.service.ShopService;
import com.igame.work.sign.SignService;
import com.igame.work.turntable.dto.Turntable;
import com.igame.work.turntable.service.TurntableService;
import com.igame.work.user.PlayerService;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.PlayerTop;
import com.igame.work.user.dto.Team;
import com.igame.work.user.load.ResourceService;
import com.igame.work.user.service.HeadService;
import com.igame.work.user.service.PlayerCacheService;
import com.igame.work.vip.VIPService;
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
public class PlayerHandler extends BaseHandler {

	@Inject private ActivityService activityService;
	@Inject private ArenaService robotService;
	@Inject private ResourceService resourceService;
	@Inject private PlayerService playerService;
	@Inject private MonsterService monsterService;
	@Inject private QuestService questService;
	@Inject private ShopActivityService shopActivityService;
	@Inject private TurntableService turntableService;
	@Inject private FriendService friendService;
	@Inject private PlayerDAO playerDAO;
	@Inject private MonsterDAO monsterDAO;
	@Inject private ShopService shopService;
	@Inject private HeadService headService;
	@Inject private VIPService vIPService;
	@Inject private ItemService itemService;
	@Inject private GodsDAO godsDAO;
	@Inject private WordEventDAO wordEventDAO;
	@Inject private MailService mailService;
	@Inject private PlayerMessageDAO playerMessageDAO;
	@Inject private ComputeFightService computeFightService;
	@Inject private SessionManager sessionManager;
	@Inject private IDFactory idFactory;
	@Inject private SignService signService;
	@Inject private DengluService dengluService;
	@Inject private MessageBoardService messageBoardService;
	@Inject private PrivateMessageService privateMessageService;
	@Inject private CheckPointService checkPointService;
	@Inject private EndlessService endlessService;
	@Inject private PlayerCacheService playerCacheService;

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
		
		Player player = playerDAO.getPlayerByUserId(userId);
		if(player == null){//不存在就默认创建一个
			try {
				player = newPlayer(userId, serverId);	// todo 有跟loadPlayer共用的部分 提到loadPlayer方法中
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
			loadPlayer(player);//载入玩家数据				// todo 跟newPlayer有共用的部分 挪到下面else外面
		}

		try {
			player.setUser(user);
			sessionManager.addSession(player);
			afterPlayerLogin(player);
		} catch (Exception e) {
			this.getLogger().warn("PlayerHandler load error", e);
			e.printStackTrace();
			sendClient(MProtrol.PLAYER_ENTER,error(ErrorCode.NEWPLAYER_ERROR),user);
		}

		List<TaskDayInfo> qs = Lists.newArrayList();
		for(TaskDayInfo td :questService.getAchievement(player.getPlayerId()).values()){
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
		vo.addData("mail", mailService.getMails(player).values());
		vo.addData("meetM", MyUtil.toString(player.getMeetM(), ","));
		vo.addData("resC", checkPointService.getRec(player));
		vo.addData("draw", player.getDraw());
		vo.addData("quest", qs);
		vo.addData("wuMap", player.getWuMap().values());
		vo.addData("wuZheng", CheckPointService.parsePlayer(player));
		vo.addData("wuEffect", player.getWuEffect());
		vo.addData("desInfo", FateDto.creatFateDto(player.getFateData()));
		FriendInfo friends = friendService.getFriends(player);
		for (Friend reqFriend : friends.getReqFriends()) {
			if (reqFriend.getLoginoutTime() == null) {
				reqFriend.setLoginoutTime(new Date());
			}
		}
		for (Friend reqFriend : friends.getCurFriends()) {
			if (reqFriend.getLoginoutTime() == null) {
				reqFriend.setLoginoutTime(new Date());
			}
		}
		vo.addData("friends", friends);
		vo.addData("teams",player.getTeams().values());
		vo.addData("vipPrivileges",player.getVipPrivileges());
		vo.addData("showActivities", activityService.clientData(player));
		vo.addData("sign", signService.clientData(player));
		vo.addData("dateTime", DateUtil.formatClientDateTime(new Date()));
		vo.addData("activitylimit",shopActivityService.clientData(player));
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
		player.setPlayerId(idFactory.getNewIdByType(IDFactory.PL, serverId));
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
		int monsterId1 = 1001;
		Monster m1 = new Monster(player, idFactory.getNewIdMonster(serverId), player.getPlayerId(), monsterService.MONSTER_DATA.getMonsterTemplate(monsterId1).getMonster_hp(), 0, monsterId1);
		MonsterTemplate mt1 = monsterService.MONSTER_DATA.getMonsterTemplate(monsterId1);
		if(mt1 != null && mt1.getSkill() != null){
			String[] skills = mt1.getSkill().split(",");
			if(skills.length != 0){
				for(String skill : skills){
					m1.skillMap.put(Integer.parseInt(skill), 1);
					m1.skillExp.put(Integer.parseInt(skill), 0);
				}
			}
			m1.atkType = mt1.getAtk_type();
		}
		monsterService.initSkillString(m1);

		int monsterId2 = 1002;
		Monster m2 = new Monster(player, idFactory.getNewIdMonster(serverId), player.getPlayerId(),  monsterService.MONSTER_DATA.getMonsterTemplate(monsterId2).getMonster_hp(), 0, monsterId2);
		MonsterTemplate mt2 = monsterService.MONSTER_DATA.getMonsterTemplate(monsterId1);
		if(mt2 != null && mt2.getSkill() != null){
			String[] skills = mt2.getSkill().split(",");
			if(skills.length != 0){
				for(String skill : skills){
					m2.skillMap.put(Integer.parseInt(skill), 1);
					m2.skillExp.put(Integer.parseInt(skill), 0);
				}
			}
			m2.atkType = mt2.getAtk_type();
		}
		monsterService.initSkillString(m2);

		monsterService.reCalculate(player,monsterId1, m1,true);
		monsterService.reCalculate(player,monsterId2,m2,true);

		monsterDAO.saveNewMonster(m1);
		monsterDAO.saveNewMonster(m2);
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

		//初始化商店
		shopService.initShop(player);

		//初始化头像和头像框
		headService.initHead(player);
		headService.initFrame(player);

		//初始化好友
		friendService.newPlayer(player);

		return playerDAO.savePlayer(player);
	}

	private void loadPlayer(Player player){
		itemService.loadPlayer(player);

		player.setGods(godsDAO.getByPlayer(player.getPlayerId()));

		monsterService.loadPlayer(player);

		player.setWordEvent(wordEventDAO.getByPlayer(player.getPlayerId()));

		mailService.loadPlayer(player);
		shopService.loadPlayer(player);
		friendService.loadPlayer(player);

		privateMessageService.setPrivateMessages(player,playerMessageDAO.getMessageByPlayerId(player.getPlayerId()).getMessages());

		questService.loadPlayer(player);

		playerCacheService.remove(player);

	}

	/**
	 * 玩家登录成功后的操作
	 */
	private void afterPlayerLogin(Player player) throws Exception {
		player.setLoginTime(new Date());
		fireEvent(player, PlayerEvents.RESET_ONCE, null);	// todo 这里走到SystemService的resetOnce方法只是初始化了其他数据，不需要发送事件吧 直接调 或者有隐藏的其他逻辑


		//初始化留言板
		messageBoardService.afterPlayerLogin(player);

		// todo 这个是不是应该在newPlayer里
		vIPService.afterPlayerLogin(player);

		//初始化商店或者刷新
		shopService.afterPlayerLogin(player);

		//初始化头像和头像框
		headService.afterPlayerLogin(player);


		//初始化角色挑战次数上限与剩余挑战次数
		if (player.getPlayerTop() == null){
			player.setPlayerTop(playerService.initPlayerTop(new PlayerTop()));
			BeanUtils.copyProperties(player.getPlayerCount(),player.getPlayerTop());
		}

		if(player.getTimeResCheck() == null){
			player.setTimeResCheck(Maps.newHashMap());
		}
		if(player.getPlayerLevel() >= 30 && player.getTonghua() == null){
			player.setTonghua(playerService.getRandomTongHuaDto());
			player.getTonghua().setStartRefTime(System.currentTimeMillis());
		}
		for(int i = 3;i<=7;i++){
			if( i == 5){
				continue;
			}
			player.getResMintues().putIfAbsent(i, 0);
		}
		for(Integer type : FightService.godsData.getSets()){
			GodsdataTemplate gt = FightService.godsData.getTemplate(type+"_0");
			if(gt != null && player.getPlayerLevel() >= gt.getUnlockLv() && player.getGods().get(type) == null){
				Gods gods = new Gods(player.getPlayerId(), type, 0, 1);
				player.getGods().put(gods.getGodsType(), gods);
			}
		}
		checkPointService.afterPlayerLogin(player);


		checkPointService.calPlayerTimeRes(player);//计算金币关卡的获得数量 和心魔 以及各货币资源的定时更新
		monsterService.reCalMonsterExtPre(player,true);//计算图鉴增加属性
		computeFightService.computePlayerFight(player);
		//player.reCalFightValue();//计算战斗力
		playerService.checkDrawData(player, false);//检测造物台数据
		questService.checkPlayerQuest(player);//检测玩家任务
		if(player.getWuMap().isEmpty()){
			endlessService.refEndlessRef(player);
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

		Turntable turntable = turntableService.getTurntable(player);
		if (turntable != null && turntableService.needRealod(turntable.getLastUpdate()))
			turntableService.reloadTurntable(player, turntable);

		if(player.getLastNickname()!=null && !"".equals(player.getLastNickname())) {
			player.setModifiedName(1);
		}
		signService.loadPlayer(player);		// todo event?
	}

	@Override
	public int protocolId() {
		return MProtrol.PLAYER_ENTER;
	}
}
