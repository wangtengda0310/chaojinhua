package com.igame.work.monster.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.di.LoadXml;
import com.igame.core.handler.RetVO;
import com.igame.core.log.ExceptionLog;
import com.igame.core.log.GoldLog;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.item.dto.Item;
import com.igame.work.item.service.ItemService;
import com.igame.work.monster.dao.MonsterDAO;
import com.igame.work.monster.data.*;
import com.igame.work.monster.dto.Effect;
import com.igame.work.monster.dto.JiyinType;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.data.ItemTemplate;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.TongAddDto;
import com.igame.work.user.load.ResourceService;

import java.util.*;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterService implements ISFSModule {
	/**
	 * 资源购买
	 */
	@LoadXml("exchangedata.xml") public ExchangeData exchangeData;
	/**
	 * 怪物基因突破
	 */
	@LoadXml("monster_break.xml" )public MonsterBreakData monsterBreakData;
	/**
	 * 怪物模版
	 */
	@LoadXml("monsterdata.xml")public MonsterData MONSTER_DATA;
	/**
	 * 怪物模版
	 */
	@LoadXml("PVEmonster.xml")public PVEMonsterData PVE_MONSTER;
	/**
	 * 怪物模版
	 */
	@LoadXml("PVEmonsterset.xml")public MonstersetData MONSTERSET_DATA;
	/**
	 * 怪物进化
	 */
	@LoadXml("monster_evolution.xml")public MonsterEvolutionData monsterEvolutionData;
	/**
	 * 怪兽召唤数据
	 */
	@LoadXml("monstergroup.xml") public MonsterGroupData monsterGroupData;
	/**
	 * 怪物等级模板
	 */
	@LoadXml("monsterlv.xml")public MonsterLvData monsterLvData;
	/**
	 * 怪物新生
	 */
	@LoadXml("newmonster.xml") public NewMonsterData newMonsterData;
	/**
	 * 图鉴
	 */
	@LoadXml("pokedex.xml") public PokedexData pokedexData;
	/**
	 * 同化怪物配置
	 */
	@LoadXml("strengthenmonster.xml") public StrengthenmonsterData strengthenmonsterData;
	/**
	 * 同化节点配置
	 */
	@LoadXml("strengthenplace.xml") public StrengthenplaceData strengthenplaceData;
	/**
	 * 同化奖励配置
	 */
	@LoadXml("strengthenreward.xml") public StrengthenrewardData strengthenrewardData;
	/**
	 * 路线配置
	 */
	@LoadXml("strengthen_route.xml") public StrengthenRouteData strengthenRouteData;
	/**
	 * 同调
	 */
	@LoadXml("suitdata.xml") public TongDiaoData tongDiaoData;
	/**
	 * 同化等级配置
	 */
	@LoadXml("strengthenlevel.xml") public TongHuaData tongHuaData;

	@Inject private ItemService itemService;
	@Inject private MonsterDAO monsterDAO;
	@Inject private ResourceService resourceService;
	@Inject private ComputeFightService computeFightService;

	public int monsterEV(Player player,long objectId,int nextObject){
		
		int ret = 0;
		Monster mm = player.getMonsters().get(objectId);
		if(mm == null){
			return ErrorCode.MONSTER_NOT;//玩家没有此怪物
		}
		MonsterEvolutionTemplate mt = monsterEvolutionData.getTemplate(mm.getMonsterId());
		if(mt == null){
			return ErrorCode.MONSTER_NOT;//玩家没有此怪物
		}
		if(MyUtil.isNullOrEmpty(mt.getMonsterObject())){
			return ErrorCode.MONSTER_EV_MAX;//已进化到最大品级。
		}
		if(!mt.getMonsterObject().contains(String.valueOf(nextObject))
				|| MONSTER_DATA.getMonsterTemplate(nextObject) == null){
			return ErrorCode.NOT_NEXT_OBJECT;//无法进化到此品质
		}
		if(mm.getLevel() < mt.getLevelLimit()){
			return ErrorCode.LEVEL_NOT_ENOUGH;//等级不够。
		}
		if(player.getGold() < mt.getGold()){
			return ErrorCode.GOLD_NOT_ENOUGH;//金币不足。
		}
		if(!MyUtil.isNullOrEmpty(mt.getItem())){
			String[] items = mt.getItem().split(";");
			for(String item :items){
				String[] it = item.split(",");
				if(player.getItems().get(Integer.parseInt(it[1])) == null 
						|| player.getItems().get(Integer.parseInt(it[1])).getUsableCount(-1) < Integer.parseInt(it[2])){
					return ErrorCode.ITEM_NOT_ENOUGH;//道具不足
				}
			} 
		}		
		
		mm.setMonsterId(nextObject);
		MonsterTemplate nt = MONSTER_DATA.getMonsterTemplate(nextObject);
		if(nt.getSkill() != null){//解锁新的技能
			String[] skills = nt.getSkill().split(",");
			if(skills != null){
				for(String skill : skills){
					if(!mm.containSkill(Integer.parseInt(skill))){
						mm.putSkill(Integer.parseInt(skill), 1);
					}
				}
			}
		}
//		initSkillString(mm);
//		mm.setHp(DataManager.MONSTER_DATA.getMonsterTemplate(nextObject).getMonster_hp() * 10);
		reCalculate(player, mm.getMonsterId(), mm,true);
		mm.setDtate(2);
		
		reCalMonsterExtPre(player, true);
		
		resourceService.addGold(player, 0-mt.getGold());
		
		List<Item> chitems = Lists.newArrayList();
		
		if(!MyUtil.isNullOrEmpty(mt.getItem())){
			String[] items = mt.getItem().split(";");
			for(String item :items){
				String[] it = item.split(",");
				Item ii = player.getItems().get(Integer.parseInt(it[1]));
				ii.setCount(ii.getCount() - Integer.parseInt(it[2]));
				ii.setDtate(2);
				if(ii.getCount() <= 0){
					ii.setCount(0);
					ii.setDtate(3);
					player.getRemoves().add(player.getItems().remove(ii.getItemId()));
				}
				chitems.add(ii);
			} 
		}
		
		List<Monster> mmos = Lists.newArrayList();
		mmos.add(mm);
		RetVO vo1 = new RetVO();		
    	vo1.addData("monsters", mmos);
		MessageUtil.sendMessageToPlayer(player, MProtrol.MONSTER_UPDATE, vo1);

		if(!chitems.isEmpty()){
			RetVO vo = new RetVO();
    		vo.addData("items", chitems);
    		MessageUtil.sendMessageToPlayer(player, MProtrol.ITEM_UPDATE, vo);
		}
		
    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
    			+"#act:monsterEV" + "#objectId:" + objectId+"#nextObject:"+nextObject);
		
		return ret;
	}
	
	/**
	 * 计算图鉴增加属性
	 */
	public void reCalMonsterExtPre(Player player,boolean reCalMonsterTotalValue){
		
		for(Monster mm : player.getMonsters().values()){
			mm.hpExt = 0;
			mm.attackExt = 0;
			mm.damgeRedExt = 0;
			mm.damgeAddExt = 0;
		}
		
		for(PokedexdataTemplate pt  : pokedexData.getAll()){
			if(!MyUtil.isNullOrEmpty(pt.getTakeMonster()) && !MyUtil.isNullOrEmpty(pt.getEffectId()) && !MyUtil.isNullOrEmpty(pt.getValue())){
				String[] takeMonster = pt.getTakeMonster().split(",");
				List<Monster> plus100 = getMonster(player,pt.getPlus100());//加100%属性的怪物
				List<Monster> plus50 = getMonster(player,pt.getPlus50());//加50%属性的怪物
				List<Monster> plus25 = getMonster(player,pt.getPlus25());//加25%属性的怪物
				for(int i = 0;i < takeMonster.length;i++){
					int mid = Integer.parseInt(takeMonster[i]);
					if(containsMonster(player,mid)){//包含此怪物
						String[] effects = pt.getEffectId().split(",");
						String[] values = pt.getValue().split(",");
						switch (Integer.parseInt(effects[i])){
							case 103:
								for(Monster mm : plus100){
									mm.hpExt += Integer.parseInt(values[i]);
								}
								for(Monster mm : plus50){
									mm.hpExt += (int)Math.round(Integer.parseInt(values[i]) * 0.5);
								}
								for(Monster mm : plus25){
									mm.hpExt += (int)Math.round(Integer.parseInt(values[i]) * 0.25);
								}
								break;
							case 106:
								for(Monster mm : plus100){
									mm.attackExt += Integer.parseInt(values[i]);
								}
								for(Monster mm : plus50){
									mm.attackExt += (int)Math.round(Integer.parseInt(values[i]) * 0.5);
								}
								for(Monster mm : plus25){
									mm.attackExt += (int)Math.round(Integer.parseInt(values[i]) * 0.25);
								}
								break;
							case 102:
								for(Monster mm : plus100){
									mm.damgeRedExt += Integer.parseInt(values[i]);
								}
								for(Monster mm : plus50){
									mm.damgeRedExt += (int)Math.round(Integer.parseInt(values[i]) * 0.5);
								}
								for(Monster mm : plus25){
									mm.damgeRedExt += (int)Math.round(Integer.parseInt(values[i]) * 0.25);
								}
								break;
							case 112:
								for(Monster mm : plus100){
									mm.damgeAddExt += Integer.parseInt(values[i]);
								}
								for(Monster mm : plus50){
									mm.damgeAddExt += (int)Math.round(Integer.parseInt(values[i]) * 0.5);
								}
								for(Monster mm : plus25){
									mm.damgeAddExt += (int)Math.round(Integer.parseInt(values[i]) * 0.25);
								}
								break;
							default:
								break;
						}
					}
				}
			}
		}
		if(reCalMonsterTotalValue){
			for(Monster mm : player.getMonsters().values()){
				reCalculate(player, mm.getMonsterId(), mm,true);
			}
		}
		
		
	}
	
	private boolean containsMonster(Player player, int mid){
		for(Monster mm : player.getMonsters().values()){
			if(mm.getMonsterId() == mid){
				return true;
			}
		}
		return false;
	}
	
	
	private List<Monster> getMonster(Player player, String mids){
		List<Monster> mms = Lists.newArrayList();
		if(!MyUtil.isNullOrEmpty(mids)){
			Set<Integer> set = Sets.newHashSet();
			for(String mid : mids.split(",")){
				set.add(Integer.parseInt(mid));
			}
			for(Monster mm : player.getMonsters().values()){
				if(set.contains(mm.getMonsterId())){
					mms.add(mm);
				}
				
			}
		}

		return mms;
	}

	public Map<Long, Monster> getMonsterByPlayerFromDb(Player player) {
		Map<Long, Monster> monsters = monsterDAO.getMonsterByPlayer(player, player.getPlayerId());
		monsters.forEach((mid,mm)->{
			MonsterTemplate mt = MONSTER_DATA.getMonsterTemplate(mm.getMonsterId());
			if (mt != null && mt.getSkill() != null) {
				String[] skills = mt.getSkill().split(",");
				if (mm.getSkillMap().isEmpty()) {
					mm.setDtate(2);
				}
				for (String skill : skills) {
					if (!mm.getSkillMap().containsKey(Integer.parseInt(skill))) {
						mm.getSkillMap().put(Integer.parseInt(skill), 1);
					}
				}
				List<Integer> temp = Lists.newArrayList();
				for (Integer skill : mm.getSkillMap().keySet()) {
					if (!mt.getSkill().contains(String.valueOf(skill))) {
						temp.add(skill);
					}
				}
				for (int rem : temp) {
					mm.removeSkill(rem);
				}
			}
//			initSkillString(mm);//初始化技能列表字符串
			reCalculate(player,mm.getMonsterId(), mm, true);//计算值
		});

		return monsters;
	}

	public void loadPlayer(Player player) {
		player.setMonsters(getMonsterByPlayerFromDb(player));
	}

	// todo merge multi monsters notify
	public void meet(Player player, int monsterId) {
        if(player.getMeetM().contains(monsterId)){
            return;
        }
        MessageUtil.notifyMeetM(player);
    }
	public boolean isChange(Player player, String monsters) {
	    boolean change = false;
		for(String id :monsters.split(",")){
			int mid = Integer.parseInt(id);
			if(MONSTER_DATA.getMonsterTemplate(mid) != null && !player.getMeetM().contains(mid)){
				player.getMeetM().add(mid);
				change = true;
			}
		}
		return change;
	}

	//重新计算怪物身上的符文增加属性，包含装备同调加成
	public void reCalEquip(int monsterId, Monster monster){

		List<Effect> effes = Lists.newArrayList();
		List<Effect> effeAdd = Lists.newArrayList();
		String[] is = monster.equip.split(",");
		int total = 0;
		if(is.length != 0){
			for(String s : is){
				Effect ef = getEffectByItem(Integer.parseInt(s), true);
				if(ef != null){
					effes.add(ef);
				}
				if(!"0".equals(s) && !"-1".equals(s)){
					total++;
				}
			}
		}

		monster.suitLv = 0;
		if(total >= 4){//触发同调
			List<TongDiaoTemplate> lts = Lists.newArrayList();
			for(String s : is){
				TongDiaoTemplate tt = tongDiaoData.getByItemId(s);
				if(tt != null){
					lts.add(tt);
				}
			}
			if(!lts.isEmpty()){
				lts.sort(Comparator.comparingInt(TongDiaoTemplate::getSuitLv));
				TongDiaoTemplate index = lts.get(0);
				monster.suitLv = index.getSuitLv();
				String[] effect_id = index.getEffectId().split(",");
				String[] ability_up = index.getAbilityUp().split(",");
				for(int i = 0;i < effect_id.length;i++){
					effeAdd.add(new Effect(i,Integer.parseInt(effect_id[i]),Float.parseFloat(ability_up[i])));
				}
			}

		}


		MonsterTemplate mt = MONSTER_DATA.getMonsterTemplate(monsterId);
		if(mt != null){
			for(Effect vl : effes){
				switch (vl.getEffectId()){
					case 103: //体力+-
						monster.hp += vl.getValue();
						monster.hpInit += vl.getValue();
						break;
					case 104: //体力%+-
						monster.hp += mt.getMonster_hp() * vl.getValue()/100.0;
						monster.hpInit += mt.getMonster_hp() * vl.getValue()/100.0;
						break;
					case 105: //攻击力%
						monster.attack += mt.getMonster_atk() * vl.getValue()/100.0;
						break;
					case 106: //攻击力+-
						monster.attack += vl.getValue();
						break;
					case 109: //攻击速度%
						monster.ias += mt.getMonster_ias() * vl.getValue()/100.0;
						break;
					case 110: //攻击速度+-
						monster.ias += vl.getValue();
						break;
					case 126: //生效范围%
						break;
					case 127: //移动速度%+-
						monster.speed += mt.getMonster_speed() * vl.getValue()/100.0;
						break;
					case 128: //移动速度+-
						monster.speed += vl.getValue();
						break;
					case 133: //被击退%
						break;
					case 140: //攻击力%+-（仅普通攻击有效）
						monster.attack += mt.getMonster_atk() * vl.getValue()/100.0;
						break;
					case 143: //143.怪物经验增加X点
						monster.expAadd += vl.getValue();
						break;
					default:
						break;
				}
			}

			//同调
			for(Effect vl : effeAdd){
				switch (vl.getEffectId()){
					case 104: //体力%+-
						monster.hp += mt.getMonster_hp() * vl.getValue()/100.0;
						monster.hpInit += mt.getMonster_hp() * vl.getValue()/100.0;
						break;
					case 105: //攻击力%
						monster.attack += mt.getMonster_atk() * vl.getValue()/100.0;
						break;
					default:
						break;
				}
			}
		}


	}

	private Effect getEffectByItem(int item, boolean equip){


		ItemTemplate it = itemService.itemData.getTemplate(item);
		if(it != null && it.getEffect() != 0){
			if(equip && it.getItemType() != 1){
				return null;
			}
			return new Effect(0,it.getEffect(),it.getValue());
		}

		return null;

	}


	//重新计算怪物各项属性  包含模板、等级、突破、同化、符文系统加成、图鉴增加
	public void reCalculate(Player player,int monsterId,Monster monster, boolean dbHp){

		MonsterTemplate mt = MONSTER_DATA.getMonsterTemplate(monsterId);
		if(mt != null){

			String[] vrl = monster.breaklv.split(",");
			if(vrl.length != 0){
				for(String vl : vrl){
					switch (vl){
						case JiyinType.TYPE_001: //攻击+30
							monster.attack += 30;
							break;
						case JiyinType.TYPE_002: //体力+90
							monster.hp += 90;
							monster.hpInit += 90;
							break;
						case JiyinType.TYPE_003: //霸气+20
							monster.repel += 20;
							break;
						case JiyinType.TYPE_004: //移速+20
							monster.speed += 20;
							break;
						case JiyinType.TYPE_005: //攻击+40 体力-30
							monster.attack += 40;
							monster.hp -= 30;
							monster.hpInit -= 30;
							break;
						case JiyinType.TYPE_006: //体力+120 攻击-10
							monster.hp += 120;
							monster.hpInit += 120;
							monster.attack -= 10;
							break;
						case JiyinType.TYPE_007: //霸气+10 移速+10
							monster.repel += 10;
							monster.speed += 10;
							break;
						case JiyinType.TYPE_008: //攻击+15 霸气+10
							monster.attack += 15;
							monster.repel += 10;
							break;
						case JiyinType.TYPE_009: //攻击+15 移速+10
							monster.attack += 15;
							monster.speed += 10;
							break;
						case JiyinType.TYPE_010: //体力+45 霸气+10
							monster.hp += 45;
							monster.hpInit += 45;
							monster.repel += 10;
							break;
						case JiyinType.TYPE_011: //体力+45 移速+10
							monster.hp += 45;
							monster.hpInit += 45;
							monster.speed += 10;
							break;
						case JiyinType.TYPE_017: //射程+15%
							monster.rng += mt.getMonster_rng() * 0.15;
							break;
						default:
							break;
					}
				}
			}
			if(player != null){
				TongAddDto tad = player.getTongAdd();
				if(tad != null){
					if(dbHp){
						monster.hp += tad.getHpAdd();
						monster.hp += mt.getMonster_hp() * tad.getHpAddPer()/100.0;
					}
					monster.hpInit += tad.getHpAdd();
					monster.hpInit += mt.getMonster_hp() * tad.getHpAddPer()/100.0;
					monster.attack += tad.getAttackAdd();
					monster.attack += mt.getMonster_atk() * tad.getAttackAddPer()/100.0;
					monster.damgeRed += tad.getDamgeRed();
					monster.repel += mt.getMonster_repel() * tad.getRepelAddPer()/100.0;
					monster.repeled += tad.getRepeledAddPer();
				}
			}

		}else{
			ExceptionLog.error("get MonsterTemplate null,monsterId:" + monsterId);
		}
		reCalEquip(monsterId, monster);
		monster.reCalExtValue();
		//reCalFightValue();
		computeFightService.computeMonsterFight(monster);
	}

    public void afterPlayerLogin(Player player) {
        reCalMonsterExtPre(player,true);//计算图鉴增加属性
    }

    public List<Optional<Monster>> createMonsterOfAll(int monstersetId) {
	    // 这里怪物跟位置相关 用null的话外面空指针和position不好处理
        MonstersetTemplate monstersetTemplate = MONSTERSET_DATA.getMonsterTemplate(monstersetId);
        String monsterId = monstersetTemplate.getMonsterId();
        List<Optional<Monster>> result = new LinkedList<>();
        for (String mid : monsterId.split("\\|")) {
        	if("".equals(mid) || "0".equals(mid)) {
        		result.add(Optional.ofNullable(null));
			} else {
                PVEMonsterTemplate monsterTemplate = PVE_MONSTER.getMonsterTemplate(Integer.parseInt(mid));
                result.add(Optional.of(mapTemplateToMonster(monsterTemplate, Integer.parseInt(mid))));
            }
        }
        return result;
    }

    public List<MatchMonsterDto> createMonsterDtoOfAll(int monstersetId) {
        List<Optional<Monster>> monsterOfAll = createMonsterOfAll(monstersetId);
        List<MatchMonsterDto> result = new LinkedList<>();
        for (int index = 0; index< monsterOfAll.size(); index++) {
            Optional<Monster> m = monsterOfAll.get(index);
            if(m.isPresent()) {
                MatchMonsterDto dto = m.get().toMatchMonsterDto();
                dto.setLocation(index);
                result.add(dto);
            }
        }
        return result;
    }

    private Monster mapTemplateToMonster(PVEMonsterTemplate t, int id) {
	    MonsterTemplate card = MONSTER_DATA.getMonsterTemplate(t.getMonsterId());   // 策划说这个表可以理解成卡牌或者武将、英雄
        Monster m = new Monster();

        m.setObjectId(id);
        m.setLevel(t.getLvShow());
        m.setMonsterId(id);
        m.setHp(t.getMonsterHp());
        m.setAttack(t.getMonsterAtk());
        m.setSpeed(t.getMonsterSpeed());
        m.setIas(t.getMonsterIas());
        m.setRng(t.getMonsterRng());
        m.setRepel(t.getMonsterRepel());
        m.setBulletSpeed(card.getBulletSpeed());
        m.setHpInit(t.getMonsterHp());
        m.setSkill(card.getSkill());
        m.setEquip(t.getEquip());
        m.setBodySize(t.getSize());
        return m;
    }
}
