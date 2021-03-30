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
import logic.Logic;
import logic.LogicFactory;

/**
 *
 * @author sarah
 */
@WebServlet(name = "DonationRecordTable", urlPatterns = {"/DonationRecordTable"})
public class DonationRecordTable extends HttpServlet {

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
            out.println("<title>DonationRecordTable</title>");            
            out.println("</head>");
            out.println("<body>");
            
            out.println("<table style=\"margin-left: auto; margin-right: auto;\" border=\"1\">");
            out.println("<caption>Donation Record</caption>");
            
            Logic<DonationRecord> logic1 = LogicFactory.getFor("DonationRecord");
            out.print("<tr>");
            logic1.getColumnNames().forEach(e -> out.printf("<th>%s</th>", e));
            
             out.println( "</tr>" );
          logic1.getAll().forEach(e -> out.printf("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><tr>"
                  , logic1.extractDataAsList(e).toArray()));
            
            out.println( "<tr>" );
            //this is an example, for your other tables use getColumnNames from
            //logic to create headers in a loop.
            logic1.getColumnNames().forEach( e -> out.printf( "<th>%s</th>", e ) );

            out.println( "</tr>" );
            out.println( "</table>" );
            out.printf( "<div style=\"text-align: center;\"><pre>%s</pre></div>", toStringMap( request.getParameterMap() ) );
            out.println( "</body>" );
            out.println( "</html>" );
        }
    }
    
        private String toStringMap( Map<String, String[]> m ) {
        StringBuilder builder = new StringBuilder();
        for( String k: m.keySet() ) {
            builder.append( "Key=" ).append( k )
                    .append( ", " )
                    .append( "Value/s=" ).append( Arrays.toString( m.get( k ) ) )
                    .append( System.lineSeparator() );
        }
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
         log( "GET" );
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
         log( "POST" );
    
        processRequest( request, response );
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Donation Record Creation";
    }// </editor-fold>

}
