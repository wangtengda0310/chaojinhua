package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.fight.FightService;
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

	@Inject private ResourceService resourceService;
	@Inject private QuestService questService;

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
			GodsdataTemplate curr = FightService.godsData.getTemplate(gods.getGodsType()+"_"+ gods.getGodsLevel());
			GodsdataTemplate gt = FightService.godsData.getNextLevelTemplate(gods.getGodsType(), gods.getGodsLevel());
			if(curr == null || gt == null){
				return error(ErrorCode.GODS_MAX);
			}else{
				if(player.getGold() < curr.getGold()){
					return error(ErrorCode.GOLD_NOT_ENOUGH);
				}else{
					RewardDto rt = null;
					if(curr.getItem() != null && !"".equals(curr.getItem())){
						rt = resourceService.getRewardDto(curr.getItem(), "100");
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
							ll.add(resourceService.addItem(player, m.getKey(), 0-m.getValue(), false));
						}
						MessageUtil.notifyItemChange(player, ll);
					}
					resourceService.addGold(player, 0-curr.getGold());
					gods.setGodsLevel(gt.getGodsLevel());
					gods.setDtate(2);
					List<Gods> ll = Lists.newArrayList();
					ll.add(gods);
					MessageUtil.notifyGodsChange(player, ll);
					questService.processTask(player, 21, 0);
				}
			}
		}

		vo.addData("gods", gods);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.Gods_UP;
	}

}
