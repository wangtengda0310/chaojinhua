package com.igame.work.chat.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.chat.dto.MessageBoard;
import com.igame.work.chat.service.MessageBoardService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xym
 *
 * 获取留言板
 */
public class MessageBoardGetHandler extends BaseHandler{

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

        int type = jsonObject.getInt("type");
        int id = jsonObject.getInt("id");
        int difficulty = jsonObject.getInt("difficulty");

        vo.addData("type", type);
        vo.addData("id", id);
        vo.addData("difficulty", difficulty);

        String sType = MessageBoardService.ins().getSType(type,id,difficulty);
        if (sType.isEmpty()){
            sendError(ErrorCode.PARAMS_INVALID,MProtrol.toStringProtrol(MProtrol.MESSAGE_BOARD_GET),vo,user);
            return;
        }

        List<MessageBoard> voList = new ArrayList<>();
        List<MessageBoard> messageBoards = MessageBoardService.ins().getMessageBoard(player,sType);

        //根据赞同数取前三
        voList.addAll(getHot(messageBoards,3));
        //其余按时间
        if (messageBoards.size() > 0){
            messageBoards.sort(MessageBoard::compareTo);
            voList.addAll(messageBoards);
        }

        vo.addData("messageBoard",voList);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.MESSAGE_BOARD_GET),vo,user);
    }

    /**
     * 根据点赞数取前X条,并从原列表中移除
     * @param messageBoards 留言列表
     * @param size X
     * @return 前X条
     */
    private static List<MessageBoard> getHot(List<MessageBoard> messageBoards,int size) {

        List<MessageBoard> ret = new ArrayList<>();

        if (messageBoards == null) {
            return ret;
        }

        if (messageBoards.size() <= size){
            ret.addAll(messageBoards);
            messageBoards.clear();
            return ret;
        }

        for (int i = 0; i < size; i++) {

            MessageBoard temp = messageBoards.get(0);
            int flag = 0; // 将当前下标定义为最大值下标

            for (int j = 1; j < messageBoards.size(); j++) {
                if (messageBoards.get(j).getLikeCount() > temp.getLikeCount()) {// a[j] < temp 从小到大排序；a[j] > temp 从大到小排序
                    temp = messageBoards.get(j);
                    flag = j; // 如果有大于当前最大值的关键字将此关键字的下标赋值给flag
                }
            }

            if (flag != 0) {
                messageBoards.set(flag,messageBoards.get(0));
                ret.add(temp);
                messageBoards.remove(0);
            }else {
                ret.add(temp);
                messageBoards.remove(0);
            }

        }

        return ret;
    }

    public static void main(String[] args) {

        List<MessageBoard> list = new ArrayList<>();
        for (int i = 0; i <3; i++) {
            MessageBoard messageBoard = new MessageBoard();
            messageBoard.setObjectId(""+i);
            messageBoard.setLikeCount(i);

            list.add(messageBoard);
        }

        System.out.println(list.size()+":"+list);
        List<MessageBoard> hot = getHot(list,3);
        System.out.println(list.size()+":"+list);
        System.out.println(hot.size()+":"+hot);
    }
}
