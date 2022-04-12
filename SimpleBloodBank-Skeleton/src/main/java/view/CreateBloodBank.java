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
 * @author Xiaomeng Xu
 * extends HttpServlet
 */
@WebServlet(name = "CreateBloodBank", urlPatterns = {"/CreateBloodBank"})
public class CreateBloodBank extends HttpServlet {
    private String errorMessage = null;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        response.setContentType( "text/html;charset=UTF-8" );
        try( PrintWriter out = response.getWriter() ) {            
            out.println( "<!DOCTYPE html>" );
            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<title>Create BloodBank</title>" );
            out.println( "</head>" );
            out.println( "<body>" );
            out.println( "<div style=\"text-align: center;\">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
            out.println( "<form method=\"post\">" );
            out.println( "Name:<br>" );
          
            out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", BloodBankLogic.NAME );
            out.println( "<br>" );
            out.println( "EmployeeCount:<br>" );
            out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", BloodBankLogic.EMPLOYEE_COUNT );
            out.println( "<br>" );
            out.println( "PrivatelyOwned:<br>" );
            out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", BloodBankLogic.PRIVATELY_OWNED );
            out.println( "<br>" );
            out.println( "OwnerID:<br>" );
            out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", BloodBankLogic.OWNER_ID );
            out.println( "<br>" );
            out.println( "Established:<br>" );
            out.printf( "<input type=\"datetime-local\" name=\"%s\" value=\"\"><br>", BloodBankLogic.ESTABLISHED );
            out.println( "<br>" );
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
     * method is used to handle the HTTP <code>GET</code>
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
     * method is used to handle the HTTP <code>POST</code>
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log( "POST" );
        
        BloodBankLogic bbLogic = LogicFactory.getFor( "BloodBank" );
        String name = request.getParameter( BloodBankLogic.NAME );
        if( bbLogic.getBloodBankWithName( name ) == null ){
            try {
                BloodBank bloodbank = bbLogic.createEntity( request.getParameterMap() );
                
                if (bloodbank.getPrivatelyOwned() == true) {
                    int ownerID = Integer.parseInt(request.getParameter(BloodBankLogic.OWNER_ID));
                    PersonLogic pl = LogicFactory.getFor("Person");
                    Person owner = pl.getWithId(ownerID);
                    if (owner == null) {
                        log("Invalid owner id! Forcing privatelyOwned to false");
                        bloodbank.setPrivatelyOwned(false);
                    }

                    bloodbank.setOwner(owner);
                }
                bbLogic.add( bloodbank );
                
            } catch( NumberFormatException ex ) {
                errorMessage = ex.getMessage();
            }
        } else {
            errorMessage = "Name: \"" + name + "\" already exists";
        }
        if( request.getParameter( "add" ) != null ){
            processRequest( request, response );
        } else if( request.getParameter( "view" ) != null ){            
            response.sendRedirect( "BloodBankTable" );
        }
    }

    /**
     * Used to return a short description of the servlet  
     * @return servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a BloodBank";
    }
}
