package eel.kitchen.jsonschema.validators;

import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;
import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.Map;

public final class ObjectSchemaProvider
    implements SchemaProvider
{
    private final Map<String, JsonNode> properties
        = new HashMap<String, JsonNode>();
    private final Map<Pattern, JsonNode> patternProperties
        = new HashMap<Pattern, JsonNode>();
    private final JsonNode additionalProperties;
    private final PatternMatcher matcher = new Perl5Matcher();

    public ObjectSchemaProvider(final Map<String, JsonNode> properties,
        final Map<Pattern, JsonNode> patternProperties,
        final JsonNode additionalProperties)
    {
        this.properties.putAll(properties);
        this.patternProperties.putAll(patternProperties);
        this.additionalProperties = additionalProperties;
    }

    @Override
    public JsonNode getSchemaForPath(final String path)
    {
        if (properties.containsKey(path))
            return properties.get(path);

        for (final Pattern pattern: patternProperties.keySet())
            if (matcher.contains(path, pattern))
                return patternProperties.get(pattern);

        return additionalProperties;
    }
}
