package view;

import entity.BloodBank;
import entity.BloodDonation;
import java.io.IOException;
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
import logic.BloodBankLogic;
import logic.BloodDonationLogic;
import logic.LogicFactory;

/**
 *
 * @author danny
 */
@WebServlet(name = "BloodDonationTableJSP", urlPatterns = {"/BloodDonationTableJSP"})
public class BloodDonationTableJSP extends HttpServlet  {
    
    String message;
    private void fillPersonTableData(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String path = req.getServletPath();
        req.setAttribute("entities", extractPersonTableData(req));
        req.setAttribute("request", toStringMap(req.getParameterMap()));
        req.setAttribute("path", path);
        req.setAttribute("message", message);
        req.setAttribute("title", path.substring(1));
        req.getRequestDispatcher("jsp/ShowBloodDonationJSP.jsp").forward(req, resp);
    }
    
     /**
     * used to extract the search from JSP and pass in the String to the search method
     * if no results, return empty list, if nothing searched get all
     */
    private Object extractPersonTableData(HttpServletRequest req) {
       String search = req.getParameter("searchText");
        BloodDonationLogic logic = LogicFactory.getFor("BloodDonation");
        req.setAttribute("columnName", logic.getColumnNames());
        req.setAttribute("columnCode", logic.getColumnCodes());
        List<BloodDonation> list;
        
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
        List<List<?>> newList = new ArrayList<>(list.size());
        list.forEach(e -> newList.add(toArray.apply(e)));
        return newList;
    }

    private Object toStringMap(Map<String, String[]> parameterMap) {
        StringBuilder builder = new StringBuilder();
        parameterMap.keySet().forEach((k) -> {
            builder.append("Key=").append(k).append(",")
                    .append("Value/s=").append(Arrays.toString(parameterMap.get(k)))
                    .append(System.lineSeparator());
        });
        return builder.toString();
    }
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        
        BloodDonationLogic logic = LogicFactory.getFor( "BloodDonation" );
        BloodBankLogic bbLogic = LogicFactory.getFor("BloodBank");
        
        try{
        if(req.getParameter("edit") != null) {
            BloodDonation bloodDon = logic.updateEntity(req.getParameterMap());
            String bankId = req.getParameter(BloodDonationLogic.BANK_ID);
            BloodBank bb = bbLogic.getWithId(Integer.parseInt(bankId));
            bloodDon.setBloodBank(bb);
            logic.update(bloodDon);
        }
        else if(req.getParameter("delete") != null){
            String[] ids = req.getParameterMap().get("deleteMark");
            for(String id : ids) {
                logic.delete(logic.getWithId(Integer.valueOf(id)));
            }
        }
        } catch(Exception e) {
            message = e.getMessage();
        }
        fillPersonTableData(req, resp);
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
        return "Blood Donation JSP";
    }
}