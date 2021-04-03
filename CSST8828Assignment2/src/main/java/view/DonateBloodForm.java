package view;

import entity.DonationRecord;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.DonationRecordLogic;
import logic.LogicFactory;

/**
 *
 * @author sarah
 */
@WebServlet(name = "DonateBloodFrom", urlPatterns = {"/DonateBloodFrom"})
public class DonateBloodForm extends HttpServlet {

    
    private String message=null;
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
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Donation Blood Form</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println( "<div style=\"text-align: center; \">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
            out.println( "<form action=\"DonateBloodForm\" method=\"post\">" );
            
            out.println("<div class=\"grid-container\" style=\"background-color: #ABBAEA;\">");
            out.println("<div class=\"item\"><h2>Administration</h2></div>");
            out.print("<div style=\"display: inline-block; text-align: center; \">");
            
            out.println("<label for=\"hospital\" style=\"width: 150px; display: inline-block; text-align:left; \">Hospital</label>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\" style=\"width: 230px;\">", DonationRecordLogic.HOSPITAL);
            
           out.println("<label for=\"admin\" style=\"width: 150px;  display: inline-block \" >Administrator</label>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\" style=\"width: 230px; float:right\">", DonationRecordLogic.ADMINSTRATOR);
            out.print("</div>");
            
            out.print("<div style=\"padding: 5px;\"></div>");
            
            out.print("<div style=\"display: inline-block; text-align: center; \">");
            
            out.println("<label for=\"date\" style=\"width: 150px; display: inline-block; text-align:left; \">Date</label>");
            out.printf("<input type=\"datetime-local\" step=\"1\" name=\"%s\" value=\"\">", DonationRecordLogic.CREATED);
        
            out.println("<label for=\"fname\" style=\"width: 150px;  display: inline-block\">BloodBank</label>");
            out.println( "<select name=\"sub\" style=\"width: 230px; float:right\">" );
            out.println( "<option value=\"volvo\">BloddyBank</option>" );
            out.println( "<option value=\"saab\">Bank</option>" );
            out.println( "</select><br><br>" );
            out.print("</div>");
            out.println("</form>");
            out.print("</div>");
            out.print("<div style=\"padding:5px; margin-top: 10px; float: right;\">");
            out.println("<input type=\"submit\" name=\"add\" value=\"Add\">" );
            out.print("</div>");
                      
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
        
            DonationRecordLogic donRecordLogic =  LogicFactory.getFor("DonationRecord");
        
        try {
           DonationRecord donRecord =  donRecordLogic.createEntity(request.getParameterMap());
             donRecordLogic.add(donRecord);
            
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
        return "Short description";
    }// </editor-fold>

}