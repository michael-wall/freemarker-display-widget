package com.mw.freemarker.portlet;

import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.Sort;
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
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.mw.freemarker.portlet.config.FreeMarkerDisplayInstanceConfiguration;
import com.mw.freemarker.portlet.config.FreeMarkerDisplayPortletInstanceConfiguration;
import com.mw.freemarker.portlet.constants.FreeMarkerConstants;
import com.mw.freemarker.portlet.constants.FreeMarkerDisplayPortletKeys;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
		
		if (Validator.isNull(instanceConfig) || Validator.isNull(instanceConfig.templateObjectDefinitionExternalReferenceCode())
				|| Validator.isNull(instanceConfig.templateId())
						|| Validator.isNull(instanceConfig.templateContent())) {
			_log.info("Instance Settings or field values missing, unable to proceed...");
		
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
	
		if (!templateObjectEntryValues.containsKey(instanceConfig.templateId()) || !templateObjectEntryValues.containsKey(instanceConfig.templateContent())) {
			_log.info("Template Object Entry Field(s) missing, unable to proceed...");
			
			include("/templateObjectEntryDetailsMissing.jsp", renderRequest, renderResponse);
			
			return;	
		}
		
		String templateId = (String)templateObjectEntryValues.get(instanceConfig.templateId());
		String templateContent = (String)templateObjectEntryValues.get(instanceConfig.templateContent());
		
		if (Validator.isNull(templateId) || Validator.isNull(templateContent)) {
			_log.info("Template Object Entry Field Value(s) missing, unable to proceed...");
			
			include("/templateObjectEntryDetailsMissing.jsp", renderRequest, renderResponse);
			
			return;	
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
		
		Locale locale = LocaleUtil.getDefault(); // Virtual Instance Default Language
	
		try {
			TemplateResource templateResource = new StringTemplateResource(templateId, templateContent);
			Template template = _templateManager.getTemplate(templateResource, true);
			_templateContextHelper.prepare(template, themeDisplay.getRequest());

			template.put(FreeMarkerConstants.FREEMARKER_VARIABLES.OBJECT_DEFINITION_ID, sourceObjectDefinition.getObjectDefinitionId());
			template.put(FreeMarkerConstants.FREEMARKER_VARIABLES.LANGUAGE_ID, locale.toString()); 
			
			template.put(FreeMarkerConstants.FREEMARKER_VARIABLES.RECORDS, getRecords(sourceObjectDefinition, locale));
						
			for (Map.Entry<String, List<ListTypeEntry>> picklist : picklistsMap.entrySet()) {
				String key = FreeMarkerConstants.FREEMARKER_VARIABLES.PICKLIST_PREFIX + picklist.getKey();
				template.put(key, picklist.getValue());
				
				_log.info("Added " + key);
			}
			
			for (ObjectRelationship objectRelationship : objectRelationships) {
				String key = FreeMarkerConstants.FREEMARKER_VARIABLES.RELATIONSHIP_PREFIX + objectRelationship.getName();
				template.put(key, objectRelationship);
				
				_log.info("Added " + key);
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
	
	/**
	 * Retrieve the records and return list of ObjectEntry... this IS permission aware...
	 */
	private List<ObjectEntry> getRecords(ObjectDefinition sourceObjectDefinition, Locale locale) {
		
		List<ObjectEntry> objectEntries = new ArrayList<ObjectEntry>();
		
		try {
			DTOConverterContext dtoConverterContext = new DefaultDTOConverterContext(null, locale);			
			Sort[] sort = {new Sort("studentName", false)};
			
			Page<com.liferay.object.rest.dto.v1_0.ObjectEntry> page = _objectEntryManager.getObjectEntries(sourceObjectDefinition.getCompanyId(), sourceObjectDefinition, ObjectDefinitionConstants.SCOPE_COMPANY, null, dtoConverterContext, null, Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null, sort);
		
			Collection<com.liferay.object.rest.dto.v1_0.ObjectEntry> dtoObjectEntries = page.getItems();

			for (com.liferay.object.rest.dto.v1_0.ObjectEntry dtoObjectEntry : dtoObjectEntries) {
				ObjectEntry objectEntry = _objectEntryService.fetchObjectEntry(dtoObjectEntry.getId());
				
				if (Validator.isNotNull(objectEntry)) objectEntries.add(objectEntry);
			}
		} catch (Exception e) {
			_log.error(e);
		}
		
		_log.info("Source Object ERC: " + sourceObjectDefinition.getExternalReferenceCode() + ", Object Entries: " + objectEntries.size());
		
		return objectEntries;
	}
	
    @Reference
    private TemplateManager _templateManager;

    @Reference
    private TemplateContextHelper _templateContextHelper;

    @Reference
    private ObjectFieldLocalService _objectFieldLocalService;
    
    @Reference
    private ObjectEntryLocalService _objectEntryLocalService;
    
    @Reference
    private ObjectEntryService _objectEntryService;
    
    @Reference
    private ObjectDefinitionLocalService _objectDefinitionLocalService;
    
    @Reference
    private ObjectRelationshipLocalService _objectRelationshipLocalService;
	
    @Reference
    private ListTypeEntryLocalService _listTypeEntryLocalService;
    
	@Reference
	private ConfigurationProvider _configurationProvider;
	
	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

    private static final Log _log = LogFactoryUtil.getLog(FreeMarkerDisplayPortlet.class);
}