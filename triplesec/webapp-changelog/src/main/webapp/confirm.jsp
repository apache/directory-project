<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Triplesec Changelog Administration</title>
<link rel="stylesheet" type="text/css"
            href='<%= request.getContextPath() + "/style.css" %>'>
</head>
<body>

<f:view>
        <h:form>
            <h1><h:outputText value="Triplesec Changelog Administration"/></h1>
            <f:verbatim><br></f:verbatim>
            <h:panelGroup>
                <h:outputText value="Changes made to the server after the operation whose details given below will be rolled back. Do you confirm?"/>
                <f:verbatim><br></f:verbatim>
                <h:commandButton value="Yes, roll'em back!" action="rollback"/>
                <f:verbatim>&nbsp;&nbsp;&nbsp;</f:verbatim>
                <h:commandButton value="No, cancel." action="listing"/>
                <f:verbatim><br></f:verbatim>
            </h:panelGroup>
            <f:verbatim><br></f:verbatim>
            <h:panelGrid border="1" columns="2">
                <h:outputText value="Event Id: "/>
                <h:outputText value="#{ChangelogController.changeEvent.eventId}"/>
                <h:outputText value="Operation: "/>
                <h:outputText value="#{ChangelogController.changeEvent.eventTypeName}"/>
                <h:outputText value="Affected Entry: "/>
                <h:outputText value="#{ChangelogController.changeEvent.affectedEntryName}"/>
                <h:outputText value="Principal: "/>
                <h:outputText value="#{ChangelogController.changeEvent.principalName}"/>
                <h:outputText value="Time: "/>
                <h:outputFormat value="#{ChangelogController.changeEvent.eventTime}">
                    <f:convertDateTime pattern="MM/dd/yy - hh:mm a"/>
                </h:outputFormat>
            </h:panelGrid>
         </h:form>
</f:view>


</body>
</html>