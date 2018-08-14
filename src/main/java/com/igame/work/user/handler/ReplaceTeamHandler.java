package com.igame.work.user.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xym
 *
 * 更换出战阵容
 */
public class ReplaceTeamHandler extends BaseHandler{

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

        int teamId = jsonObject.getInt("teamId");
        vo.addData("teamId",teamId);

        if (teamId < 1|| 5 < teamId){
            sendError(ErrorCode.PARAMS_INVALID,MProtrol.toStringProtrol(MProtrol.REPLACE_TEAM),vo,user);
            return;
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
        sendSucceed(MProtrol.toStringProtrol(MProtrol.REPLACE_TEAM),vo,user);
    }
}
