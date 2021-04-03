package view;

import entity.Person;
import java.io.IOException;
import java.io.PrintWriter;
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
 * @author markg
 */
@WebServlet(name = "DonateBloodForm", urlPatterns = {"/DonateBloodFrom"})
public class DonateBloodForm extends HttpServlet {

    private String message;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Donation Blood Form</title>"); 
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/bloodform.css\">");
            out.println("</head>");
            out.println("<body>");
            out.println("<form method=\"post\">");
            //use this to create a border & only 1 div wrapped for the input type
            out.println("<div class=\"grid-container\">");
            out.println("<div class=\"item\"><h2>Person</h2></div>");
            out.println("<div class=\"item\">First Name</div>");
            out.print("<div>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"></div>", PersonLogic.FIRST_NAME);
            
            out.println("Last Name");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\">", PersonLogic.LAST_NAME);
            out.println("Phone");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\">", PersonLogic.PHONE);
            out.println("Address");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\">", PersonLogic.ADDRESS);
            out.println("Date of Birth");
            out.printf("<input type=\"datetime-local\" step=\"1\" name=\"%s\" value=\"\">", PersonLogic.BIRTH);
            out.println("</div>");
            
            //insert here
            out.println("<input type=\"submit\" name=\"add\" value=\"Add\">" );
            out.println("</form>");
                      
            if(message != null && !message.isEmpty()) {    
                out.println("<p>");
                out.println(message);
                out.println("</p>");                
            }
            out.println("<pre>" );
            out.println("Submitted keys and values:" );
            out.println(toStringMap( request.getParameterMap()));
            out.println("</pre>" );
            out.println("</body>");
            out.println("</html>");
        }
    }
        
    private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((e, v) -> builder.append("Key=").append(e)
                .append(", ").append("Value/s=")
                .append( Arrays.toString(v))
                .append( System.lineSeparator()));
        return builder.toString();
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
        processRequest(request, response);
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
        processRequest(request, response);
        
        PersonLogic personLogic =  LogicFactory.getFor("Person");
        
        try {
            Person person = personLogic.createEntity(request.getParameterMap());
            personLogic.add(person);
            
        } catch(Exception e) {
            message = e.getMessage();
        }
        if(request.getParameter("add") != null) {
            processRequest( request, response );
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Blood Donation Form For All Entities";
    }
}
