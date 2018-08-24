package com.igame.work.user.service;

import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.HeadFrameTemplate;
import com.igame.work.user.data.HeadTemplate;
import com.igame.core.log.GoldLog;
import com.igame.core.handler.RetVO;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.igame.work.user.HeadConstants.*;

/**
 * @author xym
 */
public class HeadService {

    private static final HeadService domain = new HeadService();

    public static final HeadService ins() {
        return domain;
    }



    /**
     * 初始化头像
     * @param player 玩家
     */
    public void initHead(Player player) {
        List<HeadTemplate> all = PlayerDataManager.headData.getAll();
        for (HeadTemplate headTemplate : all) {
            if (isUnlockHead(player,headTemplate))
                addHead(player,headTemplate.getHeadId(),headTemplate.getTouchLimit(),false);
        }
        //设置默认头像
        player.setPlayerHeadId(1);
    }

    /**
     * 初始化头像框
     * @param player 玩家
     */
    public void initFrame(Player player) {
        List<HeadFrameTemplate> all = PlayerDataManager.headFrameData.getAll();
        for (HeadFrameTemplate frameTemplate : all) {
            if (isUnlockHeadFrame(player,frameTemplate))
                addFrame(player,frameTemplate.getHeadFrameId(),frameTemplate.getTouchLimit(),false);
        }
    }

    /**
     * 根据触发条件 判断并解锁 对应头像
     * @param player 玩家
     * @param touchLimit 触发条件类型
     * @return true = 解锁了某个头像
     */
    public boolean unlockHead(Player player, int touchLimit) {

        List<HeadTemplate> headTemplates = PlayerDataManager.headData.getTemplates(touchLimit);

        for (HeadTemplate headTemplate : headTemplates) {

            if (isUnlockHead(player,headTemplate)){
                addHead(player,headTemplate.getHeadId(),touchLimit,true);
                return true;
            }
        }

        return false;
    }

    /**
     * 根据触发条件 判断并解锁 对应头像框
     * @param player 玩家
     * @param touchLimit 触发条件类型
     * @return true = 解锁了某个头像
     */
    public boolean unlockHeadFrame(Player player, int touchLimit) {

        List<HeadFrameTemplate> frameTemplates = PlayerDataManager.headFrameData.getTemplates(touchLimit);

        for (HeadFrameTemplate frameTemplate : frameTemplates) {

            if (isUnlockHeadFrame(player,frameTemplate)){
                addFrame(player,frameTemplate.getHeadFrameId(),touchLimit,true);
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个玩家的某个头像是否满足解锁条件
     * @param player 玩家
     * @param template 头像模板
     * @return true = 满足解锁条件
     */
    public boolean isUnlockHead(Player player, HeadTemplate template){

        Set<Integer> unlockHead = player.getUnlockHead();
        int headId = template.getHeadId();
        if (unlockHead.contains(headId))
            return false;

        int touchLimit = template.getTouchLimit();
        int value = template.getValue();

        if (touchLimit == HEAD_TOUCH_NULL){   //无

            return true;

        }else if (touchLimit == HEAD_TOUCH_LV){   //等级

            return player.getPlayerLevel() >= value;

        }else if (touchLimit == HEAD_TOUCH_MONSTER){    //怪兽

            Map<Long, Monster> monsters = player.getMonsters();
            for (Monster monster : monsters.values()) {
                int monsterId = monster.getMonsterId();
                if (monsterId == value)
                    return true;
            }

            return false;

        }else if (touchLimit == HEAD_TOUCH_EVENT){  //活动
            return false;

        }else{
            return false;
        }
    }

    /**
     * 判断某个玩家的某个头像框是否满足解锁条件
     * @param player 玩家
     * @param frameTemplate 头像框模板
     * @return true = 满足解锁条件
     */
    public boolean isUnlockHeadFrame(Player player, HeadFrameTemplate frameTemplate) {

        Set<Integer> unlockFrame = player.getUnlockFrame();
        int headFrameId = frameTemplate.getHeadFrameId();
        if (unlockFrame.contains(headFrameId))
            return false;

        int touchLimit = frameTemplate.getTouchLimit();
        int value = frameTemplate.getValue();

        if (touchLimit == HEAD_TOUCH_NULL){   //无

            return true;

        }else if (touchLimit == FRAME_TOUCH_LV){  //等级

            return player.getPlayerLevel() >= value;

        }else if (touchLimit == FRAME_TOUCH_ARENA){ //竞技场

            return false;

        }else if (touchLimit == FRAME_TOUCH_PVP){   //PVP组别

            return false;

        }else if (touchLimit == FRAME_TOUCH_VIP){   //VIP等级

            return player.getVip() >= value;

        }else if (touchLimit == FRAME_TOUCH_EVENT){ //活动

            return false;

        }else{
            return false;
        }
    }

    /**
     * 增加头像
     */
    public void addHead(Player player, int headId, int touchLimit,boolean notiy) {

        player.getUnlockHead().add(headId);

        RetVO vo = new RetVO();
        vo.addData("touchLimit", touchLimit);
        vo.addData("headId", headId);
        GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
                +"#act:addHead"+"#touchLimit:" + touchLimit + "#headId:"+headId);
        if(notiy){
            MessageUtil.sendMessageToPlayer(player, MProtrol.HEAD_UPDATE, vo);
        }

    }

    /**
     * 增加头像框
     */
    public void addFrame(Player player, int frameId, int touchLimit,boolean notiy) {

        player.getUnlockFrame().add(frameId);

        RetVO vo = new RetVO();
        vo.addData("touchLimit", touchLimit);
        vo.addData("frameId", frameId);
        GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
                +"#act:addFrame"+"#touchLimit:" + touchLimit + "#frameId:"+frameId);
        if(notiy){
            MessageUtil.sendMessageToPlayer(player, MProtrol.FRAME_UPDATE, vo);
        }
    }
}
