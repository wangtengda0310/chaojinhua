import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.data.DataManager;
import com.igame.util.GameMath;
import com.igame.work.checkpoint.baozouShike.BallisticRank;
import com.igame.work.checkpoint.baozouShike.BallisticService;
import com.igame.work.checkpoint.baozouShike.BaozouShikeDataManager;
import com.igame.work.checkpoint.baozouShike.data.RunBattlerewardData;
import com.igame.work.checkpoint.baozouShike.data.RunBattlerewardTemplate;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.service.FriendService;
import com.igame.work.monster.dto.Monster;
import com.igame.work.shop.dto.MysticalShop;
import com.igame.work.shop.dto.ShopInfo;
import com.igame.work.shop.service.ShopService;
import com.igame.work.turntable.service.TurntableService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.Team;
import com.igame.work.user.service.PlayerCacheService;
import com.igame.work.user.service.VIPService;

import java.awt.event.ActionListener;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xym
 */
public class MyTest {

    public static void main(String[] args) throws Exception{

        DataManager.load("resource/");

        //testLottery();
        //testAddMysticalShopLv();
        //testAddVipLv();
        //testSetTeamEquip();
        //testLambda();
        //PlayerCacheService.ins().loadData();
        //testFriendNom();
        //testFriendFind();
        //testBallReward();
        //testData();
        //testGetMonster();
        //testUpdateBallRank();
        //testLoadBallRank();

    }

    /**
     * 随机出对手
     * @param rank 当前角色排名
     */
    private static List<Integer> getOpponent(int rank){
        List<Integer> opponent = Lists.newArrayList();
        if(rank<=6){
            for(int i = 0;i<=4;i++){
                opponent.add(i+1);
            }
        }else if (rank < 10){	//小于10时下面算法可能随机出重复的

            int r1 = GameMath.getRandomInt(rank - 6 ,rank - 5);
            int r2 = GameMath.getRandomInt(rank - 5 ,rank - 4);
            int r3 = GameMath.getRandomInt(rank - 4 ,rank - 3);
            int r4 = GameMath.getRandomInt(rank - 3 ,rank - 2);
            int r5 = GameMath.getRandomInt(rank - 2 ,rank);

            opponent.add(r1);
            opponent.add(r2);
            opponent.add(r3);
            opponent.add(r4);
            opponent.add(r5);

        }else{
            int r1 = (int)(rank * GameMath.getRandomDouble(0.4,  0.5) - 1);
            int r2 = (int)(rank * GameMath.getRandomDouble(0.51, 0.6) - 1);
            int r3 = (int)(rank * GameMath.getRandomDouble(0.61, 0.7) - 1);
            int r4 = (int)(rank * GameMath.getRandomDouble(0.71, 0.8) - 1);
            int r5 = (int)(rank * GameMath.getRandomDouble(0.81, 1.0) - 1);

            opponent.add(r1);
            opponent.add(r2);
            opponent.add(r3);
            opponent.add(r4);
            opponent.add(r5);
        }
        return opponent;
    }

