package com.igame.work.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.mingyunZhiMen.FateSelfData;
import com.igame.work.checkpoint.tansuo.TansuoDto;
import com.igame.work.checkpoint.worldEvent.WorldEventDto;
import com.igame.work.checkpoint.xinmo.XingMoDto;
import com.igame.work.draw.DrawData;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.GodsDto;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.dto.Gods;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.dto.WuEffect;
import com.igame.work.sign.SignData;
import com.smartfoxserver.v2.entities.User;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Marcus.Z
 */
@Entity(value = "players", noClassnameStored = true)
public class Player extends PlayerDto {



    /**
     *  -----------------------       po字段   -----------------------------------------------
     */



    @JsonIgnore
    private Date loginTime;//登录时间

    @JsonIgnore
    private Date loginoutTime;//离线时间

    @JsonIgnore
    private Map<Integer, Integer> timeResCheck = Maps.newHashMap();//定时产资源的关卡 关卡ID-分钟数

    @JsonIgnore
    private Map<Integer, XingMoDto> xinMo = Maps.newHashMap();//定时产资源的关卡 关卡ID-心魔

    @JsonIgnore
    private int xinMoMinuts;//心魔分钟数

    @JsonIgnore
    private TongHuaDto tonghua;//当前正在同化的对象

    @JsonIgnore
    private Map<Integer, TansuoDto> tangSuo = Maps.newHashMap();//探索

    @JsonIgnore
    private TongAddDto tongAdd = new TongAddDto();//同化的属性加成

    @JsonIgnore
    private Map<Integer, Integer> resMintues = Maps.newConcurrentMap();//计算分钟数

    @JsonIgnore
    private Set<Integer> meetM = Sets.newHashSet();

    @JsonIgnore
    private DrawData draw = new DrawData();//造物台对象//4 击杀心魔次数
    //6 提升怪物技能次数
    //7 怪物突破/升级次数
    //8 怪物净化次数
    //9 合成文章次数
    //16 收集怪物数
    //17 击败BOSS关卡次数
    @JsonIgnore
    private Map<Integer, Integer> countMap = Maps.newHashMap();//各种操作类型历史计数

//	@JsonIgnore
//	private Map<Long,Integer> wuHpMap = Maps.newHashMap();//无尽之森保存的怪物HP

    @JsonIgnore
    private Map<Integer, String> wuMap = Maps.newHashMap();//无尽之森保存的关卡情况 关卡ID,是否已过,怪物ID;怪物ID,怪物等级;怪物等级

    @JsonIgnore
    private Map<Long, MatchMonsterDto> wuZheng = Maps.newHashMap();//无尽之森当前阵容

    @JsonIgnore
    private GodsDto wuGods = new GodsDto();//无尽之森当前神灵

    @JsonIgnore
    private List<WuEffect> wuEffect = Lists.newArrayList();

    @JsonIgnore
    private int wuScore;//商店积分

    @JsonIgnore
    private FateSelfData fateData = new FateSelfData();

    @JsonIgnore
    private int doujiScore;    //斗技商店积分

    @JsonIgnore
    private int qiyuanScore;    //起源商店积分

    @JsonIgnore
    private int buluoScore;    //部落商店积分

    @JsonIgnore
    private int yuanzhengScore;    //远征商店积分

    @JsonIgnore
    private int xuanshangScore;    //悬赏商店积分

    @JsonIgnore
    private int ballisticCount;   //暴走时刻当日挑战次数

    @JsonIgnore
    private Map<String,Date> lastMessageBoard = Maps.newHashMap();    //留言板留言时间

    @JsonIgnore
    private Map<Integer,Team> teams = Maps.newHashMap();    //阵容 <阵容ID,阵容>

    @JsonIgnore
    private Map<String,Object> vipPrivileges = Maps.newLinkedHashMap();    //vip权限 <key,对应的值>

    @JsonIgnore
    private PlayerCount playerCount = new PlayerCount();    //角色剩余挑战次数

