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
            <h:dataTable width="100%" border="1" value="#{ChangelogController.logs}" var="log">
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{log.eventId}"/>
                </h:column>            
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Operation"/>
                    </f:facet>
                    <h:outputText value="#{log.eventTypeName}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Affected Entry"/>
                    </f:facet>
                    <h:outputText title="#{log.affectedEntryName}" value="#{log.affectedEntryShortName}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Principal"/>
                    </f:facet>
                    <h:outputText value="#{log.principalName}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Time"/>
                    </f:facet>
                    <h:outputFormat value="#{log.eventTime}">
                    		<f:convertDateTime pattern="MM/dd/yy - hh:mm a"/>
                    </h:outputFormat>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Rollback .."/>
                    </f:facet>
                    <h:commandLink action="#{ChangelogController.rollbackPointSelected}">
				        <h:outputText value=".. to this point!"/>
				        <f:param name="eventId" value="#{log.eventId}" />
				    </h:commandLink>
                </h:column>
            </h:dataTable>
         </h:form>
</f:view>


</body>
</html>