package com.igame.work.checkpoint.mingyunZhiMen.handler;


import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.*;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.mingyunZhiMen.GateDto;
import com.igame.work.checkpoint.mingyunZhiMen.GateService;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * @author Marcus.Z
 */
public class GateEndHandler extends ReconnectedHandler {

    @Inject private ResourceService resourceService;
    @Inject private QuestService questService;
    @Inject private GateService gateService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);


        int win = jsonObject.getInt("win");
        String monsHp = jsonObject.getString("monsHp");
        vo.addData("win", win);

        //校验等级
        /*if (player.getPlayerLevel() < 18) {
            return error(ErrorCode.LEVEL_NOT);
        }*/

        //校验是否领取
        if (player.getFateData().getGetReward() == 1) {
            return error(ErrorCode.GATE_NOT);
        }

        //异常校验
        int gateId = player.getFateData().getGateId();
        GateDto gto = player.getFateData().getGate(gateId);
        if (gto == null) {
            return error(ErrorCode.ERROR);
        }

        String reward = "";
        if (gto.getType() != 1) {//战斗

            if (win == 1) { //胜利
                for (String mh : monsHp.split(";")) {
                    long oId = Long.parseLong(mh.split(",")[0]);
                    int hp = Integer.parseInt(mh.split(",")[1]);
                    if (gateService.getMingZheng(player).get(oId) != null && hp < gateService.getMingZheng(player).get(oId).getHpInit()) {
                        gateService.getMingZheng(player).get(oId).setHp(hp);
                    }
                }
                player.getFateData().addTempBoxCount(gto.getBoxCount());//加宝箱
                player.getFateData().addTodayFateLevel();//到下一层
                List<GateDto> gls = GateService.createGate(player);//创建新的门
                player.getFateData().setGate(gls);
                MessageUtil.notifyDeInfoChange(player);
                MessageUtil.notifyGateChange(player);
                RewardDto rt = new RewardDto();
                resourceService.addRewarToPlayer(player, rt);
                reward = resourceService.getRewardString(rt);
                questService.processTask(player, 10, 1);

            } else {    //失败，即挑战结束

                fireEvent(player, PlayerEvents.UPDATE_M_RANK, null);

                MessageUtil.notifyDeInfoChange(player);
            }

        }

        vo.addData("checkReward", reward);

        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.GATE_END;
    }

}
