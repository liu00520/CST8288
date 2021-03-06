package view;

import common.ValidationException;
import entity.BloodBank;
import entity.BloodDonation;
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
import logic.BloodDonationLogic;
import logic.LogicFactory;

/**
 *
 * @author danny
 */
@WebServlet(name = "CreateBloodDonation", urlPatterns = {"/CreateBloodDonation"})
public class CreateBloodDonation extends HttpServlet {
    
    private String errorMessage = null;
    
   /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /*outputting the page here, using sample code provided*/
            out.println( "<!DOCTYPE html>" );
            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<title>Create Blood Donation</title>" );
            out.println("</head>");
            out.println("<body>");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            out.println("<form method=\"post\">");
            
            out.println("Bank_Id:<br>");
            out.printf("<input type=\"number\" name=\"%s\" value=\"\"><br>", BloodDonationLogic.BANK_ID);
            out.println("<br>");
            out.println("Milliliters:<br>");
            out.printf("<input type=\"number\" name=\"%s\" value=\"\"><br>", BloodDonationLogic.MILLILITERS);
            out.println("<br>");
            out.println("Blood Group:<br>");
            out.printf("<input type=\"name\" name=\"%s\" value=\"\"><br>", BloodDonationLogic.BLOOD_GROUP);
            out.println("<br>");
            out.println("RHD:<br>");
            out.printf("<input type=\"name\" name=\"%s\" value=\"\"><br>", BloodDonationLogic.RHESUS_FACTOR);
            out.println("<br>");
            out.println("Created:<br>");
                       out.printf("<br><input type=\"datetime-local\" step='1' name=\"%s\" value=\"\"><br><br>", BloodDonationLogic.CREATED);

            out.println("<br>");
            out.println( "<input type=\"submit\" name=\"view\" value=\"Add and View\">" );
            out.println( "<input type=\"submit\" name=\"add\" value=\"Add\">" );
            out.println( "</form>" );
            
            if( errorMessage != null && !errorMessage.isEmpty() ){
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
            out.println( "</body>" );
            out.println( "</html>" );
        }
    }
    
    private String toStringMap( Map<String, String[]> values ) {
        StringBuilder builder = new StringBuilder();
        values.forEach( ( k, v ) -> builder.append( "Key=" ).append( k )
                .append( ", " )
                .append( "Value/s=" ).append( Arrays.toString( v ) )
                .append( System.lineSeparator() ) );
        return builder.toString();
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * get method is called first when requesting a URL. since this servlet will create a host this method simple
     * delivers the html code. creation will be done in doPost method.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        //log( "GET" );
        processRequest( request, response );
    }
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * this method will handle the creation of entity. as it is called by user submitting data through browser.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        //log( "POST" );
        //blood donation logic
        BloodDonationLogic bdLogic = LogicFactory.getFor("BloodDonation" );
         BloodBankLogic blDon =LogicFactory.getFor("BloodBank");
        try {
            BloodDonation bloodDonation = bdLogic.createEntity(request.getParameterMap());
            String bankId = request.getParameter(BloodDonationLogic.BANK_ID);
            BloodBank b = blDon.getWithId(Integer.parseInt(bankId));
            bloodDonation.setBloodBank(b);
            bdLogic.add(bloodDonation);
        }
        catch (ValidationException ex) {
            errorMessage = ex.getMessage();
        }
        if( request.getParameter("add") != null ) {
        processRequest(request, response);
        }
        else if(request.getParameter("view") != null) { 
            response.sendRedirect("BloodDonationTable");
        }
    }
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Blood Donation Entity Creation";
    }
    
}
