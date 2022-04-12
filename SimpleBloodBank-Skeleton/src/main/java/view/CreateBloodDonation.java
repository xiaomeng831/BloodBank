package view;

import entity.BloodBank;
import entity.BloodDonation;
import logic.BloodBankLogic;
import logic.BloodDonationLogic;
import logic.LogicFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static logic.BloodDonationLogic.BANK_ID;

/**
 * This class handles HTTP request to url (/CreateBloodDonation). 
 * @author Wenwen Ji
 */
@WebServlet( name = "CreateBloodDonation", urlPatterns = { "/CreateBloodDonation" } )
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        response.setContentType( "text/html;charset=UTF-8" );
        try( PrintWriter out = response.getWriter() ) {
            /* TODO output your page here. You may use following sample code. */
            out.println( "<!DOCTYPE html>" );
            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<title>Create Blood Donation</title>" );
            out.println( "</head>" );
            out.println( "<body>" );
            out.println( "<div style=\"text-align: center;\">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
            out.println( "<form method=\"post\">" );
            out.println( "MILLILITERS:<br>" );
            out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", BloodDonationLogic.MILLILITERS );
            out.println( "<br>" );
            out.println( "Blood Group:<br>" );
            //instead of typing the name of column manualy use the static vraiable in logic
            //use the same name as column id of the table. will use this name to get date
            //from parameter map.
            out.printf( "<input type=\"radio\" id=\"group-a\" name=\"%s\" value=\"A\"><label for=\"group-a\">A</label>",
                    BloodDonationLogic.BLOOD_GROUP );
            out.printf( "<input type=\"radio\" id=\"group-b\" name=\"%s\" value=\"B\"><label for=\"group-b\">B</label>",
                    BloodDonationLogic.BLOOD_GROUP );
            out.printf( "<input type=\"radio\" id=\"group-ab\" name=\"%s\" value=\"AB\"><label for=\"group-ab\">AB</label>",
                    BloodDonationLogic.BLOOD_GROUP );
            out.printf( "<input type=\"radio\" id=\"group-o\" name=\"%s\" value=\"O\"><label for=\"group-o\">O</label>",
                    BloodDonationLogic.BLOOD_GROUP );
            out.println( "<br><br>" );
            out.println( "RHD:<br>" );
            out.printf( "<input type=\"radio\" id=\"positive\" name=\"%s\" value=\"Positive\"><label for=\"positive\">Positive</label><br>",
                    BloodDonationLogic.RHESUS_FACTOR );
            out.printf( "<input type=\"radio\" id=\"negative\" name=\"%s\" value=\"Negative\"><label for=\"negative\">Negative</label><br>",
                    BloodDonationLogic.RHESUS_FACTOR );
            out.println( "<br>" );
            out.println( "Blood Bank:<br>" );
            out.printf( "<select name=\"%s\">", BloodBankLogic.NAME );
            List<String> banks = (List<String>) request.getAttribute("banks");
            banks.forEach(bank -> {
                out.printf("<option>%s</option>", bank);
            });
            out.println( "</select><br><br>" );
            out.printf("<label for=\"%s\" class=\"lf\">Created At</label><br>", BloodDonationLogic.CREATED);
            out.printf("<input type=\"datetime-local\" step=\"1\" name=\"%s\"><br><br>", BloodDonationLogic.CREATED);
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
        log( "GET" );
        BloodBankLogic bbLogic = LogicFactory.getFor("BloodBank");
        List<BloodBank> banks = bbLogic.getAll();
        request.setAttribute("banks", banks.stream().map(BloodBank::getName).collect(Collectors.toList()));
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
        log( "POST" );
        BloodDonationLogic logic = LogicFactory.getFor( "BloodDonation" );
        BloodBankLogic bbLogic = LogicFactory.getFor("BloodBank");
        // TODO: retrieve bank by bank_id, make sure bank exists before donation
        Map<String, String[]> parameterMap = request.getParameterMap();
        String bloodBankId = parameterMap.get(BloodBankLogic.NAME)[0];
        BloodBank bank = bbLogic.getBloodBankWithName(bloodBankId);
        if( bank == null ){
            //if blood bank id does not exist, print the error message
            errorMessage = "bank_id: \"" + bloodBankId + "\" does not exist";
            processRequest( request, response );
            return;
        }
        BloodDonation donation = logic.createEntity(parameterMap);
        donation.setBloodBank(bank);
        logic.add(donation);
        if( request.getParameter( "add" ) != null ){
            //if add button is pressed return the same page
            processRequest( request, response );
        } else if( request.getParameter( "view" ) != null ){
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect( "BloodDonationTable" );
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a Account Entity";
    }

    private static final boolean DEBUG = true;

    public void log( String msg ) {
        if( DEBUG ){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
            getServletContext().log( message );
        }
    }

    public void log( String msg, Throwable t ) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
        getServletContext().log( message, t );
    }
}
