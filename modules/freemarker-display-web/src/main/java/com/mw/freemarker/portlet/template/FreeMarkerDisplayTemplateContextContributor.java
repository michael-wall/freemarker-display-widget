package com.mw.freemarker.portlet.template;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.mw.freemarker.portlet.constants.FreeMarkerConstants;
import com.mw.freemarker.portlet.template.helper.AttachmentFieldHelper;
import com.mw.freemarker.portlet.template.helper.ObjectEntryHelper;
import com.mw.freemarker.portlet.template.helper.PicklistHelper;
import com.mw.freemarker.portlet.template.helper.RelationshipHelper;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	property = {"type=" + TemplateContextContributor.TYPE_GLOBAL},
	service = TemplateContextContributor.class
)
public class FreeMarkerDisplayTemplateContextContributor implements TemplateContextContributor {

	@Activate
    protected void activate(Map<String, Object> properties) throws Exception {		
		if (_log.isInfoEnabled()) _log.info("Activated");
	}
	
	@Override
	public void prepare(Map<String, Object> contextObjects, HttpServletRequest request) {
		
		contextObjects.put(FreeMarkerConstants.CONTEXT_VARIABLES.OBJECT_ENTRY_HELPER, _objectEntryHelper);
		contextObjects.put(FreeMarkerConstants.CONTEXT_VARIABLES.PICKLIST_HELPER, _picklistHelper);
		contextObjects.put(FreeMarkerConstants.CONTEXT_VARIABLES.RELATIONSHIP_HELPER, _relationshipHelper);
		contextObjects.put(FreeMarkerConstants.CONTEXT_VARIABLES.ATTACHMENT_FIELD_HELPER, _attachmentFieldHelper);
	}	
	
    @Reference
    private ObjectEntryHelper _objectEntryHelper;	
    
    @Reference
    private PicklistHelper _picklistHelper;	
    
    @Reference
    private RelationshipHelper _relationshipHelper;	
    
    @Reference
    private AttachmentFieldHelper _attachmentFieldHelper;	
	
    private static final Log _log = LogFactoryUtil.getLog(FreeMarkerDisplayTemplateContextContributor.class);	
}