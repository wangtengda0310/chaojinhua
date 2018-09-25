package com.igame.work.checkpoint.guanqia.handler;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.guanqia.data.CheckPointTemplate;
import com.igame.work.checkpoint.xinmo.XingMoDto;
import com.igame.work.fight.dto.GodsDto;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.service.RobotService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Marcus.Z
 *
 */
public class CheckInfoHandler extends ReconnectedHandler {

	@Inject private RobotService robotService;
	@Inject private CheckPointService checkPointService;
	@Inject private MonsterService monsterService;


	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int chapterId = jsonObject.getInt("chapterId");
		vo.addData("chapterId", chapterId);

		//入参校验
		CheckPointTemplate ct = checkPointService.checkPointData.getTemplate(chapterId);
		if(ct == null){
			return error(ErrorCode.CHECKPOINT_ENTER_ERROR);
		}

		//校验前置关卡
		if(!MyUtil.isNullOrEmpty(ct.getLimit()) && isLock(player, ct)){
			return error(ErrorCode.CHECKPOINT_ENTER_ERROR);
		}

		//return
		int ctype;
		int todayCount = 0;
		long playerId = 0;
		String name = "";
		int level = 0;
		int playerFrameId = 0;
		int playerHeadId = 0;
		GodsDto gods = new GodsDto();
		List<MatchMonsterDto> lb = Lists.newArrayList();

		if (isExistXinmo(player,chapterId)){	//如果关卡有心魔,返回心魔数据

			Map<String,RobotDto> ro = robotService.getRobot();
			XingMoDto xingMoDto = player.getXinMo().get(chapterId);
			if(ro == null || xingMoDto == null){
				return error(ErrorCode.XINGMO_LEAVEL);
			}

			ctype = 2;
			RobotDto robotDto = ro.get(xingMoDto.getMid());
			playerId = robotDto.getPlayerId();
			name = robotDto.getName();
			level = robotDto.getLevel();
			gods = robotDto.getGods();
			long id =100;
			for(MatchMonsterDto mo : robotDto.getMon()){//处理神灵加成属性
				MatchMonsterDto mto = mo.clonew(player.currentFightGods(), robotDto.getGods());
				mto.setObjectId(id);
				lb.add(mto);
				id++;
			}
			playerFrameId = robotDto.getPlayerFrameId();
			playerHeadId = robotDto.getPlayerHeadId();

		}else {	//如果关卡没有心魔,返回普通关卡数据

			ctype = 1;
			todayCount = player.getPlayerCount().getCheckPoint(ct.getChapterType(),chapterId);

			if(ct.getChapterType() == 1 || ct.getChapterType()==3){
				AtomicInteger idx = new AtomicInteger(0);

				if(ct.getRound() == 1){
					process(player, lb, chapterId,idx);
				}
				if(ct.getRound() == 2){
					int front =  chapterId-140;
					CheckPointTemplate ft = checkPointService.checkPointData.getTemplate(front);
					if(ft != null && !player.hasCheckPoint(String.valueOf(front))){
						return error(ErrorCode.CHECKPOINT_ENTER_ERROR);
					}else{
						process(player, lb, chapterId,idx);
						process(player, lb, chapterId-140,idx);
					}

				}
				if(ct.getRound() == 3){
					int front =  chapterId-280;
					CheckPointTemplate ft = checkPointService.checkPointData.getTemplate(front);
					if(ft != null && !player.hasCheckPoint(String.valueOf(front))){
						return error(ErrorCode.CHECKPOINT_ENTER_ERROR);
					}else{
						process(player, lb, chapterId,idx);
						process(player, lb, chapterId-140,idx);
						process(player, lb, chapterId-280,idx);
					}
				}

			}

		}

		vo.addData("ctype", ctype);
		vo.addData("playerId", playerId);
		vo.addData("name", name);
		vo.addData("level", level);
		vo.addData("playerFrameId", playerFrameId);
		vo.addData("playerHeadId", playerHeadId);
		vo.addData("gods", gods);
		vo.addData("m", lb);
		vo.addData("todayCount", todayCount);

		return vo;
	}

	@Override
	public int protocolId() {
		return MProtrol.ENTER_CHECK;
	}

	/**
	 * 前置关卡是否通关
	 * @param player 当前角色
	 * @param template 关卡模板
	 * @return true = 未通关 false = 已通关
	 */
	private boolean isLock(Player player, CheckPointTemplate template) {

		if(MyUtil.isNullOrEmpty(player.getCheckPoint())){
			return true;
		}

		boolean isNotExist = true;
		for(String temp : template.getLimit().split(",")){
			if(player.hasCheckPoint(temp)){
				isNotExist = false;
				break;
			}
		}

		return isNotExist;
	}

	/**
	 * 关卡是否存在心魔
	 * @param player 当前角色
	 * @param chapterId 关卡ID
	 */
	private boolean isExistXinmo(Player player, int chapterId) {
		XingMoDto xingMoDto = player.getXinMo().get(chapterId);
		return (xingMoDto != null && xingMoDto.calLeftTime(System.currentTimeMillis()) > 0);
	}

	private void process(Player player,List<MatchMonsterDto> lb,int chapterId,AtomicInteger idx){
		CheckPointTemplate ct = checkPointService.checkPointData.getTemplate(chapterId);
		if(ct != null){
			if(ct.getRound() == 1){//一周目
				String monsterId = ct.getMonsterId();
				String monsterLevel = ct.getLevel();
				String position = ct.getSite();
				String skillLv = "";
				String round = "1-1";

				createMatchMonsterDto(player, lb, idx, monsterId, monsterLevel, position, skillLv, ct.getMonsterProp(), round);
			}else if(ct.getRound() > 1){//二 三周目
				
				String[] mid = ct.getMonsterId().split(":");
				String[] lv = ct.getLevel().split(":");
				String[] site = ct.getSite().split(":");
				if(mid.length > 0){
					int index = 1;
					for(int i =0;i<mid.length;i++){
						String round = ct.getRound() + "-" + index++;

						String monsterIds = mid[i];
						String monsterLevel = lv[i];
						String position = site[i];
						String skillLv = "";

						createMatchMonsterDto(player, lb, idx, monsterIds, monsterLevel, position, skillLv, ct.getMonsterProp(), round);
					}
				}
				
			}
		}
	}

	private void createMatchMonsterDto(Player player, List<MatchMonsterDto> lb, AtomicInteger idx
            , String monsterId, String monsterLevel, String position, String skillLv, String equips, String round) {
		Map<Long, Monster> monster = monsterService.batchCreateMonster(monsterId, monsterLevel, position, skillLv, equips);
		for (Map.Entry<Long, Monster> e : monster.entrySet()) {
			Monster m = e.getValue();
			m.setObjectId(idx.incrementAndGet());
			MatchMonsterDto mto = new MatchMonsterDto(m, e.getKey().intValue());
			mto.reCalGods(player.currentFightGods(), null);//神灵被动属性
			mto.setRound(round);
			lb.add(mto);
		}
	}


}
