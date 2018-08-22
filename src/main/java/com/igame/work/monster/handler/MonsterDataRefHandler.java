package com.igame.work.monster.handler;



import com.igame.work.monster.MonsterDataManager;
import net.sf.json.JSONObject;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterDataRefHandler extends BaseHandler{
	

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

		long objectId = jsonObject.getLong("objectId");
		int level = jsonObject.getInt("level");
		int ret = 0;
		int hp = 0;
		int attack = 0;
		long fightValue = 0;

		Monster mm = player.getMonsters().get(objectId);
		if(mm != null){
			MonsterTemplate mt = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm.getMonsterId());
			if(mt != null){
				hp = (int)(mm.getHpInit() + (level - mm.getLevel()) * mt.getHp_up());
				attack = (int)(mm.getAttack() + (level - mm.getLevel()) * mt.getAtk_up());
				mm.hpTemp = hp;
				mm.attackTemp = attack;
				String[] types = mt.getMonstertype().split(",");
				if(types != null){
					double hprate = 0;//hp
					double attrate = 0;//攻击
					double sprate = 0;//移速
					double repelrate = 0;//霸气
					double rngrate = 0;//射程
					double iasrate = 0;//攻速
					for(String type : types){
						if("1".equals(type)){//近战
							hprate+= 0.5;
							attrate+=0.3;
							sprate+=0.3;
							repelrate+=0.3;
							rngrate+=0;
							iasrate+=0;
						}else if("2".equals(type)){//远程
							hprate+= 0.1;
							attrate+=0.5;
							sprate+=0.1;
							repelrate+=0;
							rngrate+=0.1;
							iasrate+=100;
						}else if("3".equals(type)){//坦克
							hprate+= 0.6;
							attrate+=0.1;
							sprate+=0.3;
							repelrate+=0.3;
							rngrate+=0;
							iasrate+=0;
						}else if("4".equals(type)){//输出
							hprate+= 0.3;
							attrate+=1;
							sprate+=0.2;
							repelrate+=0.1;
							rngrate+=0;
							iasrate+=0;
						}else if("5".equals(type)){//辅助
							hprate+= 0.6;
							attrate+=0.3;
							sprate+=0.4;
							repelrate+=0.2;
							rngrate+=0;
							iasrate+=0;
						}
					}
					
					fightValue = (long)(hp*hprate + attack*attrate + mm.getSpeed()*sprate + mm.getRepel()*repelrate + mm.getRng()*rngrate + mm.getIas() * iasrate);
					mm.fightValueTemp = fightValue;
				}
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("objectId", objectId);
		vo.addData("level", level);
		vo.addData("hp", hp);
		vo.addData("attack", attack);
		vo.addData("fightValue", fightValue);

		send(MProtrol.toStringProtrol(MProtrol.MONSTER_REF), vo, user);
	}

	
}
