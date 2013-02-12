package com.github.fge.jsonschema.processors;

import com.github.fge.jsonschema.exceptions.InvalidInstanceException;
import com.github.fge.jsonschema.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ExceptionProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;

public enum ValidationDomain
{
    SYNTAX
    {
        @Override
        public ExceptionProvider exceptionProvider()
        {
            return new ExceptionProvider()
            {
                @Override
                public ProcessingException doException(
                    final ProcessingMessage message)
                {
                    return new InvalidSchemaException(message);
                }
            };
        }
    },
    INSTANCE
    {
        @Override
        public ExceptionProvider exceptionProvider()
        {
            return new ExceptionProvider()
            {
                @Override
                public ProcessingException doException(
                    final ProcessingMessage message)
                {
                    return new InvalidInstanceException(message);
                }
            };
        }
    },
    ;

    public abstract ExceptionProvider exceptionProvider();
}
