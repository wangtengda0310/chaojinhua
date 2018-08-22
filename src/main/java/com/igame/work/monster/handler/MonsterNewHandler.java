package com.igame.work.monster.handler;



import java.util.Map;
import java.util.List;

import com.igame.work.monster.MonsterDataManager;
import net.sf.json.JSONObject;

import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.work.monster.data.NewMonsterTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.igame.work.item.dto.Item;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterNewHandler extends BaseHandler{
	

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
		int ret = 0;
		int newMonster = jsonObject.getInt("newMonster");
		NewMonsterTemplate nt = MonsterDataManager.NewMonsterData.getTemplate(newMonster);
		MonsterTemplate mt = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(newMonster);
		if(nt == null || mt == null){
			ret = 1;
		}else{
			String[] cons = nt.getCondition().split(",");
			boolean can = true;
			StringBuffer sb = new StringBuffer();
			for(Monster mm : player.getMonsters().values()){
				sb.append(mm.getMonsterId()).append(",");
			}
			for(String con : cons){
				if(sb.toString().indexOf(con) == -1){
					can = false;
					break;
				}
			}
			if(!can){
				ret = ErrorCode.MONSTER_CON_NOT;
			}else{
				RewardDto rt = ResourceService.ins().getRewardDto(nt.getItem(), "100");
				if(rt != null ){
					if(rt.getGold() > 0 && player.getGold() < rt.getGold()){
						ret = ErrorCode.GOLD_NOT_ENOUGH;
					}
					if(!rt.getItems().isEmpty()){//道具不足
						for(Map.Entry<Integer, Integer> m :rt.getItems().entrySet()){
							if(player.getItems().get(m.getKey()) == null || player.getItems().get(m.getKey()).getUsableCount(-1) < m.getValue()){
								ret = ErrorCode.ITEM_NOT_ENOUGH;
								break;
							}
						}
					}
					if(ret == 0){
						//生成新怪物
						ResourceService.ins().addMonster(player, newMonster, 1, true);
						if(rt.getGold()>0){
							ResourceService.ins().addGold(player, 0-rt.getGold());
						}
						if(!rt.getItems().isEmpty()){
							List<Item> ll = Lists.newArrayList();
							for(Map.Entry<Integer, Integer> m :rt.getItems().entrySet()){
								ll.add(ResourceService.ins().addItem(player, m.getKey(), 0-m.getValue(), false));
							}
							MessageUtil.notiyItemChange(player, ll);
						}
					}

				}
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}

		vo.addData("newMonster", newMonster);

		send(MProtrol.toStringProtrol(MProtrol.MONSTER_NEW), vo, user);
	}

	
}
