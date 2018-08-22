package com.igame.work.user.service;

import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.log.GoldLog;
import com.igame.work.user.dao.MailDAO;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Mail;
import com.igame.work.user.dto.Player;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MailService {
	
    private static final MailService domain = new MailService();

    public static final MailService ins() {
        return domain;
    }
	
    public int getMaxId(Map<Integer,Mail> mail) {
        Optional<Integer> o = mail.keySet().stream().max((a, b)->a-b);
        if(o.isPresent()){
            return o.get()+1;
        }
        return 1;
    }
    
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
     * @return
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
    	Player player = SessionManager.ins().getSessionByPlayerId(target);
    	if(player != null){
    		String maxId = String.valueOf(getMaxId(player.getMail()));
    		if(maxId.length() < 5){
    			maxId = String.valueOf(serverId * 100000 + Integer.parseInt(maxId));
    		}
    		mail.setId(serverId * 100000 + Integer.parseInt(maxId.substring(maxId.length() - 5)));
    		player.getMail().put(mail.getId(), mail);
    		MessageUtil.notiyNewMail(player, mail); 		
    	}else{
    		mail.setDtate(0);
    		  		
//    		int serverId = 0;
//	    	for(Integer sId :ServerListHandler.servers.keySet()){
//	    		
//	    		if(PlayerDAO.ins().getPlayerByPlayerId(sId, target)!= null){
//	    			serverId = sId;
//	    			break;
//	    		}
//	    	}	    	
    		String maxId = String.valueOf(getMaxId(MailDAO.ins().getByPlayer(serverId, target)));
    		if(maxId.length() < 5){
    			maxId = String.valueOf(serverId * 100000 + Integer.parseInt(maxId));
    		}
    		mail.setId(serverId * 100000 + Integer.parseInt(maxId.substring(maxId.length() - 5)));
    		MailDAO.ins().save(serverId, mail);
    	}

    	if (exttype == 1 && type == 1){	//如果是系统发的奖励邮件
			GoldLog.info("#serverId:"+serverId+"#userId:"+"#playerId:"+target
					+"#act:addRes"+"#addtype:mail"+ "#title:"+ title + "#reward:"+attach);
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
    	Player player = SessionManager.ins().getSession(username);
    	if(player != null){
    		String maxId = String.valueOf(getMaxId(player.getMail()));
    		if(maxId.length() < 5){
    			maxId = String.valueOf(serverId * 100000 + Integer.parseInt(maxId));
    		}
    		mail.setId(serverId * 100000 + Integer.parseInt(maxId.substring(maxId.length() - 5)));
    		player.getMail().put(mail.getId(), mail);
    		MessageUtil.notiyNewMail(player, mail); 		
    	}else{
    		mail.setDtate(0);
    		  		
//    		int serverId = 0;
//	    	for(Integer sId :ServerListHandler.servers.keySet()){
//	    		
//	    		if(PlayerDAO.ins().getPlayerByPlayerId(sId, target)!= null){
//	    			serverId = sId;
//	    			break;
//	    		}
//	    	}

    		player = PlayerDAO.ins().getPlayerByPlayerNickName(serverId, username);
    		String maxId = String.valueOf(getMaxId(MailDAO.ins().getByPlayer(serverId, player.getPlayerId())));
    		if(maxId.length() < 5){
    			maxId = String.valueOf(serverId * 100000 + Integer.parseInt(maxId));
    		}
    		mail.setPlayerId(player.getPlayerId());
    		mail.setId(serverId * 100000 + Integer.parseInt(maxId.substring(maxId.length() - 5)));
    		MailDAO.ins().save(serverId, mail);
    	}
    	return mail;
    }


    public void loadPlayer(Player player, int serverId) {
		player.setMail(MailDAO.ins().getByPlayer(serverId, player.getPlayerId()));
    }
}
