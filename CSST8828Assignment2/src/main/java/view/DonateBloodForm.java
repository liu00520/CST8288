package view;


import common.ValidationException;
import entity.BloodBank;
import entity.BloodDonation;
import entity.DonationRecord;
import entity.Person;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.BloodBankLogic;
import logic.BloodDonationLogic;
import logic.DonationRecordLogic;
import logic.LogicFactory;
import logic.PersonLogic;

/**
 *
 * @author Jia Liu, Sarah Kelly, Danny Pham, Mark Newport Servlet that accepts
 * input (creates) for all entities; Used the provided CSS file provided (May
 * update for bonus?)
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

            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
          
            //use this to create a border & only 1 div wrapped for the input type
            //Accepting and creating the person entity 

            out.println("<title>Donation Blood Form</title>"); 
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/bloodform.css\">");
            out.println("</head>");
            out.println("<body>");
            out.println( "<form method=\"post\">" );
            

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

            
            out.println("<div class=\"grid-container\">");
            out.println("<div class=\"item\"><h2>Blood</h2></div>");
            out.println("<div class=\"item\">Blood Group</div>");
 
            out.printf("<select type=\"text\" name=\"%s\" value=\"\">", BloodDonationLogic.BLOOD_GROUP);
          
            out.println( "<option value=\"O\">O</option>" );
            out.println( "<option value=\"A\">A</option>" );
            out.println( "<option value=\"AB\">AB</option>" );
            out.println( "<option value=\"B\">B</option>" );
            out.println( "</select><br><br>");
       
            out.println("Amount");
            out.printf("<input type=\"number\" name=\"%s\" value=\"\">", BloodDonationLogic.MILLILITERS);
           
            out.println("Tested");
            out.printf("<select type=\"text\" name=\"%s\" value=\"\">", DonationRecordLogic.TESTED);
        
            out.println( "<option value=\"true\">True</option>" );
            out.println( "<option value=\"false\">False</option>" );
           
            out.println( "</select><br><br>");
       
            out.println("RHD");
            out.printf("<select type=\"text\" name=\"%s\" value=\"\">", BloodDonationLogic.RHESUS_FACTOR);
            out.println( "<option value=\"-\">Negative</option>" );
            out.println( "<option value=\"+\">Positive</option>" );
           
            out.println( "</select><br><br>");
       
            out.println("</div>");
           
            
            out.println("<div class=\"grid-container\">");
            out.println("<div class=\"item\"><h2>Administrator</h2></div>");
            out.println("<div class=\"item\">Hospital</div>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\">",DonationRecordLogic.HOSPITAL);
            out.println("Administrator");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\" >",DonationRecordLogic.ADMINSTRATOR);
            out.println("Date");
            out.printf("<input type=\"datetime-local\" step=\"1\" name=\"%s\" value=\"\">",DonationRecordLogic.CREATED);
            out.println("BloodBank");
              
                
         


            out.printf("<select type=\"text\" name=\"%s\" value=\"\">", BloodDonationLogic.BANK_ID);
            out.println( "<option value=\"1\">BloddyBank</option>" );
            out.println( "<option value=\"2\">Bank</option>" );
           
            out.println( "</select><br><br>");
            out.print("</div>");
            
    
            out.println("<input type=\"submit\" name=\"add\" value=\"Add\">");
            out.println("</form>");

            if (message != null && !message.isEmpty()) {
                out.println("<p>");
                out.println(message);
                out.println("</p>");
            }
            out.println("<pre>");
            out.println("Submitted keys and values:");
            out.println(toStringMap(request.getParameterMap()));
            out.println("</pre>");
            out.println("</body>");
            out.println("</html>");

        }
    }

    private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((e, v) -> builder.append("Key=").append(e)
                .append(", ").append("Value/s=")
                .append(Arrays.toString(v))
                .append(System.lineSeparator()));
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
     * @throws IOException if an I/O error occurs doPost that creates All 4 new
     * entities after submitting if the input is correct
     *
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);

        DonationRecordLogic donL = LogicFactory.getFor("DonationRecord");
        PersonLogic personLogic = LogicFactory.getFor("Person");
        BloodDonationLogic bD=LogicFactory.getFor("BloodDonation");
        BloodBankLogic bln=LogicFactory.getFor("BloodBank");
     
        try {

         
          Person person = personLogic.createEntity(request.getParameterMap());
          
           BloodDonation blDon = bD.createEntity(request.getParameterMap());
           String bankId = request.getParameter(BloodDonationLogic.BANK_ID);
           BloodBank b = bln.getWithId(Integer.parseInt(bankId));
            blDon.setBloodBank(b);
              
          DonationRecord don = donL.createEntity(request.getParameterMap());
          
          personLogic.add(person);
          bD.add(blDon);
          don.setBloodDonation(blDon);
          don.setPerson(person);
          donL.add(don);
        
            
         
    
        } catch(ValidationException e) {

          message = e.getMessage();
        } catch(Exception e) {

            message = e.getMessage();
        }
        if (request.getParameter("add") != null) {
            
            processRequest(request, response);
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
