package com.igame.work.monster.handler;

import com.igame.core.di.Inject;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * @author Marcus.Z
 */
public class MonsterHandler extends ReconnectedHandler {


    @Inject private ComputeFightService computeFightService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        RetVO vo = new RetVO();

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
                    computeFightService.computeTeamFight(player, teamId);
                    MessageUtil.notifyTeamChange(player,player.getTeams().get(teamId));

                    if (teamId == player.getCurTeam()) {
                        computeFightService.computePlayerFight(player);
                    }
                }
            } catch (Exception ignore) {
            }

        }

        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.CHANGE_TEAM;
    }

    private boolean modifyTeam(Player player, int teamId, String monsters) {

        String[] monsArry = monsters.split(",");
        long[] team = player.getTeams().get(teamId).getTeamMonster();
        boolean modified = false;
        for (int j = 0; j < 5; j++) {
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
        if (monsArry.length>5) {
            try {
                String god = monsArry[5];
                int teamGod = player.getTeams().get(teamId).getTeamGod();
                if (!String.valueOf(teamGod).equals(god)) {
                    player.getTeams().get(teamId).setTeamGod(Integer.parseInt(god));
                    modified = true;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return modified;
    }
}
