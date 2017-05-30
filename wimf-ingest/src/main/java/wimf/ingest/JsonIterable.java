package wimf.ingest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * consumes a data source as a JSON array of T using the Jackson streaming API
 */
final class JsonIterable<T> implements IterableCloser<T> {
    private final JsonParser parser;
    private final Class<T> type;

    static <U> JsonIterable<U> from(final InputStream in, final Class<U> type) throws Exception {
        final JsonParser parser = new ObjectMapper().getFactory().createParser(in);

        // skip first token (START_ARRAY)
        parser.nextToken();
        parser.nextToken();

        return new JsonIterable<>(parser, type);
    }

    private JsonIterable(final JsonParser parser, final Class<T> type) {
        this.parser = parser;
        this.type = type;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return !parser.hasToken(JsonToken.END_ARRAY);
            }

            @Override
            public T next() {
                final T val;
                try {
                    val = parser.readValueAs(type);
                    parser.nextToken();
                } catch(IOException e) {
                    throw new RuntimeException(e);
                }

                return val;
            }
        };
    }

    @Override
    public void close() throws Exception {
        parser.close();
    }
}
