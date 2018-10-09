package com.igame.work.checkpoint.guanqia.handler;


import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.guanqia.data.CheckPointTemplate;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class CheckEnterHandler extends ReconnectedHandler {

	@Inject private ResourceService resourceService;
	@Inject private CheckPointService checkPointService;
	@Inject private MonsterService monsterService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int chapterId = jsonObject.getInt("chapterId");
		vo.addData("chapterId", chapterId);

		//校验关卡ID
		CheckPointTemplate ct = checkPointService.checkPointData.getTemplate(chapterId);
		if(ct == null){
			return error(ErrorCode.CHECKPOINT_ENTER_ERROR);
		}

		//校验背包空间
		if (player.getItems().size() >= player.getBagSpace()){
			return error(ErrorCode.BAGSPACE_ALREADY_FULL);
		}

		//校验前置关卡
		if(!MyUtil.isNullOrEmpty(ct.getLimit()) && isLock(player, ct)){
			return error(ErrorCode.CHECKPOINT_ENTER_ERROR);
		}

		//校验体力
		if(player.getPhysical() < ct.getPhysical()){
			return error(ErrorCode.PHYSICA_NOT_ENOUGH);
		}

		//校验心魔
		if(player.getXinMo().get(chapterId) != null && player.getXinMo().get(chapterId).calLeftTime(System.currentTimeMillis()) > 0){
			return error(ErrorCode.XINGMO_EXIT);
		}

        //校验挑战次数
        if (player.getPlayerCount().getCheckPoint(ct.getChapterType(),chapterId) <= 0){
            return error(ErrorCode.CHECKCOUNT_NOT_ENOUGH);
        }


		if(ct.getChapterType() == 1 || ct.getChapterType()==3){	//普通关卡

			meetMonster(player, ct);

			/*player.setFightBase(fb);
			for(Monster m : fb.getFightB().getMonsters().values()){
				MatchMonsterDto mto = new MatchMonsterDto(m);
				lb.add(mto);
			}
			pvpFightService.fights.put(fb.getObjectId(), fb);*/

		}else{	//资源关卡
			/*boolean resChec = false;
			if(!MyUtil.isNullOrEmpty(ct.getDropPoint())){
				if(player.getTimeResCheck().get(chapterId) == null){//资源关卡
					player.getTimeResCheck().put(chapterId, ct.getMaxTime() * 60);
					resChec = true;
				}
			}
			if(resChec){
				if(player.getCheckPoint() == null || !MyUtil.hasCheckPoint(player.getCheckPoint(), String.valueOf(chapterId))){
					if(player.getCheckPoint() == null || "".equals(player.getCheckPoint())){
						player.setCheckPoint(String.valueOf(chapterId));
					}else{
						player.setCheckPoint(player.getCheckPoint()+"," +String.valueOf(chapterId));
					}
					RetVO v = new RetVO();
					v.addData("checkPoint", player.getCheckPoint());
					MessageUtil.sendMessageToPlayer(player, MProtrol.CHECKPOINT_LIST, v);

					MessageUtil.notiyUnLockCheck(player, ct.getUnlock(),chapterId);//推送解锁关卡

					for(TansuoTemplate ts : DataManager.tansuoData.getAll()){//解锁探索关卡
						if(chapterId ==  ts.getUnlock() && player.getTangSuo().get(ts.getNum()) == null){
							player.getTangSuo().put(ts.getNum(), new TansuoDto(ts));
						}
					}
					for(WorldEventTemplate ts : DataManager.worldEventData.getAll()){//解锁世界事件
						if(ts.getLevel() == 1 && chapterId ==  ts.getUnlock()){
							WorldEventDto wet = player.getWordEvent().get(ts.getEvent_type());
							if(wet == null){
								wet = new WorldEventDto(player.getPlayerId(), ts.getEvent_type(), String.valueOf(ts.getLevel()), 0,1);
								player.getWordEvent().put(ts.getEvent_type(), wet);
							}else{
								if(wet.getLevel().indexOf(String.valueOf(ts.getLevel())) == -1){
									wet.setLevel(wet.getLevel()+"," + ts.getLevel());
									wet.setDtate(2);
								}
							}
						}
					}
					PlayerService.checkDrawData(player, true);//检测造物台数据
					QuestService.processTask(player, 23, 0);//占领金矿数
					QuestService.processTask(player, 25, 0);//城市占领完成度

				}
			}*/
		}

		resourceService.addPhysica(player, 0-ct.getPhysical());
		checkPointService.setEnterCheckpointId(player,chapterId);
		checkPointService.setEnterCheckPointTime(player);

		//减少可挑战次数
        player.getPlayerCount().addCheckPoint(player,ct,-1);

		Map<String, Object> param = new HashMap<>();
		param.put("battleType", 1);
		param.put("chapterId", chapterId);
		param.put("nianya", player.hasCheckPoint(String.valueOf(chapterId)) ? 1 : 0);
		checkPointService.setLastBattleParam(player.getPlayerId(), param);

		addNianyaBuff(player);
		return vo;
	}

	private void addNianyaBuff(Player player) {
		long[] teamMonster = player.getTeams().get(player.getCurTeam()).getTeamMonster();
		Arrays.stream(teamMonster).forEach(monsterId->{

			Monster monster = player.getMonsters().get(monsterId);
			if (monster != null) {
				monster.setHp(monster.getHp()*2);
				monster.setAttack(monster.getAttack()*15/10);
			}
		});
	}

	@Override
    public int protocolId() {
		return MProtrol.CHECKPOINT_ENTER;
	}

	/**
     * 怪兽图鉴
     * @param player 角色
     * @param template 关卡模板
     */
	// todo async?
	private void meetMonster(Player player, CheckPointTemplate template) {

		String meetM = template.getMonsterSet();
		if(!MyUtil.isNullOrEmpty(meetM)){
			throw new UnsupportedOperationException("怪物这里调用怪物组模块生成怪物");
//            boolean change = false;
//            for(String ids :meetM.split(":")){
//				change = monsterService.isChange(player, ids, change);
//			}
//
//            if(change){
//                MessageUtil.notifyMeetM(player);
//            }
        }
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


}
