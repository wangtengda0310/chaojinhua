package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.fight.FightDataManager;
import com.igame.work.fight.data.GodsdataTemplate;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.dto.Gods;
import com.igame.work.quest.service.QuestService;
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
public class GodsUpHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int godsType = jsonObject.getInt("godsType");
		Gods gods = player.getGods().get(godsType);
		
		if(gods == null){
			return error(ErrorCode.ERROR);
		}else{
			GodsdataTemplate curr = FightDataManager.GodsData.getTemplate(gods.getGodsType()+"_"+ gods.getGodsLevel());
			GodsdataTemplate gt = FightDataManager.GodsData.getNextLevelTemplate(gods.getGodsType(), gods.getGodsLevel());
			if(curr == null || gt == null){
				return error(ErrorCode.GODS_MAX);
			}else{
				if(player.getGold() < curr.getGold()){
					return error(ErrorCode.GOLD_NOT_ENOUGH);
				}else{
					RewardDto rt = null;
					if(curr.getItem() != null && !"".equals(curr.getItem())){
						rt = ResourceService.ins().getRewardDto(curr.getItem(), "100");
						if(!rt.getItems().isEmpty()){//道具不足
							for(Map.Entry<Integer, Integer> m :rt.getItems().entrySet()){
								if(player.getItems().get(m.getKey()) == null || player.getItems().get(m.getKey()).getUsableCount(-1) < m.getValue()){
									return error(ErrorCode.ITEM_NOT_ENOUGH);
								}
							}
						}
					}
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

		vo.addData("gods", gods);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.Gods_UP;
	}

}
