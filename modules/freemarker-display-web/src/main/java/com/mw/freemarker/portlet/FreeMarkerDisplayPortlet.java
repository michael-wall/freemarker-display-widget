package com.mw.freemarker.portlet;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.mw.freemarker.portlet.config.FreeMarkerDisplayPortletInstanceConfiguration;
import com.mw.freemarker.portlet.constants.FreeMarkerDisplayPortletKeys;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael Wall
 */
@Component(
	configurationPid = FreeMarkerDisplayPortletInstanceConfiguration.PID,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.ftl",
		"javax.portlet.init-param.config-template=/configuration.jsp",
		"javax.portlet.name=" + FreeMarkerDisplayPortletKeys.FREEMARKER_DISPLAY_PORTLET,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"javax.portlet.version=3.0"
	},
	service = Portlet.class
)
public class FreeMarkerDisplayPortlet extends MVCPortlet {
	private static String TEMPLATE_OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE = "TEMPLATE_OBJECT_DEFINITION"; 
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);	
		
		String portletId = themeDisplay.getPortletDisplay().getId();

		FreeMarkerDisplayPortletInstanceConfiguration config = getConfig(themeDisplay, portletId);
		
		if (Validator.isNull(config) || Validator.isNull(config.templateObjectEntryExternalReferenceCode())) {
			_log.info("Configuration not present, unable to proceed...");
		
			include("/configurationMissing.jsp", renderRequest, renderResponse);
			
			return;			
		}
		
		ObjectDefinition templateObjectDefinition = objectDefinitionLocalService.fetchObjectDefinitionByExternalReferenceCode(TEMPLATE_OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE, themeDisplay.getCompanyId());

		if (Validator.isNull(templateObjectDefinition)) {
			_log.info("Template Object Definition not present, unable to proceed...");
			
			include("/templateObjectDefinitionMissing.jsp", renderRequest, renderResponse);
			
			return;
		}

		String templateObjectEntryExternalReferenceCode = config.templateObjectEntryExternalReferenceCode();
		
		ObjectEntry templateObjectEntry = objectEntryLocalService.fetchObjectEntry(templateObjectEntryExternalReferenceCode, templateObjectDefinition.getObjectDefinitionId());
		
		if (Validator.isNull(templateObjectEntry)) {
			_log.info("Template Object Entry not present, unable to proceed...");
			
			include("/templateObjectEntryMissing.jsp", renderRequest, renderResponse);
			
			return;	
		}
		
		Map<String, Serializable> values = templateObjectEntry.getValues();
		
		String templateContent = (String)values.get("templateContent");
		String templateId = (String)values.get("templateId");
		String sourceObjectDefinitionERC = (String)values.get("sourceObjectDefinitionERC");
		
		ObjectDefinition sourceObjectDefinition = objectDefinitionLocalService.fetchObjectDefinitionByExternalReferenceCode(sourceObjectDefinitionERC, themeDisplay.getCompanyId());
		
		if (Validator.isNull(sourceObjectDefinition)) {
			_log.info("Source Object Definition not present, unable to proceed...");
			
			include("/sourceObjectDefinitionMissing.jsp", renderRequest, renderResponse);
			
			return;
		}
		
		List<ObjectEntry> objectEntries = objectEntryLocalService.getObjectEntries(0, sourceObjectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		
		System.out.println("objectEntries size: " + objectEntries.size());
		
		try {
			TemplateResource templateResource = new StringTemplateResource(templateId, templateContent);

			Template template = TemplateManagerUtil.getTemplate(TemplateConstants.LANG_TYPE_FTL, templateResource, true);

			template.put("records", objectEntries);
			
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
	
    @Reference
    private ObjectEntryLocalService objectEntryLocalService;
	
    @Reference
    private ObjectDefinitionLocalService objectDefinitionLocalService;
	
	@Reference
	private ConfigurationProvider _configurationProvider;	

    private static final Log _log = LogFactoryUtil.getLog(FreeMarkerDisplayPortlet.class);
}