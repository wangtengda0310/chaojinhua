package com.igame.work.activity.denglu;

import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class DengluHandler extends ReconnectedHandler {
    @Override
    public int protocolId() {
        return 4054;
    }

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int id = jsonObject.getInt("id");
        int index = jsonObject.getInt("index");

        if (!DengluService.configs.containsKey(id)) {
            return error(ErrorCode.ERROR);
        }

        Map<Integer, DengluDto> byPlayer = DengluDAO.ins().getByPlayer(player.getSeverId(), player.getPlayerId());
        DengluDto dengluDto = byPlayer.get(id);

        if (dengluDto == null) {
            return error(ErrorCode.ERROR);
        }

        if (dengluDto.getRecord()[index - 1]!=1) {
            return error(ErrorCode.PACK_PURCHASED);
        }
        dengluDto.getRecord()[index - 1] = 2;
        DengluDAO.ins().save(player.getSeverId(), dengluDto);

        DengluService.configs.get(id).stream()
                .filter(c -> c.getOrder() == index)
                .forEach(c -> GMService.processGM(player, c.getActivity_drop()));
        RetVO vo = new RetVO();
        vo.addData("d", Arrays.stream(dengluDto.getRecord()).mapToObj(String::valueOf).collect(Collectors.joining(",")));
        return vo;
    }
}
