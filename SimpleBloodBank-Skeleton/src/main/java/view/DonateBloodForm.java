package view;

import entity.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logic.*;

/**
 *
 * @author Hongxin Yin, Wenwen Ji, Xiaomeng Xu, Jinyu Li
 */
@WebServlet(name = "DonateBloodForm", urlPatterns = {"/DonateBloodForm"})
public class DonateBloodForm extends HttpServlet {
    private static final  String ENTITY_ACCOUNT = "Account";
    private static final  String ENTITY_PERSON = "Person";
    private static final  String ENTITY_DONATION_RECORD = "DonationRecord";
    private static final  String ENTITY_BLOODDONATION = "BloodDonation";
    private static final  String ENTITY_BLOODBANK = "BloodBank";

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

        request.setAttribute("title", path.substring(1));
        request.getRequestDispatcher("/jsp/donateblood.jsp").forward(request, response);
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
        log("GET");

        BloodBankLogic bloodBankLogic = LogicFactory.getFor(ENTITY_BLOODBANK);
        List<BloodBank> banks = bloodBankLogic.getAll();
        request.setAttribute("banks", banks);
        AccountLogic accountLogic = LogicFactory.getFor(ENTITY_ACCOUNT);
        List<Account> Accounts = accountLogic.getAll();
        request.setAttribute("accounts", Accounts);
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
        Map<String, String[]> parameterMap = request.getParameterMap();

        try {
            Person person = generatePerson(request);
            BloodBank bloodBank = retrieveBloodBank(request);
            BloodDonation bloodDonation = generateBloodDonation(request, bloodBank);
            generateDonationRecord(request, person, bloodDonation);
        } catch (IllegalArgumentException e) {
            log("Failed to generate entities", e);
        }

        if (parameterMap.containsKey("view")) {
            response.sendRedirect("PersonTable");
        } else if (parameterMap.containsKey("submit")) {
            processRequest(request, response);
        }
    }

    private Person generatePerson(HttpServletRequest request) {
        PersonLogic personLogic = LogicFactory.getFor(ENTITY_PERSON);
        Person person = null;
        try {
            person = personLogic.createEntity(request.getParameterMap());
            personLogic.add(person);
        } catch (IllegalArgumentException ex ) {
            log("Failed to generate Person entity", ex);
        }
        return person;
    }
    
    private BloodBank retrieveBloodBank(HttpServletRequest request) throws IllegalArgumentException {
        String name = request.getParameter(BloodBankLogic.NAME);
        if (name.isBlank()) {
            throw new IllegalArgumentException("Failed to get name");
        }
        BloodBankLogic bloodBankLogic = LogicFactory.getFor(ENTITY_BLOODBANK);

        BloodBank bloodbank = bloodBankLogic.getBloodBankWithName(name);
        if (bloodbank == null) {
            throw new IllegalArgumentException("Failed to get blood bank with name!");
        }
        return bloodbank;
    }
    
    private void generateDonationRecord(HttpServletRequest request, Person person, BloodDonation bloodDonation) {
        DonationRecordLogic donationRecordLogic = LogicFactory.getFor(ENTITY_DONATION_RECORD);
        try {
            DonationRecord donationRecord = donationRecordLogic.createEntity(request.getParameterMap());
            donationRecord.setPerson(person);
            donationRecord.setBloodDonation(bloodDonation);
            donationRecordLogic.add(donationRecord);
        } catch (IllegalArgumentException ex) {
            log("Failed to generate Donation Record entity", ex);
        }
    }

    private BloodDonation generateBloodDonation(HttpServletRequest request, BloodBank bloodBank) {
        BloodDonationLogic bloodDonationLogic = LogicFactory.getFor(ENTITY_BLOODDONATION);
        BloodDonation bloodDonation = null;
        try {
            bloodDonation = bloodDonationLogic.createEntity(request.getParameterMap());
            bloodDonation.setBloodBank(bloodBank);
            bloodDonationLogic.add(bloodDonation);
        } catch (NumberFormatException ex) {
            log("Failed to generate Blood Donation entity", ex);
        }
        return bloodDonation;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Sample page of multiple Eelements";
    }

    private static final boolean DEBUG = true;

    @Override
    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

    @Override
    public void log(String msg, Throwable t) {
        String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log(message, t);
    }
}
