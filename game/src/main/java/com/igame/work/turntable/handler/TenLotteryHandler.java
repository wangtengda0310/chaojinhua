package com.igame.work.turntable.handler;

import com.igame.core.di.Inject;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.PlayerEvents;
import com.igame.work.gm.service.GMService;
import com.igame.work.item.dto.Item;
import com.igame.work.turntable.service.TurntableService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 幸运大转盘十连抽
 */
public class TenLotteryHandler extends ReconnectedHandler {
    private ResourceService resourceService;
    private GMService gmService;
    @Inject
    private TurntableService turntableService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        //校验等级
        if (player.getPlayerLevel() < 15 || turntableService.getTurntable(player) == null){
            return error(ErrorCode.LEVEL_NOT);
        }

        //校验道具
        Item item = player.getItems().get(200072);
        if (item == null || item.getUsableCount(-1) < 8){
            return error(ErrorCode.ITEM_NOT_ENOUGH);
        }

        //扣除道具
        resourceService.addItem(player,200072,-8,true);

        //抽取奖励
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {

            int site = turntableService.lottery(player);

            //发放奖励
            String reward = turntableService.getTurntable(player).getRewards().get(site);
            gmService.processGM(player,reward);

            sb.append(reward).append(";");
        }

        if(sb.length() > 0){
            sb.deleteCharAt(sb.length()-1);
        }

        fireEvent(player, PlayerEvents.TURN_TABLE,10);

        vo.addData("reward",sb);
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.LUCKTABLE_LOTTERY_TEN;
    }

}
