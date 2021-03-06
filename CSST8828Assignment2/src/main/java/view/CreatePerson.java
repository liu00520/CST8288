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
@WebServlet(name = "CreatePerson", urlPatterns = {"/CreatePerson"})
public class CreatePerson extends HttpServlet {

    private String errorMessage = null;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
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
            out.println("<title>Create Person</title>");            
            out.println("</head>");
           
            out.println("<body>");
            out.println( "<div style=\"text-align: center;\">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
            out.println( "<form method=\"post\">" );
            
            out.println("FirstName:");
            out.printf("<br><input type=\"text\" name=\"%s\" value=\"\"><br><br>", PersonLogic.FIRST_NAME );
            out.println("LastName:");
            out.printf("<br><input type=\"text\" name=\"%s\" value=\"\"><br><br>", PersonLogic.LAST_NAME );
            out.println("Phone:");
            out.printf("<br><input type=\"text\" name=\"%s\" value=\"\"><br><br>", PersonLogic.PHONE );
            out.println("Address:");
            out.printf("<br><input type=\"text\" name=\"%s\" value=\"\"><br><br>", PersonLogic.ADDRESS );
            out.println("Birthdate:");
            out.printf("<br><input type=\"datetime-local\" step=\"1\" name=\"%s\" value=\"\"><br><br>", PersonLogic.BIRTH );
            out.println( "<input type=\"submit\" name=\"view\" value=\"Add and View\">" );
            out.println( "<input type=\"submit\" name=\"add\" value=\"Add\">" );
            out.println( "</form>" );
            if(errorMessage != null) {
                out.println( "<p color=red>" );
                out.println( "<font color=red size=4px>" );
                out.println( errorMessage );
                out.println( "</font>" );
                out.println( "</p>" );                
            }
            out.println( "<pre>" );
            out.println( "Submitted keys and values:" );
            out.println( toStringMap( request.getParameterMap() ) );
            out.println( "</pre>" );
            out.println( "</div>" );
            out.println( "</div>" );
            out.println("</body>");
            out.println("</html>");
        }
    }
    private String toStringMap( Map<String, String[]> values ) {
        StringBuilder builder = new StringBuilder();
        values.forEach( ( e, v ) -> builder.append( "Key=" ).append( e )
                .append( ", " ).append( "Value/s=" ).append( Arrays.toString( v ) )
                .append( System.lineSeparator() ) );
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
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        PersonLogic personLogic = LogicFactory.getFor("Person");
        try{
            Person person = personLogic.createEntity(request.getParameterMap());
            personLogic.add(person);
        } catch(Exception e){
            errorMessage = e.getMessage();
        }
        if( request.getParameter("add") != null ) {
        processRequest(request, response);
        }
        else if(request.getParameter("view") != null) { 
            response.sendRedirect("PersonTable");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Person Entity Creation";
    }

}
