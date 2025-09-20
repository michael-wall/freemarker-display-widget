package com.mw.freemarker.portlet;

import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManager;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.engine.TemplateContextHelper;
import com.mw.freemarker.portlet.config.FreeMarkerDisplayInstanceConfiguration;
import com.mw.freemarker.portlet.config.FreeMarkerDisplayPortletInstanceConfiguration;
import com.mw.freemarker.portlet.constants.FreeMarkerDisplayPortletKeys;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael Wall
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.init-param.config-template=/configuration.jsp",
		"javax.portlet.name=" + FreeMarkerDisplayPortletKeys.FREEMARKER_DISPLAY_PORTLET,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.security-role-ref=power-user,user",
		"javax.portlet.version=3.0"
	},
	service = Portlet.class
)
public class FreeMarkerDisplayPortlet extends MVCPortlet {	
	
	@Activate
    protected void activate(Map<String, Object> properties) throws Exception {		
		if (_log.isInfoEnabled()) _log.info("Activated");
	}
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);	
		
		String portletId = themeDisplay.getPortletDisplay().getId();

		FreeMarkerDisplayInstanceConfiguration instanceConfig = getConfig(themeDisplay.getCompanyId());
		
		if (Validator.isNull(instanceConfig) || Validator.isNull(instanceConfig.templateObjectDefinitionExternalReferenceCode())) {
			_log.info("Instance Settings missing, unable to proceed...");
		
			include("/configurationMissing.jsp", renderRequest, renderResponse);
			
			return;			
		}
		
		FreeMarkerDisplayPortletInstanceConfiguration portletInstanceConfig = getConfig(themeDisplay, portletId);
		
		if (Validator.isNull(portletInstanceConfig) || Validator.isNull(portletInstanceConfig.templateObjectEntryExternalReferenceCode())
				|| Validator.isNull(portletInstanceConfig.sourceObjectDefinitionExternalReferenceCode())) {
			_log.info("Portlet Instance Configuration not present, unable to proceed...");
		
			include("/configurationMissing.jsp", renderRequest, renderResponse);
			
			return;			
		}
		
		ObjectDefinition templateObjectDefinition = _objectDefinitionLocalService.fetchObjectDefinitionByExternalReferenceCode(instanceConfig.templateObjectDefinitionExternalReferenceCode(), themeDisplay.getCompanyId());
		
		if (Validator.isNull(templateObjectDefinition)) {
			_log.info("Template Object Definition missing, unable to proceed...");
			
			include("/templateObjectDefinitionMissing.jsp", renderRequest, renderResponse);
			
			return;
		}
		
		ObjectDefinition sourceObjectDefinition = _objectDefinitionLocalService.fetchObjectDefinitionByExternalReferenceCode(portletInstanceConfig.sourceObjectDefinitionExternalReferenceCode(), themeDisplay.getCompanyId());		
		
		if (Validator.isNull(sourceObjectDefinition)) {
			_log.info("Source Object Definition missing, unable to proceed...");
			
			include("/sourceObjectDefinitionMissing.jsp", renderRequest, renderResponse);
			
			return;
		}		

		String templateObjectEntryExternalReferenceCode = portletInstanceConfig.templateObjectEntryExternalReferenceCode();
		
		ObjectEntry templateObjectEntry = _objectEntryLocalService.fetchObjectEntry(templateObjectEntryExternalReferenceCode, templateObjectDefinition.getObjectDefinitionId());
		
		if (Validator.isNull(templateObjectEntry)) {
			_log.info("Template Object Entry missing, unable to proceed...");
			
			include("/templateObjectEntryMissing.jsp", renderRequest, renderResponse);
			
			return;	
		}
		
		Map<String, Serializable> templateObjectEntryValues = templateObjectEntry.getValues();
		
		String templateContent = (String)templateObjectEntryValues.get("templateContent");
		String templateId = (String)templateObjectEntryValues.get("templateId");
		
		List<ObjectEntry> objectEntries = _objectEntryLocalService.getObjectEntries(0, sourceObjectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		
		_log.info("objectEntries size: " + objectEntries.size());	
		
		for (ObjectEntry objectEntry: objectEntries) {
			Map<String, Serializable> objectEntryValues = objectEntry.getValues();

			for (Map.Entry<String, Serializable> field : objectEntryValues.entrySet()) {
				_log.info(field.getKey() + " >> " + field.getValue().getClass() + " >> " + field.getValue());
			}
		}
		
		List<ObjectField> objectFields = _objectFieldLocalService.getObjectFields(sourceObjectDefinition.getObjectDefinitionId());
		
		Map<String, List<ListTypeEntry>> picklistsMap = new HashMap<String, List<ListTypeEntry>>();
		
		for (ObjectField objectField : objectFields) {	
			_log.info("fieldName: " + objectField.getName() + ", fieldType: " + objectField.getBusinessType());

			if (ObjectFieldConstants.BUSINESS_TYPE_PICKLIST.equals(objectField.getBusinessType()) || ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST.equals(objectField.getBusinessType())) {
				long listTypeDefinitionId = objectField.getListTypeDefinitionId();
				List<ListTypeEntry> picklistEntries = _listTypeEntryLocalService.getListTypeEntries(listTypeDefinitionId);
				picklistsMap.put(objectField.getName(), picklistEntries); //Using the fieldName to simplify mapping logic
			}
		}
		
		List<ObjectRelationship> objectRelationships = _objectRelationshipLocalService.getObjectRelationships(sourceObjectDefinition.getObjectDefinitionId());
		
		for (ObjectRelationship objectRelationship : objectRelationships) {
			_log.info("RelationshipName: " + objectRelationship.getName());
		}
	
		try {
			TemplateResource templateResource = new StringTemplateResource(templateId, templateContent);
			Template template = _templateManager.getTemplate(templateResource, true);
			_templateContextHelper.prepare(template, themeDisplay.getRequest());
			
			template.put("records", objectEntries);
			template.put("objectDefinitionId", sourceObjectDefinition.getObjectDefinitionId());
			template.put("languageId", LocaleUtil.getDefault().toString()); // Virtual Instance Default Language
						
			for (Map.Entry<String, List<ListTypeEntry>> picklist : picklistsMap.entrySet()) {
				template.put("picklist_" + picklist.getKey(), picklist.getValue());
				
				_log.info("Added picklist_" + picklist.getKey());
			}
			
			for (ObjectRelationship objectRelationship : objectRelationships) {
				template.put("relationship_" + objectRelationship.getName(), objectRelationship);
				
				_log.info("Added relationship_" + objectRelationship.getName());
			}
			
			try (PrintWriter writer = renderResponse.getWriter()) {
				template.processTemplate(writer);
			}
		} catch (TemplateException e) {
			_log.error(e);
		} catch (IOException e) {
			_log.error(e);
		}
	}

	private FreeMarkerDisplayPortletInstanceConfiguration getConfig(ThemeDisplay themeDisplay, String portletId) {
		FreeMarkerDisplayPortletInstanceConfiguration config = null;
		
		try {			
			config = _configurationProvider.getPortletInstanceConfiguration(
				FreeMarkerDisplayPortletInstanceConfiguration.class,
			    themeDisplay.getLayout(),
			    portletId
			);
			
			return config;	
		} catch (ConfigurationException e) {
			_log.error(e);
		}
		
		return null;
	}
	
	private FreeMarkerDisplayInstanceConfiguration getConfig(long companyId) {
		FreeMarkerDisplayInstanceConfiguration config = null;
		
		try {
			config = _configurationProvider.getCompanyConfiguration(FreeMarkerDisplayInstanceConfiguration.class, companyId);
			
			return config;	
		} catch (ConfigurationException e) {
			_log.error(e);
		}		 
		
		return null;
	}	
	
    @Reference
    private TemplateManager _templateManager;

    @Reference
    private TemplateContextHelper _templateContextHelper;	
	
    @Reference
    private ObjectEntryLocalService _objectEntryLocalService;

    @Reference
    private ObjectFieldLocalService _objectFieldLocalService;
    
    @Reference
    private ObjectDefinitionLocalService _objectDefinitionLocalService;
    
    @Reference
    private ObjectRelationshipLocalService _objectRelationshipLocalService;
	
    @Reference
    private ListTypeEntryLocalService _listTypeEntryLocalService;
    
	@Reference
	private ConfigurationProvider _configurationProvider;

    private static final Log _log = LogFactoryUtil.getLog(FreeMarkerDisplayPortlet.class);
}