package com.igame.work.chat.service;

import com.igame.core.di.Inject;
import com.igame.util.MyUtil;
import com.igame.work.chat.dao.MessageBoardDAO;
import com.igame.work.chat.dto.MessageBoard;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.igame.work.chat.MessageContants.*;

/**
 * @author xym
 *
 * 留言板服务
 */
public class MessageBoardService {

    @Inject private MessageBoardDAO messageBoardDAO;
    @Inject private PlayerCacheService playerCacheService;

    public String getSType(int type, int id, int difficulty) {
        String sid;
        switch (type){
            case MSG_BOARD_TYPE_XING:   //星核之眼
                sid = type+","+id+","+"-1";
                break;
            case MSG_BOARD_TYPE_CHECKPOINT: //冒险关卡
                sid = type+","+id+","+difficulty;
                break;
            case MSG_BOARD_TYPE_WORLDEVENT: //世界事件
                sid = type+","+id+","+difficulty;
                break;
            case MSG_BOARD_TYPE_LONGMIAN:   //龙眠海峡
                sid = type+","+id+","+difficulty;
                break;
            case MSG_BOARD_TYPE_MONSTER:    //怪物图鉴
                sid = type+","+id+","+"-1";
                break;
            default:
                return "";
        }
        return sid;
    }

    private Map<Long, Map<String,List<String>>> messageBoard = new ConcurrentHashMap<>();    //记录留言板操作 <"like",留言ID列表>&&<"disLike",留言ID列表>

    public void afterPlayerLogin(Player player) {
        Map<String, List<String>> stringListMap = messageBoard.computeIfAbsent(player.getPlayerId(), pid -> new HashMap<>());
        stringListMap.put(MSG_BOARD_OPE_LIKE,new ArrayList<>());
        stringListMap.put(MSG_BOARD_OPE_DISLIKE,new ArrayList<>());
    }

    /**
     * 获取留言板
     * @param player 当前角色
     * @param type 类型
     * @return 留言板
     */
    public List<MessageBoard> getMessageBoard(Player player, String type){

        List<MessageBoard> messageBoard = messageBoardDAO.getMessageBoard(type);
        for (MessageBoard board : messageBoard) {

            board.setObjectId(board.get_id().toHexString());

            Map<String, List<String>> messageBoardOpe = this.messageBoard.get(player.getPlayerId());
            List<String> likes = messageBoardOpe.get(MSG_BOARD_OPE_LIKE);
            if (likes.contains(board.get_id().toString())){  //如果当前角色操作过
                board.setIsLike(board.getLike().contains(player.getPlayerId())? 0 : 1);
                board.setLikeCount(board.getIsLike() == 0? board.getLike().size()-1 : board.getLike().size()+1);
            }else {
                board.setIsLike(board.getLike().contains(player.getPlayerId())? 1 : 0);
                board.setLikeCount(board.getLike().size());
            }

            List<String> disLikes = messageBoardOpe.get(MSG_BOARD_OPE_DISLIKE);
            if (disLikes.contains(board.get_id().toString())){  //如果当前角色操作过
                board.setIsDislike(board.getDislike().contains(player.getPlayerId())? 0 : 1);
                board.setDislikeCount(board.getIsDislike() == 0? board.getDislike().size()-1 : board.getDislike().size()+1);
            }else {
                board.setIsDislike(board.getDislike().contains(player.getPlayerId())? 1 : 0);
                board.setDislikeCount(board.getDislike().size());
            }

            //cache
            Player cacheDto = playerCacheService.getPlayerById(board.getPlayerId());
            board.setName(cacheDto.getNickname());
            board.setPlayerFrameId(cacheDto.getPlayerFrameId());
            board.setPlayerHeadId(cacheDto.getPlayerHeadId());
            board.setPlayerLv(cacheDto.getPlayerLevel());
        }

        return messageBoard;
    }

    /**
     * 添加留言
     * @param type 留言类型
     * @param playerId 角色ID
     * @param userId uid
     * @param content 内容
     */
    public MessageBoard addMessageBoard(String type, long playerId, long userId, String content){

        MessageBoard messageBoard = new MessageBoard();
        messageBoard.setType(type);
        messageBoard.setPlayerId(playerId);
        messageBoard.setUserId(userId);
        messageBoard.setContent(content);
        messageBoard.setTime(new Date());

        return messageBoardDAO.saveMessageBoard(messageBoard);
    }

    /**
     * 给某个留言点赞或取消点赞
     * @param player 角色
     * @param oId 留言ID
     */
    public void likeMessageBoard(Player player,String oId){

        if (MyUtil.isNullOrEmpty(oId))
            return;

        Map<String, List<String>> messageBoardOpe = messageBoard.get(player.getPlayerId());

        List<String> likes = messageBoardOpe.get(MSG_BOARD_OPE_LIKE);
        if (likes.contains(oId)){
            likes.remove(oId);
        }else {
            likes.add(oId);
        }

    }

    /**
     * 反对某个留言或取消反对
     * @param player 角色
     * @param oId 留言ID
     */
    public void dislikeMessageBoard(Player player,String oId){

        if (MyUtil.isNullOrEmpty(oId))
            return;

        Map<String, List<String>> messageBoardOpe = messageBoard.get(player.getPlayerId());

        List<String> likes = messageBoardOpe.get(MSG_BOARD_OPE_DISLIKE);
        if (likes.contains(oId)){
            likes.remove(oId);
        }else {
            likes.add(oId);
        }

    }

    /**
     * 保存
     */
    public void saveMessageBoard(Player player){

        Map<String, List<String>> messageBoardOpe = messageBoard.get(player.getPlayerId());

        List<String> likes = messageBoardOpe.get(MSG_BOARD_OPE_LIKE);
        for (String oId : likes) {
            MessageBoard messageBoard = messageBoardDAO.getMessageBoardById(oId);
            if (messageBoard.getLike().contains(player.getPlayerId())){ //取消点赞
                messageBoard.getLike().remove(player.getPlayerId());
            }else { //点赞
                messageBoard.getLike().add(player.getPlayerId());
                //todo 解锁成就
            }
            messageBoardDAO.updateMessageBoard(messageBoard);
        }

        List<String> disLikes = messageBoardOpe.get(MSG_BOARD_OPE_DISLIKE);

        for (String oId : disLikes) {
            MessageBoard messageBoard = messageBoardDAO.getMessageBoardById(oId);
            if (messageBoard.getDislike().contains(player.getPlayerId())){  //取消反对
                messageBoard.getDislike().remove(player.getPlayerId());
            }else { //反对
                messageBoard.getDislike().add(player.getPlayerId());
            }
            messageBoardDAO.updateMessageBoard(messageBoard);
        }
    }

}
