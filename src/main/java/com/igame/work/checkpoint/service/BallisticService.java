package com.igame.work.checkpoint.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.work.checkpoint.GuanQiaDataManager;
import com.igame.work.checkpoint.data.RunTemplate;
import com.igame.work.checkpoint.data.RunTypeTemplate;
import com.igame.core.db.BallisticRankDAO;
import com.igame.core.db.DBManager;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.system.BallisticRank;
import com.igame.work.system.BallisticRanker;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.MailService;

import java.util.*;

/**
 * @author xym
 *
 * 暴走时刻
 */
public class BallisticService {

    private static final BallisticService domain = new BallisticService();

    public static final BallisticService ins() {
        return domain;
    }

    private static final int[] killNums = {0,30,50,70,110,150,200,250,300,400,500,600};

    /**
     * 更新暴走时刻排行榜
     * @param player 要更新的玩家
     * @param killNum 杀敌数
     */
    public void updateRank(Player player, int killNum) {

        //统计用时
        long time = new Date().getTime() - player.getBallisticEnter().getTime();

        //初始化排行榜
        Map<Integer, Map<Long, BallisticRanker>> rankMap = BallisticRank.ins().getRankMap();
        Map<Integer, List<BallisticRanker>> rankList = BallisticRank.ins().getRankList();
        if (rankMap.get(player.getSeverId()) == null){
            BallisticRank.ins().getRankMap().put(player.getSeverId(), Maps.newHashMap());
        }
        if (rankList.get(player.getSeverId()) == null){
            BallisticRank.ins().getRankList().put(player.getSeverId(),Lists.newArrayList());
        }

        Map<Long, BallisticRanker> rankerMap = BallisticRank.ins().getRankMap().get(player.getSeverId());
        List<BallisticRanker> rankerList = BallisticRank.ins().getRankList().get(player.getSeverId());
        BallisticRanker ranker = rankerMap.get(player.getPlayerId());
        if (ranker == null){    //新增玩家到排行榜

            ranker = new BallisticRanker();
            ranker.setPlayerId(player.getPlayerId());
            ranker.setName(player.getNickname());

            ranker.setScore(killNum);
            ranker.setTime(time);

            rankerList.add(ranker);
            rankerMap.put(player.getPlayerId(),ranker);
        }else if (killNum > ranker.getScore()){ //如果成绩好于上次,更新排行榜中的已有玩家

            ranker.setScore(killNum);
            ranker.setTime(time);

            for (BallisticRanker ballisticRanker : rankerList) {
                if (ballisticRanker.equals(ranker)){

                    ranker.setScore(killNum);
                    ranker.setTime(time);
                }
            }
        }
    }

    /**
     * 根据当前杀敌数 生成怪物
     * @param killNum 杀敌数
     * @param tocalBuildNum 生成数量
     */
    public List<MatchMonsterDto> buildMonsterByKillNum(int killNum, int tocalBuildNum){

        List<MatchMonsterDto> matchMonsterDtos = new ArrayList<>();

        for (int i = 1; i < killNums.length; i++) {

            //确定递归初始入口
            if (killNums[i-1] <= killNum && killNum < killNums[i]){

                int curBuildNum = killNums[i] - killNum;
                addMonster(matchMonsterDtos,curBuildNum,tocalBuildNum,i);
            }
        }

        return matchMonsterDtos;
    }

    /**
     * 递归生成并添加怪兽
     *      如果当前等级可生成怪物个数小于需要生成的怪物个数，则生成并添加当前等级可生成怪兽个数并生成下一等级怪兽
     * @param matchMonsterDtos 怪兽list
     * @param curBuildNum 当前等级可生成怪物个数
     * @param tocalBuildNum 剩余生成怪物个数
     * @param index killNums下标
     */
    private List<MatchMonsterDto> addMonster(List<MatchMonsterDto> matchMonsterDtos,int curBuildNum,int tocalBuildNum,int index){

        if (index >= killNums.length)
            return matchMonsterDtos;

        RunTemplate template = GuanQiaDataManager.runData.getTemplate(killNums[index]);

        if (curBuildNum >= tocalBuildNum){  //如果当前等级可以生成的怪物数量大于需要生成的怪物数量
            matchMonsterDtos.addAll(buildMonster(template, tocalBuildNum));
        }else {
            matchMonsterDtos.addAll(buildMonster(template, curBuildNum));

            if (index+1 < killNums.length)
                addMonster(matchMonsterDtos,killNums[index+1] - killNums[index],tocalBuildNum - curBuildNum, ++index);
        }
        return matchMonsterDtos;
    }

    /**
     * 根据 模板 随机生成怪物
     * @param template 暴走时刻怪兽模板
     * @param buildNum 生成数量
     */
    public List<MatchMonsterDto> buildMonster(RunTemplate template, int buildNum){

        List<MatchMonsterDto> matchMonsterDtos = new ArrayList<>();

        if (template == null)
            return matchMonsterDtos;

        int monsterLv = template.getMonsterLv();
        int skillLv = template.getSkillLv();
        String[] monsterIds = template.getMonster().split(",");
        String equip = template.getProp() == 0 ? "" :String.valueOf(template.getProp());
        for (int i = 0; i < buildNum; i++) {

            String monsterId = monsterIds[(new Random().nextInt(monsterIds.length))];

            matchMonsterDtos.add(new MatchMonsterDto(new Monster(i, Integer.parseInt(monsterId), monsterLv, -1, skillLv,equip)));
        }

        return matchMonsterDtos;
    }

