import com.igame.work.fight.FightDataManager;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.data.MonsterGroupTemplate;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.work.fight.data.SkillLvTemplate;
import com.igame.core.db.DBManager;
import com.igame.dto.IDFactory;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.*;

/**
 * @author xym
 *
 * 更新数据库
 */
public class UpdateDB{

    private static String dbPer = "igame";

    public static void main(String[] args) {

        delPlayerTop();
        //updatePlayerItem2(player);
    }

    /**
     * 删除玩家playerTop字段，触发重新计算上限
     */
    private static void delPlayerTop() {
        getDatastores().forEach(ds -> {
            UpdateOperations<Player> up = ds.createUpdateOperations(Player.class);
            up.unset("playerTop");
            ds.update(ds.find(Player.class),up);
        });
    }

    /**
     * 某个玩家新增怪兽
     */
    private static void updatePlayerMonster(Player player) {

        int maxAtx = 0;
        int maxAtxId = 0;
        int maxHp = 0;
        int maxHpId = 0;
        List<MonsterTemplate> all = MonsterDataManager.MONSTER_DATA.getAll();
        for (MonsterTemplate monsterTemplate : all) {
            int atk = (int) (monsterTemplate.getMonster_atk() + monsterTemplate.getAtk_up() * 80);
            int hp = (int) (monsterTemplate.getMonster_hp() + monsterTemplate.getHp_up() * 80);

            if (atk > maxAtx){
                maxAtx = atk;
                maxAtxId = monsterTemplate.getMonster_id();
            }
            if (hp > maxHp){
                maxHp = hp;
                maxHpId = monsterTemplate.getMonster_id();
            }
        }

        Monster maxAtkM = new Monster(player, IDFactory.ins().getNewIdMonster(player.getSeverId()), player.getPlayerId(),  MonsterDataManager.MONSTER_DATA.getMonsterTemplate(maxAtxId).getMonster_hp(), 0,maxAtxId);
        maxAtkM.setLevel(80);
        getDatastore(player.getSeverId()).save(maxAtkM);

        Monster maxHpM = new Monster(player, IDFactory.ins().getNewIdMonster(player.getSeverId()), player.getPlayerId(),  MonsterDataManager.MONSTER_DATA.getMonsterTemplate(maxHpId).getMonster_hp(), 0,maxHpId);
        maxHpM.setLevel(80);
        getDatastore(player.getSeverId()).save(maxHpM);

        System.out.println("执行成功:最高攻击-"+maxAtx+"-"+maxAtxId);
        System.out.println("执行成功:最高血量-"+maxHp+"-"+maxHpId);
    }

    /**
     * 新增可召唤怪兽的道具
     */
    private static void updatePlayerItem(Player player) {
        int count = 0;
        List<MonsterGroupTemplate> all = MonsterDataManager.monsterGroupData.getAll();
        for (MonsterGroupTemplate template : all) {

            if (template.getNum() > 0){
                Item item = new Item();
                item.setPlayerId(player.getPlayerId());
                item.setItemId(template.getItemId());
                item.setCount(template.getNum());
                item.setDtate(1);
                getDatastore(player.getSeverId()).save(item);
                count++;
            }
        }
        System.out.println("执行成功:"+count);
    }

    /**
     * 新增可升级怪兽技能的道具
     */
    private static void updatePlayerItem2(Player player) {

        int count = 0;

        Set<Integer> addItems = new HashSet();
        Map<Integer,Item> odlItems = new HashMap<>();

        List<SkillLvTemplate> all = FightDataManager.SkillLvData.getAll();
        for (SkillLvTemplate template : all) {
            String useItem = template.getUseItem();
            addItems.add(Integer.parseInt(useItem));
        }

        List<Item> ls = getDatastore(player.getSeverId()).find(Item.class, "playerId", player.getPlayerId()).asList();
        for(Item mm : ls){
            odlItems.put(mm.getItemId(), mm);
        }

        for (Integer itemId : addItems) {
            Item item = odlItems.get(itemId);
            if (item == null){  //新增
                item = new Item();
                item.setPlayerId(player.getPlayerId());
                item.setItemId(itemId);
                item.setCount(10);
                getDatastore(player.getSeverId()).save(item);
            }else { //增加数量
                item.setCount(item.getCount()+10);

                UpdateOperations<Item> up = getDatastore(player.getSeverId()).createUpdateOperations(Item.class)
                        .set("count", item.getCount());
                getDatastore(player.getSeverId()).update(item, up);
            }
            count++;
        }


        System.out.println("执行成功:"+count);
    }

    /**
     * 更新所有角色背包空间为100
     */
    private static void updatePlayerBagSpace() {
        getDatastores().forEach(ds -> {
            UpdateOperations<Player> up = ds.createUpdateOperations(Player.class)
                    .set("bagSpace", 100);
            ds.update(ds.find(Player.class),up);
        });
    }

    /**
     * 获取所有的数据库Datastore对象
     */
    private static List<Datastore> getDatastores(){
        List<Datastore> datastores = new ArrayList<>();

        String dBName = DBManager.getInstance().p.getProperty("DBName");
        String[] dBNames = dBName.split(",");
        for(String db : dBNames){
            int serverId=Integer.parseInt(db.substring(5));
            Datastore ds = getDatastore(serverId);
            datastores.add(ds);
        }
        return datastores;
    }

    /**
     * 获取所有的数据库Datastore对象
     */
    private static Datastore getDatastore(int serverId){

        return DBManager.getInstance().getDatastore(dbPer + serverId);
    }

}
