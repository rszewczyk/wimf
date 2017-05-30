package wimf.ingest;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * tests for {@link JsonIterable}
 */
public class JsonIterableTest {
    @Test
    public void it_parses_an_empty_array() throws Exception {
        final Iterator<SomeType> iterator = JsonIterable.from(
                new ByteArrayInputStream("[]".getBytes()),
                SomeType.class).iterator();

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    public void it_parses_an_array() throws Exception {
        final Iterator<SomeType> iterator = JsonIterable.from(
                new ByteArrayInputStream("[{\"name\":\"foo\"},{\"name\":\"bar\"}]".getBytes()),
                SomeType.class
        ).iterator();

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next().name).isEqualTo("foo");
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next().name).isEqualTo("bar");
        assertThat(iterator.hasNext()).isFalse();
    }

    static class SomeType {
        public String name;
    }
}
