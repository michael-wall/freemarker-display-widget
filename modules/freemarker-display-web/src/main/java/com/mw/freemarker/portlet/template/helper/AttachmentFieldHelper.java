package com.mw.freemarker.portlet.template.helper;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.mw.freemarker.portlet.model.AttachmentFieldFile;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	service = AttachmentFieldHelper.class
)
public class AttachmentFieldHelper {

	public AttachmentFieldFile getAttachmentURL(ThemeDisplay themeDisplay, long fileEntryId) {
		
		try {
			FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);
			FileVersion fileVersion = fileEntry.getFileVersion();
			
	        String fileUrl = DLURLHelperUtil.getDownloadURL(
	        	fileEntry,
	        	fileVersion,
	        	themeDisplay,
	        	StringPool.BLANK
	        );
	        
	        return new AttachmentFieldFile(fileUrl, fileEntry.getSize());
		} catch (PortalException e) {
			_log.error(e);
		}
		
        return null;
	}
	
	public String sizeFormatted(long size) {
	    final long KB = 1024;
	    final long MB = KB * 1024;

	    if (size < MB) {
	        long kbSize = (long) Math.ceil((double) size / KB);
	        return kbSize + " KB";
	    } else {
	        double mbSize = (double) size / MB;
	        return String.format("%.2f MB", mbSize);
	    }
	}
	
    @Reference
    private DLAppService _dlAppService;			
	
    private static final Log _log = LogFactoryUtil.getLog(AttachmentFieldHelper.class);	
}