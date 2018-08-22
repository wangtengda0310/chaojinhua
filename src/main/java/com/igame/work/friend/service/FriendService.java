package com.igame.work.friend.service;

import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.GuanQiaDataManager;
import com.igame.work.checkpoint.data.TangSuoTemplate;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.dto.TangSuoDto;
import com.igame.work.friend.dao.FriendDAO;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.dto.FriendInfo;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.PlayerCacheDto;
import com.igame.work.user.service.PlayerCacheService;

import java.util.*;

import static com.igame.work.friend.FriendConstants.FRIEND_STATE_CAN_HELP;
import static com.igame.work.friend.FriendConstants.FRIEND_STATE_NO_HELP;

/**
 * @author xym
 *
 * 好友模块服务
 *
 *      本服务内方法涉及当前角色更新均不推送
 */
public class FriendService {


    private static final FriendService domain = new FriendService();

    public static final FriendService ins() {
        return domain;
    }

    /**
     * 获取好友探索列表
     * @param friendPlayer 好友角色对象
     * @return 正在探索中且剩余时间大于0的探索小队
     */
    public List<TangSuoDto> getExploreList(Player friendPlayer) {

        //计算剩余时间
        for(TangSuoTemplate ts : GuanQiaDataManager.TangSuoData.getAll()){
            if(MyUtil.hasCheckPoint(friendPlayer.getCheckPoint(), String.valueOf(ts.getUnlock())) && friendPlayer.getTangSuo().get(ts.getNum()) == null){
                friendPlayer.getTangSuo().put(ts.getNum(), new TangSuoDto(ts));
            }
        }

        long now = System.currentTimeMillis();
        friendPlayer.getTangSuo().values().forEach(e -> e.calLeftTime(now));

        List<TangSuoDto> exploreList = new ArrayList<>();
        for (TangSuoDto tangSuoDto : friendPlayer.getTangSuo().values()) {
            if (tangSuoDto.getLeftTime() > 0)
                exploreList.add(tangSuoDto);
        }

        return exploreList;
    }

    /**
     * 获取帮助状态
     * @param exploreList 探索列表
     * @return 0等于不可帮助，1等于可帮助
     */
    public int getHelpState(Collection<TangSuoDto> exploreList) {

        int helpState = FRIEND_STATE_NO_HELP;  //0等于不可帮助，1等于可帮助

        for (TangSuoDto value : exploreList) {
            //如果剩余时间大于0并且还没人帮助
            if (value.getLeftTime() > 0 && value.getIsHelp() == 0)
                helpState = FRIEND_STATE_CAN_HELP;
        }

        return helpState;
    }

    /**
     * 添加好友请求
     *      如果对方在线则更新缓存并推送，不在线则存库
     *      ps:推送对方好友请求更新
     * @param sendPlayer 发送好友请求的角色
     * @param recPlayerId 接收好友请求的角色
     */
    public void addReqFriend(Player sendPlayer, long recPlayerId) {

        Player reqPlayer = SessionManager.ins().getSessionByPlayerId(recPlayerId);
        PlayerCacheDto reqPlayerCache = PlayerCacheService.ins().getPlayerById(sendPlayer.getSeverId(), recPlayerId);

        if (reqPlayer == null && reqPlayerCache == null){
            return;
        }

        Friend friend = new Friend(sendPlayer);
        if (reqPlayer != null && !reqPlayer.getFriends().getReqFriends().contains(friend)){ //在线 并且自己不存在于对方好友列表中

            reqPlayer.getFriends().getReqFriends().add(friend);

            //推送
            List<Friend> reqFriends = new ArrayList<>();
            reqFriends.add(new Friend(sendPlayer));
            pushReqFriends(reqPlayer,new ArrayList<>(),reqFriends);

        }else{ //不在线

            //存库
            FriendInfo friendInfo = FriendDAO.ins().getFriendInfoByPlayerId(sendPlayer.getSeverId(), recPlayerId);
            if (friendInfo == null){    //todo 处理老数据

                friendInfo = new FriendInfo();

                friendInfo.setPlayerId(recPlayerId);

                friendInfo.getReqFriends().add(friend);

                FriendDAO.ins().saveFriendInfo(sendPlayer.getSeverId(), friendInfo);
            }else {
                if (!friendInfo.getReqFriends().contains(friend)){  //自己不存在于对方好友列表中
                    friendInfo.getReqFriends().add(friend);
                    FriendDAO.ins().updateFriends(sendPlayer.getSeverId(),friendInfo);
                }
            }
        }
    }

