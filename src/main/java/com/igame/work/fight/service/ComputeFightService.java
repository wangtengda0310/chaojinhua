package com.igame.work.fight.service;

import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.Team;

/**
 * @author xym
 *
 * 战力计算服务
 *
 */
public class ComputeFightService {

    private static final ComputeFightService domain = new ComputeFightService();

    public static final ComputeFightService ins() {
        return domain;
    }


    /**
     * 计算 玩家战力
     * @param player 角色
     */
    public long computePlayerFight(Player player) {
        player.setFightValue(computeTeamFight(player,player.getCurTeam()));
        return player.getFightValue();
    }


    /**
     *
     * 计算玩家阵容战力
     * @param player
     * @param teamId
     */
    public long computeTeamFight(Player player, int teamId) {

        Team team = player.getTeams().get(teamId);

        long fightValue = 0;
        long[] teamMonster = team.getTeamMonster();
        for (long mid : teamMonster) {
            if (-1 != mid) {
                Monster mm = player.getMonsters().get(mid);
                if (mm != null) {
                    fightValue += computeMonsterFight(mm);
                }
            }
        }
        team.setFightValue(fightValue);

        return fightValue;
    }

    /**
     * 计算怪兽战力
     */
    public long computeMonsterFight(Monster monster){

        MonsterTemplate mt = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(monster.getMonsterId());
        if(mt != null){
            String[] types = mt.getMonstertype().split(",");
            if(types.length != 0){
                double hprate = 0;//hp
                double attrate = 0;//攻击
                double sprate = 0;//移速
                double repelrate = 0;//霸气
                double rngrate = 0;//射程
                double iasrate = 0;//攻速
                for(String type : types){
                    if("1".equals(type)){//近战
                        hprate+= 0.5;
                        attrate+=0.3;
                        sprate+=0.3;
                        repelrate+=0.3;
                        rngrate+=0;
                        iasrate+=0;
                    }else if("2".equals(type)){//远程
                        hprate+= 0.1;
                        attrate+=0.5;
                        sprate+=0.1;
                        repelrate+=0;
                        rngrate+=0.1;
                        iasrate+=100;
                    }else if("3".equals(type)){//坦克
                        hprate+= 0.6;
                        attrate+=0.1;
                        sprate+=0.3;
                        repelrate+=0.3;
                        rngrate+=0;
                        iasrate+=0;
                    }else if("4".equals(type)){//输出
                        hprate+= 0.3;
                        attrate+=1;
                        sprate+=0.2;
                        repelrate+=0.1;
                        rngrate+=0;
                        iasrate+=0;
                    }else if("5".equals(type)){//辅助
                        hprate+= 0.6;
                        attrate+=0.3;
                        sprate+=0.4;
                        repelrate+=0.2;
                        rngrate+=0;
                        iasrate+=0;
                    }
                }

                monster.fightValue = (long)(monster.hp*hprate + monster.attack*attrate + monster.speed*sprate + monster.repel*repelrate + monster.rng*rngrate + monster.ias * iasrate);
//				this.ff = "hp:"+this.hp+"*hprate:"+ hprate +"+attack:" + this.attack+"*attrate:"+attrate+"+speed:" + this.speed+"*sprate:"+sprate+"+repel:" + this.repel+"*repelrate:"+
//					repelrate+ "+rng:" +  this.rng +"*rngrate:"+ rngrate+"+ias:" +this.ias +"*iasrate:"+iasrate;
            }
        }

        return monster.fightValue;
    }

}