    @JsonIgnore
    private PlayerTop playerTop;    //角色挑战次数上限

    @JsonIgnore
    private String lastNickname="";//修改前的昵称

    @JsonIgnore
    private SignData sign;//签到数据





    /**
     *  -----------------------       业务字段   -----------------------------------------------
     */

    @Transient
    private int modifiedName;



    @Transient
    @JsonIgnore
    private User user;

    @Transient
    @JsonIgnore
    private Map<Long, Monster> monsters = Maps.newHashMap();//怪物

    @Transient
    @JsonIgnore
    private Map<Integer, Item> items = Maps.newHashMap();//道具

    @Transient
    @JsonIgnore
    private List<Item> removes = Lists.newArrayList();//删除的道具

    @Transient
    @JsonIgnore
    private Map<Integer, WorldEventDto> wordEvent = Maps.newHashMap();//世界事件

    @Transient
    @JsonIgnore
    private Map<Integer, Gods> gods = Maps.newHashMap();//神灵

    @Transient
    @JsonIgnore
    private FightBase fightBase;

//	@Transient
//	@JsonIgnore
//	private Map<Integer,TaskDayInfo> dayTask = Maps.newHashMap();//每日任务

    @Transient
    @JsonIgnore
    private Map<Integer,MessageCache> proTuiMap = new ConcurrentHashMap<>();//推送消息缓存

    @Transient
    @JsonIgnore
    private Object proPushLock = new Object();//推送消息同步锁

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<Long, Monster> getMonsters() {
        return monsters;
    }

