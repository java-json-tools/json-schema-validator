package eel.kitchen.jsonschema.validators.format;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import java.math.BigInteger;

public final class UnixEpochFormatValidator
    extends AbstractValidator
{
    private static final BigInteger ZERO = new BigInteger("0");
    private static final int EPOCH_SHIFT = 31;

    public UnixEpochFormatValidator(final JsonNode ignored)
    {
        super(ignored);
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        final BigInteger epoch;

        validationErrors.clear();

        if (!node.isNumber()) {
            validationErrors.add("input is not a number");
        }

        epoch = node.getDecimalValue().toBigInteger();

        if (ZERO.equals(epoch.shiftRight(EPOCH_SHIFT)))
            return true;

        validationErrors.add("epoch time would overflow");
        return false;
    }
}
