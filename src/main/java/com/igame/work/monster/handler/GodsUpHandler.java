package com.igame.work.monster.handler;



import java.util.List;
import java.util.Map;

import com.igame.work.fight.FightDataManager;
import net.sf.json.JSONObject;

import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.fight.data.GodsdataTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.dto.Gods;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GodsUpHandler extends BaseHandler{
	

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
		
		int godsType = jsonObject.getInt("godsType");
		int ret = 0;
		Gods gods = player.getGods().get(godsType);
		
		if(gods == null){
			ret = ErrorCode.ERROR;
		}else{
			GodsdataTemplate curr = FightDataManager.GodsData.getTemplate(gods.getGodsType()+"_"+ gods.getGodsLevel());
			GodsdataTemplate gt = FightDataManager.GodsData.getNextLevelTemplate(gods.getGodsType(), gods.getGodsLevel());
			if(curr == null || gt == null){
				ret = ErrorCode.GODS_MAX;
			}else{
				if(player.getGold() < curr.getGold()){
					ret = ErrorCode.GOLD_NOT_ENOUGH;
				}else{
					RewardDto rt = null;
					if(curr.getItem() != null && !"".equals(curr.getItem())){
						rt = ResourceService.ins().getRewardDto(curr.getItem(), "100");
						if(!rt.getItems().isEmpty()){//道具不足
							for(Map.Entry<Integer, Integer> m :rt.getItems().entrySet()){
								if(player.getItems().get(m.getKey()) == null || player.getItems().get(m.getKey()).getUsableCount(-1) < m.getValue()){
									ret = ErrorCode.ITEM_NOT_ENOUGH;
									break;
								}
							}
						}
					}
					if(ret == 0){
						if(rt != null && !rt.getItems().isEmpty()){
							List<Item> ll = Lists.newArrayList();
							for(Map.Entry<Integer, Integer> m :rt.getItems().entrySet()){
								ll.add(ResourceService.ins().addItem(player, m.getKey(), 0-m.getValue(), false));
							}
							MessageUtil.notiyItemChange(player, ll);
						}
						ResourceService.ins().addGold(player, 0-curr.getGold());
						gods.setGodsLevel(gt.getGodsLevel());
						gods.setDtate(2);
						List<Gods> ll = Lists.newArrayList();
						ll.add(gods);
						MessageUtil.notiyGodsChange(player, ll);
						QuestService.processTask(player, 21, 0);
					}
				}				
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("gods", gods);

		send(MProtrol.toStringProtrol(MProtrol.Gods_UP), vo, user);
	}

	
}
