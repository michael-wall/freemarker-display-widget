package com.mw.freemarker.portlet.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.mw.freemarker.portlet.FreeMarkerDisplayPortlet;

import java.util.regex.Pattern;

public class FreeMarkerPortletValidator {
	public static final int ERC_MAX_LENGTH = 75;

    private static final Pattern ERC_ALLOWED_PATTERN = Pattern.compile("^[A-Za-z0-9_.-]+$");

    public static boolean isValidERC(String erc) {
    	if (Validator.isNull(erc)) return false;
    	
        return ERC_ALLOWED_PATTERN.matcher(erc).matches();
    }	
	
    private static final Log _log = LogFactoryUtil.getLog(FreeMarkerDisplayPortlet.class);    
}