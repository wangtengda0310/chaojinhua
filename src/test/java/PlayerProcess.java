import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.MonsterTemplate;
import com.igame.util.MyUtil;
import com.igame.work.monster.dao.MonsterDAO;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PlayerProcess {

	public static void main(String[] args) {
		
		DataManager.ins();
		
		List<Player> ll = PlayerDAO.ins().getALLPlayer(1);
		for(Player mm : ll){
			mm.getWuMap().clear();
			 PlayerDAO.ins().updatePlayerTest(mm, 1);
		}

	}

}
