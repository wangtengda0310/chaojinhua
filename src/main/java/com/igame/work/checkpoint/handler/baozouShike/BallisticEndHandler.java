package com.igame.work.checkpoint.handler.baozouShike;

import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.GuanQiaDataManager;
import com.igame.work.checkpoint.data.RunBattlerewardData;
import com.igame.work.checkpoint.data.RunBattlerewardTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.service.BallisticService;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.igame.work.checkpoint.CheckPointContants.BALL_PHYSICAL;
import static com.igame.work.checkpoint.CheckPointContants.BALL_REWARD_EXP;

/**
 * @author xym monster
 *
 * 暴走时刻结束战斗
 */
public class BallisticEndHandler extends BaseHandler{

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if (player == null) {
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }

        int killNum = jsonObject.getInt("killNum");

        vo.addData("killNum",killNum);

        //校验杀敌数
        if (player.getBallisticMonsters() < killNum){
            sendError(ErrorCode.CHECKPOINT_END_ERROR, MProtrol.toStringProtrol(MProtrol.BALLISTIC_END), vo, user);
            return;
        }

        //校验上次进入的关卡ID
        /*if (player.getLastCheckpointId() != CHECKPOINT_BALL_ID){
            sendError(ErrorCode.CHECKPOINT_END_ERROR, MProtrol.toStringProtrol(MProtrol.BALLISTIC_END), vo, user);
            return;
        }*/

        //校验暴走时刻挑战时间
        if (player.getBallisticEnter() == null){
            sendError(ErrorCode.CHECKPOINT_END_ERROR, MProtrol.toStringProtrol(MProtrol.BALLISTIC_END), vo, user);
            return;
        }

        //更新暴走时刻排行榜
        BallisticService.ins().updateRank(player, killNum);

        //结算并推送奖励
        String reward = settlementReward(player,killNum);

        //结算人物经验
        ResourceService.ins().addExp(player,BALL_REWARD_EXP);
        //结算并推送怪兽经验
        String monsterExpStr = settlementExp(player);

        //扣除体力
        ResourceService.ins().addPhysica(player,BALL_PHYSICAL*player.getBallisticCount());

        //增加挑战次数
        player.addBallisticCount(1);

        //重置开始时间
        player.setBallisticEnter(null);
        //重置怪兽刷新数量
        player.setBallisticMonsters(0);
        //重置援助怪兽
        player.setBallisticAid("");

        //返回
        vo.addData("playerExp", BALL_REWARD_EXP);
        vo.addData("monsterExp", monsterExpStr);
        vo.addData("reward",reward);

        sendSucceed(MProtrol.toStringProtrol(MProtrol.BALLISTIC_END), vo, user);
    }

    private String settlementExp(Player player) {

        //怪兽
        String monsterExpStr = "";

        List<Monster> ll = Lists.newArrayList();
        List<Long> allMonsters = new ArrayList<>();

        //上阵怪兽
        long[] monsters = player.getTeams().get(player.getCurTeam()).getTeamMonster();
        for (long mid : monsters) {
            allMonsters.add(mid);
        }

        //援助怪兽
        String aidMonsters = player.getBallisticAid();
        if (!aidMonsters.isEmpty()){
            String[] aidMonstersArr = aidMonsters.split(",");
            for (String mid : aidMonstersArr) {
                allMonsters.add(Long.parseLong(mid));
            }
        }

        for (Long mid : allMonsters) {
            if(-1 != mid){
                Monster mm = player.getMonsters().get(mid);
                if(mm != null){
                    monsterExpStr += mid;
                    if(ResourceService.ins().addMonsterExp(player, mid, BALL_REWARD_EXP, false) == 0){
                        ll.add(mm);
                        monsterExpStr += ("," + BALL_REWARD_EXP +";");
                    }else{
                        monsterExpStr += ",0;";
                    }
                }
            }
        }

        if(monsterExpStr.lastIndexOf(";") >0){
            monsterExpStr = monsterExpStr.substring(0,monsterExpStr.lastIndexOf(";"));
        }

        //推送怪兽更新
        MessageUtil.notiyMonsterChange(player, ll);

        return monsterExpStr;
    }

    private String settlementReward(Player player, int killNum) {

        int gold = 0;

        RunBattlerewardData runBattlerewardData = GuanQiaDataManager.runBattlerewardData;
        List<RunBattlerewardTemplate> runBattlerewardDataAll = runBattlerewardData.getAll();
        int i = runBattlerewardDataAll.indexOf(runBattlerewardData.getTemplate(killNum));
        for (int j = 0; j <= i; j++) {
            gold += runBattlerewardDataAll.get(j).getGold();
        }

        ResourceService.ins().addGold(player,gold);

        return "1,1,"+gold;
    }
}
