package wimf.ingest;

import com.google.common.collect.Iterables;
import io.reactivex.Observable;

import java.util.Objects;

/**
 * Creates an Observable of T from a paginated query
 */
final class RxPageUtil {
    static <T> Observable<T> getAll(final Pager<T> pager) {
        return getAll(0, pager, null);
    }

    private static <T> Observable<T> getAll(final int pageNumber,
                                            final Pager<T> pager,
                                            final IterableCloser<T> prevPage) {
        return Observable.defer(() -> {
            // TODO: look into how to create/cleanup resources in Rx - this only works if the stream is completely consumed (I think)
            if (!Objects.isNull(prevPage)) {
                prevPage.close();
            }

            final IterableCloser<T> page = pager.getPage(pageNumber);
            if (Iterables.isEmpty(page)) {
                page.close();
                return Observable.empty();
            }

            return Observable
                    .fromIterable(page)
                    .concatWith(getAll(pageNumber + 1, pager, page));
        });
    }

    interface Pager<T> {
        IterableCloser<T> getPage(int pageNumber) throws Exception;
    }
}
