import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.MonsterTemplate;
import com.igame.core.db.RankServiceDAO;
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
public class DAOProcessTest {

	public static void main(String[] args) {
		
		DataManager.ins();
		
		RankServiceDAO.ins().updateTest();

	}

}
