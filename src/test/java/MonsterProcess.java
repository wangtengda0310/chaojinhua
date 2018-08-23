import com.google.common.collect.Lists;
import com.igame.core.data.DataManager;
import com.igame.util.MyUtil;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.dao.MonsterDAO;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.work.monster.dto.Monster;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterProcess {

	public static void main(String[] args) {
		
		DataManager.load("resource");
		
		List<Monster> ll = MonsterDAO.ins().getALLMonster(1);
		int i = 0;
		for(Monster mm : ll){
			MonsterTemplate mt = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm.getMonsterId());
			if(mt != null){
				String[] skillMt = new String[0];
				if(mt.getSkill() != null){
					skillMt = mt.getSkill().split(",");
				}
				Map<Integer,Integer> map = mm.getSkillMap();
				List<Integer> re = Lists.newArrayList();
				for(String skill : skillMt){
					if(!map.containsKey(Integer.parseInt(skill))){
						map.put(Integer.parseInt(skill),1);
					}
				}
				for(Integer r : map.keySet()){
					if(mt.getSkill() == null || mt.getSkill().indexOf(String.valueOf(r)) == -1){
						re.add(r);
					}
				}
				for(Integer r : re){
					map.remove(r);
				}
				String[] equs = mm.getEquip().split(",");
				boolean change = false;
				if(mm.getLevel() >= 5 && "-1".equals(equs[0])){
					change = true;
					equs[0] = "0";
				}
				if(mm.getLevel() >= 15 && "-1".equals(equs[1])){
					change = true;
					equs[1] = "0";
				}
				if(mm.getLevel() >= 22 && "-1".equals(equs[2])){
					change = true;
					equs[2] = "0";
				}
				if(mm.getLevel() >= 30 && "-1".equals(equs[3])){
					change = true;
					equs[3] = "0";
				}
				if(change){
					mm.setEquip(MyUtil.toString(equs, ","));
				}
				MonsterDAO.ins().updateMonster(1, mm);
				i++;
				
			}
		}
		System.out.println("process mon:" + i);

	}

}
