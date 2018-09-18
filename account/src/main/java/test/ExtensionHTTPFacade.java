package test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExtensionHTTPFacade extends HttpServlet
{
    @Override
    public void init()
    {
    }
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        Object result = "servlet is running ";
        resp.getWriter().write(result.toString());
    }
}