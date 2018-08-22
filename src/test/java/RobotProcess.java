import java.util.Map;

import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.service.RobotService;

public class RobotProcess {

	public static void main(String[] args) {
		
		RobotService.ins().load();
		for(Map.Entry<Integer,Map<String,RobotDto>> m : RobotService.ins().getRobot().entrySet()){
			int serverId = m.getKey();
			Map<String,RobotDto> v = m.getValue();
			for(RobotDto r : v.values()){
				for(MatchMonsterDto mt :r.getMon()){
					if(MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mt.getMonsterId()) == null){
						r.setDtate(3);
						break;
					}
				}
			}
			System.out.println(v.size());
		}
		
		RobotService.ins().save();
		
	}

}
