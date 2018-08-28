package com.igame.work.user.load;

import com.igame.core.log.DebugLog;
import com.igame.work.chat.dao.PlayerMessageDAO;
import com.igame.work.chat.service.MessageBoardService;
import com.igame.work.checkpoint.worldEvent.WordEventDAO;
import com.igame.work.friend.dao.FriendDAO;
import com.igame.work.item.dao.ItemDAO;
import com.igame.work.monster.dao.GodsDAO;
import com.igame.work.monster.dao.MonsterDAO;
import com.igame.work.quest.dao.QuestDAO;
import com.igame.work.shop.dao.ShopDAO;
import com.igame.work.user.dao.MailDAO;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PlayerLoad {
	
    private static final PlayerLoad domain = new PlayerLoad();

    public static PlayerLoad ins() {
        return domain;
    }

    /**
     * 保存角色相关数据
     */
    public void savePlayer(Player player,boolean loginOutTime){
    	synchronized(player.dbLock){
			DebugLog.debug("palyer leave save---- serverId:" + player.getSeverId() + "," +"userId:" + player.getUserId() + "," +"playerId:" + player.getPlayerId() + "," +"playerName:" + player.getNickname());
			PlayerDAO.ins().updatePlayer(player,loginOutTime);
        	MonsterDAO.ins().updatePlayer(player);
        	ItemDAO.ins().updatePlayer(player);
        	WordEventDAO.ins().updatePlayer(player);
        	GodsDAO.ins().updatePlayer(player);
        	MailDAO.ins().updatePlayer(player);
        	QuestDAO.ins().updatePlayer(player);
        	ShopDAO.ins().updatePlayer(player);
        	FriendDAO.ins().updatePlayer(player);
        	PlayerMessageDAO.ins().updatePlayer(player);
			MessageBoardService.ins().saveMessageBoard(player);
			PlayerCacheService.cachePlayer(player);

			PlayerCacheService.cachePlayer(player);
    	}
    }

}
