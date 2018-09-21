package com.igame.work.checkpoint.baozouShike;

import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.di.LoadXml;
import com.igame.core.event.EventService;
import com.igame.core.event.EventType;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.quartz.TimeListener;
import com.igame.work.PlayerEvents;
import com.igame.work.checkpoint.baozouShike.data.*;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.mail.MailService;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xym
 * <p>
 * 暴走时刻
 */
public class BallisticService extends EventService implements ISFSModule, TimeListener {
    /**
     * 暴走时刻怪物数据
     */
    @LoadXml("rundata.xml")public RunData runData;
    /**
     * 暴走时刻排名奖励
     */
    @LoadXml("runreward.xml")public RunRewardData runRewardData;
    /**
     * 暴走时刻buff数据
     */
    @LoadXml("runtype.xml")public RunTypeData runTypeData;
    /**
     * 暴走时刻结束奖励
     */
    @LoadXml("runbattlereward.xml")public RunBattlerewardData runBattlerewardData;

    @Inject private BallisticRankDAO dao;
    @Inject private MailService mailService;
    @Inject private static MonsterService monsterService;

    @Override
    public void minute5() {
        saveData();
    }

    @Override
    public void zero() {
        //暴走时刻 结算奖励 && 清空排行榜 && 随机当日buff
        zeroJob();
    }

    private static final int[] killNums = {0, 30, 50, 70, 110, 150, 200, 250, 300, 400, 500, 600};

    @Override
    protected PlayerEventObserver playerObserver() {
        return new PlayerEventObserver() {
            @Override
            public EventType interestedType() {
                return PlayerEvents.UPDATE_BALLISTIC_RANK;
            }

            @Override
            public void observe(Player eventOwner, EventType eventType, Object event) {
                updateRank(eventOwner, (int) event);
            }
        };
    }

    /**
     * 更新暴走时刻排行榜
     *
     * @param player  要更新的玩家
     * @param killNum 杀敌数
     */
    private void updateRank(Player player, int killNum) {

        //统计用时
        long timeCost = new Date().getTime() - getBallisticEnter(player).getTime();    // todo 这里改成了时间出发 结束时间是不是需要传过来 不要用new的

        //初始化排行榜
        Map<Long, BallisticRanker> rankMap = rankDto.getRank();

        if (rankMap == null) {
            rankDto.setRank(new HashMap<>());
        }

        BallisticRanker self = rankDto.getRank().computeIfAbsent(player.getPlayerId(), playerId -> {

            BallisticRanker rank = new BallisticRanker();
            rank.setPlayerId(player.getPlayerId());

            return rank;
        });
        self.setScore(killNum);
        self.setTime(timeCost);

    }

    /**
     * 根据当前杀敌数 生成怪物
     *
     * @param killNum       杀敌数
     * @param tocalBuildNum 生成数量
     */
    public List<MatchMonsterDto> buildMonsterByKillNum(int killNum, int tocalBuildNum) {

        List<MatchMonsterDto> matchMonsterDtos = new ArrayList<>();

        for (int i = 1; i < killNums.length; i++) {

            //确定递归初始入口
            if (killNums[i - 1] <= killNum && killNum < killNums[i]) {

                int curBuildNum = killNums[i] - killNum;
                addMonster(matchMonsterDtos, curBuildNum, tocalBuildNum, i);
            }
        }

        return matchMonsterDtos;
    }

    /**
     * 递归生成并添加怪兽
     * 如果当前等级可生成怪物个数小于需要生成的怪物个数，则生成并添加当前等级可生成怪兽个数并生成下一等级怪兽
     *
     * @param matchMonsterDtos 怪兽list
     * @param curBuildNum      当前等级可生成怪物个数
     * @param tocalBuildNum    剩余生成怪物个数
     * @param index            killNums下标
     */
    private List<MatchMonsterDto> addMonster(List<MatchMonsterDto> matchMonsterDtos, int curBuildNum, int tocalBuildNum, int index) {

        if (index >= killNums.length)
            return matchMonsterDtos;

        RunTemplate template = runData.getTemplate(killNums[index]);

        if (curBuildNum >= tocalBuildNum) {  //如果当前等级可以生成的怪物数量大于需要生成的怪物数量
            matchMonsterDtos.addAll(buildMonster(template, tocalBuildNum));
        } else {
            matchMonsterDtos.addAll(buildMonster(template, curBuildNum));

            if (index + 1 < killNums.length)
                addMonster(matchMonsterDtos, killNums[index + 1] - killNums[index], tocalBuildNum - curBuildNum, ++index);
        }
        return matchMonsterDtos;
    }

    /**
     * 根据 模板 随机生成怪物
     *
     * @param template 暴走时刻怪兽模板
     * @param buildNum 生成数量
     */
    public List<MatchMonsterDto> buildMonster(RunTemplate template, int buildNum) {

        List<MatchMonsterDto> matchMonsterDtos = new ArrayList<>();

        if (template == null)
            return matchMonsterDtos;

        int monsterLv = template.getMonsterLv();
        int skillLv = template.getSkillLv();
        String[] monsterIds = template.getMonster().split(",");
        String equip = template.getProp() == 0 ? "" : String.valueOf(template.getProp());
        for (int i = 0; i < buildNum; i++) {

            String monsterId = monsterIds[(new Random().nextInt(monsterIds.length))];

            Monster monster = new Monster(i, Integer.parseInt(monsterId), monsterLv, -1, equip);
            MonsterTemplate mt = monsterService.MONSTER_DATA.getMonsterTemplate(Integer.parseInt(monsterId));
            if(mt != null && mt.getSkill() != null){
                String[] skills = mt.getSkill().split(",");
                for(String skill : skills){
                    if(skillLv<=0){
                        monster.skillMap.put(Integer.parseInt(skill), 1);
                    }else{
                        monster.skillMap.put(Integer.parseInt(skill), skillLv);
                    }

                    monster.skillExp.put(Integer.parseInt(skill), 0);
                }
                monster.atkType = mt.getAtk_type();
            }
            monsterService.initSkillString(monster);
            monsterService.reCalLevelValue(Integer.parseInt(monsterId),monster,true);
            monsterService.reCalEquip(Integer.parseInt(monsterId),monster);
            matchMonsterDtos.add(new MatchMonsterDto(monster,-1));
        }

        return matchMonsterDtos;
    }

