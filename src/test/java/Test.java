import com.igame.core.data.DataManager;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Marcus.Z
 * test
 *
 */
public class Test {
	
	public static final Random Rnd = new Random();

	public static void main(String[] args) {

		DataManager.load();

//		Player player = PlayerDAO.ins().getPlayerByPlayerId(1, 10001132);
//		PlayerLoad.ins().loadPlayer(player,1,1);
//
//		HeadService.ins().initHead(player);
//		HeadService.ins().initFrame(player);
//
//		Set<Integer> unlockHead = player.getUnlockHead();
//		Set<Integer> unlockFrame = player.getUnlockFrame();
//		System.out.println(unlockHead);
//		System.out.println(unlockFrame);
//		System.out.println(player);


//		GoldLog.info("ssssssssssssssssssssssssssssssssssssssss");
//		Tes t1 = new Tes(1,"11111111111111");
//		Tes t2 = new Tes(2,"22222222222222");
//		Map<Integer,Tes> map = Maps.newHashMap();
//		map.put(1, t1);
//		map.put(2, t2);
//		map.forEach((k,v) -> v.setName("aaaaaaaa"));
//		for(Map.Entry<Integer, Tes> m : map.entrySet()){
//			System.out.println(m.getKey() + ":" + m.getValue().getName());
//		}
//		TesDAO.ins().store(tt);
//		System.out.println(String.valueOf(1000+1)+1);
//		System.out.println(DataManager.ins().MONSTER_DATA.getAll().size());
//		Player player = PlayerDAO.ins().getPlayerByUserId(1, 1000113);
//		System.out.println(player.getTeams()[0]);
//		player.getTeams()[0] = 100016+"," +100016+"," + "-1,-1,-1";
//		System.out.println(player.getTeams()[0]);
//		PlayerLoad.ins().savePlayer(player);
//		player = PlayerDAO.ins().getPlayerByUserId(1, 1000113);
//		System.out.println(player.getTeams()[0]);
//		Map<Integer,Integer> map = new TreeMap<Integer,Integer>();
//		map.put(8, 8);
//		map.put(1, 1);
//		map.put(2, 2);
//		map.put(9, 9);
//		map.put(3, 3);
//		map.put(4, 4);
//		map.put(5, 5);
//		for(Integer ii: map.values()){
//			System.out.println(ii);
//		}
//		map.entrySet().stream().collect(Collectors.toList());
//		System.out.println(10 + 10/5);
		
//		String[] aa = "".split(",");
//		for(String temp : aa){
//			System.out.println(temp.indexOf(","));
//		}
//		Map<Integer,Integer> map = Maps.newHashMap();
//		
//		map.put(1, 1);
//		map.put(2, 2);
//		map.put(0, 0);
//		for(Map.Entry<Integer, Integer> m : map.entrySet()){
//			map.put(m.getKey(), m.getValue() + 10);
//			
//		}
//		for(Map.Entry<Integer, Integer> m : map.entrySet()){
//			System.out.println(m.getKey() + ":" + m.getValue());
//		}
//		System.out.println(new Date());
//		Optional< String > fullName = Optional.ofNullable( "aaaaaaaaaaaa" );
//		if(fullName.isPresent()){
//			System.out.println(fullName.get());
//		}select   a.name,b.name as manager    from   employees as a,(select * from employees  where mgrId  >0) as b   left   join  a.mgrId=b.mgrId 
//		Collections.reverse(Arrays.asList(source));
//		String[] skillMt = new String[0];
//		for(String ss :skillMt){
//			System.out.println(ss);
//		}
//		Integer ii = 1;
//		
//		Monster  m =new Monster();
//		m.setPlayerId(11);
//		m.getSkillMap().put(1, 2);
//		
//		Monster n = m.clonew();
//		System.out.println(n.getPlayerId());
//		System.out.println(n.getSkillMap().get(1));
//		for(int i = 0;i <1;i++){
//			TongHuaDto dt = PlayerService.getRandomTongHuaDto();
//			System.out.println(dt.getTongStr());
//		}
//		String ss = "0,0,3,200003,6;1,-1,1,3,1;1,-1,1,1,1;0,-1,5,5,110;0,-1,5,5,78;3,-1,3,200004,5;4,-1,3,200006,5;0,-1,3,200003,5;0,-1,3,200003,6;0,-1,3,200002,7;4,-1,3,200006,10;0,-1,5,5,166;4,-1,3,200006,10;0,-1,5,5,179;0,-1,3,200003,5;0,-1,3,200002,8;0,-1,3,200003,6;0,-1,3,200001,7;0,-1,3,200001,10;0,-1,3,200001,10;0,-1,3,200002,4;0,-1,5,5,169";
//		for(int i = 0;i <10;i++){
//			System.out.println(GameMath.getRandomDouble(0.1, 0.5));
//		}

//		String ss = "booandfoool";
//		for(String temp : ss.split("o")){
//			System.out.println(temp);
//		}
//		System.out.println(GameMath.formatNumber(1110.457845485345));
		
//		String maxId = "100001";
//		System.out.println(Integer.parseInt(maxId.substring(maxId.length() - 5)));
//		List<Integer> ll = Lists.newArrayList();
//		ll.add(1);
//		ll.add(2);
//		ll.remove(new Integer(1));
//		for(Integer ii : ll){
//			System.out.println(ii);
//		}
//		Set<Integer> set = Sets.newHashSet();
//		System.out.println(ResourceService.ins().getRewardString(PlayerService.couKa(1001, 10)));
		
//		JSONObject jsonObject = JSONObject.fromObject("{\"c\":[{\"attackerType\":1,\"attackerId\":1,\"cmdType\":1,\"cmdId\":1,\"targetId\":1},{\"attackerType\":2,\"attackerId\":2,\"cmdType\":2,\"cmdId\":2,\"targetId\":2}]}");
//		for(int i = 0;i < jsonObject.getJSONArray("t").size();i++){
//			System.out.println(jsonObject.getJSONArray("t").get(i));
//		}
//		List<FightCmd> ll  = (List<FightCmd>)JSONArray.toList(jsonObject.getJSONArray("c"), FightCmd.class);
//		for(FightCmd ff : ll ){
//			System.out.println(ff.getA());
//		}
//		System.out.println();
//		for(FightCmd ff : (List<FightCmd>)JSONArray.toCollection(jsonObject.getJSONArray("c"), FightCmd.class)){
//			System.out.println(ff.getAttackerType());
//		}
		
//		
//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			List<FightCmd> ll = (List<FightCmd>)mapper.readValue(jsonObject.getJSONArray("c").toString(), List.class);
//			for(FightCmd ff : ll ){
//				System.out.println(ff.getA());
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
		
		//2021    chapterId:i:2
		//3100  		"c":[{"attackerType":3,"attackerId":1,"cmdType":1,"cmdId":0,"targetId":1000128},{"attackerType":3,"attackerId":2,"cmdType":1,"cmdId":0,"targetId":1000128}]
			//c:s:3,1,1,0,1000128;3,2,1,0,1000128
//		System.out.println((int)(41/(100+.0) * 100/20));
//		System.out.println((int)(41/(100+.0) * 5));

//		List<Effect> effectList = Lists.newArrayList();
//		Effect ee = new Effect();
//		Effect ee2 = new Effect();
//		effectList.add(ee);
//		effectList.add(ee2);
//		effectList.remove(ee);
//		System.out.println(ee);
//		System.out.println(ee2);
//		System.out.println(effectList.get(0));
//		RetVO vo = new RetVO();
//		vo.addData("questId", "questId");
//		vo.addData("reward", "reward");
//		for(Object oo : vo.data.values()){
//			System.out.println(oo);
//		}
		
		System.out.println(System.getProperty("java.library.path"));
		System.out.println(85.4/121f);
		AtomicInteger idx = new AtomicInteger(0);
		System.out.println(idx.incrementAndGet());

	}

}
