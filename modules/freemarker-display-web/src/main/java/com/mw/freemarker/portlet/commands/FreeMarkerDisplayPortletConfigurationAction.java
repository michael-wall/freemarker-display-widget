package com.mw.freemarker.portlet.commands;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.mw.freemarker.portlet.constants.FreeMarkerDisplayPortletKeys;
import com.mw.freemarker.portlet.util.FreeMarkerPortletValidator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

import org.osgi.service.component.annotations.Component;

@Component(
	property = "javax.portlet.name=" + FreeMarkerDisplayPortletKeys.FREEMARKER_DISPLAY_PORTLET,
	service = ConfigurationAction.class
)
public class FreeMarkerDisplayPortletConfigurationAction extends DefaultConfigurationAction {

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		String templateObjectEntryExternalReferenceCode = ParamUtil.getString(actionRequest, "templateObjectEntryExternalReferenceCode");
	
		if (Validator.isNull(templateObjectEntryExternalReferenceCode)) {
			SessionErrors.add(actionRequest, "template-object-entry-external-reference-code-required");
		} else {
			templateObjectEntryExternalReferenceCode = templateObjectEntryExternalReferenceCode.trim();
			
			if (templateObjectEntryExternalReferenceCode.length() > FreeMarkerPortletValidator.ERC_MAX_LENGTH) {
				SessionErrors.add(actionRequest, "template-object-entry-external-reference-code-length");
			} else if (!FreeMarkerPortletValidator.isValidERC(templateObjectEntryExternalReferenceCode)) {
				SessionErrors.add(actionRequest, "template-object-entry-external-reference-code-invalid");
			} else {
				setPreference(actionRequest, "templateObjectEntryExternalReferenceCode", templateObjectEntryExternalReferenceCode);	
			}
		}
		
		super.processAction(portletConfig, actionRequest, actionResponse);
	}

    private static final Log _log = LogFactoryUtil.getLog(FreeMarkerDisplayPortletConfigurationAction.class);
}