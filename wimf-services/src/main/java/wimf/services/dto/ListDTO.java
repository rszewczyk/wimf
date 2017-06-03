package wimf.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ListDTO<T> {
    public final List<T> items;
    public final long total;

    public static <U> ListDTO<U> from(final long total, List<U> items) {
        return new ListDTO<>(total, items);
    }

    @JsonCreator
    public static <U> ListDTO<U> of(@JsonProperty("total") long total,
                                    @JsonProperty("items") List<U> items) {
        return new ListDTO<>(total, items);
    }

    private ListDTO(final long total, final List<T> items) {
        this.total = total;
        this.items = items;
    }
}
