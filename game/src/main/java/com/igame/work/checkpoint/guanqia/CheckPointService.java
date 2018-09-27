package com.igame.work.checkpoint.guanqia;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.ISFSModule;
import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.di.LoadXml;
import com.igame.core.event.RemoveOnLogout;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.core.quartz.TimeListener;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.data.CheckPointData;
import com.igame.work.checkpoint.guanqia.data.CheckPointTemplate;
import com.igame.work.checkpoint.guanqia.data.DropData;
import com.igame.work.checkpoint.guanqia.data.DropDataTemplate;
import com.igame.work.checkpoint.mingyunZhiMen.GateService;
import com.igame.work.checkpoint.tansuo.TansuoData;
import com.igame.work.checkpoint.worldEvent.WorldEventData;
import com.igame.work.checkpoint.wujinZhiSen.EndlessService;
import com.igame.work.checkpoint.xinmo.XingMoDto;
import com.igame.work.item.service.ItemService;
import com.igame.work.monster.dto.JiyinType;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.data.ItemTemplate;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.ResCdto;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.load.ResourceService;
import com.igame.work.user.service.RobotService;
import com.igame.work.vip.VIPService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * 
 * @author Marcus.Z
 *
 */
public class CheckPointService implements ISFSModule, TimeListener {
	/**
	 * 关卡列表
	 */
	@LoadXml("chapterdata.xml") public CheckPointData checkPointData;
	/**
	 * 掉落包
	 */
	@LoadXml("dropdata.xml") public DropData dropData;
	@LoadXml("questteam.xml") public TansuoData tansuoData;
	@LoadXml("worldevent.xml")public WorldEventData worldEventData;

	@Inject private GateService gateService;
	@Inject private RobotService robotService;
	@Inject private ResourceService resourceService;
	@Inject private SessionManager sessionManager;
	@Inject private EndlessService endlessService;
    @Inject private ItemService itemService;
    @Inject private VIPService vipService;

	@RemoveOnLogout() private Map<Long, Long> enterCheckPointTime = new ConcurrentHashMap<>();//进入的关卡时间
	@RemoveOnLogout() private Map<Long, Long> enterWordEventTime = new ConcurrentHashMap<>();//进入世界事件关卡时间
	@RemoveOnLogout() private Map<Long, Integer> enterCheckpointId = new ConcurrentHashMap<>();//进入的关卡ID



    @RemoveOnLogout() private Map<Long, Map<String,Object>> lastBattleParam = new ConcurrentHashMap<>();//上次战斗的关卡参数

    public Map<String, Object> getLastBattleParam(long playerId) {
		return lastBattleParam.get(playerId);
	}

	public void setLastBattleParam(long playerId, Map<String,Object> param) {
		lastBattleParam.put(playerId,param);
	}

	public String getString(Player player, RewardDto reward, List<Monster> ll, String monsterExpStr) {
		StringBuilder monsterExpStrBuilder = new StringBuilder(monsterExpStr);
		for(long mid : player.getTeams().get(player.getCurTeam()).getTeamMonster()){
			if(-1 != mid){
				Monster mm = player.getMonsters().get(mid);
				if(mm != null){
					int mmExp = getTotalExp(mm, reward.getExp());
					monsterExpStrBuilder.append(mid);
					if(resourceService.addMonsterExp(player, mid, mmExp, false) == 0){
						ll.add(mm);
						monsterExpStrBuilder.append(",").append(mmExp).append(";");
					}else{
						monsterExpStrBuilder.append(",0;");
					}
				}
			}
		}
		monsterExpStr = monsterExpStrBuilder.toString();
		return monsterExpStr;
	}

	@Override
	public void minute() {

		calPlayerTimeRes();
	}

    @RemoveOnLogout() private Map<Long,Object> timeLock = new ConcurrentHashMap<>();//定时同步锁

	public Object getTimeLock(Player player) {
		return timeLock.computeIfAbsent(player.getPlayerId(), pid->new Object());
	}
	//定时执行玩家金币关卡计算
	private void calPlayerTimeRes(){	// todo 这逻辑停复杂 全服计算还有锁 会阻塞了定时器线程
		for(Player player : sessionManager.getSessions().values()){
			synchronized (getTimeLock(player)) {
                checkResPerMinute(player);  // minute timer
                processXinMoPerMinute(player);  // todo move to RobotService or XinmoService?
				calResPerMinute(player);
			}
		}
	}

    /**
     * 推送玩家倒计时开始
     */
    private void notifyCDDown(Player player, int type){

        RetVO vo = new RetVO();
        vo.addData("type", type);
        MessageUtil.sendMessageToPlayer(player, MProtrol.CD_DOWN, vo);

    }

