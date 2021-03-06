package view;


import common.ValidationException;
import entity.BloodDonation;
import entity.DonationRecord;
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
import logic.BloodDonationLogic;
import logic.DonationRecordLogic;

import logic.LogicFactory;
import logic.PersonLogic;

/**
 *
 * @author sarah
 */

@WebServlet(name = "CreateDonationRecord", urlPatterns = {"/CreateDonationRecord"})
public class CreateDonationRecord extends HttpServlet {

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
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Create Donation Record</title>");
            out.println("</head>");
            out.println("<body style =\"background-color: darksalmon\">");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            out.println("<form method=\"post\" style =\"background-color: darksalmon; text-align: center;font-weight: bold\">");

            out.println("Person_Id:<br>");

            // do i get all columns minus the primary key column?
            out.printf("<input type=\"number\" name=\"%s\" value=\"\"><br>", DonationRecordLogic.PERSON_ID);
            out.println("<br>");
            out.println("Donation_Id:<br>");
            out.printf("<input type=\"number\" name=\"%s\" value=\"\"><br>", DonationRecordLogic.DONATION_ID);
            out.println("<br>");
            out.println("Administrator:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", DonationRecordLogic.ADMINSTRATOR);
            out.println("<br>");
            out.println("Hospital:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", DonationRecordLogic.HOSPITAL);
            out.println("<br>");
            out.println("Tested:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", DonationRecordLogic.TESTED);
            out.println("<br>");
            out.println("Created:<br>");
            out.printf("<br><input type=\"datetime-local\" step='1' name=\"%s\" value=\"\"><br><br>", DonationRecordLogic.CREATED);
            out.println("<br>");
            out.println("<input type=\"submit\" name=\"view\" value=\"Add and View\" style=\"color: aqua; background: gray\">");
            out.println("<input type=\"submit\" name=\"add\" value=\"Add\" style=\"color: aqua; background: gray\">");
            out.println("</form>");

            if (errorMessage != null && !errorMessage.isEmpty()) {
                out.println("<p color=red>");
                out.println("<font color=red size=4px>");
                out.println(errorMessage);
                out.println("</font>");
                out.println("</p>");
            }
            out.println("<pre>");
            out.println("Submitted keys and values:");
            out.println(toStringMap(request.getParameterMap()));
            out.println("</pre>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((k, v) -> builder.append("Key=").append(k)
                .append(", ")
                .append("Value/s=").append(Arrays.toString(v))
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
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        DonationRecordLogic dRLogic = LogicFactory.getFor("DonationRecord");
        // need person logic
        PersonLogic personLogic = LogicFactory.getFor("Person");
        BloodDonationLogic blDon =LogicFactory.getFor("BloodDonation");

        try {
            DonationRecord donationRecord = dRLogic.createEntity(request.getParameterMap());
            //get Person_id the user inputed

            String personId = request.getParameter(DonationRecordLogic.PERSON_ID);
            String bloodId = request.getParameter(DonationRecordLogic.DONATION_ID);

            //use the id and person logic to get the entity from database
            Person c = personLogic.getWithId(Integer.parseInt(personId));
            BloodDonation b = blDon.getWithId(Integer.parseInt(bloodId));
            // set person on donationRecord Object
            donationRecord.setPerson(c);
            donationRecord.setBloodDonation(b);
            // create dependancy logic Person and BloodDonation. need to merge for this step
            dRLogic.add(donationRecord);

        } catch (ValidationException ex) {
            log("POST",ex);
            errorMessage = ex.getMessage();
        }

       
        if (request.getParameter("add") != null) {
            //if add button is pressed return the same page
            processRequest(request, response);
        } else if (request.getParameter("view") != null) {
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect("DonationRecordTable");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Donation Record Creattion";
    }

}
