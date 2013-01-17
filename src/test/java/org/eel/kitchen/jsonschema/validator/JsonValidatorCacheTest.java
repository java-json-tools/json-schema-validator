package org.eel.kitchen.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.SampleNodeProvider;
import org.eel.kitchen.jsonschema.metaschema.BuiltinSchemas;
import org.eel.kitchen.jsonschema.metaschema.MetaSchema;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.schema.AddressingMode;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.schema.SchemaNode;
import org.eel.kitchen.jsonschema.schema.SchemaRegistry;
import org.eel.kitchen.jsonschema.uri.URIManager;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.util.jackson.JacksonUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Iterator;

import static org.testng.Assert.assertFalse;

public final class JsonValidatorCacheTest
{
    private JsonValidatorCache cache;
    private final AddressingMode addressingMode = AddressingMode.CANONICAL;

    @BeforeClass
    public void initCache()
    {
        final URIManager manager = new URIManager();
        final SchemaRegistry registry = new SchemaRegistry(manager,
            URI.create(""), addressingMode);
        final MetaSchema metaSchema
            = MetaSchema.copyOf(BuiltinSchemas.byDefault());
        cache = new JsonValidatorCache(metaSchema, registry);
    }

    @DataProvider
    public Iterator<Object[]> invalidSchemas()
    {
        return SampleNodeProvider.getSamplesExcept(NodeType.OBJECT);
    }

    @Test(dataProvider = "invalidSchemas")
    public void illegalSchemasTriggerFailingValidator(final JsonNode schema)
    {
        final SchemaContainer container = addressingMode.forSchema(schema);
        final SchemaNode schemaNode = new SchemaNode(container, schema);

        final JsonValidator validator = cache.getValidator(schemaNode);

        final ValidationContext context = new ValidationContext(cache);
        final ValidationReport report = new ValidationReport();

        validator.validate(context, report, JacksonUtils.emptyObject());

        assertFalse(report.isSuccess());
    }
}
