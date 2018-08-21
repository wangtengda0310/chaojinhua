package com.igame.core.data;


import com.igame.work.activity.ActivityConfig;
import com.igame.work.activity.sign.SignConfig;

public class DataManager {


	
	/**
	 * 怪物模版
	 */
	public static MonsterData MONSTER_DATA;
	
	/**
	 * 人物等级模板
	 */
	public static PlayerLvData PlayerLvData; 
	
	/**
	 * 怪物等级模板
	 */
	public static MonsterLvData MonsterLvData; 
	
	/**
	 * 道具数据
	 */
	public static ItemData ItemData;
	
	
	/**
	 * 怪物进化
	 */
	public static MonsterEvolutionData MonsterEvolutionData;
	
	/**
	 * 怪物基因突破
	 */
	public static MonsterBreakData MonsterBreakData;
	
	/**
	 * 关卡列表
	 */
	public static CheckPointData CheckPointData;
	
	/**
	 * 掉落包
	 */
	public static  DropData  DropData;
	
	/**
	 * 怪物新生
	 */
	public static  NewMonsterData NewMonsterData;
	
	/**
	 * 装备合成
	 */
	public static  PropGroupData PropGroupData;
	
	/**
	 * 探索
	 */
	public static  TangSuoData TangSuoData;
	
	/**
	 * 世界事件
	 */
	public static  WordEventData WordEventData;
	
	/**
	 * 同调
	 */
	public static  TongDiaoData TongDiaoData;
	
	/**
	 * 同化等级配置
	 */
	public static  TongHuaData TongHuaData;

	/**
	 * 同化怪物配置
	 */
	public static  StrengthenmonsterData StrengthenmonsterData;
	
	/**
	 * 同化节点配置
	 */
	public static  StrengthenplaceData StrengthenplaceData; 
	
	/**
	 * 同化奖励配置
	 */
	public static  StrengthenrewardData StrengthenrewardData; 
	
	/**
	 * 神灵配置
	 */
	public static  GodsData GodsData; 
	
	/**
	 * 资源购买
	 */
	public static  ExchangeData ExchangeData;
	
	/**
	 * 路线配置
	 */
	public static  StrengthenRouteData StrengthenRouteData; 
	
	/**
	 * 过滤
	 */
	public static ChatTabooData CHAT_TABOO_DATA;
	
	
	/**
	 * 图鉴
	 */
	public static PokedexData PokedexData;
	
	
	/**
	 * 造物台等级
	 */
	public static DrawLevelData DrawLevelData;
	
	/**
	 * 造物台数据
	 */
	public static DrawdataData DrawdataData;
	
	/**
	 * 奖励库
	 */
	public static DrawrewardData DrawrewardData;
	
	/**
	 * 技能
	 */
	public static SkillData SkillData;
	
	
	/**
	 * 技能等级数据
	 */
	public static SkillLvData SkillLvData;
	
	/**
	 * buffer
	 */
	public static EffectData EffectData;
	
	/**
	 * 任务成就数据
	 */
	public static QuestData QuestData;
	
	/**
	 * 星河之眼
	 */
	public static TrialData TrialData;
	
	/**
	 * 无尽之森
	 */
	public static EndlessData EndlessData;
	
	/**
	 * 命运之门
	 */
	public static FateData FateData;
	
	/**
	 * 命运之门概率
	 */
	public static DestinyData DestinyData;

	/**
	 * 商店基础信息
	 */
	public static ShopData shopData;

	/**
	 * shoptype = 2 时商品数据
	 */
	public static ShopOutPutData shopOutPutData;

	/**
	 * shoptype = 1 时商品数据
	 */
	public static ShopRandomData shopRandomData;

	/**
	 * 神秘商店等级数据
	 */
	public static ShopRandomLvData shopRandomLvData;

	/**
	 * 头像数据
	 */
	public static HeadData headData;

	/**
	 * 头像框数据
	 */
	public static HeadFrameData headFrameData;
	
	/**
	 * 竞技场数据
	 */
	public static ArenaData ArenaData;

	/**
	 * 暴走时刻怪物数据
	 */
	public static RunData runData;

	/**
	 * 暴走时刻buff数据
	 */
	public static RunTypeData runTypeData;

	/**
	 * 暴走时刻排名奖励
	 */
	public static RunRewardData runRewardData;

	/**
	 * 暴走时刻结束奖励
	 */
	public static RunBattlerewardData runBattlerewardData;

	/**
	 * 怪兽召唤数据
	 */
	public static MonsterGroupData monsterGroupData;

	/**
	 * 道具合成数据
	 */
	public static ItemGroupData itemGroupData;

	/**
	 * vip礼包数据
	 */
	public static VipPackData vipPackData;

	/**
	 * vip等级数据
	 */
	public static VipLevelData vipLevelData;

	/**
	 * vip配置
	 */
	public static VipData vipData;

	/**
	 * 大转盘数据
	 */
	public static LuckTableData luckTableData;
	
	/**
	 * 神灵技能buffer配置
	 */
	public static GodsEffectData GodsEffectData;

	/**
	 * 签到
	 */
	public static SignConfig signConfig;

	/**
	 * 大部分活动共用的配置
	 */
	public static ActivityConfig activityConfig;
	
    private static final DataManager domain = new DataManager();

    public static final DataManager ins() {
        return domain;
    }

