package com.mw.freemarker.portlet.template.helper;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.ArrayList;
import java.util.List;

public class RelationshipHelper {

	public List<ObjectEntry> getRecords(ObjectRelationship objectRelationship, long objectEntryId) {
		
		try {
			List<ObjectEntry> relatedOjectEntries = ObjectEntryLocalServiceUtil.getOneToManyObjectEntries(objectRelationship.getCompanyId(), objectRelationship.getObjectRelationshipId(), objectEntryId, true, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
			
			return relatedOjectEntries;
		} catch (PortalException e) {
			_log.error(e);
		}
		
		return new ArrayList<ObjectEntry>();
	}
	
	public long getRecordCount(ObjectRelationship objectRelationship, long objectEntryId) {
		
		try {
			List<ObjectEntry> relatedOjectEntries = ObjectEntryLocalServiceUtil.getOneToManyObjectEntries(objectRelationship.getCompanyId(), objectRelationship.getObjectRelationshipId(), objectEntryId, true, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
			
			return relatedOjectEntries.size();
		} catch (PortalException e) {
			_log.error(e);
		}
		
		return 0;
	}	

    private static final Log _log = LogFactoryUtil.getLog(RelationshipHelper.class);	
}