    /**
     * 删除好友请求
     *      必定在线，更新缓存
     *      ps:不推送
     * @param player 角色
     * @param delReqPlayerId 待删除的好友ID
     */
    public void delReqFriend(Player player, long delReqPlayerId){

        List<Friend> reqFriends = player.getFriends().getReqFriends();

        for (int i = 0; i < reqFriends.size(); i++) {
            Friend reqFriend = reqFriends.get(i);
            if (reqFriend.getPlayerId() == delReqPlayerId) {
                reqFriends.remove(reqFriend);
            }
        }

    }

    /**
     * 双向添加好友
     *      更新当前角色缓存，如果对方在线则更新缓存，不在线则存库并更新cache
     *      ps:只推送对方好友更新
     * @param player 角色
     * @param reqPlayerId 好友ID
     */
    public void addFriend(Player player, long reqPlayerId){

        Player reqPlayer = SessionManager.ins().getSessionByPlayerId(reqPlayerId);
        PlayerCacheDto reqPlayerCache = PlayerCacheService.ins().getPlayerById(player.getSeverId(), reqPlayerId);

        if (reqPlayer == null && reqPlayerCache == null){
            return;
        }

        //当前角色添加好友
        Friend reqFriend;
        if (reqPlayer != null) {
            reqFriend = new Friend(reqPlayer);
        }else {
            reqFriend = new Friend(reqPlayerCache);
        }

        player.getFriends().getCurFriends().add(reqFriend);

        //对方添加好友
        if (reqPlayer != null){ //在线,更新缓存

            reqPlayer.getFriends().getCurFriends().add(new Friend(player));

            //推送对方好友列表更新
            List<Friend> reqAddFriends = new ArrayList<>();
            reqAddFriends.add(new Friend(player));

            pushFriends(reqPlayer,new ArrayList<>(),reqAddFriends);


        }else { //不在线,更新缓存并存库

            FriendInfo friendInfo = FriendDAO.ins().getFriendInfoByPlayerId(player.getSeverId(), reqPlayerId);

            if (friendInfo == null){

                friendInfo = new FriendInfo();

                friendInfo.setPlayerId(reqPlayerId);

                //添加好友并增加当前好友数量
                reqPlayerCache.addCurFriendCount(1);
                friendInfo.getCurFriends().add(new Friend(player));

                FriendDAO.ins().saveFriendInfo(player.getSeverId(), friendInfo);
            }else {

                //更新缓存
                reqPlayerCache.addCurFriendCount(1);

                //添加好友并增加当前好友数量
                friendInfo.getCurFriends().add(new Friend(player));

                FriendDAO.ins().updateFriends(player.getSeverId(), friendInfo);
            }
        }
    }

    /**
     * 双向删除好友
     *      更新当前角色缓存，如果对方在线则更新缓存，不在线则存库并更新cache
     *      ps:只推送对方好友更新
     * @param player 当前角色
     * @param delPlayerId 好友ID
     *
     */
    public void delFriend(Player player, long delPlayerId) {

        Player delPlayer = SessionManager.ins().getSessionByPlayerId(delPlayerId);
        PlayerCacheDto delPlayerCache = PlayerCacheService.ins().getPlayerById(player.getSeverId(), delPlayerId);

        //如果对方在线，推送好友更新，不在线，则减少好友数量并存库
        if (delPlayer == null && delPlayerCache == null){
            return;
        }

        //当前角色删除好友
        Friend delFriend;
        if (delPlayer != null) {
            delFriend = new Friend(delPlayer);
        }else {
            delFriend = new Friend(delPlayerCache);
        }

        player.getFriends().getCurFriends().remove(delFriend);

        //如果对方在线，推送好友更新，不在线，则增加好友数量并存库
        if (delPlayer != null){

            //对方删除好友
            delPlayer.getFriends().getCurFriends().remove(new Friend(player));

            //推送对方好友列表更新
            List<Long> reqDelFriends = new ArrayList<>();
            reqDelFriends.add(player.getPlayerId());

            pushFriends(delPlayer,reqDelFriends,new ArrayList<>());

        } else {

            //更新cache
            delPlayerCache.addCurFriendCount(-1);

            //删除好友并减少当前好友数量
            FriendInfo friendInfo = FriendDAO.ins().getFriendInfoByPlayerId(player.getSeverId(), delPlayerId);

            friendInfo.getCurFriends().remove(new Friend(player));
            FriendDAO.ins().updateFriends(player.getSeverId(), friendInfo);
        }

    }

