package com.igame.work.checkpoint.tansuo.handler;


import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.tansuo.TansuoDataManager;
import com.igame.work.checkpoint.tansuo.TansuoDto;
import com.igame.work.checkpoint.tansuo.TansuoTemplate;
import com.igame.work.monster.dto.Monster;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TansuoEndHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int sid = jsonObject.getInt("sid");
		vo.addData("sid", sid);

		//入参校验
		TansuoDto dto = player.getTangSuo().get(sid);
		TansuoTemplate ts = TansuoDataManager.TansuoData.getTemplate(sid);
		if(dto == null || ts == null || dto.getState() == 0 || dto.getStartTime() == 0){
			return error(ErrorCode.ERROR);
		}

		//校验背包空间
		if (player.getItems().size() >= player.getBagSpace()){
			return error(ErrorCode.BAGSPACE_ALREADY_FULL);
		}

		//校验时间
		dto.calLeftTime(System.currentTimeMillis());
		if(dto.getLeftTime() > 0 ){
			return error(ErrorCode.TANG_TIME_NOT);
		}

		//计算怪兽数量与战力
		int monCount = 0;
		long fightValue = 0;
		String[] ls = dto.getMons().split(",");
		for(String l : ls){
			if(!"0".equals(l) && !"-1".equals(l)){
				Monster mm = player.getMonsters().get(Long.parseLong(l));
				if(mm != null){
					fightValue += mm.getFightValue();
				}
				monCount++;
			}
		}
		if(monCount < 2){
			monCount = 2;
		}

		//奖励
		RewardDto rt = new RewardDto();

		//金币
		long up = (long) (fightValue * ts.getUp() * dto.getState());//额外金币
		rt.gold = ts.getGold() + up;

		//道具
		String[] items = ts.getItem().split(",");
		String rate = ts.getRate(dto.getState());
		String value = ts.getValue(dto.getState());

		if(rate != null && value != null){
			String[] rates = rate.split(",");
			String[] values = value.split(",");
			for(int i = 0;i < monCount;i++){
				if(GameMath.hitRate(Integer.parseInt(rates[i]) * 100)){
					int itemId = Integer.parseInt(items[i]);
					int count = GameMath.getRandomCount(Integer.parseInt(values[i].split("-")[0]), Integer.parseInt(values[i].split("-")[1]));
					rt.addItem(itemId, count);
				}
			}
		}

		if(ts.getLimit_item()!= null && ts.getLmit() > 0 && MyUtil.hasCheckPoint(player.getCheckPoint(), String.valueOf(ts.getLmit()))){
			String lrate = ts.getLimitRate(dto.getState());
			String lvalue = ts.getLimitValue(dto.getState());
			if(lrate != null && lvalue != null){
				if(GameMath.hitRate(Integer.parseInt(lrate) * 100)){
					int itemId = Integer.parseInt(ts.getLimit_item());
					int count = GameMath.getRandomCount(Integer.parseInt(lvalue.split("-")[0]), Integer.parseInt(lvalue.split("-")[1]));
					rt.addItem(itemId, count);
				}
			}
		}

		//发放奖励
		ResourceService.ins().addRewarToPlayer(player, rt);
		//处理任务埋点
		QuestService.processTask(player, 15, 1);

		//恢复至初始状态
		dto.setStartTime(0);
		dto.setState(0);
		dto.clearhelp();

		//奖励字符串
		String reward = ResourceService.ins().getRewardString(rt);

		vo.addData("leftTime", dto.getLeftTime());
		vo.addData("state", dto.getState());
		vo.addData("reward", reward);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.TANGSUO_END;
	}

}
