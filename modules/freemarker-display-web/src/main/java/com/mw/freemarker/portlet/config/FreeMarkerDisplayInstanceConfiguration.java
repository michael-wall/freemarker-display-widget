package com.mw.freemarker.portlet.config;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(
	category = "object",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY,
	strictScope = true
)
@Meta.OCD(
    id = FreeMarkerDisplayInstanceConfiguration.PID,
    localization = "content/Language",
    name = "freemarker-display-instance-configuration"
)
public interface FreeMarkerDisplayInstanceConfiguration {
	public static final String PID = "com.mw.freemarker.portlet.config.FreeMarkerDisplayInstanceConfiguration";

    @Meta.AD(
        required = false,
        deflt = "TEMPLATE_OBJECT_DEFINITION",
        name = "template-object-definition-external-reference-code",
        description = "template-object-definition-external-reference-code-description"
    )
    public String templateObjectDefinitionExternalReferenceCode();
    
    @Meta.AD(
            required = false,
            deflt = "templateId",
            name = "template-id-field-name",
            description = "template-id-field-name-description"
        )
    public String templateId();
    
    @Meta.AD(
            required = false,
            deflt = "templateContent",
            name = "template-content-field-name",
            description = "template-content-field-name-description"
        )
    public String templateContent();    
}