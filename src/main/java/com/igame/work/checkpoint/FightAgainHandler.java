package com.igame.work.checkpoint;

import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.checkpoint.baozouShike.BallisticConstant;
import com.igame.work.checkpoint.baozouShike.BallisticService;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.guanqia.GuanQiaDataManager;
import com.igame.work.checkpoint.guanqia.data.CheckPointTemplate;
import com.igame.work.checkpoint.worldEvent.WorldEventDataManager;
import com.igame.work.checkpoint.worldEvent.WorldEventDto;
import com.igame.work.checkpoint.worldEvent.WorldEventService;
import com.igame.work.checkpoint.worldEvent.WorldEventTemplate;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.Map;

public class FightAgainHandler extends ReconnectedHandler {
    @Inject
    private ResourceService resourceService;
    @Inject
    private CheckPointService checkPointService;
    @Inject private WorldEventService worldEventService;
    @Inject private BallisticService ballisticService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        Map<String, Object> param = checkPointService.getLastBattleParam(player.getPlayerId());
        if (param == null || !param.containsKey("battleType")) {
            return error(ErrorCode.CHECKPOINT_END_ERROR);
        }

        int battleType = (int)param.get("battleType");

        if(battleType == 1) { // 从CheckEnterHandler复制过来的
            int chapterId = (int) param.get("chapterId");

            //校验关卡ID
            CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(chapterId);
            if(ct == null){
                return error(ErrorCode.CHECKPOINT_ENTER_ERROR);
            }

            //校验背包空间
            if (player.getItems().size() >= player.getBagSpace()){
                return error(ErrorCode.BAGSPACE_ALREADY_FULL);
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

            resourceService.addPhysica(player, -ct.getPhysical());
            checkPointService.setEnterCheckpointId(player,chapterId);
            checkPointService.setEnterCheckPointTime(player);

            //减少可挑战次数
            player.getPlayerCount().addCheckPoint(player,ct,-1);

        } else if(battleType == 2) {   // 从WordEventEnterHandler复制过来的

            int eventType = (int) param.get("eventType");
            int level = (int) param.get("level");

            //入参校验
            WorldEventDto wd = player.getWordEvent().get(eventType);
            WorldEventTemplate wt = WorldEventDataManager.WorldEventData.getTemplate(eventType+"_"+level);
            if (wd == null || wt ==null){
                return error(ErrorCode.CHECKPOINT_ENTER_ERROR);
            }

            //校验前置难度是否通过, level = 1 时 wd.getLevel() 为 空字符串
            if (level != 1 && !wd.getLevel().contains(String.valueOf(level-1))){
                return error(ErrorCode.CHECKPOINT_ENTER_ERROR);
            }

            //校验背包
            if (player.getItems().size() >= player.getBagSpace()){
                return error(ErrorCode.BAGSPACE_ALREADY_FULL);
            }

            //校验体力
            if(player.getPhysical() < wt.getPhysical()){
                return error(ErrorCode.PHYSICA_NOT_ENOUGH);
            }

            //校验挑战次数
            if(wd.getCount() >= wt.getTimes()){
                return error(ErrorCode.TODAY_COUNT_NOTENOUGH);
            }

            //扣除体力
            resourceService.addPhysica(player, 0-wt.getPhysical());

            //防作弊
            checkPointService.setEnterWordEventTime(player);
            worldEventService.setEnterWordEventId(player,eventType+"_"+level);

        } else if (battleType == 3) {  // 从BallisticEnterHandler复制过来
            //校验等级
            int playerLevel = player.getPlayerLevel();
            if (playerLevel < BallisticConstant.BALL_LOCK_LV){
                return error(ErrorCode.BALLISTIC_LOCK);
            }

            //校验挑战次数
            int ballisticCount = player.getBallisticCount();
            if (ballisticCount >= BallisticConstant.BALL_CHALLENGE_COUNT_MAX){
                return error(ErrorCode.BALLISTIC_NOT_ENTER);
            }

            //校验体力
            int physical = player.getPhysical();
            if (physical < ballisticCount* BallisticConstant.BALL_PHYSICAL){
                return error(ErrorCode.PHYSICA_NOT_ENOUGH);
            }

            //记录开始时间
            ballisticService.setBallisticEnter(player,new Date());

        }

        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.FIGHT_AGAIN;
    }

}
