package com.igame.work.checkpoint.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.GuanQiaDataManager;
import com.igame.work.checkpoint.data.CheckPointTemplate;
import com.igame.work.checkpoint.data.WorldEventTemplate;
import com.igame.work.checkpoint.dto.WordEventDto;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.Map;

import static com.igame.work.checkpoint.CheckPointContants.*;

public class FightAgainHandler extends BaseHandler {
    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        RetVO vo = new RetVO();
        if(reviceMessage(user,params,vo)){
            return;
        }

        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if (player == null) {
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        Map<String, Object> param = player.getLastBattleParam();
        int battleType = (int)param.get("battleType");

        if(battleType == 1) { // 从CheckEnterHandler复制过来的
            int chapterId = (int) param.get("chapterId");

            //校验关卡ID
            CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(chapterId);
            if(ct == null){
                sendError(ErrorCode.CHECKPOINT_ENTER_ERROR, MProtrol.toStringProtrol(MProtrol.CHECKPOINT_ENTER), vo, user);
                return;
            }

            //校验背包空间
            if (player.getItems().size() >= player.getBagSpace()){
                sendError(ErrorCode.BAGSPACE_ALREADY_FULL,MProtrol.toStringProtrol(MProtrol.CHECKPOINT_ENTER), vo, user);
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

            ResourceService.ins().addPhysica(player, -ct.getPhysical());
            player.setEnterCheckpointId(chapterId);
            player.setEnterCheckPointTime(System.currentTimeMillis());

            //减少可挑战次数
            player.getPlayerCount().addCheckPoint(player,ct,-1);

        } else if(battleType == 2) {   // 从WordEventEnterHandler复制过来的

            int eventType = (int) param.get("eventType");
            int level = (int) param.get("level");

            //入参校验
            WordEventDto wd = player.getWordEvent().get(eventType);
            WorldEventTemplate wt = GuanQiaDataManager.WordEventData.getTemplate(eventType+"_"+level);
            if (wd == null || wt ==null){
                sendError(ErrorCode.CHECKPOINT_ENTER_ERROR,MProtrol.toStringProtrol(MProtrol.WWORDEVENT_ENTER), vo, user);
                return;
            }

            //校验前置难度是否通过, level = 1 时 wd.getLevel() 为 空字符串
            if (level != 1 && !wd.getLevel().contains(String.valueOf(level-1))){
                sendError(ErrorCode.CHECKPOINT_ENTER_ERROR,MProtrol.toStringProtrol(MProtrol.WWORDEVENT_ENTER), vo, user);
                return;
            }

            //校验背包
            if (player.getItems().size() >= player.getBagSpace()){
                sendError(ErrorCode.BAGSPACE_ALREADY_FULL,MProtrol.toStringProtrol(MProtrol.WWORDEVENT_ENTER), vo, user);
                return;
            }

            //校验体力
            if(player.getPhysical() < wt.getPhysical()){
                sendError(ErrorCode.PHYSICA_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.WWORDEVENT_ENTER), vo, user);
                return;
            }

            //校验挑战次数
            if(wd.getCount() >= wt.getTimes()){
                sendError(ErrorCode.TODAY_COUNT_NOTENOUGH,MProtrol.toStringProtrol(MProtrol.WWORDEVENT_ENTER), vo, user);
                return;
            }

            //扣除体力
            ResourceService.ins().addPhysica(player, 0-wt.getPhysical());

            //防作弊
            player.setEnterWordEventTime(System.currentTimeMillis());
            player.setEnterWordEventId(eventType+"_"+level);

        } else if (battleType == 3) {  // 从BallisticEnterHandler复制过来
            //校验等级
            int playerLevel = player.getPlayerLevel();
            if (playerLevel < BALL_LOCK_LV){
                sendError(ErrorCode.BALLISTIC_LOCK, MProtrol.toStringProtrol(MProtrol.BALLISTIC_ENTER), vo, user);
                return;
            }

            //校验挑战次数
            int ballisticCount = player.getBallisticCount();
            if (ballisticCount >= BALL_CHALLENGE_COUNT_MAX){
                sendError(ErrorCode.BALLISTIC_NOT_ENTER, MProtrol.toStringProtrol(MProtrol.BALLISTIC_ENTER), vo, user);
                return;
            }

            //校验体力
            int physical = player.getPhysical();
            if (physical < ballisticCount*BALL_PHYSICAL){
                sendError(ErrorCode.PHYSICA_NOT_ENOUGH, MProtrol.toStringProtrol(MProtrol.BALLISTIC_ENTER), vo, user);
                return;
            }

            //记录开始时间
            player.setBallisticEnter(new Date());

        }
    }
}
