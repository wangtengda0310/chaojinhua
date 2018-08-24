package com.igame.work.checkpoint.wujinZhiSen.handler;


import com.google.common.collect.Lists;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.wujinZhiSen.EndlessdataTemplate;
import com.igame.work.checkpoint.wujinZhiSen.WujinZhiSenDataManager;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.FightData;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.fight.service.FightUtil;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.dto.WuEffect;
import com.igame.work.monster.handler.TuJianHandler;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EndlessEnterHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int buffer = jsonObject.getInt("buffer");

		int currIndex = 0;
		List<String> ll = Lists.newArrayList();
		ll.addAll(player.getWuMap().values());
		ll.sort(Comparator.comparingInt(h -> Integer.parseInt(h.split(";")[1])));
		currIndex = Integer.parseInt(ll.get(0).split(";")[0]);
		int total = 0;
		List<MatchMonsterDto> lb = Lists.newArrayList();
		for(String m : ll){
			int num = Integer.parseInt(m.split(";")[0]);
			int guo = Integer.parseInt(m.split(";")[2]);
			if(guo == 0){
				currIndex = num;
				break;
			}
			total++;
		}
		if(total >= ll.size()){//已结全部过关
			currIndex = 0;
		}	
		if(currIndex == 0 || player.getWuZheng().isEmpty() || (currIndex != Integer.parseInt(ll.get(0).split(";")[0]) && !"101,105,127,107".contains(String.valueOf(buffer)))){
			return error(ErrorCode.ERROR);
		}else{
			String[] ct = player.getWuMap().get(currIndex).split(";");
			String meetM = ct[3];
			
			//怪物装备
			String equips = "";
			EndlessdataTemplate edt = WujinZhiSenDataManager.EndlessData.getTemplate(currIndex);
			String[] props = null;
			if(edt != null && !MyUtil.isNullOrEmpty(edt.getMonsterProp())){
				props = edt.getMonsterProp().split(";");
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
					equips += ";" + props[GameMath.getRandInt(props.length)];
				}
				//怪物装备
			}
			if(equips.length() > 0){
				equips = equips.substring(1);
			}
			
			FightBase fb  = new FightBase(player.getPlayerId(),new FightData(player),new FightData(null,FightUtil.createMonster(ct[3], ct[4], "","",equips)));
//			player.setFightBase(fb);
			
	    	for(Monster m : fb.getFightB().getMonsters().values()){
	    		MatchMonsterDto mto = new MatchMonsterDto(m);
				mto.reCalGods(player.callFightGods(), null);
	    		lb.add(mto);
	    	}
	    	List<WuEffect> ls = Lists.newArrayList();
	    	if(buffer > 0 && currIndex != Integer.parseInt(ll.get(0).split(";")[0]) && player.getWuEffect().size() < total){
	    		player.setTempBufferId(buffer);
	    		ls.addAll(player.getWuEffect());
	    		ls.add(new WuEffect(buffer));
	    		MessageUtil.notifyWuBufferChange(player,ls);
	    	}
	    	for(MatchMonsterDto mdt : player.getWuZheng().values()){
	    		mdt.reCalValue(ls);
	    	}
//				   PVPFightService.ins().fights.put(fb.getObjectId(), fb);

			
		}

		vo.addData("a", player.getWuZheng().values());
		vo.addData("m", lb);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.WU_ENTER;
	}

}