    /**
     * 暴走时刻 结算奖励 && 清空排行榜 && 随机当日buff
     */
    private void zeroJob() {

        List<BallisticRanker> rankList = new ArrayList<>(rankDto.rank.values());

        rankList.sort(BallisticRanker::compareTo);
        int rank = 0;
        for (BallisticRanker ranker : rankList) {
            rank += 1;

            int percent = rank / rankList.size() * 100;
            int rankPercent = Math.round(percent);

            String reward = "";
            if (rank == 1) {
                reward = runRewardData.getTemplate(1).getReward();
            } else if (rank == 2) {
                reward = runRewardData.getTemplate(2).getReward();
            } else if (rank == 3) {
                reward = runRewardData.getTemplate(3).getReward();
            } else if (4 <= rank && rank <= 10) {
                reward = runRewardData.getTemplate(4).getReward();
            } else if (10 < rank && rankPercent <= 15) {
                reward = runRewardData.getTemplate(5).getReward();
            } else if (15 < rankPercent && rankPercent <= 25) {
                reward = runRewardData.getTemplate(6).getReward();
            } else if (15 < rankPercent && rankPercent <= 25) {
                reward = runRewardData.getTemplate(7).getReward();
            } else if (25 < rankPercent && rankPercent <= 35) {
                reward = runRewardData.getTemplate(8).getReward();
            } else if (35 < rankPercent && rankPercent <= 50) {
                reward = runRewardData.getTemplate(9).getReward();
            } else if (50 < rankPercent && rankPercent <= 70) {
                reward = runRewardData.getTemplate(10).getReward();
            } else if (70 < rankPercent && rankPercent <= 90) {
                reward = runRewardData.getTemplate(11).getReward();
            } else if (90 < rankPercent && rankPercent <= 100) {
                reward = runRewardData.getTemplate(12).getReward();
            }

            mailService.senderMail(Integer.parseInt(extensionHolder.SFSExtension.getConfigProperties().getProperty("serverId"))
                    , ranker.getPlayerId(), 1, 1, "系统", "标题", "正文", reward);
        }

        //随机当日buff
        List<RunTypeTemplate> all = runTypeData.getAll();
        int buffMap = this.rankDto.getBuff();

        int newValue;
        do {
            newValue = new Random().nextInt(all.size() + 1);
        } while (newValue == buffMap);

        this.rankDto.setBuff(newValue);


        //清空排行榜
        Map<Long, BallisticRanker> rankMap = this.rankDto.getRank();
        rankMap.clear();

    }

    /**
     * 加载暴走时刻排行榜
     */
    @Override
    public void init() {

        rankDto = dao.get();
        if (rankDto == null) { //创建排行榜

            //随机buff

            List<RunTypeTemplate> config = runTypeData.getAll();

            int buffMap = new Random().nextInt(config.size() + 1);

            rankDto = new BallisticRankDto();
            rankDto.setBuff(buffMap);
            rankDto.setRank(new HashMap<>());
            dao.save(rankDto);

        }
    }

    private BallisticRankDto rankDto;

    public BallisticRankDto getRankDto() {
        return rankDto;
    }

    /**
     * 保存暴走时刻排行榜
     */
    private void saveData() {
        dao.update(rankDto);
    }

    private Map<Long, Date> ballisticEnter = new ConcurrentHashMap<>();    //记录暴走时刻开始挑战时间
    private Map<Long, AtomicInteger> ballisticMonsters = new ConcurrentHashMap<>();    //记录暴走时刻刷新怪兽数量
    private Map<Long, String> ballisticAid = new ConcurrentHashMap<>();    //记录暴走时刻援助怪兽

    public void addBallisticMonsters(Player player, int value) {
        ballisticMonsters.computeIfAbsent(player.getPlayerId(), pid -> new AtomicInteger(0)).addAndGet(value);
    }

    public int getBallisticMonsters(Player player) {
        return ballisticMonsters.computeIfAbsent(player.getPlayerId(), pid -> new AtomicInteger(0)).get();
    }

    public void setBallisticMonsters(Player player, int ballMonsterInit) {
        ballisticMonsters.computeIfAbsent(player.getPlayerId(), pid -> new AtomicInteger(0)).set(ballMonsterInit);
    }

    public void setBallisticEnter(Player player, Date date) {
        ballisticEnter.put(player.getPlayerId(), date);
    }

    public Date getBallisticEnter(Player player) {
        return ballisticEnter.get(player.getPlayerId());
    }

    public void setBallisticAid(Player player, String aidMonsters) {
        ballisticAid.put(player.getPlayerId(), aidMonsters);
    }

    public String getBallisticAid(Player player) {
        return ballisticAid.get(player.getPlayerId());
    }
}
