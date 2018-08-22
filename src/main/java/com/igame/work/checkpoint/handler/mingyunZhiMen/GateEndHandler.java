package com.igame.work.checkpoint.handler.mingyunZhiMen;


import java.util.List;

import net.sf.json.JSONObject;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.dto.GateDto;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.checkpoint.service.CheckPointService;
import com.igame.work.quest.service.QuestService;
import com.igame.work.system.RankService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * @author Marcus.Z
 */
public class GateEndHandler extends BaseHandler {


    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        RetVO vo = new RetVO();
        if (reviceMessage(user, params, vo)) {
            return;
        }

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if (player == null) {
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }


        int win = jsonObject.getInt("win");
        String monsHp = jsonObject.getString("monsHp");
        vo.addData("win", win);

        //校验等级
        /*if (player.getPlayerLevel() < 18) {
            sendError(ErrorCode.LEVEL_NOT, MProtrol.toStringProtrol(MProtrol.GATE_END), vo, user);
            return;
        }*/

        //校验是否领取
        if (player.getFateData().getGetReward() == 1) {
            sendError(ErrorCode.GATE_NOT, MProtrol.toStringProtrol(MProtrol.GATE_END), vo, user);
            return;
        }

        //异常校验
        int gateId = player.getFateData().getGateId();
        GateDto gto = player.getFateData().getGate(gateId);
        if (gto == null) {
            sendError(ErrorCode.ERROR, MProtrol.toStringProtrol(MProtrol.GATE_END), vo, user);
            return;
        }

        String reward = "";
        if (gto.getType() != 1) {//战斗

            if (win == 1) { //胜利
                for (String mh : monsHp.split(";")) {
                    long oId = Long.parseLong(mh.split(",")[0]);
                    int hp = Integer.parseInt(mh.split(",")[1]);
                    if (player.getMingZheng().get(oId) != null && hp < player.getMingZheng().get(oId).getHpInit()) {
                        player.getMingZheng().get(oId).setHp(hp);
                    }
                }
                player.getFateData().addTempBoxCount(gto.getBoxCount());//加宝箱
                player.getFateData().addTodayFateLevel();//到下一层
                List<GateDto> gls = CheckPointService.creatGate(player);//创建新的门
                player.getFateData().setGate(gls);
                MessageUtil.notiyDeInfoChange(player);
                MessageUtil.notiyGateChange(player);
                RewardDto rt = new RewardDto();
                ResourceService.ins().addRewarToPlayer(player, rt);
                reward = ResourceService.ins().getRewardString(rt);
                QuestService.processTask(player, 10, 1);

            } else {    //失败，即挑战结束

                //加入并刷新排行榜
                RankService.ins().setMRank(player);
                RankService.ins().sort();

                MessageUtil.notiyDeInfoChange(player);
            }

        }

        vo.addData("reward", reward);

        sendSucceed(MProtrol.toStringProtrol(MProtrol.GATE_END), vo, user);
    }


}
