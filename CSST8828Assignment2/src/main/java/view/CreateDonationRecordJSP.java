package view;

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
@WebServlet(name = "CreateDonationRecordJSP", urlPatterns = {"/CreateDonationRecordJSP"})
public class CreateDonationRecordJSP extends HttpServlet {

    String message = null;
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
      String path = request.getServletPath();
        request.setAttribute("columns", getRecord().getColumnCodes());
        request.setAttribute("columnNames", getRecord().getColumnNames());
        request.setAttribute("request", toStringMap(request.getParameterMap()));
        request.setAttribute("path", path);
        request.setAttribute("message", message);
        request.setAttribute("title", path.substring(1));
        request.getRequestDispatcher("jsp/CreateDonationRecordJSP.jsp").forward(request, response);
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
    
    private DonationRecordLogic getRecord() {
        DonationRecordLogic logic = LogicFactory.getFor("DonationRecord");
        
        return logic;
    }
    
    private BloodDonationLogic getDon() {
        BloodDonationLogic logic = LogicFactory.getFor("BloodDonation");
        
        return logic;
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
      
        try {
            DonationRecord record = getRecord().createEntity(request.getParameterMap());
            String personId = request.getParameter(DonationRecordLogic.PERSON_ID);
            String bloodId = request.getParameter(DonationRecordLogic.DONATION_ID);
            Person c = getPerson().getWithId(Integer.parseInt(personId));
            BloodDonation b = getDon().getWithId(Integer.parseInt(bloodId));
            // set person on donationRecord Object
            record.setPerson(c);
            record.setBloodDonation(b);
            
            getRecord().add(record);
        } catch (Exception e) {
            message = e.getMessage();
        }
        if(request.getParameter("add") != null) {
        processRequest(request, response);
        }
        else if(request.getParameter("view") != null) {
            response.sendRedirect("DonationRecordTable");
        }
    }

    /**
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create Donation Record JSP";
    }
}