    /**
     * 推送好友列表更新
     * @param player 推送给谁
     * @param delFriends 删除的好友ID
     * @param addFriends 增加的好友
     */
    public void pushFriends(Player player,List<Long> delFriends,List<Friend> addFriends){
        RetVO vo = new RetVO();
        vo.addData("delFriend",delFriends);
        vo.addData("addFriend",addFriends);
        MessageUtil.sendMessageToPlayer(player, MProtrol.FRIEND_LIST_UPDATE, vo);
    }

    /**
     * 推送好友请求更新
     * @param player 角色
     * @param delReqFriends 删除的好友请求
     * @param addReqFriends 增加的好友请求
     */
    public void pushReqFriends(Player player,List<Long> delReqFriends,List<Friend> addReqFriends){
        RetVO vo = new RetVO();
        vo.addData("delReqFriend",delReqFriends);
        vo.addData("addReqFriend",addReqFriends);
        MessageUtil.sendMessageToPlayer(player, MProtrol.FRIEND_REQ_UPDATE, vo);
    }

    /**
     * 推送体力赠送
     * @param player 角色
     * @param playerId 赠送体力的好友角色ID
     */
    public void pushFriendPhy(Player player,long playerId){
        RetVO vo = new RetVO();
        vo.addData("fromPlayerId",playerId);
        MessageUtil.sendMessageToPlayer(player, MProtrol.FRIEND_PHY_UPDATE, vo);
    }

    /**
     * 初始化玩家好友信息
     * @param player 角色
     */
    public void initFriend(Player player) {

        FriendInfo friendInfo = new FriendInfo();

        friendInfo.setPlayerId(player.getPlayerId());

        player.setFriends(friendInfo);
    }

    /**
     * 零点执行
     * @param player
     */
    public void zero(Player player) {

        //刷新时间，如更改为每日4点执行，需将此值改为4
        int hour = 12;

        //计算刷新时间
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY,hour);
        instance.set(Calendar.MINUTE,0);
        instance.set(Calendar.SECOND,0);
        instance.set(Calendar.MILLISECOND,0);

        FriendInfo friendInfo = player.getFriends();

        List<Friend> curFriends = friendInfo.getCurFriends();
        for (Friend curFriend : curFriends) {
            curFriend.setGivePhy(0);    //重置体力赠送状态

            Date givePhyDate = curFriend.getGivePhyDate();
            if (givePhyDate == null){   //处理老数据
                curFriend.setReceivePhy(0);
            }else if (givePhyDate != null && givePhyDate.before(instance.getTime())){  //如果赠送时间在刷新时间之前，则重置体力领取状态
                curFriend.setReceivePhy(0);
            }
        }
    }

    public void loadPlayer(Player player) {
        player.setFriends(FriendDAO.ins().getFriendInfoByPlayerId(player.getSeverId(), player.getPlayerId()));
        long explorerCount = player.getFriends().getCurFriends().stream().filter(friend -> friend.getHelpAcc() == 1).count();
        player.getFriends().setExploreCount((int) explorerCount);
        player.getFriends().setMaxFriendCount(20);
        long phyCount = player.getFriends().getCurFriends().stream().filter(friend -> friend.getReceivePhy() == 2).count();
        player.getFriends().setPhysicalCount((int) phyCount);
    }
}
