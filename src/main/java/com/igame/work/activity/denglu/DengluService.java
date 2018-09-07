package com.igame.work.activity.denglu;

import com.igame.util.DateUtil;
import com.igame.work.activity.ActivityConfigTemplate;
import com.igame.work.user.dto.Player;

import java.util.*;
import java.util.stream.Collectors;

public class DengluService {
    public static Map<Integer, List<ActivityConfigTemplate>> configs = new HashMap<>();

    public static void addActivityConfigTemplate(ActivityConfigTemplate template) {
        if (template.getGift_bag() == 3) {
            configs.putIfAbsent(template.getActivity_sign(), new ArrayList<>());
            configs.get(template.getActivity_sign()).add(template);
        }
    }

    public static void loadPlayer(Player player) {

        Date now = new Date();
        // 配置中没找到 认为是活动下线 把数据清掉
        Map<Integer, DengluDto> byPlayer = DengluDAO.ins().getByPlayer(player.getPlayerId());
        byPlayer.values().stream()
                .filter(a -> !configs.containsKey(a.getActivityId())
                        || configs.get(a.getActivityId()).stream().anyMatch(c->!c.isActive(player, now)))
                .forEach(a -> DengluDAO.ins().remove(a));

        // 新的登录活动开始后 删除上轮的数据
        // id相同id时间有修改的话 认为是新一轮活动 把上一轮活动数据清掉
        // todo 修改数据库以活动为主数据后 统一在加载数据的地方处理
        byPlayer.values().stream()
                .filter(a -> configs.containsKey(a.getActivityId()))
                .filter(a -> configs.get(a.getActivityId()).stream()
                        .anyMatch(c ->
                                    !String.valueOf(c.startTime(player).getTime()).equals(a.getOpenTime())))
                .peek(a->
                        configs.get(a.getActivityId()).stream().findAny()
                                .ifPresent(c->{
                                    if(a.getOpenTime()==null || c.startTime(player).getTime()>Long.parseLong(a.getOpenTime())) {
                                        a.setOpenTime(String.valueOf(c.startTime(player).getTime()));
                                    }
                                }))
                .forEach(a -> a.setRecord(new int[configs.get(a.getActivityId()).size()]));

        configs.keySet().forEach(configId -> {
            byPlayer.computeIfAbsent(configId, activityId -> {
                DengluDto dto = new DengluDto();
                dto.setActivityId(activityId);
                dto.setPlayerId(player.getPlayerId());
                dto.setRecord(new int[configs.get(activityId).size()]);
                configs.get(activityId).stream().findAny()
                        .ifPresent(c->dto.setOpenTime(String.valueOf(c.startTime(player).getTime())));
                DengluDAO.ins().save(dto);
                return dto;
            });
        });

        byPlayer.forEach((configId,a) -> {
            configs.get(a.getActivityId()).stream()
                    .filter(c->c.isActive(player, now))
                    .filter(c -> a.getRecord()[c.getOrder()-1] != 2)
                    .filter(c -> c.getOrder() - 1 <= DateUtil.getIntervalDays(c.startTime(player), now))  // order从1开始 interval days从0开始
                    .forEach(c -> a.getRecord()[c.getOrder()-1] = 1);
            DengluDAO.ins().update(a);
        });

    }

    public static Object clientData(Player player) {
        Map<Integer, DengluDto> byPlayer = DengluDAO.ins().getByPlayer(player.getPlayerId());
        Map<Integer, String> dengluRecords = new HashMap<>();
        configs.forEach((configId, configs) -> {

            dengluRecords.put(configId, Arrays.stream(byPlayer.get(configId).getRecord())
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining(",")));
        });
        return dengluRecords;
    }
}
