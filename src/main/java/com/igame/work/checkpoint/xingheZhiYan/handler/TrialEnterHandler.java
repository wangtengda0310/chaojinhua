package com.igame.work.checkpoint.xingheZhiYan.handler;


import com.google.common.collect.Lists;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.xingheZhiYan.TrialdataTemplate;
import com.igame.work.checkpoint.xingheZhiYan.XingheZhiYanDataManager;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.FightData;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.fight.service.FightUtil;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.handler.TuJianHandler;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TrialEnterHandler extends ReconnectedHandler {

	private ResourceService resourceService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

    	List<MatchMonsterDto> lb = Lists.newArrayList();
		TrialdataTemplate ct = XingheZhiYanDataManager.TrialData.getTemplate(player.getTowerId() + 1);
		if(ct == null){
			return error(ErrorCode.ERROR);
		}else if (player.getItems().size() >= player.getBagSpace()){
			return error(ErrorCode.BAGSPACE_ALREADY_FULL);
		}else{
			if(player.getXing() < 1){
				return error(ErrorCode.XING_NOT_ENOUGH);
			}else{
				resourceService.addXing(player, -1);
//				player.setEnterCheckpointId(chapterId);
//				player.setEnterCheckPointTime(System.currentTimeMillis());
				

				String meetM = ct.getMonsterData();
				
				//怪物装备
				String equips = "";
				String[] props = null;
				if(!MyUtil.isNullOrEmpty(ct.getMonsterProp())){
					props = ct.getMonsterProp().split(";");
				}
				//怪物装备
				
				if(!MyUtil.isNullOrEmpty(meetM)){
					boolean change = false;
					change = TuJianHandler.isChange(player, meetM, change);
					if(change){
						MessageUtil.notifyMeetM(player);
					}
					//怪物装备
					if(props != null){
						equips = MyUtil.toString(props, ";");
					}
					//怪物装备
				}
				if(equips.length() > 0){
					equips = equips.substring(1);
				}
				
				FightBase fb  = new FightBase(player.getPlayerId(),new FightData(player),new FightData(null,FightUtil.createMonster(ct.getMonsterData(), ct.getMonsterLv(), "",ct.getMonsterSkilllv(),equips)));
//				player.setFightBase(fb);
		    	for(Monster m : fb.getFightB().getMonsters().values()){
		    		MatchMonsterDto mto = new MatchMonsterDto(m);
					mto.reCalGods(player.callFightGods(), null);
		    		lb.add(mto);
		    	}
//				   PVPFightService.ins().fights.put(fb.getObjectId(), fb);
				
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
