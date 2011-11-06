## Introduction

<p>This is an implementation of the <a href="http://www.json-schema.org/">JSON
Schema specification</a> written in pure Java. This allows pure server side JSON
schema validation if this is what you are looking for.

<p>The draft helping as a reference is, at the moment, draft version 3, which can
be found <a href="http://json-schema.org/draft-03/schema">here</a> (version 4
is around the corner, for some defintion of "corner" -- understand, days, or a
few weeks).

<p>This implementation supports all of section 5 except the following:

<ul>
<li>5.27: <tt>id</tt> is not supported yet;</li>
<li>5.28: <tt>$ref</tt> only understands URLs (not all URIs) or JSON paths (ie,
<tt>#/x/y</tt>), or a combination of both;</li>
<li>5.29: <tt>$schema</tt> is not supported yet.</li>
</ul>

## Sample usage:

<pre><code>
public final class SampleValidation()
{
    public static void main(final String... args)
    {
        //
        // See JsonLoader for loading schemas. You can load from URLs, File
        // objects, resources and more.
        //
        // Here, "schema" is the JSON document representing the schema, and
        // "instance" is the JSON document to validate.
    
        final JsonNode schema = JsonLoader.fromURL("http://some.host/path/to/schema");
        final JsonNode instance = JsonLoader.fromFile(...);
    
        final JsonValidator validator = new JsonValidator(schema);
    
        //
        // A ValidationReport object contains the state of validation. Right now
        // it ALWAYS collects validation failure messages, as deep as it can. In
        // the future, a version will exist which will NOT collect messages but
        // which will answer correctly to the .isSuccess() method below:
        //
    
        final ValidationReport report = validator.validate(instance);
    
        if (validator.isSuccess())
            System.exit(0);
    
        //
        // On failure, with the default (as of now) ValidationReport object, you
        // can collect error messages. This sample prints them out.
        //
    
        final List<String> errs = report.getMessages();
    
        for (final String err: errs)
            System.err.println(err);

        // Everything BUT 0 here -- behave
        System.exit(1);
    }
}
</code></pre>

