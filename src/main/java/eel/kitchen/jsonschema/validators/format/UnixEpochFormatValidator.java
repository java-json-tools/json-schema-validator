package eel.kitchen.jsonschema.validators.format;

import org.codehaus.jackson.JsonNode;

import java.math.BigDecimal;
import java.math.BigInteger;

public class UnixEpochFormatValidator
    extends AbstractFormatValidator
{
    private static final BigInteger ZERO = new BigInteger("0");

    @Override
    public boolean validate(final JsonNode node)
    {
        final BigInteger epoch;

        validationErrors.clear();

        if (!node.isNumber()) {
            validationErrors.add("input is not a number");
        }

        epoch = node.getDecimalValue().toBigInteger();

        if (ZERO.equals(epoch.shiftRight(31)))
            return true;

        validationErrors.add("epoch time would overflow");
        return false;
    }
}
