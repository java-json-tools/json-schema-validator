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
    }

    public UnixEpochFormatValidator()
    {
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        messages.clear();

        final BigInteger epoch = node.getDecimalValue().toBigInteger();

        if (ZERO.equals(epoch.shiftRight(EPOCH_SHIFT)))
            return true;

        messages.add("epoch time would overflow");
        return false;
    }
}