    public void setMonsters(Map<Long, Monster> monsters) {
        this.monsters = monsters;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLoginoutTime() {
        return loginoutTime == null? new Date() : loginoutTime;
    }

    public void setLoginoutTime(Date loginoutTime) {
        this.loginoutTime = loginoutTime;
    }

    public Map<Integer, Item> getItems() {
        return items;
    }

    public void setItems(Map<Integer, Item> items) {
        this.items = items;
    }

    public List<Item> getRemoves() {
        return removes;
    }

    public void setRemoves(List<Item> removes) {
        this.removes = removes;
    }

    public Map<Integer, Integer> getTimeResCheck() {
        return timeResCheck;
    }

    public void setTimeResCheck(Map<Integer, Integer> timeResCheck) {
        this.timeResCheck = timeResCheck;
    }

    public Map<Integer, XingMoDto> getXinMo() {
        return xinMo;
    }

    public void setXinMo(Map<Integer, XingMoDto> xinMo) {
        this.xinMo = xinMo;
    }

    public int getXinMoMinuts() {
        return xinMoMinuts;
    }

    public void setXinMoMinuts(int xinMoMinuts) {
        this.xinMoMinuts = xinMoMinuts;
    }

    public Map<Integer, TansuoDto> getTangSuo() {
        return tangSuo;
    }

    public void setTangSuo(Map<Integer, TansuoDto> tangSuo) {
        this.tangSuo = tangSuo;
    }

    public Map<Integer, WorldEventDto> getWordEvent() {
        return wordEvent;
    }

    public void setWordEvent(Map<Integer, WorldEventDto> wordEvent) {
        this.wordEvent = wordEvent;
    }

    public TongHuaDto getTonghua() {
        return tonghua;
    }

    public void setTonghua(TongHuaDto tonghua) {
        this.tonghua = tonghua;
    }

    public TongAddDto getTongAdd() {
        return tongAdd;
    }

    public void setTongAdd(TongAddDto tongAdd) {
        this.tongAdd = tongAdd;
    }

    public Map<Integer, Integer> getResMintues() {
        return resMintues;
    }

    public void setResMintues(Map<Integer, Integer> resMintues) {
        this.resMintues = resMintues;
    }

    public Map<Integer, Gods> getGods() {
        return gods;
    }

    public void setGods(Map<Integer, Gods> gods) {
        this.gods = gods;
    }

    public Set<Integer> getMeetM() {
        return meetM;
    }

    public void setMeetM(Set<Integer> meetM) {
        this.meetM = meetM;
    }

    public DrawData getDraw() {
        return draw;
    }

    public void setDraw(DrawData draw) {
        this.draw = draw;
    }

    public FightBase getFightBase() {
        return fightBase;
    }

    public void setFightBase(FightBase fightBase) {
        this.fightBase = fightBase;
    }

    public Map<Integer, Integer> getCountMap() {
        return countMap;
    }

    public void setCountMap(Map<Integer, Integer> countMap) {
        this.countMap = countMap;
    }

    public Map<Integer, String> getWuMap() {
        return wuMap;
    }

    public void setWuMap(Map<Integer, String> wuMap) {
        this.wuMap = wuMap;
    }

    public Map<Long, MatchMonsterDto> getWuZheng() {
        return wuZheng;
    }

    public void setWuZheng(Map<Long, MatchMonsterDto> wuZheng) {
        this.wuZheng = wuZheng;
    }

    public GodsDto getWuGods() {
        return wuGods;
    }

    public void setWuGods(GodsDto wuGods) {
        this.wuGods = wuGods;
    }

    public List<WuEffect> getWuEffect() {
        return wuEffect;
    }

    public void setWuEffect(List<WuEffect> wuEffect) {
        this.wuEffect = wuEffect;
    }

    public int getWuScore() {
        return wuScore;
    }

    public void setWuScore(int wuScore) {
        this.wuScore = wuScore;
    }

    public FateSelfData getFateData() {
        return fateData;
    }

    public void setFateData(FateSelfData fateData) {
        this.fateData = fateData;
    }

    public int getDoujiScore() {
        return doujiScore;
    }

    public void setDoujiScore(int doujiScore) {
        this.doujiScore = doujiScore;
    }

    public int getQiyuanScore() {
        return qiyuanScore;
    }

    public void setQiyuanScore(int qiyuanScore) {
        this.qiyuanScore = qiyuanScore;
    }

    public int getBuluoScore() {
        return buluoScore;
    }

    public void setBuluoScore(int buluoScore) {
        this.buluoScore = buluoScore;
    }

    public int getYuanzhengScore() {
        return yuanzhengScore;
    }

    public void setYuanzhengScore(int yuanzhengScore) {
        this.yuanzhengScore = yuanzhengScore;
    }

    public int getXuanshangScore() {
        return xuanshangScore;
    }

    public void setXuanshangScore(int xuanshangScore) {
        this.xuanshangScore = xuanshangScore;
    }

    public int getBallisticCount() {
        return ballisticCount;
    }

    public void setBallisticCount(int ballisticCount) {
        this.ballisticCount = ballisticCount;
    }

    public Map<String, Date> getLastMessageBoard() {
        return lastMessageBoard;
    }

    public void setLastMessageBoard(Map<String, Date> lastMessageBoard) {
        this.lastMessageBoard = lastMessageBoard;
    }

    public Map<Integer, Team> getTeams() {
        return teams;
    }

    public void setTeams(Map<Integer, Team> teams) {
        this.teams = teams;
    }

    public Map<Integer, MessageCache> getProTuiMap() {
        return proTuiMap;
    }

    public void setProTuiMap(Map<Integer, MessageCache> proTuiMap) {
        this.proTuiMap = proTuiMap;
    }

    public Object getProPushLock() {
        return proPushLock;
    }

    public void setProPushLock(Object proPushLock) {
        this.proPushLock = proPushLock;
    }

    public Map<String, Object> getVipPrivileges() {
        return vipPrivileges;
    }

    public void setVipPrivileges(Map<String, Object> vipPrivileges) {
        this.vipPrivileges = vipPrivileges;
    }

    public PlayerCount getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(PlayerCount playerCount) {
        this.playerCount = playerCount;
    }

    public PlayerTop getPlayerTop() {
        return playerTop;
    }

    public void setPlayerTop(PlayerTop playerTop) {
        this.playerTop = playerTop;
    }

    public String getLastNickname() {
        return lastNickname;
    }

    public void setLastNickname(String lastNickname) {
        this.lastNickname = lastNickname;
    }

    public int getModifiedName() {
        return modifiedName;
    }

    public void setModifiedName(int modifiedName) {
        this.modifiedName = modifiedName;
    }

    public SignData getSign() {
        return sign;
    }

    public void setSign(SignData sign) {
        this.sign = sign;
    }

    /**
     *  -----------------------       自定义方法   -----------------------------------------------
     */



    public void addCountMap(int type, int count) {
        Integer now = countMap.get(type);
        if (now == null) {
            now = 0;
        } else {
            now += count;
        }
        countMap.put(type, now);
    }

    public int getCountdByType(int type) {
        Integer now = countMap.get(type);
        return now == null ? 0 : now;
    }

    public void calLeftTime() {
        long now = System.currentTimeMillis();
        for (XingMoDto xin : xinMo.values()) {
            xin.calLeftTime(now);
        }
    }

    public List<Monster> reCalMonsterValue() {
        List<Monster> ll = Lists.newArrayList();
        this.fightValue = 0;
        /*String[] mms = this.teams[0].split(",");
        for (String mid : mms) {
            if (!"-1".equals(mid)) {
                Monster mm = this.monsters.get(Long.parseLong(mid));
                if (mm != null) {
                    mm.reCalculate(this, true);
                    this.fightValue += mm.getFightValue();
                    ll.add(mm);
                }
            }
        }*/
        long[] teamMonster = this.teams.get(this.curTeam).getTeamMonster();
        for (long mid : teamMonster) {
            if (-1 != mid) {
                Monster mm = this.monsters.get(mid);
                if (mm != null) {
                    mm.reCalculate(this, true);
                    this.fightValue += mm.getFightValue();
                    ll.add(mm);
                }
            }
        }
        return ll;
    }

    public synchronized void addDoujiScore(int value) {
        this.doujiScore += value;
    }

    public synchronized void addQiyuanScore(int value) {
        this.qiyuanScore += value;
    }

    public synchronized void addBuluoScore(int value) {
        this.buluoScore += value;
    }

    public synchronized void addWuScore(int value) {
        this.wuScore += value;
    }

    public synchronized void addTongRes(int res) {
        this.tongRes += res;
    }

    public synchronized void addPhysical(int value) {
        this.physical += value;
    }

    public synchronized void addDiamond(int value) {
        this.diamond += value;
    }

    public synchronized void addGold(long value) {
        this.gold += value;
    }

    public synchronized void addMoney(long value) {
        this.totalMoney += value;
    }

    public synchronized void addXing(int value) {
        this.xing += value;
    }

    public synchronized void addSao(int value) {
        this.sao += value;
    }

    public synchronized void addBallisticCount(int value) {
        this.ballisticCount += value;
    }

    public synchronized void addAreaCount(int value) {
        this.areaCount += value;
    }

    public synchronized void addBagSpace(int value) {
        this.bagSpace += value;
    }

    /**
     * 出战神灵
     */
    public Gods callFightGods(){
		Team team = this.getTeams().get(this.getCurTeam());
		if(team != null){
            return this.getGods().get(team.getTeamGod());
		}
		return null;
    }

    public boolean hasCheckPoint(String cId){

        String checkpoint = getCheckPoint();
        if (MyUtil.isNullOrEmpty(checkpoint))
            return false;

        String[] ssc = checkpoint.split(",");
        for(String ss : ssc){
            if(ss.equals(cId)){
                return true;
            }
        }
        return false;

    }


    public boolean hasCheckPointDraw(String cId){
        String checkpoint = getDraw().getDrawList();
        if (MyUtil.isNullOrEmpty(checkpoint))
            return false;

        String[] ssc = checkpoint.split(",");
        for(String ss : ssc){
            if(ss.equals(cId)){
                return true;
            }
        }
        return false;

    }

}
