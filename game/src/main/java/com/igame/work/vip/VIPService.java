package com.igame.work.vip;

import com.igame.core.di.Inject;
import com.igame.core.di.LoadXml;
import com.igame.core.event.EventService;
import com.igame.core.event.EventType;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.handler.RetVO;
import com.igame.util.DateUtil;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.PlayerEvents;
import com.igame.work.user.dto.Player;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author xym
 * <p>
 * 会员服务
 */
public class VIPService extends EventService {
    @LoadXml("vippack.xml")
    VipPackData vipPackData;
    @LoadXml("vipdata.xml")
    private VipData vipData;
    @LoadXml("viplevel.xml")
    private VipLevelData vipLevelData;

    @Inject
    private VIPConfig vipConfig;
    @Inject
    private VipDAO dao;

    /**
     * 推送玩家vip更新
     */
    private void notifyVipPrivilegesChange(Player player) {

        //推送
        RetVO vo = new RetVO();
        vo.addData("vipLv", player.getVip());
        vo.addData("le", needExp(player));

        MessageUtil.sendMessageToPlayer(player, MProtrol.VIP_UPDATE, vo);
    }

    private double needExp(Player player) {
        BigDecimal bigDecimal1 = new BigDecimal(String.valueOf(player.getTotalMoney()));
        double reduce = vipLevelData.getAll().stream()
                .filter(c -> c.getVipExp() >= 0)
                .sorted(Comparator.comparingInt(VipLevelTemplate::getVipLevel))
                .mapToDouble(VipLevelTemplate::getVipExp)
                .reduce(0, (a, b) -> a + b < player.getTotalMoney() ? a + b : a);
        BigDecimal subtrahend2 = new BigDecimal(String.valueOf(reduce));
        return bigDecimal1.subtract(subtrahend2).doubleValue();
    }

    @Override
    protected PlayerEventObserver playerObserver() {
        return new PlayerEventObserver() {
            @Override
            public EventType interestedType() {
                return PlayerEvents.RECHARGE;
            }

            @Override
            public void observe(Player player, EventType eventType, Object event) {
                checkVipLv(player);
            }
        };
    }

    /**
     * 增加VIP等级
     */
    private void checkVipLv(Player player) {

        int vip = player.getVip();

        int level = vipLevelData.getAll().stream()
                .filter(c->c.getVipExp()>0)
                .sorted(Comparator.comparingDouble(VipLevelTemplate::getVipExp))
                .reduce(new LinkedList<VipLevelTemplate>(), (l, e) -> {
                            double sum = l.stream().mapToDouble(VipLevelTemplate::getVipExp).sum();
                            if (sum + e.getVipExp() < player.getTotalMoney()) {
                                l.add(e);
                            }
                            return l;
                        },
                        (l1, l2) -> {
                            l2.addAll(l1);
                            return l2;
                        })
                .stream()
                .mapToInt(VipLevelTemplate::getVipLevel)
                .max()
                .orElse(vip);

        if (level > vip) {
            player.setVip(level);
            notifyVipPrivilegesChange(player);
        }
    }

    private Map<Long, VipDto> dtos = new ConcurrentHashMap<>();

    public void afterPlayerLogin(Player player) {
        VipDto value = dao.get(player.getPlayerId());
        if (value == null) {
            value = new VipDto();
            value.playerId = player.getPlayerId();
            value.firstPack = new int[vipLevelData.size()];
            dao.save(value);
        }
        VipDto v = value;
        vipPackData.getAll().stream()
                .filter(c->player.getVip()>=c.getVipLv())
                .forEach(c->{
            if (v.firstPack[c.getVipLv()]!=2) {
                v.firstPack[c.getVipLv()] = 1;
            }
        });
        dtos.put(player.getPlayerId(), value);
    }

    VipDto getVipData(Player player) {
        return dtos.get(player.getPlayerId());
    }

    public void updatePlayer(Player player) {
        dao.update(dtos.remove(player.getPlayerId()));
    }

    public Object toClientData(Player player) {
        VipDto vipDto = dtos.get(player.getPlayerId());

        Map<String, Object> ret = new HashMap<>();
        ret.put("d", receivedTodayPack(vipDto));
        ret.put("f", getFirstPackStatus(vipDto));
        ret.put("l", player.getVip());
        ret.put("le", needExp(player));
        return ret;
    }

    int receivedTodayPack(VipDto vipDto) {
        return DateUtil.formatToday().equals(vipDto.lastDailyPack) ? 2 : 1;
    }

    String getFirstPackStatus(VipDto vipDto) {
        return Arrays.stream(vipDto.firstPack).boxed().map(String::valueOf).collect(Collectors.joining(","));
    }

    public int getPhBuyCount(Player player) {
        return 0;
    }

    public int getGoldBuyCountLimit(Player player) {
        return 0;
    }

    public long getFriendReceivePhyLimit(Player player) {
        return 0;
    }

    public long getFriendExploreAccLimit(Player player) {
        return 0;
    }

    public boolean canTenLottery() {
        return false;
    }

    public int getResCheckpointHourLimit(Player player) {
        return 60;
    }
}
