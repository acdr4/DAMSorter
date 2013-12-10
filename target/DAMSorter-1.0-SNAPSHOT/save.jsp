<%-- 
    Document   : save
    Created on : May 27, 2013, 3:45:47 PM
    Author     : acdr4
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import = "java.io.*" %>

<jsp:useBean id="savebean" scope="session" class="edu.yale.damsorter.SaveDataHandler" />
<%
    String jdata = request.getParameter("json_data");
%>
<jsp:setProperty name="savebean" property="json_data" value="<%=jdata%>"/>

<%
    savebean.saveJson();
    //String json_data = request.getParameter("json_data");
%>
