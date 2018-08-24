package com.igame.work.user.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xym
 *
 * 更换出战阵容
 */
public class ReplaceTeamHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int teamId = jsonObject.getInt("teamId");

        RetVO vo = new RetVO();

        vo.addData("teamId",teamId);

        if (teamId < 1|| 5 < teamId){
            return error(ErrorCode.PARAMS_INVALID);
        }

        player.setCurTeam(teamId);

        //重新计算阵容战力
        ComputeFightService.ins().computeTeamFight(player,teamId);
        player.setFightValue(player.getTeams().get(teamId).getFightValue());

        List<Long> changeMonster = player.getTeams().get(teamId).getChangeMonster();
        vo.addData("changeMonsters",new ArrayList<>(changeMonster));

        if (changeMonster.size() > 0){
            changeMonster.clear();
        }

        vo.addData("fightValue",player.getTeams().get(teamId).getFightValue());
        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.REPLACE_TEAM;
    }

}
