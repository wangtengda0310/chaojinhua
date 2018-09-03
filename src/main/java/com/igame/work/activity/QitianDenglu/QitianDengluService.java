package com.igame.work.activity.QitianDenglu;

import com.igame.work.activity.ActivityConfigTemplate;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.MailService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QitianDengluService {
    private static List<ActivityConfigTemplate> configs = new ArrayList<>();
    public static void addActivityConfigTemplate(ActivityConfigTemplate template) {
        if (1001==template.getActivity_sign()) {
            configs.add(template);
        }
    }
    public static void loadPlayer(Player player) {
        Map<Integer, QitianDengluDto> activityDtos = QitianDengluDAO.ins().getByPlayer(player.getSeverId(), player.getPlayerId());
        if (activityDtos.isEmpty()) {
            QitianDengluDto dto = new QitianDengluDto();
            dto.setActivityId(1001);
            dto.setPlayerId(player.getPlayerId());
            dto.setLoginTimes(0);
            activityDtos.put(dto.getActivityId(), dto);
            QitianDengluDAO.ins().save(player.getSeverId(), dto);
        }
        activityDtos.values().stream()
        .filter(dto->dto.getActivityId()==1001)
        .forEach(dto->{
            dto.setLoginTimes(dto.getLoginTimes()+1);
            QitianDengluDAO.ins().update(player.getSeverId(), dto);

            configs.stream().filter(c->c.getOrder()==dto.getLoginTimes()).forEach(c->{
                MailService.ins().senderMail(player.getSeverId(), player.getPlayerId(), 1, 1, "", "", "", c.getActivity_drop());
            });
        });
    }
}