	private void calResPerMinute(Player player){
		Map<Integer, Integer> resMintues = player.getResMintues();

		for(Map.Entry<Integer, Integer> m : resMintues.entrySet()){
			if(!(m.getKey() == 6 && player.getTongRes() >= 15 || m.getKey() == 7 && player.getXing() >= 10)) {
                resMintues.put(m.getKey(), m.getValue() + 1);
			}

		}
		if(resMintues.get(3) != null){
			if(resMintues.get(3) >=6){
				resourceService.addPhysica(player, 1);
				notifyCDDown(player, 3);
				resMintues.put(3, 0);
			}

		}
		if(resMintues.get(4) != null){
			if(resMintues.get(4) >=60){
				resourceService.addSao(player, 1);
				resMintues.put(4, 0);
			}

		}
		if(resMintues.get(6) != null){
			if(resMintues.get(6) >=120 && player.getTongRes() < 15){
				resourceService.addTongRes(player, 1);
				resMintues.put(6, 0);
			}

		}
		if(resMintues.get(7) != null){
			if(resMintues.get(7) >=120  && player.getXing() < 10){
				resourceService.addXing(player, 1);
				resMintues.put(7, 0);
			}

		}

	}

    /**
     * 体力 扫荡 同化 星
     */
    private void calResOnLogin(Player player){
        if(player.getLoginoutTime() != null){
            long now = System.currentTimeMillis();
            int timeAdd = (int)((now - player.getLoginoutTime().getTime())/60000);

            Map<Integer, Integer> resMintues = player.getResMintues();

            for(Map.Entry<Integer, Integer> m : resMintues.entrySet()){
                resMintues.put(m.getKey(), m.getValue() + timeAdd);
            }
            if(resMintues.get(3) != null){
                if(resMintues.get(3) >=6){
                    resourceService.addPhysica(player, resMintues.get(3)/6);
                    resMintues.put(3, resMintues.get(3)%6);
                }
            }
            if(resMintues.get(4) != null){
                if(resMintues.get(4) >=60){
                    resourceService.addSao(player, resMintues.get(4)/60);
                    resMintues.put(4, resMintues.get(4)%60);
                }

            }
            if(resMintues.get(6) != null){
                if(resMintues.get(6) >=120 && player.getTongRes() < 15){
                    int add = resMintues.get(6)/120;
                    if(add > 15 - player.getTongRes()){
                        add = 15 - player.getTongRes();
                    }
                    resourceService.addTongRes(player, add);
                    resMintues.put(6, resMintues.get(6)%120);
                }

            }
            if(resMintues.get(7) != null){
                if(resMintues.get(7) >=120 && player.getXing() < 10){
                    int add = resMintues.get(7)/120;
                    if(add > 10 - player.getXing()){
                        add = 10 - player.getXing();
                    }
                    resourceService.addXing(player, add);
                    resMintues.put(7, resMintues.get(7)%120);
                }

            }
        }

    }

	private void processXinMoPerMinute(Player player){

		if(MyUtil.isNullOrEmpty(player.getCheckPoint())
                || player.getCheckPoint().split(",").length < 30){
		    return;
        }

        long now = System.currentTimeMillis();
        //先是删掉24小时没打的
        List<Integer> removed = findTimeoutXinmo(player, now);

        List<XingMoDto> created = Collections.emptyList();
        //判断是否刷新新的
        Map<String, RobotDto> robs = robotService.getRobot();
        if(player.getXinMoMinuts() < 60){
            player.setXinMoMinuts(player.getXinMoMinuts() + 1);
        }else if(player.getXinMoMinuts() >= 60 && !robs.isEmpty()){//到了一小时  可能刷新
            created = Arrays.stream(player.getCheckPoint().split(","))
                    .map(Integer::parseInt)
                    .filter(cc->!player.getXinMo().containsKey(cc))
                    .filter(cc->checkPointData.getTemplate(cc).getChapterType() != 2)
                    .filter(cc->cc <=140)
                    .limit(1)
                    .peek(cid-> GoldLog.info("add xinMo:" + cid))
                    .map(cid->{
                        XingMoDto xx = new XingMoDto();
                        updatePlayerXinmo(player, now, cid, xx, robs);
                        player.setXinMoMinuts(0);//重新累算
                        return xx;
                    })
                    .collect(Collectors.toList());
        }

        if(!removed.isEmpty() || !created.isEmpty()){
            List<String> collect = removed.stream().map(i -> ""+i).collect(Collectors.toList());
            MessageUtil.notifyXingMoChange(player, String.join(",",collect),created);//推送更新
        }

	}