    private static void testLottery() {
        Player player = new Player();
        player.setPlayerLevel(15);

        TurntableService.ins().initTurntable(player);

        Map<Integer,Integer> map = new HashMap<>();
        for (int i = 0; i < 10000; i++) {

            int lottery = TurntableService.ins().lottery(player); //需更改方法返回值为index
            //int lottery = 0;
            if (map.get(lottery) == null){
                map.put(lottery,1);
            }else {
                map.put(lottery,map.get(lottery)+1);
            }
        }

        System.out.println(map);

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey()+"="+entry.getValue() / 100.0);
        }
    }

    private static void testAddMysticalShopLv() {
        Player player = new Player();
        ShopInfo shopInfo = new ShopInfo();
        MysticalShop mysticalShop = new MysticalShop();
        mysticalShop.setShopLv(1);
        mysticalShop.setExp(0);

        shopInfo.setMysticalShop(mysticalShop);
        player.setShopInfo(shopInfo);
        ShopService.ins().addMysticalExp(player,10000);
    }

    private static void testAddVipLv() throws JsonProcessingException {
        Map<String,Object> linkedMap = Maps.newLinkedHashMap();
        ObjectMapper mapper = new ObjectMapper();

        Player player = new Player();
        player.setVip(0);
        player.setTotalMoney(1000000);
        player.setVipPrivileges(linkedMap);

        VIPService.ins().initPrivileges(player.getVipPrivileges());
        System.out.println(player.getVip());
        System.out.println(mapper.writeValueAsString(player.getVipPrivileges()));

        VIPService.ins().addVipLv(player);
        System.out.println(player.getVip());
        System.out.println(mapper.writeValueAsString(player.getVipPrivileges()));
    }

    private static void testSetTeamEquip() {
        List<Team> teams = new ArrayList<>();

        Team team1 = new Team();
        team1.setTeamId(1);
        Map<Long,Map<Integer,Integer>> teamEquip = new HashMap<>(); //怪兽装备 <怪兽ID,<位置,装备ID>>
        Map<Integer,Integer> equipMap = new HashMap<>();
        equipMap.put(1,1001);
        teamEquip.put(10001l,equipMap);
        team1.setTeamEquip(teamEquip);
        teams.add(team1);

        Team team2 = new Team();
        team2.setTeamId(2);
        Map<Long,Map<Integer,Integer>> teamEquip2 = new HashMap<>(); //怪兽装备 <怪兽ID,<位置,装备ID>>
        Map<Integer,Integer> equipMap2 = new HashMap<>();
        equipMap2.put(2,1002);
        teamEquip2.put(10001l,equipMap2);
        team2.setTeamEquip(teamEquip2);
        teams.add(team2);

        System.out.println(teamEquip);

        Monster monster = new Monster();
        monster.setObjectId(10001);
        monster.setLevel(31);

        System.out.println(Arrays.toString(monster.getTeamEquip()));

        monster.setTeamEquip(teams);

        System.out.println(Arrays.toString(monster.getTeamEquip()));
    }

    private static void testLambda() {

        /*Runnable noArguments = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello World")
            }
        };*/
        Runnable noArguments = () -> System.out.println("Hello World");

        /*ActionListener oneArgument = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("button clicked")
            }
        };*/
        ActionListener oneArgument = event -> System.out.println("button clicked");

        /*Runnable multiStatement = new Runnable() {
            @Override
            public void run() {
                System.out.print("Hello");
                System.out.println(" World");
            }
        };*/
        Runnable multiStatement = () -> {
            System.out.print("Hello");
            System.out.println(" World");
        };

        /*BinaryOperator<Integer> add = new BinaryOperator<Long>() {
            @Override
            public Long apply(Integer x, Integer y) {
                return x+y;
            }
        };*/
        BinaryOperator<Integer> add = (x, y) -> x + y;

        /*BinaryOperator<Long> add = new BinaryOperator<Long>() {
            @Override
            public Long apply(Long x, Long y) {
                return x+y;
            }
        };*/
        BinaryOperator<Long> addExplicit = (Long x, Long y) -> x + y;

        /*UnaryOperator<Friend> unaryOperator = new UnaryOperator<Friend>() {
            @Override
            public Friend apply(Friend friend) {
                friend.setNickName("张三");
                return friend;
            }
        };*/
        UnaryOperator<Friend> unaryOperator = friend -> {
            friend.setNickName("张三");
            return friend;
        };

        /*Function<Integer,Long> function = new Function<Integer, Long>() {
            @Override
            public Long apply(Integer integer) {
                return new Long(integer);
            }
        };*/
        Function<Integer,Long> function = i -> new Long(i);

        long count = new ArrayList<String>().stream()
                .filter(s -> s.length() > 20)
                .count();

        List<String> collected = Stream.of("a", "b", "c")
                .collect(Collectors.toList());

    }

    private static void testFriendNom() {
        PlayerCacheService.ins().loadData();

        List<Friend> nomFriends = new ArrayList<>();
        //搜索好友的优先级：离线24小时以内，等级差10级以内的优先抽取推荐
        int playerLevel = 10;

        List<Player> players = PlayerCacheService.ins().getPlayers(1);
        List<Player> one = new ArrayList<>();
        List<Player> two = new ArrayList<>();
        List<Player> three = new ArrayList<>();
        for (Player playerCacheDto : players) {
            if (playerCacheDto.getPlayerId() == 1000119)
                continue;

            Date lastLoginOutDate = playerCacheDto.getLoginoutTime();
            int playerLevel1 = playerCacheDto.getPlayerLevel();
            if (new Date().getTime() - lastLoginOutDate.getTime() <= 24*60*60*1000){
                if (Math.abs(playerLevel - playerLevel1) <= 10){
                    one.add(playerCacheDto);
                }
                two.add(playerCacheDto);
            }
            three.add(playerCacheDto);
        }

        //随机十个
        if (one.size() > 10){
            //随机并添加新的好友
            random(nomFriends, one);
        }else if (two.size() > 10){
            //随机并添加新的好友
            random(nomFriends, two);

        }else if (three.size() > 10){
            //随机并添加新的好友
            random(nomFriends, three);
        }else {
            for (Player cacheDto : three) {
                nomFriends.add(new Friend(cacheDto));
            }
        }

        System.out.println(nomFriends.size());
    }

    private static void random(List<Friend> nomFriends, List<Player> three) {

        ok:
        while (true){
            int i = new Random().nextInt(three.size());
            Player playerCacheDto = three.get(i);
            nomFriends.add(new Friend(playerCacheDto));
            three.remove(i);
            if (nomFriends.size() >= 10)
                break ok;
        }
    }

    private static void testFriendFind() {
        PlayerCacheService.ins().loadData();
        Player playerByNickName = PlayerCacheService.ins().getPlayerByNickName("玩家_1000116");
        if (playerByNickName == null){
            System.out.println(1);
            return;
        }

        Player player = new Player();

        player.setSeverId(1);
        player.setPlayerId(1000120);	//好友id
        player.setPlayerLevel(10);	//玩家等级
        player.setNickname("玩家_1000116");	//玩家昵称
        player.setPlayerFrameId(1);	//玩家头像框
        player.setPlayerHeadId(1);	//玩家头像
        player.setFightValue(100);	//战力

        FriendService.ins().addReqFriend(player,playerByNickName.getPlayerId());
    }

    private static void testBallReward() {
        int gold = 0;

        RunBattlerewardData runBattlerewardData = BaozouShikeDataManager.runBattlerewardData;
        List<RunBattlerewardTemplate> runBattlerewardDataAll = runBattlerewardData.getAll();
        int i = runBattlerewardDataAll.indexOf(runBattlerewardData.getTemplate(600));
        for (int j = 0; j <= i; j++) {
            gold += runBattlerewardDataAll.get(j).getGold();
        }

        System.out.println("1,1,"+gold+";");
    }

    private static void testUpdateBallRank() {
        testLoadBallRank();

        Player player = new Player();
        player.setSeverId(1);
        player.setNickname("张三");
        player.setBallisticEnter(new Date());

        BallisticService.ins().updateRank(player,10);

        BallisticRank ins = BallisticRank.ins();
    }

    private static void testLoadBallRank() {
        BallisticService.ins().loadData();
        System.out.println(BallisticRank.ins().get_id());
    }

    private static void testGetMonster() {
        List<MatchMonsterDto> matchMonsterDtos = BallisticService.ins().buildMonsterByKillNum(150, 1000);

        System.out.println(matchMonsterDtos.size());
    }

    private static void testData() {
        DataManager.load("resource");
    }

}
