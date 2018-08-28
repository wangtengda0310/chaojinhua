package com.igame.work.checkpoint.baozouShike.handler;

import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.checkpoint.baozouShike.BallisticRanker;
import com.igame.work.checkpoint.baozouShike.BallisticService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.igame.work.checkpoint.guanqia.CheckPointContants.BALL_LOCK_LV;
import static com.igame.work.checkpoint.guanqia.CheckPointContants.BALL_RANK_SHOW;

/**
 * @author xym
 *
 * 暴走时刻排行榜
 */
public class BallisticRankHandler extends ReconnectedHandler {

    private BallisticService ballisticService;

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
        int round = -1;
        int score = 0;
        int ballisticCount;
        int buff;
        List<BallisticRanker> topFifty;

        //获取挑战次数
        ballisticCount = player.getBallisticCount();

        //获取buff
        buff = ballisticService.getRank().getBuffMap().get(player.getSeverId());

        //排序并取前50
        List<BallisticRanker> rankList = ballisticService.getRankList().get(player.getSeverId());
        if (rankList == null){
            topFifty = new ArrayList<>();
        }else if (rankList.size() <= BALL_RANK_SHOW){
            rankList.sort(BallisticRanker::compareTo);
            topFifty = rankList;
        }else {
            rankList.sort(BallisticRanker::compareTo);
            topFifty = rankList.subList(0,BALL_RANK_SHOW);
        }

        //set排名与昵称
        for (int i = 0; i < topFifty.size(); i++) {
            BallisticRanker ballisticRanker = topFifty.get(i);
            ballisticRanker.setRank(i+1);
            ballisticRanker.setName(PlayerCacheService.getPlayerById(ballisticRanker.getPlayerId()).getNickname());
        }

        //获取玩家排行榜信息
        Map<Long, BallisticRanker> rankerMap = ballisticService.getRank().getRankMap().get(player.getSeverId());
        if(rankList != null) {
            if (rankerMap != null && rankerMap.get(player.getPlayerId()) != null) {    //玩家在排行榜中

                BallisticRanker ranker = rankerMap.get(player.getPlayerId());

                rank = rankList.indexOf(ranker) + 1;
                round = Math.round(rank / rankList.size() * 100);
                score = ranker.getScore();
            }
        }

        vo.addData("playerScore", score);
        vo.addData("playerRank", rank);
        vo.addData("rankPercent", round);
        vo.addData("challengeCount", ballisticCount);
        vo.addData("currentBuff", buff);
        vo.addData("ranks", topFifty);
        return vo;

    }

    @Override
    public int protocolId() {
        return MProtrol.BALLISTIC_RANKS;
    }

}