    private void updatePlayerXinmo(Player player, long now, Integer cid, XingMoDto xx, Map<String, RobotDto> robs) {
        RobotDto rb = new ArrayList<>(robs.values()).get(GameMath.getRandInt(robs.size()));
        xx.setPlayerId(rb.getPlayerId());
        xx.setCheckPiontId(cid);
        xx.setMid(rb.getName());
        xx.setPlayerFrameId(rb.getPlayerFrameId());
        xx.setPlayerHeadId(rb.getPlayerHeadId());
        xx.setStatTime(now);
        player.getXinMo().put(cid, xx);
    }

    private List<Integer> findTimeoutXinmo(Player player, long now) {
        List<Integer> remove = new LinkedList<>();
        for (XingMoDto xin : new ArrayList<>(player.getXinMo().values())) {
            if (xin.getStatTime() == 0 || now - xin.getStatTime() >= 24 * 3600 * 1000) {
                remove.add(xin.getCheckPiontId());
                player.getXinMo().remove(xin.getCheckPiontId());
                GoldLog.info("remove xinMo:" + xin.getCheckPiontId());
            }
        }
        return remove;
    }

    //登录时候初始化
    private void initXinMo(Player player){

        if(MyUtil.isNullOrEmpty(player.getCheckPoint())
                || player.getCheckPoint().split(",").length < 30){
            return;
        }

        long now = System.currentTimeMillis();
        //先是删掉24小时没打的
        List<Integer> remove = findTimeoutXinmo(player, now);

        //判断是否刷新新的
        if(player.getLoginoutTime() != null){
            int timeAdd = (int)((now - player.getLoginoutTime().getTime())/60000);//分钟数
            int total = player.getXinMoMinuts() +  timeAdd;
            int count  = total /60; //几个小时-可刷几个
            player.setXinMoMinuts(total % 60);//保存剩余分钟数
            if (count <= 0) {
                return;
            }
            Map<String, RobotDto> robs = robotService.getRobot();
            if(robs.isEmpty()){
                return;
            }
            Arrays.stream(player.getCheckPoint().split(","))
                    .map(Integer::parseInt)
                    .filter(cc->!player.getXinMo().containsKey(cc))
                    .filter(cc->checkPointData.getTemplate(cc).getChapterType() != 2)
                    .filter(cc->cc <=140)
                    .limit(25-player.getXinMo().size())
                    .peek(cid-> GoldLog.info("add xinMo:" + cid))
                    .forEach(cid->{
                        XingMoDto xx = new XingMoDto();
                        updatePlayerXinmo(player, now, cid, xx, robs);
                    });

        }
    }

	/**
	 * 获取奖励
	 * @param chapterId 关卡ID
	 * @param win 是否胜利
	 * @param type 可能的活动ID
	 */
	public RewardDto getReward(Player player,int chapterId,int win,boolean first,int type){
		
		RewardDto reward = new RewardDto();
		int ret = 0;
		CheckPointTemplate ct = checkPointData.getTemplate(chapterId);
		DropDataTemplate dt = dropData.getTemplate(ct.getDropId());

		if(win == 1){//赢了
			reward.setExp(ct.getExp());
			if(dt != null){
				reward.setGold(dt.getGoldDrop());
				RewardDto other = null;
				if(first){
					other = resourceService.getRewardDto(dt.getFirstDrop(), "100");
				}else{
					other = resourceService.getRewardDto(dt.getItemDrop(), dt.getRate());
				}
				reward.setGold(reward.getGold() + other.getGold());
				reward.setDiamond(other.getDiamond());
				reward.items = other.items;
				reward.monsters = other.monsters;
			}
		}
		reward.setRet(ret);
		return reward;
	}
	
	
	public static int getTotalExp(Monster mm,int exp){
		
		int ret = exp;
		double add = 0.0;
		String[] vrl = mm.getBreaklv().split(",");
		for(String vl : vrl){
			if(JiyinType.TYPE_013.equals(vl)){//经验加成
				add += 0.2;
			}
		}
		ret = ret + (int)(exp * add) + mm.getExpAadd();
		return ret;
		
	}
	


	public void setEnterCheckPointTime(Player player) {
		enterCheckPointTime.put(player.getPlayerId(), System.currentTimeMillis());
	}

	public void setEnterWordEventTime(Player player) {
		enterWordEventTime.put(player.getPlayerId(), System.currentTimeMillis());
	}

	public int getEnterCheckpointId(Player player) {
		return enterCheckpointId.get(player.getPlayerId());
	}

	public void setEnterCheckpointId(Player player, int chapterId) {
		enterCheckpointId.put(player.getPlayerId(), chapterId);
	}

