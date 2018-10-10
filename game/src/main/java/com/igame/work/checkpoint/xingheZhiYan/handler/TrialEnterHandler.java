package com.igame.work.checkpoint.xingheZhiYan.handler;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.xingheZhiYan.TrialdataTemplate;
import com.igame.work.checkpoint.xingheZhiYan.XingheZhiYanService;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TrialEnterHandler extends ReconnectedHandler {

	@Inject
	private ResourceService resourceService;
	@Inject
	private XingheZhiYanService xingheZhiYanService;
	@Inject
	private MonsterService monsterService;
	@Inject private CheckPointService checkPointService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

    	List<MatchMonsterDto> lb;
		TrialdataTemplate ct = xingheZhiYanService.trialData.getTemplate(player.getTowerId() + 1);
		if(ct == null){
			return error(ErrorCode.ERROR);
		}else if (player.getItems().size() >= player.getBagSpace()){
			return error(ErrorCode.BAGSPACE_ALREADY_FULL);
		}else{
			if(player.getXing() < 1){
				return error(ErrorCode.XING_NOT_ENOUGH);
			}else{
				resourceService.addXing(player, -1);
				

				int meetM = ct.getMonsterData();
				monsterService.meet(player, meetM);

				lb = monsterService.createMonsterDtoOfAll(ct.getMonsterData());
			}
			
		}

		vo.addData("m", lb);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.TRIAL_ENTER;
	}

}
