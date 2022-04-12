<%--
    Document   : donateblood
    Created on : Aug 13, 2021
    Author     : All members of group 16
--%>


<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page import="entity.BloodGroup"%>
<%@page import="entity.RhesusFactor"%>
<%@page import="logic.PersonLogic"%>
<%@page import="logic.BloodDonationLogic"%>
<%@page import="logic.DonationRecordLogic"%>
<%@page import="logic.BloodBankLogic"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Donate Blood Form</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
        integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
</head>
<body>
    <div class="container">
        <form method="post">
            <div class="person">
                <h3>Person</h3>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <input type="text" class="form-control" name="${PersonLogic.FIRSTNAME}" placeholder="First name" />
                    </div>
                    <div class="form-group col-md-6">
                        <input type="text" class="form-control" name="${PersonLogic.LASTNAME}" placeholder="Last name"/>
                    </div>
                </div>
                    
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <input type="tel" class="form-control" name="${PersonLogic.PHONE}" placeholder="Phone"/>
                    </div>
                    <div class="form-group col-md-6">
                        <input type="date" class="form-control" name="${PersonLogic.BIRTH}" placeholder="Date of Birth" />
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group col-md-8">
                        <input type="text" class="form-control" name="${PersonLogic.ADDRESS}" placeholder="Address"/>
                    </div>
                </div>
            </div>
            <div class="bloodDonation">
                <h3>Blood Information</h3>
                <div class="form-row">
                   <div class="form-group col-md-2">
                        <label for="${BloodDonationLogic.BLOOD_GROUP}">Blood Group</label>
                   </div>
                   <div class="form-group col-md-2">
                           <select name="${BloodDonationLogic.BLOOD_GROUP}" class="form-control">
                            <c:forEach var="bloodGroup" items="${BloodGroup.values()}">
                                <option >${bloodGroup.toString()}</option>
                            </c:forEach>
                        </select>
                   </div>
                   <div class="form-group col-md-1">
                        <label for="${BloodDonationLogic.RHESUS_FACTOR}">RHD</label>
                   </div>
                    <div class="form-group col-md-2">
                       <select name="${BloodDonationLogic.RHESUS_FACTOR}" class="form-control">
                        <c:forEach var="rhd" items="${RhesusFactor.values()}">
                            <option>${rhd.toString()}</option>
                        </c:forEach>
                        </select>
                    </div>
                    <div class="form-group col-md-2">
                        <select name="${BloodBankLogic.NAME}" class="form-control">
                            <c:forEach var="bb" items="${banks}">
                                <option>${bb.getName()}</option>
                            </c:forEach>
                        </select>
                    </div>
            </div>
            <div  class="form-row">
                <div class="form-group col-md-2">
                    <label for="${BloodDonationLogic.MILLILITERS}" >MilliLitters</label>
                </div>
                <div class="form-group col-md-8">
                    <input type="number" class="form-control" name="${BloodDonationLogic.MILLILITERS}"  />
                </div>
            </div>                    
        </div>
        <div class="donationRecord">
            <h3>Donation Record</h3>
            <div  class="form-row">
                <div class="form-group col-md-1">
                    <label for="${DonationRecordLogic.HOSPITAL}">Hospital</label>
                </div>
                <div class="form-group col-md-8">
                    <input type="text" name="${DonationRecordLogic.HOSPITAL}" class="form-control" />
                </div>
            </div>
            <div  class="form-row">
                <div class="form-group col-md-2">
                    <label for="${DonationRecordLogic.ADMINISTRATOR}">Administrator</label>
                </div>
                <div class="form-group col-md-2">
                    <select name="${DonationRecordLogic.ADMINISTRATOR}" class="form-control">
                        <c:forEach var="account" items="${accounts}">
                            <option>${account.getName()}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group col-md-1">
                    <label for="${DonationRecordLogic.TESTED}">Tested</label>
                </div>
                <div class="form-group col-md-2">
                    <select name="${DonationRecordLogic.TESTED}" class="form-control">
                        <option>True</option>
                        <option>False</option>
                    </select>
                </div>
                <div class="form-group col-md-1">
                    <label for="${DonationRecordLogic.CREATED}">Created date</label>
                </div>
                <div class="form-group col-md-4">
                    <input type="datetime-local" step="1" name="${DonationRecordLogic.CREATED}" class="form-control">
                </div>
            </div>     
        </div>
            <input type="submit" class="btn btn-primary" name="submit" value="Submit">
            <input type="submit" class="btn btn-primary" name="view" value="Submit and View">
        </form>

    </div>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
        integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
        crossorigin="anonymous"></script>
</body>
</html>
