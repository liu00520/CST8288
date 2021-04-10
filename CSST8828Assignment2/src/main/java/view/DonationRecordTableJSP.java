package view;

import entity.BloodDonation;
import entity.DonationRecord;
import entity.Person;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
@WebServlet(name = "DonationRecordTableJSP", urlPatterns = {"/DonationRecordTableJSP"})
public class DonationRecordTableJSP extends HttpServlet {

  String message;
    private void fillPersonTableData(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String path = req.getServletPath();
        req.setAttribute("entities", extractPersonTableData(req));
        req.setAttribute("request", toStringMap(req.getParameterMap()));
        req.setAttribute("path", path);
        req.setAttribute("message", message);
        req.setAttribute("title", path.substring(1));
        req.getRequestDispatcher("jsp/ShowDonationRecordJSP.jsp").forward(req, resp);
    }

    /**
     * used to extract the search from JSP and pass in the String to the search method
     * if no results, return empty list, if nothing searched get all
     */
    private List<?> extractPersonTableData(HttpServletRequest req) {
        String search = req.getParameter("searchText");
        DonationRecordLogic logic = LogicFactory.getFor("DonationRecord");
        req.setAttribute("columnName", logic.getColumnNames());
        req.setAttribute("columnCode", logic.getColumnCodes());
        List<DonationRecord> list;
        
        if(search != null) {
            list = logic.search(search);
        }
        else {
            list = logic.getAll();
        }
        if(list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return appendDataToList(list, logic::extractDataAsList);
    }
    
    private <T> List<?> appendDataToList(List<T> list, Function<T, List<?>> toArray) {
        List<List<?>> dataList = new ArrayList<>(list.size());
        list.forEach(e -> dataList.add(toArray.apply(e)));
        return dataList;
    }
    
    //appending key,values for the input types at the bottom of the table
    private String toStringMap(Map<String, String[]> e) {
        StringBuilder builder = new StringBuilder();
        e.keySet().forEach((k) -> {
            builder.append("Key=").append(k).append(",")
                    .append("Value/s=").append(Arrays.toString(e.get(k)))
                    .append(System.lineSeparator());
        });
        return builder.toString();
    }
    
    
        /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * handling edit, delete parameters
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        DonationRecordLogic logic = LogicFactory.getFor("DonationRecord");
       //PersonLogic personLogic = LogicFactory.getFor("Person");
       // BloodDonationLogic blDon =LogicFactory.getFor("BloodDonation");


        try{
        if(request.getParameter("edit") != null) {
            DonationRecord donRec = logic.updateEntity(request.getParameterMap());
        //    String personId = request.getParameter(DonationRecordLogic.PERSON_ID);
//            String bloodId = request.getParameter(DonationRecordLogic.DONATION_ID);
//            
//           //String personId = request.getParameter(DonationRecordLogic.PERSON_ID);
            //Person c = personLogic.getWithId(Integer.parseInt(personId));
//           BloodDonation b = blDon.getWithId(Integer.parseInt(bloodId)); 
//           
//           // blDon.update(b);
//           //personLogic.update(c);
//           
//           donRec.setBloodDonation(b);
              
            // donRec.getPerson();
             
//            
           
           // personLogic.update(c);
            logic.update(donRec);
        }
        else if(request.getParameter("delete") != null){
            String[] ids = request.getParameterMap().get("deleteMark");
            for(String id : ids) {
                logic.delete(logic.getWithId(Integer.valueOf(id)));
            }
        }
        } catch(Exception e) {
            message = e.getMessage();
        }
        fillPersonTableData(request, response);
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
        fillPersonTableData(request, response);
    }

    @Override
    protected void doPut( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    protected void doDelete( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "DELETE" );
        doPost( request, response );
    }    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Donation Record JSP";
    }

}
