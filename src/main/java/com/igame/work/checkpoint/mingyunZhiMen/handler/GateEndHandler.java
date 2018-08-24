package com.igame.work.checkpoint.mingyunZhiMen.handler;


import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.mingyunZhiMen.GateDto;
import com.igame.work.checkpoint.mingyunZhiMen.GateService;
import com.igame.work.quest.service.QuestService;
import com.igame.work.system.RankService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * @author Marcus.Z
 */
public class GateEndHandler extends ReconnectedHandler {


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
                    if (player.getMingZheng().get(oId) != null && hp < player.getMingZheng().get(oId).getHpInit()) {
                        player.getMingZheng().get(oId).setHp(hp);
                    }
                }
                player.getFateData().addTempBoxCount(gto.getBoxCount());//加宝箱
                player.getFateData().addTodayFateLevel();//到下一层
                List<GateDto> gls = GateService.creatGate(player);//创建新的门
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

        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.GATE_END;
    }

}
