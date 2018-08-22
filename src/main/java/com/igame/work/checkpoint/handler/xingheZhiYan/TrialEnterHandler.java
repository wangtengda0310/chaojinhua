package com.igame.work.checkpoint.handler.xingheZhiYan;


import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.GuanQiaDataManager;
import com.igame.work.checkpoint.data.TrialdataTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.FightData;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.fight.service.FightUtil;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TrialEnterHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}
    	List<MatchMonsterDto> lb = Lists.newArrayList();
		int ret = 0;
		TrialdataTemplate ct = GuanQiaDataManager.TrialData.getTemplate(player.getTowerId() + 1);
		if(ct == null){
			ret = ErrorCode.ERROR;
		}else if (player.getItems().size() >= player.getBagSpace()){
			ret = ErrorCode.BAGSPACE_ALREADY_FULL;
		}else{
			if(player.getXing() < 1){
				ret = ErrorCode.XING_NOT_ENOUGH;
			}else{
				ResourceService.ins().addXing(player, -1);
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
					for(String id :meetM.split(",")){
						int mid = Integer.parseInt(id);
						if(MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mid) != null && !player.getMeetM().contains(mid)){
							player.getMeetM().add(mid);
							change = true;
						}
					}
					if(change){
						MessageUtil.notiyMeetM(player);
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

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("m", lb);

		send(MProtrol.toStringProtrol(MProtrol.TRIAL_ENTER), vo, user);
	}

	
}
