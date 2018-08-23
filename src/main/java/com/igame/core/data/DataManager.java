package com.igame.core.data;


import com.igame.work.activity.ActivityConfig;
import com.igame.work.activity.ActivityDataManager;
import com.igame.work.activity.sign.SignConfig;
import com.igame.work.checkpoint.baozouShike.BaozouShikeDataManager;
import com.igame.work.checkpoint.guanqia.GuanQiaDataManager;
import com.igame.work.checkpoint.baozouShike.data.RunBattlerewardData;
import com.igame.work.checkpoint.baozouShike.data.RunData;
import com.igame.work.checkpoint.baozouShike.data.RunRewardData;
import com.igame.work.checkpoint.baozouShike.data.RunTypeData;
import com.igame.work.checkpoint.guanqia.data.*;
import com.igame.work.checkpoint.mingyunZhiMen.data.DestinyData;
import com.igame.work.checkpoint.mingyunZhiMen.data.FateData;
import com.igame.work.checkpoint.mingyunZhiMen.MingyunZhiMenDataManager;
import com.igame.work.checkpoint.tansuo.TansuoData;
import com.igame.work.checkpoint.tansuo.TansuoDataManager;
import com.igame.work.checkpoint.worldEvent.WorldEventData;
import com.igame.work.checkpoint.worldEvent.WorldEventDataManager;
import com.igame.work.checkpoint.wujinZhiSen.EndlessData;
import com.igame.work.checkpoint.wujinZhiSen.WujinZhiSenDataManager;
import com.igame.work.checkpoint.xingheZhiYan.TrialData;
import com.igame.work.checkpoint.xingheZhiYan.XingheZhiYanDataManager;
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

	public static void load(String resourceFolder)
	{
		ClassXmlDataLoader loader = new ClassXmlDataLoader(resourceFolder);

		PlayerDataManager.DrawLevelData                 =loader.loadData(DrawLevelData.class,   "drawlevel.xml" );
		PlayerDataManager.DrawdataData                  =loader.loadData(DrawdataData.class,   "drawdata.xml" );
		PlayerDataManager.DrawrewardData                =loader.loadData(DrawrewardData.class,   "drawreward.xml" );
		PlayerDataManager.PlayerLvData                  =loader.loadData(PlayerLvData.class,   "playerlevel.xml" );//玩家等级模板
		PlayerDataManager.ItemData                      =loader.loadData(ItemData.class,   "itemdata.xml" );//道具模板
		PlayerDataManager.headData                    	=loader.loadData(HeadData.class,   "headdata.xml" );
		PlayerDataManager.headFrameData                 =loader.loadData(HeadFrameData.class,   "headframe.xml" );
		PlayerDataManager.ArenaData                     =loader.loadData(ArenaData.class,   "arenadata.xml" );
		PlayerDataManager.itemGroupData                 =loader.loadData(ItemGroupData.class,   "itemgroup.xml" );
		PlayerDataManager.vipPackData                   =loader.loadData(VipPackData.class,   "vippack.xml" );
		PlayerDataManager.vipLevelData                  =loader.loadData(VipLevelData.class,   "viplevel.xml" );
		PlayerDataManager.vipData             			=loader.loadData(VipData.class,   "vipdata.xml" );

		MonsterDataManager.MONSTER_DATA                 =loader.loadData(MonsterData.class,   "monsterdata.xml" );//怪物信息
		MonsterDataManager.MonsterLvData                =loader.loadData(MonsterLvData.class,   "monsterlevel.xml" );//怪物等级模板
		MonsterDataManager.MonsterEvolutionData         =loader.loadData(MonsterEvolutionData.class,   "monsterevolution.xml" );//怪物进化
		MonsterDataManager.MonsterBreakData             =loader.loadData(MonsterBreakData.class,   "monsterbreak.xml" );//怪物基因突破
		MonsterDataManager.NewMonsterData               =loader.loadData(NewMonsterData.class,   "newmonster.xml" );
		MonsterDataManager.TongDiaoData                 =loader.loadData(TongDiaoData.class,   "suitdata.xml" );
		MonsterDataManager.TongHuaData                  =loader.loadData(TongHuaData.class,   "strengthenlevel.xml" );
		MonsterDataManager.StrengthenmonsterData        =loader.loadData(StrengthenmonsterData.class,   "strengthenmonster.xml" );
		MonsterDataManager.StrengthenplaceData          =loader.loadData(StrengthenplaceData.class,   "strengthenplace.xml" );
		MonsterDataManager.StrengthenrewardData         =loader.loadData(StrengthenrewardData.class,   "strengthenreward.xml" );
		MonsterDataManager.StrengthenRouteData          =loader.loadData(StrengthenRouteData.class,   "strengthenroute.xml" );
		MonsterDataManager.ExchangeData                 =loader.loadData(ExchangeData.class,   "exchangedata.xml" );
		MonsterDataManager.PokedexData                  =loader.loadData(PokedexData.class,   "pokedexdata.xml" );
		MonsterDataManager.monsterGroupData             =loader.loadData(MonsterGroupData.class,   "monstergroup.xml" );

		FightDataManager.GodsData                       =loader.loadData(GodsData.class,   "godsdata.xml" );
		FightDataManager.SkillData                      =loader.loadData(SkillData.class,   "skilldata.xml" );
		FightDataManager.SkillLvData                    =loader.loadData(SkillLvData.class,   "skillexp.xml" );
		FightDataManager.EffectData                     =loader.loadData(EffectData.class,   "effectdata.xml" );
		FightDataManager.GodsEffectData                 =loader.loadData(GodsEffectData.class,   "godseffect.xml" );

		QuestDataManager.QuestData                      =loader.loadData(QuestData.class,   "questdata.xml" );

		ShopDataManager.shopData                        =loader.loadData(ShopData.class,   "shopdata.xml" );
		ShopDataManager.shopOutPutData                  =loader.loadData(ShopOutPutData.class,   "shopoutput.xml" );
		ShopDataManager.shopRandomData                  =loader.loadData(ShopRandomData.class,   "shoprandom.xml" );
		ShopDataManager.shopRandomLvData                =loader.loadData(ShopRandomLvData.class,   "shoprandom_lv.xml" );

		GuanQiaDataManager.CheckPointData               =loader.loadData(CheckPointData.class,   "chapterdata.xml" );
		GuanQiaDataManager.DropData                     =loader.loadData(DropData.class,   "dropdata.xml" );

		XingheZhiYanDataManager.TrialData                    =loader.loadData(TrialData.class,   "trialdata.xml" );

		WujinZhiSenDataManager.EndlessData                  =loader.loadData(EndlessData.class,   "endlessdata.xml" );

		MingyunZhiMenDataManager.FateData                     =loader.loadData(FateData.class,   "destinydata.xml" );
		MingyunZhiMenDataManager.DestinyData                  =loader.loadData(DestinyData.class,   "destinyrate.xml" );

		BaozouShikeDataManager.runData                      =loader.loadData(RunData.class,   "rundata.xml" );
		BaozouShikeDataManager.runTypeData                  =loader.loadData(RunTypeData.class,   "runtype.xml" );
		BaozouShikeDataManager.runRewardData                =loader.loadData(RunRewardData.class,   "runreward.xml" );
		BaozouShikeDataManager.runBattlerewardData          =loader.loadData(RunBattlerewardData.class,   "runbattlereward.xml" );

		TansuoDataManager.TansuoData =loader.loadData(TansuoData.class,   "questteam.xml" );

		WorldEventDataManager.WorldEventData =loader.loadData(WorldEventData.class,   "worldevent.xml" );

		ItemDataManager.PropGroupData                   =loader.loadData(PropGroupData.class,   "propgroup.xml" );

		LuckTableDataManager.luckTableData              =loader.loadData(LuckTableData.class,   "lucktable.xml" );

		ActivityDataManager.signConfig                  =loader.loadData(SignConfig.class,   "signreward.xml" );
		ActivityDataManager.activityConfig              =loader.loadData(ActivityConfig.class,   "activity.xml" );

	}

}
