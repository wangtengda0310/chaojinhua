package com.igame.core.data;


import com.igame.work.activity.ActivityConfig;
import com.igame.work.activity.ActivityDataManager;
import com.igame.work.activity.sign.SignConfig;
import com.igame.work.checkpoint.GuanQiaDataManager;
import com.igame.work.checkpoint.data.*;
import com.igame.work.fight.FightDataManager;
import com.igame.work.fight.data.*;
import com.igame.work.item.ItemDataManager;
import com.igame.work.item.data.PropGroupData;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.data.*;
import com.igame.work.quest.QuestDataManager;
import com.igame.work.quest.data.QuestData;
import com.igame.work.shop.ShopDataManager;
import com.igame.work.shop.data.ShopData;
import com.igame.work.shop.data.ShopOutPutData;
import com.igame.work.shop.data.ShopRandomData;
import com.igame.work.shop.data.ShopRandomLvData;
import com.igame.work.turntable.LuckTableDataManager;
import com.igame.work.turntable.data.LuckTableData;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.*;

public class DataManager {

	public static void load()
	{

		MonsterDataManager.MONSTER_DATA                 =XmlDataLoader.loadData(MonsterData.class,   "monsterdata.xml" );//怪物信息
		PlayerDataManager.PlayerLvData                  =XmlDataLoader.loadData(PlayerLvData.class,   "playerlevel.xml" );//玩家等级模板
		MonsterDataManager.MonsterLvData                =XmlDataLoader.loadData(MonsterLvData.class,   "monsterlevel.xml" );//怪物等级模板
		PlayerDataManager.ItemData                      =XmlDataLoader.loadData(ItemData.class,   "itemdata.xml" );//道具模板
		MonsterDataManager.MonsterEvolutionData         =XmlDataLoader.loadData(MonsterEvolutionData.class,   "monsterevolution.xml" );//怪物进化
		MonsterDataManager.MonsterBreakData             =XmlDataLoader.loadData(MonsterBreakData.class,   "monsterbreak.xml" );//怪物基因突破
		GuanQiaDataManager.CheckPointData               =XmlDataLoader.loadData(CheckPointData.class,   "chapterdata.xml" );
		GuanQiaDataManager.DropData                     =XmlDataLoader.loadData(DropData.class,   "dropdata.xml" );
		MonsterDataManager.NewMonsterData               =XmlDataLoader.loadData(NewMonsterData.class,   "newmonster.xml" );
		ItemDataManager.PropGroupData                   =XmlDataLoader.loadData(PropGroupData.class,   "propgroup.xml" );
		GuanQiaDataManager.TangSuoData                  =XmlDataLoader.loadData(TangSuoData.class,   "questteam.xml" );
		GuanQiaDataManager.WordEventData                =XmlDataLoader.loadData(WordEventData.class,   "worldevent.xml" );
		MonsterDataManager.TongDiaoData                 =XmlDataLoader.loadData(TongDiaoData.class,   "suitdata.xml" );
		MonsterDataManager.TongHuaData                  =XmlDataLoader.loadData(TongHuaData.class,   "strengthenlevel.xml" );
		MonsterDataManager.StrengthenmonsterData        =XmlDataLoader.loadData(StrengthenmonsterData.class,   "strengthenmonster.xml" );
		MonsterDataManager.StrengthenplaceData          =XmlDataLoader.loadData(StrengthenplaceData.class,   "strengthenplace.xml" );
		MonsterDataManager.StrengthenrewardData         =XmlDataLoader.loadData(StrengthenrewardData.class,   "strengthenreward.xml" );
		FightDataManager.GodsData                       =XmlDataLoader.loadData(GodsData.class,   "godsdata.xml" );
		MonsterDataManager.StrengthenRouteData          =XmlDataLoader.loadData(StrengthenRouteData.class,   "strengthenroute.xml" );
		MonsterDataManager.ExchangeData                 =XmlDataLoader.loadData(ExchangeData.class,   "exchangedata.xml" );
		MonsterDataManager.PokedexData                  =XmlDataLoader.loadData(PokedexData.class,   "pokedexdata.xml" );
		PlayerDataManager.DrawLevelData                 =XmlDataLoader.loadData(DrawLevelData.class,   "drawlevel.xml" );
		PlayerDataManager.DrawdataData                  =XmlDataLoader.loadData(DrawdataData.class,   "drawdata.xml" );
		PlayerDataManager.DrawrewardData                =XmlDataLoader.loadData(DrawrewardData.class,   "drawreward.xml" );
		FightDataManager.SkillData                      =XmlDataLoader.loadData(SkillData.class,   "skilldata.xml" );
		FightDataManager.SkillLvData                    =XmlDataLoader.loadData(SkillLvData.class,   "skillexp.xml" );
		FightDataManager.EffectData                     =XmlDataLoader.loadData(EffectData.class,   "effectdata.xml" );
		QuestDataManager.QuestData                      =XmlDataLoader.loadData(QuestData.class,   "questdata.xml" );
		GuanQiaDataManager.TrialData                    =XmlDataLoader.loadData(TrialData.class,   "trialdata.xml" );
		GuanQiaDataManager.EndlessData                  =XmlDataLoader.loadData(EndlessData.class,   "endlessdata.xml" );
		GuanQiaDataManager.FateData                     =XmlDataLoader.loadData(FateData.class,   "destinydata.xml" );
		GuanQiaDataManager.DestinyData                  =XmlDataLoader.loadData(DestinyData.class,   "destinyrate.xml" );
		ShopDataManager.shopData                        =XmlDataLoader.loadData(ShopData.class,   "shopdata.xml" );
		ShopDataManager.shopOutPutData                  =XmlDataLoader.loadData(ShopOutPutData.class,   "shopoutput.xml" );
		ShopDataManager.shopRandomData                  =XmlDataLoader.loadData(ShopRandomData.class,   "shoprandom.xml" );
		ShopDataManager.shopRandomLvData                =XmlDataLoader.loadData(ShopRandomLvData.class,   "shoprandom_lv.xml" );
		PlayerDataManager.headData                    	=XmlDataLoader.loadData(HeadData.class,   "headdata.xml" );
		PlayerDataManager.headFrameData                 =XmlDataLoader.loadData(HeadFrameData.class,   "headframe.xml" );
		PlayerDataManager.ArenaData                     =XmlDataLoader.loadData(ArenaData.class,   "arenadata.xml" );
		GuanQiaDataManager.runData                      =XmlDataLoader.loadData(RunData.class,   "rundata.xml" );
		GuanQiaDataManager.runTypeData                  =XmlDataLoader.loadData(RunTypeData.class,   "runtype.xml" );
		GuanQiaDataManager.runRewardData                =XmlDataLoader.loadData(RunRewardData.class,   "runreward.xml" );
		GuanQiaDataManager.runBattlerewardData          =XmlDataLoader.loadData(RunBattlerewardData.class,   "runbattlereward.xml" );
		MonsterDataManager.monsterGroupData             =XmlDataLoader.loadData(MonsterGroupData.class,   "monstergroup.xml" );
		PlayerDataManager.itemGroupData                 =XmlDataLoader.loadData(ItemGroupData.class,   "itemgroup.xml" );
		PlayerDataManager.vipPackData                   =XmlDataLoader.loadData(VipPackData.class,   "vippack.xml" );
		PlayerDataManager.vipLevelData                  =XmlDataLoader.loadData(VipLevelData.class,   "viplevel.xml" );
		PlayerDataManager.vipData             			=XmlDataLoader.loadData(VipData.class,   "vipdata.xml" );
		FightDataManager.GodsEffectData                 =XmlDataLoader.loadData(GodsEffectData.class,   "godseffect.xml" );
		LuckTableDataManager.luckTableData              =XmlDataLoader.loadData(LuckTableData.class,   "lucktable.xml" );
		ActivityDataManager.signConfig                  =XmlDataLoader.loadData(SignConfig.class,   "signreward.xml" );
		ActivityDataManager.activityConfig              =XmlDataLoader.loadData(ActivityConfig.class,   "activity.xml" );

	}

}
