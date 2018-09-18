package com.igame.work.checkpoint.baozouShike.handler;

import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.checkpoint.baozouShike.BallisticRankClientData;
import com.igame.work.checkpoint.baozouShike.BallisticRanker;
import com.igame.work.checkpoint.baozouShike.BallisticService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.igame.work.checkpoint.baozouShike.BallisticConstant.BALL_LOCK_LV;
import static com.igame.work.checkpoint.baozouShike.BallisticConstant.BALL_RANK_SHOW;

/**
 * @author xym
 *
 * 暴走时刻排行榜
 */
public class BallisticRankHandler extends ReconnectedHandler {

    @Inject private BallisticService ballisticService;
    @Inject private PlayerCacheService playerCacheService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        //校验等级
        int playerLevel = player.getPlayerLevel();
        if (playerLevel < BALL_LOCK_LV){
            return error(ErrorCode.BALLISTIC_LOCK);
        }

        //return
        int rank = 0;
        int rankPercent = -1;
        int score = 0;
        int ballisticCount;
        int buff;
        List<BallisticRankClientData> topFifty;

        //获取挑战次数
        ballisticCount = player.getBallisticCount();

        //获取buff
        buff = ballisticService.getRankDto().getBuff();

        //排序并取前50
        topFifty = ballisticService.getRankDto().getRank().values().stream()
                .sorted(BallisticRanker::compareTo)
                .limit(BALL_RANK_SHOW)
                .map(this::toClientData)
                .collect(Collectors.toList());

        //set排名与昵称
        for (int i = 0; i < topFifty.size(); i++) {
            BallisticRankClientData ballisticRanker = topFifty.get(i);
            ballisticRanker.setRank(i+1);
            ballisticRanker.setName(playerCacheService.getPlayerById(ballisticRanker.getPlayerId()).getNickname());
        }

        //获取玩家排行榜信息
        Map<Long, BallisticRanker> rankerMap = ballisticService.getRankDto().getRank();
        if (rankerMap != null && rankerMap.get(player.getPlayerId()) != null) {    //玩家在排行榜中

            BallisticRanker ranker = rankerMap.get(player.getPlayerId());

            rank = new ArrayList<>(rankerMap.values()).indexOf(ranker) + 1;
            int percent = rank / rankerMap.size() * 100;
            rankPercent = Math.round(percent);
            score = ranker.getScore();
        }

        vo.addData("playerScore", score);
        vo.addData("playerRank", rank);
        vo.addData("rankPercent", rankPercent);
        vo.addData("challengeCount", ballisticCount);
        vo.addData("currentBuff", buff);
        vo.addData("ranks", topFifty);
        return vo;

    }

    private BallisticRankClientData toClientData(BallisticRanker rankDto) {
        BallisticRankClientData r = new BallisticRankClientData();
        r.setPlayerId(rankDto.getPlayerId());
        r.setScore(r.getScore());
        return r;
    }

    @Override
    public int protocolId() {
        return MProtrol.BALLISTIC_RANKS;
    }

}
