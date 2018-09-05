package com.igame.work.activity.meiriLiangfa;

import com.igame.core.handler.RetVO;
import com.igame.util.DateUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.activity.ActivityConfigTemplate;
import com.igame.work.activity.ActivityHandler;
import com.igame.work.activity.PlayerActivityData;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Calendar;
import java.util.Optional;

/**
 * 玩家创建账号开始算登录时间
 * 签完30天再读下一个30天的配置
 * 累积签到奖励如果没有领取，到下一个签到周期数据会被清掉
 */
public class MeiriLiangfaHandler extends ActivityHandler {
    private GMService gmService;
    private ResourceService resourceService;

    @Override
    public RetVO handleClientRequest(Player player, PlayerActivityData activityData, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int index = jsonObject.getInt("index");

        if(player.getActivityData().getMeiriLiangfa()==null) {
            player.getActivityData().setMeiriLiangfa(new MeiriLiangfaData());
        }

        String date = receive(player, index);

        if (date == null) {
            return error(ErrorCode.PACK_PURCHASED);
        }

        RetVO vo = new RetVO();

        vo.addData("state", date);

        return vo;
    }

    public String receive(Player player, int index) {

        Optional<ActivityConfigTemplate> config = MeiriLiangfaData.configs.stream()
                .filter(c -> c.getOrder() == index)
                .findAny();

        if(!config.isPresent()) {   // 找不到配置
            return null;
        }

        String configTime = config.get().getGet_value();
        String[] splitTime = configTime.split(",");
        if(splitTime.length<2){return null;}
        int begin = Integer.parseInt(splitTime[0]);
        int end = Integer.parseInt(splitTime[1]);
        int current = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        String[] splitRecord = player.getActivityData().getMeiriLiangfa().getRecord().split(",");
        String today = DateUtil.formatToday();
        if (today.equals(splitRecord[index - 1])) { // 今天已经领过
            return null;
        }

        if(current<begin||current>end) {    // 不在配置时间内当做补领
            resourceService.addDiamond(player, -20);
        }

        if(index==1) {
            player.getActivityData().getMeiriLiangfa().setRecord(today + "," + splitRecord[1]);
        } else {
            player.getActivityData().getMeiriLiangfa().setRecord(splitRecord[0] + "," + today);
        }
        gmService.processGM(player, config.get().getActivity_drop());

        return player.getActivityData().getMeiriLiangfa().clientData();
    }

    @Override
    protected int activityId() {
        return 1004;
    }

    @Override
    public int protocolId() {
        return MProtrol.MEIRI_LIANGFA;
    }
}
