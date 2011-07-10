package eel.kitchen.jsonschema.validators.format;

import com.steadystate.css.parser.SACParserCSS21;
import org.codehaus.jackson.JsonNode;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

public class CSSColorValidator
    extends AbstractFormatValidator
{
    private static final List<String> colorNames = Arrays.asList(
        "maroon",
        "red",
        "orange",
        "yellow",
        "olive",
        "green",
        "purple",
        "fuschia",
        "lime",
        "teal",
        "aqua",
        "blue",
        "navy",
        "black",
        "gray",
        "silver",
        "white"
    );

    private final Parser parser = new SACParserCSS21();

    @Override
    public boolean validate(final JsonNode node)
    {
        final String value = node.getTextValue();

        validationErrors.clear();

        if (colorNames.contains(value.toLowerCase()))
            return true;

        final StringReader reader = new StringReader(value);
        final InputSource source = new InputSource(reader);
        final LexicalUnit lexicalUnit;

        try {
            lexicalUnit = parser.parsePropertyValue(source);
            if (LexicalUnit.SAC_RGBCOLOR != lexicalUnit.getLexicalUnitType())
                throw new CSSException();
            return true;
        } catch (CSSException e) {
            validationErrors.add("string is not a valid CSS 2.1 color");
            return false;
        } catch (IOException e) {
            validationErrors.add("I/O error, cannot validate!");
            return false;
        }
    }
}
