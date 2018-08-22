package com.igame.work.monster.handler;

import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * @author Marcus.Z
 */
public class MonsterHandler extends BaseHandler {


    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        RetVO vo = new RetVO();
        if (reviceMessage(user, params, vo)) {
            return;
        }
        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if (player == null) {
            this.getLogger().error(this.getClass().getSimpleName(), " get player failed Name:" + user.getName());
            return;
        }

        String infor = params.getUtfString("infor");
        String[] teamArry = infor.split(";");

        for (String teaminfoStr : teamArry) {
            String[] teaminfo = teaminfoStr.split(":");
            try {

                int teamId = Integer.parseInt(teaminfo[0]);

                if (teamId < 1 || teamId > 6) {//不存在的组
                    continue;
                }

                if (modifyTeam(player, teamId, teaminfo[1])) {

                    //更新阵容战力
                    ComputeFightService.ins().computeTeamFight(player, teamId);
                    MessageUtil.notiyTeamChange(player,player.getTeams().get(teamId));

                    if (teamId == player.getCurTeam()) {
                        ComputeFightService.ins().computePlayerFight(player);
                    }
                }
            } catch (Exception ignore) {
            }

        }

        sendSucceed(MProtrol.toStringProtrol(MProtrol.CHANGE_TEAM), vo, user);
    }

    private boolean modifyTeam(Player player, int teamId, String monsters) {

        String[] monsArry = monsters.split(",");
        long[] team = player.getTeams().get(teamId).getTeamMonster();
        boolean modified = false;
        for (int j = 0; j < monsArry.length; j++) {
            if (team[j] == -1) {//位置未解锁
                continue;
            }
            if ("".equals(monsArry[j]) || "0".equals(monsArry[j])) {
                modified = true;
                team[j] = 0;
            } else {
                try {
                    long monsterId = Long.parseLong(monsArry[j]);
                    if (player.getMonsters().get(monsterId) == null) {
                        continue;
                    }
                    modified = true;
                    team[j] = monsterId;
                } catch (Exception ignore) {
                }
            }
        }
        return modified;
    }
}
