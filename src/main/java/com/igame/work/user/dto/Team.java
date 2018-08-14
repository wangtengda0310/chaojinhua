package com.igame.work.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xym
 *
 *  阵容
 *
 */

@Entity(noClassnameStored = true)
public class Team {

    private int teamId; //阵容ID

    private String teamName;    //阵容名称

    private int teamGod;    //阵容神灵

    private long[] teamMonster = new long[]{-1,-1,-1,-1,-1}; //阵容怪兽

    @JsonIgnore
    private Map<Long,Map<Integer,Integer>> teamEquip = Maps.newConcurrentMap(); //怪兽装备 <怪兽ID,<位置,装备ID>>

    private long fightValue;    //战力

    @JsonIgnore
    private List<Long> changeMonster = Lists.newArrayList();    //装备发生改变的怪兽数组

    public Team() {
    }

    public Team(int teamId, String teamName, long mid, long mid2) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamMonster[0] = mid;
        this.teamMonster[1] = mid2;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getTeamGod() {
        return teamGod;
    }

    public void setTeamGod(int teamGod) {
        this.teamGod = teamGod;
    }

    public long[] getTeamMonster() {
        return teamMonster;
    }

    public void setTeamMonster(long[] teamMonster) {
        this.teamMonster = teamMonster;
    }

    public Map<Long,Map<Integer,Integer>> getTeamEquip() {
        return teamEquip;
    }

    public void setTeamEquip(Map<Long,Map<Integer,Integer>> teamEquip) {
        this.teamEquip = teamEquip;
    }

    public long getFightValue() {
        return fightValue;
    }

    public void setFightValue(long fightValue) {
        this.fightValue = fightValue;
    }

    public List<Long> getChangeMonster() {
        return changeMonster;
    }

    public void setChangeMonster(List<Long> changeMonster) {
        this.changeMonster = changeMonster;
    }
}
