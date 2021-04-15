<%-- 
    Document   : CreateDonationRecordJSP
    Created on : Apr. 10, 2021, 6:10:37 p.m.
    Author     : sarah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" >
        <link rel="stylesheet" type="text/css" href="style/createjsp.css" >
        <title><c:out value="${title}" /></title>
    </head>
    <body style=" background-color: darksalmon">
        <h2 style="text-align: center; align-items: center;">Create Donation Record JSP</h2><hr><br>
        <form method="post" style=" text-align: center; align-items: center; font-weight: bold" >
            <c:forEach var="cols" items="${columns}" begin="1" end="5" varStatus="loop" >
                <div style="text-align: center; font-weight: bold; align-content: center">
                    <p><c:out value="${columnNames[loop.index]}" /></p>  
                  <input type="text" class="create" name="${cols}" value="" /> 
                </div>
            </c:forEach >
            <p><c:out value="${columnNames[6]}" /></p>
            <input type="datetime-local" step="1" name="${columns[6]}" value=""  />
                 <br>
                 <br>
                 <input type="submit" name="view" value="Add and View" style="color: aqua; background: gray" />
                 <input type="submit" name="add" value="Add" style="color: aqua; background: gray"  />
        </form >
            <div style="text-align: center; align-items: center;" >
                <pre>${message}<br>${path}<br>${request}</pre>
    </body>
</html>