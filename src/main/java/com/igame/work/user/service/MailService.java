package com.igame.work.user.service;

import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.log.GoldLog;
import com.igame.work.MessageUtil;
import com.igame.work.user.dao.MailDAO;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Mail;
import com.igame.work.user.dto.Player;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MailService {

	@Inject private SessionManager sessionManager;
	@Inject private MailDAO mailDAO;
	@Inject private PlayerDAO playerDAO;

    public int getMaxId(Map<Integer,Mail> mail) {
        Optional<Integer> o = mail.keySet().stream().max((a, b)->a-b);
        if(o.isPresent()){
            return o.get()+1;
        }
        return 1;
    }

	private Map<Long, Map<Integer, Mail>> playerMails = new ConcurrentHashMap<>();//玩家邮件

	/**
     * 发送邮件
     * @param serverId 服务器ID
     * @param target 角色ID
     * @param type 类型  1-奖励邮件，2-普通邮件，3-事件邮件
     * @param exttype 类型  1-系统，2-好友
     * @param sender 发送者
     * @param title 标题
     * @param content 内容
     * @param attach 附件
     */
    public Mail senderMail(int serverId,long target,int type,int exttype,String sender,String title,String content,String attach){
    	
    	Mail mail = new Mail();
    	mail.setType(type);
    	mail.setExttype(exttype);
    	mail.setSender(sender);
    	mail.setTitle(title);
    	mail.setContent(content);
    	mail.setAttach(attach);
    	mail.setDtate(1);
    	mail.setTime(new Date());
    	mail.setPlayerId(target);
    	Player player = sessionManager.getSessionByPlayerId(target);
    	if(player != null){
    		String maxId = String.valueOf(getMaxId(playerMails.get(player.getPlayerId())));
    		if(maxId.length() < 5){
    			maxId = String.valueOf(serverId * 100000 + Integer.parseInt(maxId));
    		}
    		mail.setId(serverId * 100000 + Integer.parseInt(maxId.substring(maxId.length() - 5)));
    		playerMails.get(player.getPlayerId()).put(mail.getId(), mail);
    		MessageUtil.notifyNewMail(player, mail);
    	}else{
    		mail.setDtate(0);
    		  		
//    		int serverId = 0;
//	    	for(Integer sId :ServerListHandler.servers.keySet()){
//	    		
//	    		if(playerDAO.getPlayerByPlayerId(sId, target)!= null){
//	    			serverId = sId;
//	    			break;
//	    		}
//	    	}	    	
    		String maxId = String.valueOf(getMaxId(mailDAO.getByPlayer(target)));
    		if(maxId.length() < 5){
    			maxId = String.valueOf(serverId * 100000 + Integer.parseInt(maxId));
    		}
    		mail.setId(serverId * 100000 + Integer.parseInt(maxId.substring(maxId.length() - 5)));
    		mailDAO.save(mail);
    	}

    	if (exttype == 1 && type == 1){	//如果是系统发的奖励邮件
			GoldLog.info("#serverId:"+serverId+"#userId:"+"#playerId:"+target
					+"#act:addRes"+"#addtype:mail"+ "#title:"+ title + "#checkReward:"+attach);
		}
    	return mail;
    }
    
    public Mail senderMailByName(int serverId,String username,int type,int exttype,String sender,String title,String content,String attach){
    	
    	Mail mail = new Mail();
    	mail.setType(type);
    	mail.setExttype(exttype);
    	mail.setSender(sender);
    	mail.setTitle(title);
    	mail.setContent(content);
    	mail.setAttach(attach);
    	mail.setDtate(1);
    	mail.setTime(new Date());
    	Player player = sessionManager.getSession(username);
    	if(player != null){
			String maxId = String.valueOf(getMaxId(playerMails.get(player.getPlayerId())));
    		if(maxId.length() < 5){
    			maxId = String.valueOf(serverId * 100000 + Integer.parseInt(maxId));
    		}
    		mail.setId(serverId * 100000 + Integer.parseInt(maxId.substring(maxId.length() - 5)));
    		playerMails.get(player.getPlayerId()).put(mail.getId(), mail);
    		MessageUtil.notifyNewMail(player, mail);
    	}else{
    		mail.setDtate(0);
    		  		
//    		int serverId = 0;
//	    	for(Integer sId :ServerListHandler.servers.keySet()){
//	    		
//	    		if(playerDAO.getPlayerByPlayerId(sId, target)!= null){
//	    			serverId = sId;
//	    			break;
//	    		}
//	    	}

    		player = playerDAO.getPlayerByPlayerNickName(username);
    		String maxId = String.valueOf(getMaxId(mailDAO.getByPlayer(player.getPlayerId())));
    		if(maxId.length() < 5){
    			maxId = String.valueOf(serverId * 100000 + Integer.parseInt(maxId));
    		}
    		mail.setPlayerId(player.getPlayerId());
    		mail.setId(serverId * 100000 + Integer.parseInt(maxId.substring(maxId.length() - 5)));
    		mailDAO.save(mail);
    	}
    	return mail;
    }


    public void loadPlayer(Player player) {
		playerMails.put(player.getPlayerId(), mailDAO.getByPlayer(player.getPlayerId()));
    }

	public Map<Integer, Mail> getMails(Player player) {
		return playerMails.get(player.getPlayerId());
	}

	public void updatePlayer(Player player) {
		mailDAO.updatePlayer(playerMails.get(player.getPlayerId()));
	}
}
