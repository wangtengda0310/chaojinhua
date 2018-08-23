package com.igame.work.checkpoint.baozouShike.handler;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.baozouShike.BallisticService;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.List;

import static com.igame.work.checkpoint.guanqia.CheckPointContants.BALL_MONSTER_EVERY;

/**
 * @author xym monster
 *
 * 暴走时刻请求怪兽
 */
public class BallisticMonsterHandler extends BaseHandler{

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

        int ballisticMonsters = player.getBallisticMonsters();

        //生成怪兽
        List<MatchMonsterDto> matchMonsterDtos = BallisticService.ins().buildMonsterByKillNum(ballisticMonsters, BALL_MONSTER_EVERY);

        //增加怪兽刷新数量
        player.addBallisticMonsters(matchMonsterDtos.size());

        vo.addData("monsters", matchMonsterDtos);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.BALLISTIC_MONSTER), vo, user);

    }
}
