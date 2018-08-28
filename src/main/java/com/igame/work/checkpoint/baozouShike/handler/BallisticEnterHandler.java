package com.igame.work.checkpoint.baozouShike.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.baozouShike.BallisticService;
import com.igame.work.checkpoint.baozouShike.BaozouShikeDataManager;
import com.igame.work.checkpoint.baozouShike.data.RunTemplate;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.List;

import static com.igame.work.checkpoint.guanqia.CheckPointContants.*;

/**
 * @author xym
 *
 * 暴走时刻进入战斗
 */
public class BallisticEnterHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        String aidMonsters = jsonObject.getString("aidMonsters") == null? "" : jsonObject.getString("aidMonsters");

        //校验等级
        int playerLevel = player.getPlayerLevel();
        if (playerLevel < BALL_LOCK_LV){
            return error(ErrorCode.BALLISTIC_LOCK);
        }

        //校验挑战次数
        int ballisticCount = player.getBallisticCount();
        if (ballisticCount >= BALL_CHALLENGE_COUNT_MAX){
            return error(ErrorCode.BALLISTIC_NOT_ENTER);
        }

        //校验体力
        int physical = player.getPhysical();
        if (physical < ballisticCount*BALL_PHYSICAL){
            return error(ErrorCode.PHYSICA_NOT_ENOUGH);
        }

        //生成怪兽
        RunTemplate template = BaozouShikeDataManager.runData.getTemplate(BALL_MONSTER_INIT);
        List<MatchMonsterDto> matchMonsterDtos = BallisticService.buildMonster(template, BALL_MONSTER_INIT);

        //记录开始时间
        player.setBallisticEnter(new Date());
        //记录怪兽刷新数量
        player.setBallisticMonsters(BALL_MONSTER_INIT);
        //记录援助怪兽
        player.setBallisticAid(aidMonsters);
        for(MatchMonsterDto mto : matchMonsterDtos){
			mto.reCalGods(player.callFightGods(), null);
        }
        vo.addData("monsters", matchMonsterDtos);
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.BALLISTIC_ENTER;
    }

}
