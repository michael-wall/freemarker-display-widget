package com.mw.freemarker.portlet.template.helper;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.List;

public class ObjectEntryHelper {

	public List<ObjectEntry> getRecords(long objectDefinitionId) {
		// Using ObjectEntryLocalServiceUtil as there is no equivalent methods in ObjectEntryServiceUtil and ObjectEntryHelper is an internal class.
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

    private static final Log _log = LogFactoryUtil.getLog(ObjectEntryHelper.class);	
}