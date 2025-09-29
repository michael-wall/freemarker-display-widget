package com.mw.freemarker.portlet.template.helper;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	service = RelationshipHelper.class
)
public class RelationshipHelper {

	public List<ObjectEntry> getRecords(ObjectRelationship objectRelationship, long objectEntryId) {
		
		try {
			List<ObjectEntry> relatedOjectEntries = _objectEntryService.getOneToManyObjectEntries(0, objectRelationship.getObjectRelationshipId(), objectEntryId, true, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			return relatedOjectEntries;
		} catch (PortalException e) {
			_log.info(e.getClass() + ": " + e.getMessage(), e);
		}
		
		return new ArrayList<ObjectEntry>();
	}

	public List<ObjectEntry> getRecordsSorted(ObjectRelationship objectRelationship, long objectEntryId, String languageId) {
		List<ObjectEntry> relatedObjectEntries = null;
		
		try {
			relatedObjectEntries = _objectEntryService.getOneToManyObjectEntries(0, objectRelationship.getObjectRelationshipId(), objectEntryId, true, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
			
		} catch (PortalException e) {
			_log.info(e.getClass() + ": " + e.getMessage(), e);
			
			return new ArrayList<ObjectEntry>();
		}
		
		List<ObjectEntry> relatedObjectEntriesModifiable = new ArrayList<ObjectEntry>(relatedObjectEntries);

		relatedObjectEntriesModifiable.sort(
		    Comparator.comparing(entry -> {
		        try {
		            return entry.getTitleValue(languageId);
		        } catch (PortalException e) {
		            return "";
		        }
		    }, String.CASE_INSENSITIVE_ORDER)
		);

		return relatedObjectEntriesModifiable;
	}	
	
	public long getRecordCount(ObjectRelationship objectRelationship, long objectEntryId) {
				
		try {
			List<ObjectEntry> relatedObjectEntries = _objectEntryService.getOneToManyObjectEntries(0, objectRelationship.getObjectRelationshipId(), objectEntryId, true, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
			
			return relatedObjectEntries.size();
		} catch (PortalException e) {
			_log.info(e.getClass() + ": " + e.getMessage(), e);
		}
		
		return 0;
	}	
	
    @Reference
    private ObjectEntryService _objectEntryService;		

    private static final Log _log = LogFactoryUtil.getLog(RelationshipHelper.class);	
}