package com.igame.work.checkpoint.mingyunZhiMen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.di.LoadXml;
import com.igame.core.quartz.TimeListener;
import com.igame.util.GameMath;
import com.igame.work.checkpoint.mingyunZhiMen.data.DestinyData;
import com.igame.work.checkpoint.mingyunZhiMen.data.DestinyrateTemplate;
import com.igame.work.checkpoint.mingyunZhiMen.data.FateData;
import com.igame.work.checkpoint.mingyunZhiMen.data.FatedataTemplate;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GateService implements ISFSModule, TimeListener {
    /**
     * 命运之门概率
     */
    @LoadXml("destinyrate.xml")public DestinyData destinyData;
    /**
     * 命运之门
     */
    @LoadXml("destinydata.xml")public FateData fateData;

    @Inject
    private MonsterService monsterService;

    @Override
    public void zero() {
        refFateMap();
    }

    private Map<Long, Map<Long, MatchMonsterDto>> mingZheng = new ConcurrentHashMap<>();//命运之门当前阵容

    public Map<Long, MatchMonsterDto> getMingZheng(Player player) {
        return mingZheng.get(player.getPlayerId());
    }

    /**
     *
     * @return 命运之门
     */
    public List<GateDto> createGate(Player player){
        List<GateDto> ls = Lists.newArrayList();
        int type = player.getFateData().getFirst();
        if(type == 0){
            type = 1;
        }else{
            if(player.getVip() <5){
                type = 2;
            }else if(player.getVip() >= 5 && player.getVip() < 9){
                type = 3;
            }else{
                type = 4;
            }
        }

        //生成普通门
        int i = 1;
        List<Map<Long,Monster>> mms = fateMap.get(player.getFateData().getTodayFateLevel());
        if(mms != null){

            for(Map<Long,Monster> mm : mms){
                GateDto gd = new GateDto();
                gd.setGateId(i);
                gd.setType(0);
                List<Monster> mons = new ArrayList<>(mm.values());
                gd.setLevel(mons.get(0).getLevel());
                StringBuilder mId = new StringBuilder();
                for(int j = 0 ;j < 2;j++){
                    mId.append(",").append(mons.get(j).getMonsterId());
                }
                if(mId.length() >1){
                    mId = new StringBuilder(mId.substring(1));
                }
                gd.setMonsterId(mId.toString());
                gd.setBoxCount(2);
                gd.setMons(mm);
                ls.add(gd);
                i++;
            }
        }


        //生成特殊门
        DestinyrateTemplate dt = destinyData.getTemplate(type);
        if(dt != null){
            if(player.getFateData().getTempSpecialCount() >= dt.getMaxTimes()){//已经最大特殊门次数
                // do nothing
            }else{
                int rate = dt.getRate() + player.getFateData().getAddRate();//总概率
                boolean get = GameMath.hitRate100(rate);
                if(get){//随机到了

                    String[] doorTyep = dt.getDoorType().split(";");
                    String[] rrate = dt.getRandomRate().split(";");
                    Map<Integer,Integer> ms = Maps.newHashMap();
                    for(int index = 0 ;index < doorTyep.length; index++){
                        ms.put(Integer.parseInt(doorTyep[index]), Integer.parseInt(rrate[index]));
                    }
                    int rtype = GameMath.getAIRate100(ms);
                    if(rtype != 0){
                        player.getFateData().setAddRate(0);
                        player.getFateData().setTempSpecialCount(player.getFateData().getTempSpecialCount() + 1);
                        GateDto gd = new GateDto();
                        gd.setGateId(i);
                        gd.setType(rtype);
                        gd.setBoxCount(1);
                        ls.add(gd);
                    }

                }else{
                    player.getFateData().setAddRate(player.getFateData().getAddRate() + dt.getUpRate());
                }
            }
        }
        return ls;
    }

    private static Map<Integer, List<Map<Long, Monster>>> fateMap = Maps.newHashMap();//命运之门怪物配置

    @Override
    public void init() {
        refFateMap();
    }

    private void refFateMap(){

        fateMap.clear();
        for(FatedataTemplate ft : fateData.getAll()){
            List<Map<Long,Monster>> ls = Lists.newArrayList();
            for(int i = 1;i <= 3;i++){
                ls.add(getNormalFateMonster(ft.getFloorNum()));
            }
            fateMap.put(ft.getFloorNum(), ls);
        }
    }

    /**
     * 生成命运之门普通门怪物数据
     */
    public Map<Long,Monster> getNormalFateMonster(int floorNum){

        FatedataTemplate ft  = fateData.getTemplate(floorNum);
        if(ft == null){
            return Maps.newHashMap();
        }
        StringBuilder monsterId = new StringBuilder();
        StringBuilder monsterLevel = new StringBuilder();
        StringBuilder skillLv = new StringBuilder();
        String[] m1 = ft.getMonste1rLibrary().split(",");
//		if(ft.getMonste2rLibrary() == null){
//			System.err.println(ft.getFloorNum());
//		}
        String[] m2 = ft.getMonste2rLibrary().split(",");
        for(int i = 1;i<=2;i++){
            monsterId.append(",").append(m1[GameMath.getRandInt(m1.length)]);
            monsterLevel.append(",").append(ft.getMonster1Lv());
            skillLv.append(",").append(ft.getSkill1Lv());
        }
        for(int i = 1;i<=8;i++){
            monsterId.append(",").append(m2[GameMath.getRandInt(m2.length)]);
            monsterLevel.append(",").append(ft.getMonster2Lv());
            skillLv.append(",").append(ft.getSkill2Lv());
        }
        if(monsterId.length() > 0){
            monsterId = new StringBuilder(monsterId.substring(1));
        }
        if(monsterLevel.length() > 0){
            monsterLevel = new StringBuilder(monsterLevel.substring(1));
        }
        if(skillLv.length() > 0){
            skillLv = new StringBuilder(skillLv.substring(1));
        }
        return monsterService.createMonster(monsterId.toString(), monsterLevel.toString(), "", skillLv.toString(),"");

    }

    public List<GateDto> getGateDtos(Player player, List<GateDto> ls) {
        for(int level = player.getFateData().getTodayFateLevel();level <= player.getFateData().getFateLevel();level++){
            List<GateDto> temp = createGate(player);
            boolean special = false;
            for(GateDto gt : temp){
                if(gt.getType() != 0){//怪物关卡直接获得宝箱
                    special = true;//随机到特殊关卡就展示门
                    break;
                }
            }
            if(special){
                ls = temp;
            }else{
                player.getFateData().addTempBoxCount(2);
            }
            player.getFateData().setTodayFateLevel(level);
            if(!ls.isEmpty()){//随机到特殊关卡就展示门
                break;
            }
        }
        return ls;
    }

}