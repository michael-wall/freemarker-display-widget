package com.mw.freemarker.portlet.template.helper;

import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

public class PicklistHelper {

	public String getEntryName(List<ListTypeEntry> picklistEntries, String entryKey, String languageId) {
		if (Validator.isNull(entryKey)) return entryKey;
		if (Validator.isNull(picklistEntries) || picklistEntries.isEmpty()) return entryKey;
		
        for (ListTypeEntry picklistEntry : picklistEntries) {
        	if (picklistEntry.getKey().equalsIgnoreCase(entryKey)) {
        		return picklistEntry.getName(languageId);
        	}
        }
        
        return entryKey;
	}
	
	public String getEntryNames(List<ListTypeEntry> picklistEntries, String entryKeys, String languageId) {
		if (Validator.isNull(entryKeys)) return entryKeys;
		if (Validator.isNull(picklistEntries) || picklistEntries.isEmpty()) return entryKeys;
		
		String[] entryKeysArray = entryKeys.split(",");
		
		String entryNames = "";
		
		for (int i = 0; i < entryKeysArray.length; i++) {
			String entryKey = entryKeysArray[i].trim();
		
	        for (ListTypeEntry picklistEntry : picklistEntries) {
	        	if (picklistEntry.getKey().equalsIgnoreCase(entryKey)) {
	        		if (entryNames.length() > 0) entryNames += ", ";
	        		
	        		entryNames += picklistEntry.getName(languageId);
	        	}
	        }
		}
		
		if (Validator.isNull(entryNames)) return entryKeys;
		
		return entryNames;
	}
	
	public List<String> getEntryNamesList(List<ListTypeEntry> picklistEntries, String entryKeys, String languageId) {
		if (Validator.isNull(entryKeys)) return new ArrayList<String>();
		if (Validator.isNull(picklistEntries) || picklistEntries.isEmpty()) return new ArrayList<String>();
		
		String[] entryKeysArray = entryKeys.split(",");
		
		List<String> entryNames = new ArrayList<String>();
		
		for (int i = 0; i < entryKeysArray.length; i++) {
			String entryKey = entryKeysArray[i].trim();
		
	        for (ListTypeEntry picklistEntry : picklistEntries) {
	        	if (picklistEntry.getKey().equalsIgnoreCase(entryKey)) {
	        		entryNames.add(picklistEntry.getName(languageId));
	        	}
	        }
		}
		
		return entryNames;
	}

    private static final Log _log = LogFactoryUtil.getLog(PicklistHelper.class);	
}