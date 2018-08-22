package com.igame.work.checkpoint.handler.guanqia;


import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.GuanQiaDataManager;
import com.igame.work.checkpoint.data.CheckPointTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class CheckEnterHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {

		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}

		int chapterId = jsonObject.getInt("chapterId");
		vo.addData("chapterId", chapterId);

		//校验关卡ID
		CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(chapterId);
		if(ct == null){
			sendError(ErrorCode.CHECKPOINT_ENTER_ERROR,MProtrol.toStringProtrol(MProtrol.CHECKPOINT_ENTER), vo, user);
			return;
		}

		//校验背包空间
		if (player.getItems().size() >= player.getBagSpace()){
			sendError(ErrorCode.BAGSPACE_ALREADY_FULL,MProtrol.toStringProtrol(MProtrol.CHECKPOINT_ENTER), vo, user);
			return;
		}

		//校验前置关卡
		if(!MyUtil.isNullOrEmpty(ct.getLimit()) && isLock(player, ct)){
			sendError(ErrorCode.CHECKPOINT_ENTER_ERROR,MProtrol.toStringProtrol(MProtrol.CHECKPOINT_ENTER), vo, user);
			return;
		}

		//校验体力
		if(player.getPhysical() < ct.getPhysical()){
			sendError(ErrorCode.PHYSICA_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.CHECKPOINT_ENTER), vo, user);
			return;
		}

		//校验心魔
		if(player.getXinMo().get(chapterId) != null && player.getXinMo().get(chapterId).calLeftTime(System.currentTimeMillis()) > 0){
			sendError(ErrorCode.XINGMO_EXIT,MProtrol.toStringProtrol(MProtrol.CHECKPOINT_ENTER), vo, user);
			return;
		}

        //校验挑战次数
        if (player.getPlayerCount().getCheckPoint(ct.getChapterType(),chapterId) <= 0){
            sendError(ErrorCode.CHECKCOUNT_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.CHECKPOINT_ENTER), vo, user);
            return;
        }


		if(ct.getChapterType() == 1 || ct.getChapterType()==3){	//普通关卡

			meetMonster(player, ct);

			/*player.setFightBase(fb);
			for(Monster m : fb.getFightB().getMonsters().values()){
				MatchMonsterDto mto = new MatchMonsterDto(m);
				lb.add(mto);
			}
			PVPFightService.ins().fights.put(fb.getObjectId(), fb);*/

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

					for(TangSuoTemplate ts : DataManager.TangSuoData.getAll()){//解锁探索关卡
						if(chapterId ==  ts.getUnlock() && player.getTangSuo().get(ts.getNum()) == null){
							player.getTangSuo().put(ts.getNum(), new TangSuoDto(ts));
						}
					}
					for(WorldEventTemplate ts : DataManager.WordEventData.getAll()){//解锁世界事件
						if(ts.getLevel() == 1 && chapterId ==  ts.getUnlock()){
							WordEventDto wet = player.getWordEvent().get(ts.getEvent_type());
							if(wet == null){
								wet = new WordEventDto(player.getPlayerId(), ts.getEvent_type(), String.valueOf(ts.getLevel()), 0,1);
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

		ResourceService.ins().addPhysica(player, 0-ct.getPhysical());
		player.setEnterCheckpointId(chapterId);
		player.setEnterCheckPointTime(System.currentTimeMillis());

		//减少可挑战次数
        player.getPlayerCount().addCheckPoint(player,ct,-1);

		sendSucceed(MProtrol.toStringProtrol(MProtrol.CHECKPOINT_ENTER), vo, user);
	}

    /**
     * 怪兽图鉴
     * @param player 角色
     * @param template 关卡模板
     */
	private void meetMonster(Player player, CheckPointTemplate template) {

		String meetM = template.getMonsterId();
		if(!MyUtil.isNullOrEmpty(meetM)){

            boolean change = false;
            for(String ids :meetM.split(":")){
                for(String id :ids.split(",")){
                    int mid = Integer.parseInt(id);
                    if(MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mid) != null && !player.getMeetM().contains(mid)){
                        player.getMeetM().add(mid);
                        change = true;
                    }
                }
            }

            if(change){
                MessageUtil.notiyMeetM(player);
            }
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
            if(MyUtil.hasCheckPoint(player.getCheckPoint(), temp)){
                isNotExist = false;
                break;
            }
        }
        
		return isNotExist;
	}


}
