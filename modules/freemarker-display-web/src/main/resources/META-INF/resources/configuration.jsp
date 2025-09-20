<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.mw.freemarker.portlet.util.FreeMarkerPortletValidator" %>

<portlet:defineObjects />
<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />
<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<c:set var="templateObjectEntryExternalReferenceCodeLength"><liferay-ui:message key="template-object-entry-external-reference-code-length" arguments="<%= new Object[] {FreeMarkerPortletValidator.ERC_MAX_LENGTH} %>" translateArguments="false" /></c:set>
<c:set var="sourceObjectDefinitionExternalReferenceCodeLength"><liferay-ui:message key="source-object-definition-external-reference-code-length" arguments="<%= new Object[] {FreeMarkerPortletValidator.ERC_MAX_LENGTH} %>" translateArguments="false" /></c:set>


<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<clay:container-fluid>
		<clay:row>
			<clay:col lg="12" md="12" sm="12" xs="12">
				<aui:fieldset>
					<aui:input name="templateObjectEntryExternalReferenceCode" value="<%= (String)portletPreferences.getValue("templateObjectEntryExternalReferenceCode", "") %>" label="template-object-entry-external-reference-code" type="text" required="true" />
				</aui:fieldset>
				
				<liferay-ui:error key="template-object-entry-external-reference-code-required" message="template-object-entry-external-reference-code-required"  />
				<liferay-ui:error key="template-object-entry-external-reference-code-length" message="${templateObjectEntryExternalReferenceCodeLength}" />
				<liferay-ui:error key="template-object-entry-external-reference-code-invalid" message="template-object-entry-external-reference-code-invalid" />

				<aui:fieldset>
					<aui:input name="sourceObjectDefinitionExternalReferenceCode" value="<%= (String)portletPreferences.getValue("sourceObjectDefinitionExternalReferenceCode", "") %>" label="source-object-definition-external-reference-code" type="text" required="true" />
				</aui:fieldset>
				
				<liferay-ui:error key="source-object-definition-external-reference-code-required" message="source-object-definition-external-reference-code-required"  />
				<liferay-ui:error key="source-object-definition-external-reference-code-length" message="${sourceObjectDefinitionExternalReferenceCodeLength}" />
				<liferay-ui:error key="source-object-definition-external-reference-code-invalid" message="source-object-definition-external-reference-code-invalid" />

				<aui:button-row>
					<aui:button type="submit" />
				</aui:button-row>
			</clay:col>
		</clay:row>
	</clay:container-fluid>
</aui:form>