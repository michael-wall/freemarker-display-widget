package com.mw.freemarker.portlet.template.helper;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	service = ObjectEntryHelper.class
)
public class ObjectEntryHelper {
	
	/**
	 * Retrieve the permission aware list of records and return list of com.liferay.object.model.ObjectEntry
	 * 
	 * Sample usage: <#assign records = objectEntryHelper.getRecords("studentName", true, objectDefinition, locale, themeDisplay.siteGroup)>
	 * 
	 * It figures out the scope from objectDefinition
	 * 
	 */
	public List<ObjectEntry> getRecords(String sortFieldName, boolean sortAscending, ObjectDefinition sourceObjectDefinition, Locale locale, Group siteGroup) {
		
		List<ObjectEntry> objectEntries = new ArrayList<ObjectEntry>();
		
		try {
			DTOConverterContext dtoConverterContext = new DefaultDTOConverterContext(null, locale);			
			Sort[] sort = {new Sort(sortFieldName, !sortAscending)};
			
			String scopeKey = null;
			long siteGroupId = 0;
			
			if (sourceObjectDefinition.getScope().equalsIgnoreCase(ObjectDefinitionConstants.SCOPE_SITE)) {
				scopeKey = siteGroup.getGroupKey();
				siteGroupId = siteGroup.getGroupId();
			}
			
			Page<com.liferay.object.rest.dto.v1_0.ObjectEntry> page = _objectEntryManager.getObjectEntries(sourceObjectDefinition.getCompanyId(), sourceObjectDefinition, scopeKey, null, dtoConverterContext, null, Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null, sort);
			
			Collection<com.liferay.object.rest.dto.v1_0.ObjectEntry> dtoObjectEntries = page.getItems();
			
			Map<Long, ObjectEntry> objectEntriesMap = getRecords(siteGroupId, sourceObjectDefinition.getObjectDefinitionId());

			for (com.liferay.object.rest.dto.v1_0.ObjectEntry dtoObjectEntry : dtoObjectEntries) {			
				// Convert to com.liferay.object.model.ObjectEntry by extracting the matching records from _objectEntryLocalService.getObjectEntries
				if (objectEntriesMap.containsKey(dtoObjectEntry.getId())) objectEntries.add(objectEntriesMap.get(dtoObjectEntry.getId()));
			}
			
			for (ObjectEntry objectEntry : objectEntries) {
//				Map<String, Serializable> objectEntryValues = objectEntry.getValues();
//				
//				for (Map.Entry<String, Serializable> field : objectEntryValues.entrySet()) {
//					_log.info(field.getKey() + " >> " + field.getValue().getClass() + " >> " + field.getValue());
//				}			
				
			    objectEntriesMap.put(objectEntry.getObjectEntryId(), objectEntry);
			}			
		} catch (Exception e) {
			_log.error(e);
		}
		
		_log.info("Source Object ERC: " + sourceObjectDefinition.getExternalReferenceCode() + ", Object Entries: " + objectEntries.size());
		
		return objectEntries;
	}	
	
	private Map<Long, ObjectEntry> getRecords(long siteGroupId, long objectDefinitionId) {
		List<ObjectEntry> objectEntries = _objectEntryLocalService.getObjectEntries(siteGroupId, objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Map<Long, ObjectEntry> objectEntriesMap = new HashMap<Long, ObjectEntry>();
		
		for (ObjectEntry objectEntry : objectEntries) {
		    objectEntriesMap.put(objectEntry.getObjectEntryId(), objectEntry);
		}
		
		return objectEntriesMap;
	}
    
    @Reference
    private ObjectEntryLocalService _objectEntryLocalService;
    
	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;    

    private static final Log _log = LogFactoryUtil.getLog(ObjectEntryHelper.class);	
}