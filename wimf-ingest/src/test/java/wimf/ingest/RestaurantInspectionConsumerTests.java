package wimf.ingest;

import com.socrata.api.HttpLowLevel;
import com.socrata.api.Soda2Consumer;
import com.socrata.exceptions.LongRunningQueryException;
import com.socrata.exceptions.SodaError;
import com.socrata.model.soql.SoqlQuery;
import com.sun.jersey.api.client.ClientResponse;
import io.reactivex.Observable;
import org.junit.Before;
import org.junit.Test;
import wimf.ingest.test.FakeData;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;

/**
 * tests for {@link RestaurantInspectionConsumer}
 */
public class RestaurantInspectionConsumerTests {
    private static final String SOME_RESOURCE_ID = "some_resource_id";

    private Soda2Consumer soda2Consumer;
    private RestaurantInspectionConsumer restaurantInspectionConsumer;

    @Before
    public void before() {
        soda2Consumer = mock(Soda2Consumer.class);
        restaurantInspectionConsumer = new RestaurantInspectionConsumer(1,
                SOME_RESOURCE_ID, soda2Consumer);
    }

    @Test
    public void it_lazily_queries_only_what_is_needed() throws SodaError, LongRunningQueryException {
        // given there is one page followed by an empty page
        apiReturns(mockClientResponse(FakeData.ONE_INSPECTION), mockClientResponse(FakeData.EMPTY_ARRAY));

        // when one item is requested
        final Observable<RestaurantInspection> inspections = restaurantInspectionConsumer.getAll().take(1);

        // then one event is returned
        inspections.test().assertValueCount(1);

        // and the api was called only once
        verify(soda2Consumer, times(1))
                .query(eq(SOME_RESOURCE_ID), eq(HttpLowLevel.JSON_TYPE), any(SoqlQuery.class));
    }

    @Test
    public void it_lazily_queries_more_as_needed() throws SodaError, LongRunningQueryException {
        // given there are two pages followed by an empty page
        apiReturns(mockClientResponse(FakeData.ONE_INSPECTION),
                mockClientResponse(FakeData.ONE_INSPECTION),
                mockClientResponse(FakeData.EMPTY_ARRAY));

        // when two items are requested
        final Observable<RestaurantInspection> inspections = restaurantInspectionConsumer.getAll().take(2);

        // then two events are returned
        inspections.test().assertValueCount(2);

        // and the api was called twice
        verify(soda2Consumer, times(2))
                .query(eq(SOME_RESOURCE_ID), eq(HttpLowLevel.JSON_TYPE), any(SoqlQuery.class));
    }

    @Test
    public void it_queries_for_more_pages_until_complete() throws SodaError, LongRunningQueryException {
        // given there are two pages followed by an empty page
        apiReturns(mockClientResponse(FakeData.ONE_INSPECTION),
                mockClientResponse(FakeData.ONE_INSPECTION),
                mockClientResponse(FakeData.EMPTY_ARRAY));

        // when five items are requested
        final Observable<RestaurantInspection> inspections = restaurantInspectionConsumer.getAll().take(5);

        // then two events are returned
        inspections.test().assertValueCount(2);

        // and the api was called three times
        verify(soda2Consumer, times(3))
                .query(eq(SOME_RESOURCE_ID), eq(HttpLowLevel.JSON_TYPE), any(SoqlQuery.class));
    }

    private void apiReturns(ClientResponse first, ClientResponse ...rest) throws SodaError, LongRunningQueryException {
        when(soda2Consumer.query(eq(SOME_RESOURCE_ID), eq(HttpLowLevel.JSON_TYPE), any(SoqlQuery.class)))
                .thenReturn(first, rest);
    }

    private static ClientResponse mockClientResponse(final String content) {
        ClientResponse cr = mock(ClientResponse.class);
        when(cr.getEntityInputStream()).thenReturn(new ByteArrayInputStream(content.getBytes()));
        return cr;
    }
}
