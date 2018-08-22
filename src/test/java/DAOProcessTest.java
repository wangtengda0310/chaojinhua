import com.igame.core.data.DataManager;
import com.igame.work.system.RankServiceDAO;

/**
 * 
 * @author Marcus.Z
 *
 */
public class DAOProcessTest {

	public static void main(String[] args) {
		
		DataManager.load();
		
		RankServiceDAO.ins().updateTest();

	}

}
