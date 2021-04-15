package view;

import entity.BloodBank;
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
import logic.BloodBankLogic;
import logic.LogicFactory;
import logic.PersonLogic;


/**
 *
 * @author jiali
 */
@WebServlet(name = "CreateBloodBank", urlPatterns = {"/CreateBloodBank"})
public class CreateBloodBank extends HttpServlet {
    private String errorMessage = null;
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
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>CreateBloodBank</title>");            
            out.println("</head>");
            
            out.println("<body>");
            out.println( "<div style=\"text-align: center;\">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
            out.println( "<form method=\"post\">" );
            
            out.println("OwnerID:");
            out.printf("<br><input type=\"number\" name=\"%s\" value=\"\"><br><br>", BloodBankLogic.OWNER_ID );
            out.println("BankName:");
            out.printf("<br><input type=\"text\" name=\"%s\" value=\"\"><br><br>", BloodBankLogic.NAME );
            out.println("PrivatelyOwned?");
            out.printf("<br><input type=\"text\" name=\"%s\" value=\"\"><br><br>", BloodBankLogic.PRIVATELY_OWNED );
            
            out.println("Established:");
            out.printf("<br><input type=\"datetime-local\" step=\"1\" name=\"%s\" value=\"\"><br><br>", BloodBankLogic.ESTABLISHED );
            out.println("EmployeeCount:");
            out.printf("<br><input type=\"number\" name=\"%s\" value=\"\"><br><br>", BloodBankLogic.EMPLOYEE_COUNT );
           
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
        
        BloodBankLogic bloodBankLogic = LogicFactory.getFor("BloodBank");
        PersonLogic personLogic =LogicFactory.getFor("Person");
        try{
            BloodBank bloodBank = bloodBankLogic.createEntity(request.getParameterMap());
            
            String personId = request.getParameter(BloodBankLogic.OWNER_ID);
            Person c = personLogic.getWithId(Integer.parseInt(personId));
             
             bloodBank.setOwner(c);
             bloodBankLogic.add(bloodBank);
        } catch(Exception e){
            errorMessage = e.getMessage();
        }
        if( request.getParameter("add") != null ) {
        processRequest(request, response);
        }
        else if(request.getParameter("view") != null) { 
            response.sendRedirect("BloodBankTable");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "BloodBank Entity Creation";
    }

}
