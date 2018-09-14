package com.igame.work.checkpoint.baozouShike.handler;

import com.google.common.collect.Lists;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.PlayerEvents;
import com.igame.work.checkpoint.baozouShike.BaozouShikeDataManager;
import com.igame.work.checkpoint.baozouShike.data.RunBattlerewardData;
import com.igame.work.checkpoint.baozouShike.data.RunBattlerewardTemplate;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.igame.work.checkpoint.baozouShike.BallisticConstant.BALL_PHYSICAL;
import static com.igame.work.checkpoint.baozouShike.BallisticConstant.BALL_REWARD_EXP;

/**
 * @author xym monster
 *
 * 暴走时刻结束战斗
 */
public class BallisticEndHandler extends ReconnectedHandler {

    private ResourceService resourceService;
    
    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int killNum = jsonObject.getInt("killNum");

        vo.addData("killNum",killNum);

        //校验杀敌数
        if (player.getBallisticMonsters() < killNum){
            return error(ErrorCode.CHECKPOINT_END_ERROR);
        }

        //校验上次进入的关卡ID
        /*if (player.getLastCheckpointId() != CHECKPOINT_BALL_ID){
            return error(ErrorCode.CHECKPOINT_END_ERROR);
        }*/

        //校验暴走时刻挑战时间
        if (player.getBallisticEnter() == null){
            return error(ErrorCode.CHECKPOINT_END_ERROR);
        }

        //更新暴走时刻排行榜
        fireEvent(player, PlayerEvents.UPDATE_BALLISTIC_RANK, killNum);

        //结算并推送奖励
        String reward = settlementReward(player,killNum);

        //结算人物经验
        resourceService.addExp(player,BALL_REWARD_EXP);
        //结算并推送怪兽经验
        String monsterExpStr = settlementExp(player);

        //扣除体力
        resourceService.addPhysica(player,BALL_PHYSICAL*player.getBallisticCount());

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
        vo.addData("checkReward",reward);

        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.BALLISTIC_END;
    }

    private String settlementExp(Player player) {

        //怪兽
        StringBuilder monsterExpStr = new StringBuilder();

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
                    monsterExpStr.append(mid);
                    if(resourceService.addMonsterExp(player, mid, BALL_REWARD_EXP, false) == 0){
                        ll.add(mm);
                        monsterExpStr.append("," + BALL_REWARD_EXP + ";");
                    }else{
                        monsterExpStr.append(",0;");
                    }
                }
            }
        }

        if(monsterExpStr.lastIndexOf(";") >0){
            monsterExpStr = new StringBuilder(monsterExpStr.substring(0, monsterExpStr.lastIndexOf(";")));
        }

        //推送怪兽更新
        MessageUtil.notifyMonsterChange(player, ll);

        return monsterExpStr.toString();
    }

    private String settlementReward(Player player, int killNum) {

        int gold = 0;

        RunBattlerewardData runBattlerewardData = BaozouShikeDataManager.runBattlerewardData;
        List<RunBattlerewardTemplate> runBattlerewardDataAll = runBattlerewardData.getAll();
        int i = runBattlerewardDataAll.indexOf(runBattlerewardData.getTemplate(killNum));
        for (int j = 0; j <= i; j++) {
            gold += runBattlerewardDataAll.get(j).getGold();
        }

        resourceService.addGold(player,gold);

        return "1,1,"+gold;
    }
}
