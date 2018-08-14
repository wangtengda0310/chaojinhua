package com.igame.core.data;


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
	
    private static final DataManager domain = new DataManager();

    public static final DataManager ins() {
        return domain;
    }

	public DataManager()
	{	

		MONSTER_DATA                   =XmlDataLoader.loadData(MonsterData.class,   "resource/monsterdata.xml" );//怪物信息
		PlayerLvData                   =XmlDataLoader.loadData(PlayerLvData.class,   "resource/playerlevel.xml" );//玩家等级模板
		MonsterLvData      			   =XmlDataLoader.loadData(MonsterLvData.class,   "resource/monsterlevel.xml" );//怪物等级模板
		ItemData           			   =XmlDataLoader.loadData(ItemData.class,   "resource/itemdata.xml" );//道具模板
		MonsterEvolutionData           =XmlDataLoader.loadData(MonsterEvolutionData.class,   "resource/monsterevolution.xml" );//怪物进化
		MonsterBreakData               =XmlDataLoader.loadData(MonsterBreakData.class,   "resource/monsterbreak.xml" );//怪物基因突破
		CheckPointData                 =XmlDataLoader.loadData(CheckPointData.class,   "resource/chapterdata.xml" );
		DropData                       =XmlDataLoader.loadData(DropData.class,   "resource/dropdata.xml" );
		NewMonsterData                 =XmlDataLoader.loadData(NewMonsterData.class,   "resource/newmonster.xml" );
		PropGroupData                  =XmlDataLoader.loadData(PropGroupData.class,   "resource/propgroup.xml" );
		TangSuoData                    =XmlDataLoader.loadData(TangSuoData.class,   "resource/questteam.xml" );
		WordEventData                  =XmlDataLoader.loadData(WordEventData.class,   "resource/worldevent.xml" );
		TongDiaoData                   =XmlDataLoader.loadData(TongDiaoData.class,   "resource/suitdata.xml" );
		TongHuaData                    =XmlDataLoader.loadData(TongHuaData.class,   "resource/strengthenlevel.xml" );
		StrengthenmonsterData          =XmlDataLoader.loadData(StrengthenmonsterData.class,   "resource/strengthenmonster.xml" );
		StrengthenplaceData            =XmlDataLoader.loadData(StrengthenplaceData.class,   "resource/strengthenplace.xml" );
		StrengthenrewardData           =XmlDataLoader.loadData(StrengthenrewardData.class,   "resource/strengthenreward.xml" );
		GodsData                       =XmlDataLoader.loadData(GodsData.class,   "resource/godsdata.xml" );
		StrengthenRouteData            =XmlDataLoader.loadData(StrengthenRouteData.class,   "resource/strengthenroute.xml" );
		ExchangeData                   =XmlDataLoader.loadData(ExchangeData.class,   "resource/exchangedata.xml" );
		PokedexData                    =XmlDataLoader.loadData(PokedexData.class,   "resource/pokedexdata.xml" );
		DrawLevelData                  =XmlDataLoader.loadData(DrawLevelData.class,   "resource/drawlevel.xml" );
		DrawdataData                   =XmlDataLoader.loadData(DrawdataData.class,   "resource/drawdata.xml" );
		DrawrewardData                 =XmlDataLoader.loadData(DrawrewardData.class,   "resource/drawreward.xml" );
		SkillData                      =XmlDataLoader.loadData(SkillData.class,   "resource/skilldata.xml" );
		SkillLvData                    =XmlDataLoader.loadData(SkillLvData.class,   "resource/skillexp.xml" );
		EffectData                     =XmlDataLoader.loadData(EffectData.class,   "resource/effectdata.xml" );
		QuestData                      =XmlDataLoader.loadData(QuestData.class,   "resource/questdata.xml" );
		TrialData                      =XmlDataLoader.loadData(TrialData.class,   "resource/trialdata.xml" );
		EndlessData                    =XmlDataLoader.loadData(EndlessData.class,   "resource/endlessdata.xml" );
		FateData                       =XmlDataLoader.loadData(FateData.class,   "resource/destinydata.xml" );
		DestinyData                    =XmlDataLoader.loadData(DestinyData.class,   "resource/destinyrate.xml" );

		shopData                       =XmlDataLoader.loadData(ShopData.class,   "resource/shopdata.xml" );
		shopOutPutData                 =XmlDataLoader.loadData(ShopOutPutData.class,   "resource/shopoutput.xml" );
		shopRandomData                 =XmlDataLoader.loadData(ShopRandomData.class,   "resource/shoprandom.xml" );
		shopRandomLvData               =XmlDataLoader.loadData(ShopRandomLvData.class,   "resource/shoprandom_lv.xml" );

		headData                    	=XmlDataLoader.loadData(HeadData.class,   "resource/headdata.xml" );
		headFrameData                   =XmlDataLoader.loadData(HeadFrameData.class,   "resource/headframe.xml" );
		ArenaData                       =XmlDataLoader.loadData(ArenaData.class,   "resource/arenadata.xml" );
		runData                         =XmlDataLoader.loadData(RunData.class,   "resource/rundata.xml" );
		runTypeData                     =XmlDataLoader.loadData(RunTypeData.class,   "resource/runtype.xml" );
		runRewardData                   =XmlDataLoader.loadData(RunRewardData.class,   "resource/runreward.xml" );
		runBattlerewardData             =XmlDataLoader.loadData(RunBattlerewardData.class,   "resource/runbattlereward.xml" );

		monsterGroupData             	=XmlDataLoader.loadData(MonsterGroupData.class,   "resource/monstergroup.xml" );
		itemGroupData             		=XmlDataLoader.loadData(ItemGroupData.class,   "resource/itemgroup.xml" );
		vipPackData             		=XmlDataLoader.loadData(VipPackData.class,   "resource/vippack.xml" );
		vipLevelData             		=XmlDataLoader.loadData(VipLevelData.class,   "resource/viplevel.xml" );
		vipData             			=XmlDataLoader.loadData(VipData.class,   "resource/vipdata.xml" );
		GodsEffectData                  =XmlDataLoader.loadData(GodsEffectData.class,   "resource/godseffect.xml" );
		luckTableData             		=XmlDataLoader.loadData(LuckTableData.class,   "resource/lucktable.xml" );

	}

}
