package com.igame.work.checkpoint.handler.wujinZhiSen;



import java.util.List;

import com.igame.work.checkpoint.GuanQiaDataManager;
import com.igame.work.monster.MonsterDataManager;
import net.sf.json.JSONObject;

import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.data.EndlessdataTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.FightData;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.fight.service.FightUtil;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.dto.WuEffect;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EndlessEnterHandler extends BaseHandler{
	

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

		int buffer = jsonObject.getInt("buffer");

		int ret = 0;
		int currIndex = 0;
		List<String> ll = Lists.newArrayList();
		ll.addAll(player.getWuMap().values());
		ll.sort((h1, h2) -> Integer.parseInt(h1.split(";")[1]) - Integer.parseInt(h2.split(";")[1]));
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
		if(currIndex == 0 || player.getWuZheng().isEmpty() || (currIndex != Integer.parseInt(ll.get(0).split(";")[0]) &&  "101,105,127,107".indexOf(String.valueOf(buffer)) == -1)){
			ret = ErrorCode.ERROR;
		}else{
			String[] ct = player.getWuMap().get(currIndex).split(";");
			String meetM = ct[3];
			
			//怪物装备
			String equips = "";
			EndlessdataTemplate edt = GuanQiaDataManager.EndlessData.getTemplate(currIndex);
			String[] props = null;
			if(edt != null && !MyUtil.isNullOrEmpty(edt.getMonsterProp())){
				props = edt.getMonsterProp().split(";");
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
	    		MessageUtil.notiyWuBufferChange(player,ls);
	    	}
	    	for(MatchMonsterDto mdt : player.getWuZheng().values()){
	    		mdt.reCalValue(ls);
	    	}
//				   PVPFightService.ins().fights.put(fb.getObjectId(), fb);

			
		}
		
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("a", player.getWuZheng().values());
		vo.addData("m", lb);

		send(MProtrol.toStringProtrol(MProtrol.WU_ENTER), vo, user);
	}

	
}
