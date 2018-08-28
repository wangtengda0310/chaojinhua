package com.igame.work.checkpoint.mingyunZhiMen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.ISFSModule;
import com.igame.core.quartz.TimeListener;
import com.igame.util.GameMath;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.mingyunZhiMen.data.DestinyrateTemplate;
import com.igame.work.checkpoint.mingyunZhiMen.data.FatedataTemplate;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GateService implements ISFSModule, TimeListener {
    @Override
    public void zero() {
        refFateMap();
    }

    /**
     *
     * @return 命运之门
     */
    public static List<GateDto> createGate(Player player){
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
        DestinyrateTemplate dt = MingyunZhiMenDataManager.DestinyData.getTemplate(type);
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
        for(FatedataTemplate ft : MingyunZhiMenDataManager.FateData.getAll()){
            List<Map<Long,Monster>> ls = Lists.newArrayList();
            for(int i = 1;i <= 3;i++){
                ls.add(CheckPointService.getNormalFateMonster(ft.getFloorNum()));
            }
            fateMap.put(ft.getFloorNum(), ls);
        }
    }

}
