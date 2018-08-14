package com.igame.work.checkpoint.handler;



import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.TangSuoTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.checkpoint.dto.TangSuoDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TangEndHandler extends BaseHandler{
	

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

		int sid = jsonObject.getInt("sid");
		vo.addData("sid", sid);

		//入参校验
		TangSuoDto dto = player.getTangSuo().get(sid);
		TangSuoTemplate ts = DataManager.TangSuoData.getTemplate(sid);
		if(dto == null || ts == null || dto.getState() == 0 || dto.getStartTime() == 0){
			sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.TANGSUO_END), vo, user);
			return;
		}

		//校验背包空间
		if (player.getItems().size() >= player.getBagSpace()){
			sendError(ErrorCode.BAGSPACE_ALREADY_FULL,MProtrol.toStringProtrol(MProtrol.TANGSUO_END), vo, user);
			return;
		}

		//校验时间
		dto.calLeftTime(System.currentTimeMillis());
		if(dto.getLeftTime() > 0 ){
			sendError(ErrorCode.TANG_TIME_NOT,MProtrol.toStringProtrol(MProtrol.TANGSUO_END), vo, user);
			return;
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

		sendSucceed(MProtrol.toStringProtrol(MProtrol.TANGSUO_END), vo, user);
	}

	
}
