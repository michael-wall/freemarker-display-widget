package com.mw.freemarker.portlet.config;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(
	category = "object",
	scope = ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE,
	strictScope = true
)
@Meta.OCD(
    id = FreeMarkerDisplayPortletInstanceConfiguration.PID,
    localization = "content/Language",
    name = "freemarker-display-portlet-instance-configuration"
)
public interface FreeMarkerDisplayPortletInstanceConfiguration {
	public static final String PID = "com.mw.freemarker.portlet.config.FreeMarkerDisplayPortletInstanceConfiguration";

    @Meta.AD(
        required = false,
        deflt = "",
        name = "template-object-entry-external-reference-code",
        description = "template-object-entry-external-reference-code-description"
    )
    public String templateObjectEntryExternalReferenceCode();
}