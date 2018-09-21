package com.igame.work.checkpoint.wujinZhiSen;

import com.google.common.collect.Lists;
import com.igame.core.di.LoadXml;
import com.igame.core.handler.RetVO;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.fight.dto.GodsDto;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.WuEffect;
import com.igame.work.user.dto.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EndlessService {
    /**
     * 无尽之森
     */
    @LoadXml("endlessdata.xml")public EndlessData endlessData;
    public Map<Long, Integer> tempBufferId = new ConcurrentHashMap<>();//临时ID
    /**
     * 无尽之森刷新
     */
    public int refEndlessRef(Player player){

        int ret = 0;
        int lv = player.getPlayerLevel();
        if(lv < 21){
            lv = 21;
        }
        List<EndlessdataTemplate> ls = Lists.newArrayList();
        for(EndlessdataTemplate et : endlessData.getAll()){
            if(lv >= Integer.parseInt(et.getLvRange().split(",")[0]) && lv <= Integer.parseInt(et.getLvRange().split(",")[1])){
                ls.add(et);
            }
        }
        if(ls.isEmpty()){
            ret = ErrorCode.ERROR;
        }else{
            player.getWuMap().clear();
            player.getWuZheng().clear();
            player.setWuGods(new GodsDto());
            player.setWuNai(0);
            player.getWuEffect().clear();
            for(EndlessdataTemplate et : ls){
                String str = String.valueOf(et.getNum());
                str+=";"+String.valueOf(et.getDifficulty())+";0";
                String[] mons = et.getMonsterId().split(",");
                List<String> temp = Lists.newArrayList();
                List<Integer> lvs = Lists.newArrayList();
                for(int i = 1;i <=5;i++){
                    temp.add(mons[GameMath.getRandInt(mons.length)]);
                    lvs.add(GameMath.getRandomInt(lv+Integer.parseInt(et.getMonsterLv().split(",")[0]), lv+Integer.parseInt(et.getMonsterLv().split(",")[1])));
                }
                str += ";"+ MyUtil.toString(temp, ",");
                str += ";"+MyUtil.toStringInt(lvs, ",");
                str += ";0;0";
                player.getWuMap().put(et.getNum(), str);
            }

        }
        return ret;
    }


    public void afterPlayerLogin(Player player) {
        if(player.getWuMap().isEmpty()){
            refEndlessRef(player);
        }
    }

    public static WuZhengDto parsePlayer(Player player){
        WuZhengDto wd = new WuZhengDto();
        wd.setWuGods(player.getWuGods().getGodsType() + ","+player.getWuGods().getGodsLevel());
        for(MatchMonsterDto wt : player.getWuZheng().values()){
            String str = String.valueOf(wt.getMonsterId());
            str += ";" + wt.getLevel();
            str += ";" + wt.getHp();
            str += ";" + wt.getHpInit();
            str += ";" + wt.getBreaklv();
            wd.getWuMons().add(str);
        }
        return wd;
    }

    public boolean isFullWuHp(Player player){
        boolean is = true;
        if(player.getWuZheng().isEmpty()){
            return true;
        }
        for(MatchMonsterDto mto : player.getWuZheng().values()){
            if(mto.getHp() < mto.getHpInit()){
                is = false;
                return is;
            }
        }
        return is;
    }

    /**
     * 推送无尽之森奶更新
     */
    public void notifyWuNaiChange(Player player){

        RetVO vo = new RetVO();
        vo.addData("wuNai", player.getWuNai());
        MessageUtil.sendMessageToPlayer(player, MProtrol.WUNAI_UPDATE, vo);

    }

    /**
     * 无尽无尽之森自己怪物阵容更新
     */
    public void notifyWuZhengChange(Player player){

        RetVO vo = new RetVO();
        vo.addData("wuZheng", parsePlayer(player));
        MessageUtil.sendMessageToPlayer(player, MProtrol.WUZHENG_UPDATE, vo);

    }

    /**
     * 无尽之森关卡更新
     */
    public void notifyWuChange(Player player){

        RetVO vo = new RetVO();
        vo.addData("wuMap", player.getWuMap().values());
        MessageUtil.sendMessageToPlayer(player, MProtrol.WU_UPDATE, vo);

    }

    /**
     * 无尽无尽之森自己怪物阵容更新
     */
    public void notifyWuBufferChange(Player player, List<WuEffect> ls){

        RetVO vo = new RetVO();
        vo.addData("wuEffect", ls);
        MessageUtil.sendMessageToPlayer(player, MProtrol.WUBUFFER_UPDATE, vo);

    }


    /**
     * 无尽之森已用免费重置次数更新
     */
    public void notifyWuResetChange(Player player){

        RetVO vo = new RetVO();
        vo.addData("wuReset", player.getWuReset());
        MessageUtil.sendMessageToPlayer(player, MProtrol.WU_RESET, vo);

    }

}
