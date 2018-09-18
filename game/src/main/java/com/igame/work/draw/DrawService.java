package com.igame.work.draw;

import com.igame.core.di.LoadXml;

public class DrawService {
    /**
     * 造物台数据
     */
    @LoadXml("drawdata.xml") public DrawdataData drawdataData;
    /**
     * 造物台等级
     */
    @LoadXml("drawlevel.xml") public DrawLevelData drawLevelData;
    /**
     * 奖励库
     */
    @LoadXml("drawreward.xml") public DrawrewardData drawrewardData;
}
