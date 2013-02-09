package com.github.fge.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.metaschema.BuiltinSchemas;
import com.github.fge.jsonschema.metaschema.MetaSchema;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.schema.AddressingMode;
import com.github.fge.jsonschema.schema.SchemaContext;
import com.github.fge.jsonschema.schema.SchemaNode;
import com.github.fge.jsonschema.schema.SchemaRegistry;
import com.github.fge.jsonschema.uri.URIManager;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.NodeType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Iterator;

import static org.testng.Assert.*;

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
        final SchemaContext schemaContext = addressingMode.forSchema(schema);
        final SchemaNode schemaNode = new SchemaNode(schemaContext, schema);

        final JsonValidator validator = cache.getValidator(schemaNode);

        final ValidationContext context = new ValidationContext(cache);
        final ValidationReport report = new ValidationReport();

        validator.validate(context, report, JacksonUtils.emptyObject());

        assertFalse(report.isSuccess());
    }
}