	public void afterPlayerLogin(Player player) {
        for(int i = 3;i<=7;i++){    // todo enum
            if( i == 5){
                continue;
            }
            player.getResMintues().putIfAbsent(i, 0);
        }
        if(player.getTimeResCheck() == null){
            player.setTimeResCheck(Maps.newHashMap());
        }
		if(!MyUtil.isNullOrEmpty(player.getCheckPoint())){
			for(String cc :player.getCheckPoint().split(",")){//已过关卡有，但是资源关卡时间计数器没有添加
				int cid = Integer.parseInt(cc);
				CheckPointTemplate ct = checkPointData.getTemplate(cid);
				if(ct != null && ct.getChapterType() == 2 && !MyUtil.isNullOrEmpty(ct.getDropPoint())
						&& !player.getTimeResCheck().containsKey(cid)){
					player.getTimeResCheck().put(cid, vipService.getResCheckpointHourLimit(player) * 60);
				}
			}
			for(int id : player.getTimeResCheck().keySet()){//已过关卡里没有的，资源关卡时间计数器有的删除
				if(!player.hasCheckPoint(String.valueOf(id))){
					player.getTimeResCheck().remove(id);
				}
			}
		}

		calPlayerTimeRes(player);//计算金币关卡的获得数量 和心魔 以及各货币资源的定时更新
	}

	private Map<Long, Map<Integer, ResCdto>> resC = new ConcurrentHashMap<>();//金币资源关卡

	private void calPlayerTimeRes(Player player){
		synchronized (getTimeLock(player)) {
			calResOnLogin(player);  // afterPlayerLogin surround by synchronized
            initCheckRes(player);  // afterPlayerLogin surround by synchronized
            initXinMo(player);  // afterPlayerLogin surround by synchronized

		}

        player.calLeftTime();//算每个心魔剩余时间
	}

    private void checkResPerMinute(Player player) {
        if(player.getTimeResCheck().isEmpty()){
            return;
        }
        int hourLimit = vipService.getResCheckpointHourLimit(player);
        for(Map.Entry<Integer, Integer> m : player.getTimeResCheck().entrySet()){
            CheckPointTemplate ct = checkPointData.getTemplate(m.getKey());
            if(ct != null && !MyUtil.isNullOrEmpty(ct.getDropPoint())){
                if(m.getValue() < hourLimit){//没到上限
                    player.getTimeResCheck().put(m.getKey(), m.getValue()+ 1);
                    if(m.getValue() >= hourLimit && m.getValue() % hourLimit == 0){//到了一小时更新，推送金币数
                        RewardDto dto = getResRewardDto(ct.getDropPoint(), m.getValue(), hourLimit);
                        MessageUtil.notifyTimeResToPlayer(player, m.getKey(), dto);
                    }

                    //零时测试
//        						RewardDto dto = resourceService.getResRewardDto(ct.getDropPoint(), m.getValue(), ct.getMaxTime() * 60);
//        						MessageUtil.notifyTimeResToPlayer(player, m.getKey(), dto);
                }
            }
        }
    }

    private void initCheckRes(Player player) {
        if (player.getTimeResCheck().isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        int hourLimit = vipService.getResCheckpointHourLimit(player);
        for(Map.Entry<Integer, Integer> m : player.getTimeResCheck().entrySet()){
            CheckPointTemplate ct = checkPointData.getTemplate(m.getKey());
            if(ct != null && !MyUtil.isNullOrEmpty(ct.getDropPoint())){
                int existsMinute = m.getValue();//原有分钟数
                if(player.getLoginoutTime() != null){//计算新的
                    int minuteAdd = (int)((now - player.getLoginoutTime().getTime())/(hourLimit *60 * 60* 1000));
                    existsMinute += minuteAdd;
                    if(existsMinute > hourLimit*60){
                        existsMinute = hourLimit*60;
                    }
                    player.getTimeResCheck().put(m.getKey(), existsMinute);
                }
                RewardDto dto = getResRewardDto(ct.getDropPoint(), existsMinute, hourLimit*60);
//    					MessageUtil.notifyTimeResToPlayer(player, m.getKey(), dto);
                resC.computeIfAbsent(player.getPlayerId(),pid->new HashMap<>())
                        .put(ct.getChapterId(),new ResCdto(ct.getChapterId(), ResourceService.getRewardString(dto)));

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
                        ItemTemplate max1 = itemService.itemData.getTemplate(200003);
                        ItemTemplate max2 = itemService.itemData.getTemplate(200002);
                        ItemTemplate max3 = itemService.itemData.getTemplate(200001);
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

    public Collection<ResCdto> getRec(Player player) {
		return resC.computeIfAbsent(player.getPlayerId(),pid->new HashMap<>()).values();
	}
}
