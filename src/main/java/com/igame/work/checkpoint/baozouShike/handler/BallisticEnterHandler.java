package com.igame.work.checkpoint.baozouShike.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.baozouShike.BaozouShikeDataManager;
import com.igame.work.checkpoint.baozouShike.BallisticService;
import com.igame.work.checkpoint.baozouShike.data.RunTemplate;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
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
public class BallisticEnterHandler extends BaseHandler{

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

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        String aidMonsters = jsonObject.getString("aidMonsters") == null? "" : jsonObject.getString("aidMonsters");

        //校验等级
        int playerLevel = player.getPlayerLevel();
        if (playerLevel < BALL_LOCK_LV){
            sendError(ErrorCode.BALLISTIC_LOCK, MProtrol.toStringProtrol(MProtrol.BALLISTIC_ENTER), vo, user);
            return;
        }

        //校验挑战次数
        int ballisticCount = player.getBallisticCount();
        if (ballisticCount >= BALL_CHALLENGE_COUNT_MAX){
            sendError(ErrorCode.BALLISTIC_NOT_ENTER, MProtrol.toStringProtrol(MProtrol.BALLISTIC_ENTER), vo, user);
            return;
        }

        //校验体力
        int physical = player.getPhysical();
        if (physical < ballisticCount*BALL_PHYSICAL){
            sendError(ErrorCode.PHYSICA_NOT_ENOUGH, MProtrol.toStringProtrol(MProtrol.BALLISTIC_ENTER), vo, user);
            return;
        }

        //生成怪兽
        RunTemplate template = BaozouShikeDataManager.runData.getTemplate(BALL_MONSTER_INIT);
        List<MatchMonsterDto> matchMonsterDtos = BallisticService.ins().buildMonster(template, BALL_MONSTER_INIT);

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
        sendSucceed(MProtrol.toStringProtrol(MProtrol.BALLISTIC_ENTER), vo, user);
    }
}
