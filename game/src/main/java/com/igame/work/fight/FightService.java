package com.igame.work.fight;

import com.igame.core.di.LoadXml;
import com.igame.work.fight.data.*;

public class FightService {
    /**
     * buffer
     */
    @LoadXml("effectdata.xml") public EffectData effectData;
    /**
     * 神灵配置
     */
    @LoadXml("godsdata.xml") public GodsData godsData;
    /**
     * 神灵技能buffer配置
     */
    @LoadXml("godseffect.xml") public GodsEffectData godsEffectData;
    /**
     * 技能
     */
    @LoadXml("skilldata.xml") public SkillData skillData;
    /**
     * 技能等级数据
     */
    @LoadXml("skillexp.xml") public SkillLvData skillLvData;
}
