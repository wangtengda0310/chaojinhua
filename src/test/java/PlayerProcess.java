import com.igame.core.data.DataManager;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PlayerProcess {

	public static void main(String[] args) {
		
		DataManager.load("resource");
		
		List<Player> ll = PlayerDAO.ins().getALLPlayer(1);
		for(Player mm : ll){
			mm.getWuMap().clear();
			 PlayerDAO.ins().updatePlayerTest(mm, 1);
		}

	}

}
