package view;

import entity.BloodDonation;
import logic.BloodDonationLogic;
import logic.LogicFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class handles HTTP request to url (/BloodDonationTableJSP). 
 * @author Wenwen Ji
 */

@WebServlet( name = "BloodDonationTableJSP", urlPatterns = { "/BloodDonationTableJSP" })
public class BloodDonationTableViewJSP extends HttpServlet {

    private static final String JSP_PATH = "/jsp/ShowTable-Donation.jsp";
    private static final String ERROR_MESSAGE_KEY = "errMessage";

    /**
     * Fill JSP view page with queried data.
     * @param req Request of Servlet.
     * @param resp Response of Servlet.
     * @throws ServletException A general exception a servlet can throw when it encounters difficulty.
     * @throws IOException An exception which occurs when an IO operations fails.
     */
    private void fillTableData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("donations", extractTableData(req));
        req.setAttribute("title", req.getServletPath().substring(1));
        req.getRequestDispatcher(JSP_PATH).forward(req, resp);
    }

    private List<List<?>> extractTableData(HttpServletRequest req ) {
        String search = req.getParameter( "searchText" );
        BloodDonationLogic logic = LogicFactory.getFor(BloodDonationLogic.DOMAIN);
        req.setAttribute( "columnName", logic.getColumnNames() );
        req.setAttribute( "columnCode", logic.getColumnCodes() );
        List<BloodDonation> list;
        if( search != null ){
            list = logic.search( search );
        } else {
            list = logic.getAll();
        }
        if( list == null || list.isEmpty() ){
            return Collections.emptyList();
        }
        List<List<?>> responseList = new ArrayList<>(list.size());
        list.forEach(i -> responseList.add(logic.extractDataAsList(i)));
        return responseList;
    }

    /**
     * Handles HTTP <code>POST</code> method.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter( "edit" ) != null) {
            doPut(req, resp);
        } else if (req.getParameter( "delete" ) != null) {
            doDelete(req, resp);
        }
    }

    /**
     * Handles HTTP <code>GET</code> method.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log("GET");
        fillTableData(req, resp);
    }

    /**
     * Handles HTTP <code>PUT</code> request.
     */
    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log("PUT");
        BloodDonationLogic logic = LogicFactory.getFor(BloodDonationLogic.DOMAIN);
        BloodDonation updatedDonation = logic.updateDonation(req.getParameterMap());
        if (updatedDonation == null) {
            req.setAttribute(ERROR_MESSAGE_KEY, "target donation not found in database");
        }

        fillTableData(req, resp);
    }

    /**
     * Handles HTTP <code>DELETE</code> request.
     */
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log("DELETE");
        BloodDonationLogic logic = LogicFactory.getFor(BloodDonationLogic.DOMAIN);
        String[] idsToDelete = req.getParameterMap().get("deleteMark");

        for (int i = 0; i < idsToDelete.length; i++) {
            BloodDonation donationToDelete = logic.getWithId(Integer.parseInt(idsToDelete[i]));
            logic.delete(donationToDelete);
        }

        fillTableData(req, resp);
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
