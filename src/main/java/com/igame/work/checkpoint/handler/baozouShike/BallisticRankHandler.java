package com.igame.work.checkpoint.handler.baozouShike;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.system.BallisticRank;
import com.igame.work.system.BallisticRanker;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.igame.work.checkpoint.CheckPointContants.BALL_LOCK_LV;
import static com.igame.work.checkpoint.CheckPointContants.BALL_RANK_SHOW;

/**
 * @author xym
 *
 * 暴走时刻排行榜
 */
public class BallisticRankHandler extends BaseHandler{

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

        //校验等级
        int playerLevel = player.getPlayerLevel();
        if (playerLevel < BALL_LOCK_LV){
            sendError(ErrorCode.BALLISTIC_LOCK, MProtrol.toStringProtrol(MProtrol.BALLISTIC_RANKS), vo, user);
            return;
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
        buff = BallisticRank.ins().getBuffMap().get(player.getSeverId());

        //排序并取前50
        List<BallisticRanker> rankList = BallisticRank.ins().getRankList().get(player.getSeverId());
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
            ballisticRanker.setName(PlayerCacheService.ins().getPlayerById(ballisticRanker.getPlayerId()).getNickname());
        }

        //获取玩家排行榜信息
        Map<Long, BallisticRanker> rankerMap = BallisticRank.ins().getRankMap().get(player.getSeverId());
        if (rankerMap != null && rankerMap.get(player.getPlayerId()) != null){    //玩家在排行榜中

            BallisticRanker ranker = rankerMap.get(player.getPlayerId());

            rank = rankList.indexOf(ranker) + 1;
            round = Math.round(rank/rankList.size() * 100);
            score = ranker.getScore();
        }

        vo.addData("playerScore", score);
        vo.addData("playerRank", rank);
        vo.addData("rankPercent", round);
        vo.addData("challengeCount", ballisticCount);
        vo.addData("currentBuff", buff);
        vo.addData("ranks", topFifty);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.BALLISTIC_RANKS), vo, user);

    }

    private void initRank(){
        List<BallisticRanker> rankers = new ArrayList<>();

        Map<Integer, Map<Long, BallisticRanker>> rankMap = BallisticRank.ins().getRankMap();
        rankMap.put(1,new HashedMap());
        for (int i = 1; i <= 100; i++) {
            BallisticRanker ranker = new BallisticRanker();
            ranker.setPlayerId(i);
            ranker.setName("玩家_"+i);
            ranker.setTime(i);
            ranker.setScore(100-i);

            rankers.add(ranker);
            rankMap.get(1).put(new Long(i),ranker);
        }

        BallisticRank.ins().getRankList().put(1,rankers);
    }
}
