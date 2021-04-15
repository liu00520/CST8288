package view;

import entity.Person;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.LogicFactory;
import logic.PersonLogic;

/**
 *
 * @author Mark Newport
 */
@WebServlet(name = "CreatePersonJSP", urlPatterns = {"/CreatePersonJSP"})
public class CreatePersonJSP extends HttpServlet {

    String message;
    
    private void createPerson(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        request.setAttribute("columns", getPerson().getColumnCodes());
        request.setAttribute("columnNames", getPerson().getColumnNames());
        request.setAttribute("request", toStringMap(request.getParameterMap()));
        request.setAttribute("path", path);
        request.setAttribute("message", message);
        request.setAttribute("title", path.substring(1));
        request.getRequestDispatcher("jsp/CreatePersonJSP.jsp").forward(request, response);
        }
    
    private String toStringMap(Map<String, String[]>e) {
        StringBuilder builder = new StringBuilder();
        e.keySet().forEach((k) -> {
            builder.append("key=").append(k).append(",")
                    .append("Value/s=").append(Arrays.toString(e.get(k)))
                    .append(System.lineSeparator());
        });
        return builder.toString();
    }
    
    private PersonLogic getPerson() {
        PersonLogic logic = LogicFactory.getFor("Person");
        return logic;
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        createPerson(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        try {
            Person person = getPerson().createEntity(request.getParameterMap());
            getPerson().add(person);
        } catch (Exception e) {
            message = e.getMessage();
        }
        if(request.getParameter("add") != null) {
        createPerson(request, response);
        }
        else if(request.getParameter("view") != null) {
            response.sendRedirect("PersonTable");
        }
    }

    /**
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create Person JSP";
    }
}
