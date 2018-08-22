import com.igame.core.data.DataManager;
import com.igame.core.db.RankServiceDAO;

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