    /**
     * 暴走时刻 结算奖励 && 清空排行榜 && 随机当日buff
     */
    public void zero() {

        BallisticRank ins = BallisticRank.ins();

        Map<Integer, List<BallisticRanker>> rankList = ins.getRankList();
        Map<Integer, Map<Long, BallisticRanker>> rankMap = ins.getRankMap();
        Map<Integer, Integer> buffMap = ins.getBuffMap();

        //结算奖励
        for (Map.Entry<Integer, List<BallisticRanker>> entry : rankList.entrySet()) {

            Integer serverId = entry.getKey();
            List<BallisticRanker> rankerList = entry.getValue();

            rankerList.sort(BallisticRanker::compareTo);

            for (int i = 0; i < rankerList.size(); i++) {

                int rank = i+1;
                int rankPercent = Math.round(rank/rankList.size() * 100);

                long playerId = rankerList.get(i).getPlayerId();

                String reward = "";
                if (rank == 1){
                    reward = GuanQiaDataManager.runRewardData.getTemplate(1).getReward();
                }else if (rank == 2){
                    reward = GuanQiaDataManager.runRewardData.getTemplate(2).getReward();
                }else if (rank == 3){
                    reward = GuanQiaDataManager.runRewardData.getTemplate(3).getReward();
                }else if (4 <= rank && rank <= 10){
                    reward = GuanQiaDataManager.runRewardData.getTemplate(4).getReward();
                }else if (10 < rank && rankPercent <= 15){
                    reward = GuanQiaDataManager.runRewardData.getTemplate(5).getReward();
                }else if (15 < rankPercent && rankPercent <= 25){
                    reward = GuanQiaDataManager.runRewardData.getTemplate(6).getReward();
                }else if (15 < rankPercent && rankPercent <= 25){
                    reward = GuanQiaDataManager.runRewardData.getTemplate(7).getReward();
                }else if (25 < rankPercent && rankPercent <= 35){
                    reward = GuanQiaDataManager.runRewardData.getTemplate(8).getReward();
                }else if (35 < rankPercent && rankPercent <= 50){
                    reward = GuanQiaDataManager.runRewardData.getTemplate(9).getReward();
                }else if (50 < rankPercent && rankPercent <= 70){
                    reward = GuanQiaDataManager.runRewardData.getTemplate(10).getReward();
                }else if (70 < rankPercent && rankPercent <= 90){
                    reward = GuanQiaDataManager.runRewardData.getTemplate(11).getReward();
                }else if (90 < rankPercent && rankPercent <= 100){
                    reward = GuanQiaDataManager.runRewardData.getTemplate(12).getReward();
                }

                MailService.ins().senderMail(serverId, playerId, 1, 1, "系统", "标题", "正文", reward);
            }
        }


        //随机当日buff
        List<RunTypeTemplate> all = GuanQiaDataManager.runTypeData.getAll();
        for (Map.Entry<Integer, Integer> entry : buffMap.entrySet()) {

            int newValue;
            while (true){
                newValue = new Random().nextInt(all.size()+1);
                if (newValue != entry.getValue())
                    break;
            }

            buffMap.put(entry.getKey(),newValue);
        }

        //清空排行榜
        rankList.clear();
        rankMap.clear();

    }

    /**
     * 加载暴走时刻排行榜
     */
    public void loadData() {

        Map<Integer, Map<Long, BallisticRanker>> rankMap = Maps.newHashMap();
        Map<Integer,List<BallisticRanker>> rankList = Maps.newHashMap();
        Map<Integer, Integer> buffMap = Maps.newHashMap();

        BallisticRank ballisticRank = BallisticRankDAO.ins().get();
        if (ballisticRank == null){ //创建排行榜

            //随机buff
            String DBName = DBManager.getInstance().p.getProperty("DBName");
            String[] DBNames = DBName.split(",");
            for(String db : DBNames){
                int serverId=Integer.parseInt(db.substring(5));
                List<RunTypeTemplate> all = GuanQiaDataManager.runTypeData.getAll();

                buffMap.put(serverId,new Random().nextInt(all.size()+1));
            }

            ballisticRank = new BallisticRank();
            ballisticRank.setBuffMap(buffMap);
            BallisticRankDAO.ins().save(ballisticRank);
        }else { //初始化rankList

            if (ballisticRank.getBuffMap() != null)
                buffMap = ballisticRank.getBuffMap();
            if (ballisticRank.getRankMap() != null)
                rankMap = ballisticRank.getRankMap();

            for (Map.Entry<Integer, Map<Long, BallisticRanker>> entry : rankMap.entrySet()) {

                List<BallisticRanker> rankerList = new ArrayList<>();
                rankerList.addAll(entry.getValue().values());

                rankList.put(entry.getKey(),rankerList);
            }
        }

        BallisticRank ins = BallisticRank.ins();

        ins.set_id(ballisticRank.get_id());
        ins.setRankMap(rankMap);
        ins.setBuffMap(buffMap);
        ins.setRankList(rankList);
    }

    /**
     * 保存暴走时刻拍排行榜
     */
    public void saveData() {
        BallisticRankDAO.ins().update(BallisticRank.ins());
    }
}
