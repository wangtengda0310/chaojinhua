package com.igame.work.checkpoint.baozouShike.handler;

import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.checkpoint.baozouShike.BallisticService;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.List;

import static com.igame.work.checkpoint.baozouShike.BallisticConstant.BALL_MONSTER_EVERY;

/**
 * @author xym monster
 *
 * 暴走时刻请求怪兽
 */
public class BallisticMonsterHandler extends ReconnectedHandler {
    @Inject
    private BallisticService ballisticService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        int ballisticMonsters = ballisticService.getBallisticMonsters(player);

        //生成怪兽
        List<MatchMonsterDto> matchMonsterDtos = BallisticService.buildMonsterByKillNum(ballisticMonsters, BALL_MONSTER_EVERY);

        //增加怪兽刷新数量
        ballisticService.addBallisticMonsters(player, matchMonsterDtos.size());

        vo.addData("monsters", matchMonsterDtos);
        return vo;

    }

    @Override
    public int protocolId() {
        return MProtrol.BALLISTIC_MONSTER;
    }

}
