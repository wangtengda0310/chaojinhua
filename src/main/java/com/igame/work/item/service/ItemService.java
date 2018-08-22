package com.igame.work.item.service;

import com.igame.work.item.dao.ItemDAO;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xym
 *
 * 道具服务
 */
public class ItemService {

    private static final ItemService domain = new ItemService();

    public static final ItemService ins() {

        return domain;
    }

    /**
     *  道具合成成功以后
     * @param player    角色
     * @param item  被合成的道具
     * @return 有装备更新的怪兽
     */
    public List<Monster> processItemGroupSucceed(Player player, Item item){

        List<Monster> ret = new ArrayList<>();

        //获取当前出战阵容的可用数
        int tmepUsableCount = item.getUsableCount(player.getCurTeam());

        for (Team team : player.getTeams().values()) {  //遍历阵容

            List<Long> changeMonster = team.getChangeMonster();
            int usableCount = item.getUsableCount(team.getTeamId());

            if (usableCount < tmepUsableCount){ //如果 阵容可用数 小于 出战阵容可用数

                ok:
                for (Map.Entry<Long, Map<Integer, Integer>> mapEntry : team.getTeamEquip().entrySet()) {    //遍历阵容怪兽

                    Long mid = mapEntry.getKey();
                    Map<Integer, Integer> monsterEquip = mapEntry.getValue();
                    if (monsterEquip == null)
                        break;

                    for (Map.Entry<Integer, Integer> entry : monsterEquip.entrySet()) { //遍历怪兽装备

                        Integer location = entry.getKey();
                        Integer itemId = entry.getValue();

                        if (itemId == item.getItemId()){    //如果怪兽身上有此装备，则卸下
                            unsnatch(player,team.getTeamId(),location,monsterEquip,new ArrayList<>());
                            usableCount++;

                            Monster mm = player.getMonsters().get(mid);
                            mm.setTeamEquip(player.getTeams().values());

                            changeMonster.add(mm.getObjectId());
                            ret.add(mm);
                        }
                        if (usableCount == tmepUsableCount)
                            break ok;
                    }
                }
            }

        }

        return ret;
    }

    /**
     * 卸下装备
     * @param player 角色
     * @param teamId 阵容ID
     * @param location 卸下的位置
     * @param monsterEquip 怪兽装备 <位置，装备ID>
     * @param items
     */
    public void unsnatch(Player player, int teamId, int location, Map<Integer, Integer> monsterEquip, List<Item> items) {

        //更新道具
        Integer equipId = monsterEquip.get(location);
        Item item = player.getItems().get(equipId);

        if (item != null){
            item.getEquipCounts()[teamId-1] -= 1;
            item.setDtate(2);
            items.add(item);
        }

        //卸下怪兽装备
        monsterEquip.remove(location);
    }

    /**
     * 穿装备
     * @param player 角色
     * @param teamId 阵容ID
     * @param location 穿上的位置
     * @param itemId 装备ID
     * @param monsterEquip 怪兽装备
     * @param items
     */
    public void dress(Player player, int teamId, int location, int itemId, Map<Integer, Integer> monsterEquip, List<Item> items) {

        //如果位置上有装备
        Integer equipId =monsterEquip.get(location);
        if (equipId != null){
            unsnatch(player,teamId,location,monsterEquip,items);
        }

        //更新道具
        Item item = player.getItems().get(itemId);
        item.getEquipCounts()[teamId-1] += 1;
        item.setDtate(2);

        items.add(item);

        //更新阵容装备
        monsterEquip.put(location,itemId);

    }

    public void loadPlayer(Player player, int serverId) {
        player.setItems(ItemDAO.ins().getItemByPlayer(serverId, player.getPlayerId()));
    }
}
