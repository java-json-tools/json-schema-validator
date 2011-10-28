import java.net.URI;
import java.net.URISyntaxException;

public final class Foo
{
    public static void main(final String... args)
        throws URISyntaxException
    {
        final URI uri = new URI(null, null, null, "/my fragment");

        System.out.println(uri.toASCIIString());
        System.out.println(uri.toString());
        System.out.println(uri.getFragment());
    }
}