	public DataManager()
	{	

		MONSTER_DATA                   =XmlDataLoader.loadData(MonsterData.class,   "monsterdata.xml" );//怪物信息
		PlayerLvData                   =XmlDataLoader.loadData(PlayerLvData.class,   "playerlevel.xml" );//玩家等级模板
		MonsterLvData      			   =XmlDataLoader.loadData(MonsterLvData.class,   "monsterlevel.xml" );//怪物等级模板
		ItemData           			   =XmlDataLoader.loadData(ItemData.class,   "itemdata.xml" );//道具模板
		MonsterEvolutionData           =XmlDataLoader.loadData(MonsterEvolutionData.class,   "monsterevolution.xml" );//怪物进化
		MonsterBreakData               =XmlDataLoader.loadData(MonsterBreakData.class,   "monsterbreak.xml" );//怪物基因突破
		CheckPointData                 =XmlDataLoader.loadData(CheckPointData.class,   "chapterdata.xml" );
		DropData                       =XmlDataLoader.loadData(DropData.class,   "dropdata.xml" );
		NewMonsterData                 =XmlDataLoader.loadData(NewMonsterData.class,   "newmonster.xml" );
		PropGroupData                  =XmlDataLoader.loadData(PropGroupData.class,   "propgroup.xml" );
		TangSuoData                    =XmlDataLoader.loadData(TangSuoData.class,   "questteam.xml" );
		WordEventData                  =XmlDataLoader.loadData(WordEventData.class,   "worldevent.xml" );
		TongDiaoData                   =XmlDataLoader.loadData(TongDiaoData.class,   "suitdata.xml" );
		TongHuaData                    =XmlDataLoader.loadData(TongHuaData.class,   "strengthenlevel.xml" );
		StrengthenmonsterData          =XmlDataLoader.loadData(StrengthenmonsterData.class,   "strengthenmonster.xml" );
		StrengthenplaceData            =XmlDataLoader.loadData(StrengthenplaceData.class,   "strengthenplace.xml" );
		StrengthenrewardData           =XmlDataLoader.loadData(StrengthenrewardData.class,   "strengthenreward.xml" );
		GodsData                       =XmlDataLoader.loadData(GodsData.class,   "godsdata.xml" );
		StrengthenRouteData            =XmlDataLoader.loadData(StrengthenRouteData.class,   "strengthenroute.xml" );
		ExchangeData                   =XmlDataLoader.loadData(ExchangeData.class,   "exchangedata.xml" );
		PokedexData                    =XmlDataLoader.loadData(PokedexData.class,   "pokedexdata.xml" );
		DrawLevelData                  =XmlDataLoader.loadData(DrawLevelData.class,   "drawlevel.xml" );
		DrawdataData                   =XmlDataLoader.loadData(DrawdataData.class,   "drawdata.xml" );
		DrawrewardData                 =XmlDataLoader.loadData(DrawrewardData.class,   "drawreward.xml" );
		SkillData                      =XmlDataLoader.loadData(SkillData.class,   "skilldata.xml" );
		SkillLvData                    =XmlDataLoader.loadData(SkillLvData.class,   "skillexp.xml" );
		EffectData                     =XmlDataLoader.loadData(EffectData.class,   "effectdata.xml" );
		QuestData                      =XmlDataLoader.loadData(QuestData.class,   "questdata.xml" );
		TrialData                      =XmlDataLoader.loadData(TrialData.class,   "trialdata.xml" );
		EndlessData                    =XmlDataLoader.loadData(EndlessData.class,   "endlessdata.xml" );
		FateData                       =XmlDataLoader.loadData(FateData.class,   "destinydata.xml" );
		DestinyData                    =XmlDataLoader.loadData(DestinyData.class,   "destinyrate.xml" );
		shopData                       =XmlDataLoader.loadData(ShopData.class,   "shopdata.xml" );
		shopOutPutData                 =XmlDataLoader.loadData(ShopOutPutData.class,   "shopoutput.xml" );
		shopRandomData                 =XmlDataLoader.loadData(ShopRandomData.class,   "shoprandom.xml" );
		shopRandomLvData               =XmlDataLoader.loadData(ShopRandomLvData.class,   "shoprandom_lv.xml" );
		headData                    	=XmlDataLoader.loadData(HeadData.class,   "headdata.xml" );
		headFrameData                   =XmlDataLoader.loadData(HeadFrameData.class,   "headframe.xml" );
		ArenaData                       =XmlDataLoader.loadData(ArenaData.class,   "arenadata.xml" );
		runData                         =XmlDataLoader.loadData(RunData.class,   "rundata.xml" );
		runTypeData                     =XmlDataLoader.loadData(RunTypeData.class,   "runtype.xml" );
		runRewardData                   =XmlDataLoader.loadData(RunRewardData.class,   "runreward.xml" );
		runBattlerewardData             =XmlDataLoader.loadData(RunBattlerewardData.class,   "runbattlereward.xml" );
		monsterGroupData             	=XmlDataLoader.loadData(MonsterGroupData.class,   "monstergroup.xml" );
		itemGroupData             		=XmlDataLoader.loadData(ItemGroupData.class,   "itemgroup.xml" );
		vipPackData             		=XmlDataLoader.loadData(VipPackData.class,   "vippack.xml" );
		vipLevelData             		=XmlDataLoader.loadData(VipLevelData.class,   "viplevel.xml" );
		vipData             			=XmlDataLoader.loadData(VipData.class,   "vipdata.xml" );
		GodsEffectData                  =XmlDataLoader.loadData(GodsEffectData.class,   "godseffect.xml" );
		luckTableData             		=XmlDataLoader.loadData(LuckTableData.class,   "lucktable.xml" );
		signConfig             			=XmlDataLoader.loadData(SignConfig.class,   "signreward.xml" );
		activityConfig             		=XmlDataLoader.loadData(ActivityConfig.class,   "activity.xml" );

	}

}
