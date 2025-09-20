package com.mw.freemarker.portlet.template.helper;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	service = ObjectEntryHelper.class
)
public class ObjectEntryHelper {

	/**
	 * Retrieve the records and return list of ObjectEntry... this IS NOT permission aware...
	 * 
	 * Usage example: <#assign records = objectEntryHelper.getRecords(objectDefinition.objectDefinitionId)>
	 * 
	 */
	public List<ObjectEntry> getRecords(long objectDefinitionId) {
		
		// <#assign records = objectEntryHelper.getRecords(objectDefinition.objectDefinitionId)>
		
		// Using ObjectEntryLocalServiceUtil as there is no equivalent methods in ObjectEntryServiceUtil.
		List<ObjectEntry> objectEntries = ObjectEntryLocalServiceUtil.getObjectEntries(0, objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		_log.info("objectEntries size: " + objectEntries.size());	
		
//		for (ObjectEntry objectEntry: objectEntries) {
//			Map<String, Serializable> objectEntryValues = objectEntry.getValues();
//
//			for (Map.Entry<String, Serializable> field : objectEntryValues.entrySet()) {
//				_log.info(field.getKey() + " >> " + field.getValue().getClass() + " >> " + field.getValue());
//			}
//		}
		
		return objectEntries;
	}
	
	/**
	 * Retrieve the records and return list of ObjectEntry... this IS permission aware...
	 * 
	 * Usage example: <#assign records = objectEntryHelper.getRecords("studentName", true, objectDefinition, locale)>
	 * 
	 */
	public List<ObjectEntry> getRecords(String sortFieldName, boolean sortAscending, ObjectDefinition sourceObjectDefinition, Locale locale) {
		
		List<ObjectEntry> objectEntries = new ArrayList<ObjectEntry>();
		
		try {
			DTOConverterContext dtoConverterContext = new DefaultDTOConverterContext(null, locale);			
			Sort[] sort = {new Sort(sortFieldName, !sortAscending)}; // Temporaty hard
			
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
    private ObjectEntryService _objectEntryService;	
    
	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;    

    private static final Log _log = LogFactoryUtil.getLog(ObjectEntryHelper.class);	
}