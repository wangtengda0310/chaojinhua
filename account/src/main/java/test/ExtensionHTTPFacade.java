package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfoxserver.v2.SmartFoxServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExtensionHTTPFacade extends HttpServlet
{
    @Override
    public void init()
    {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String collect = SmartFoxServer.getInstance()
                .getZoneManager()
                .getZoneList()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
        Object result = "servlet is running\n"+collect;

//		request.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String ip = req.getParameter("ip");

        System.out.println("username:" + username);
        System.out.println("password:" + password);
        if(!Toolkit.vertifyStrNotNull(username) || !Toolkit.vertifyStrNotNull(password)){
            CRetVO re = new CRetVO(Protrol.ACCOUNT_LOGIN);
            re.setState(ErrorCode.ERR);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(re);
            resp.getWriter().write(json);
            return;
        }
        resp.setContentType("text/plain;charset=UTF-8");
        CRetVO re = AccountDao.ins().getAccuont(username, password,"2".equals(ip));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(re);
//		JSONObject jo = new JSONObject();
        resp.getWriter().write(json);
    }
}