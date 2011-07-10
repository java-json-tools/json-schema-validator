package eel.kitchen.jsonschema.validators.factories;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.EnumValidator;
import eel.kitchen.jsonschema.validators.ObjectValidator;
import eel.kitchen.jsonschema.validators.StringValidator;
import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.jsonschema.validators.format.FormatPicker;
import eel.kitchen.jsonschema.validators.format.IllegalFormatValidator;
import org.codehaus.jackson.JsonNode;

public final class StringValidatorFactory
    extends AbstractValidatorFactory
{
    public StringValidatorFactory(final JsonNode schemaNode)
    {
        super(schemaNode, StringValidator.class);

        if (schemaNode.has("format")) {
            final JsonNode formatNode = schemaNode.get("format");
            Class<? extends Validator> c;
            try {
                if (!formatNode.isTextual())
                    throw new MalformedJasonSchemaException("");
                String fmt = formatNode.getTextValue().replaceAll("-", "_")
                    .toUpperCase();
                c = FormatPicker.valueOf(fmt).getValidator();
            } catch (Exception e) {
                c = IllegalFormatValidator.class;
            }
            validatorList.add(c);
        }

        if (schemaNode.has("enum"))
            validatorList.add(EnumValidator.class);
    }
}
