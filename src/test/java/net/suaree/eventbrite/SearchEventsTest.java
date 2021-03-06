package net.suaree.eventbrite;

import net.suaree.eventbrite.exception.RequestErrorException;
import net.suaree.eventbrite.exception.RequestException;
import net.suaree.eventbrite.model.*;
import net.suaree.eventbrite.model.Error;
import net.suaree.eventbrite.operations.EventsResult;
import net.suaree.eventbrite.operations.SearchRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Unit Tests for the EventbriteClient.search wrapper of event_search.
 *
 * @author roger
 */
public final class SearchEventsTest extends TestBase {
    @Test
    public void testBasicError() {
        EventbriteClient client = new EventbriteClient(getCredentials(),
                new ResourceBasedHttpClient("/Search-Error.json"));
        SearchRequest request = new SearchRequest();

        try {
            client.search(request);
            Assert.fail();
        } catch (RequestErrorException ex) {
            Error error = ex.getError();

            Assert.assertNotNull(error);
            Assert.assertEquals("Distance error", error.getErrorType());
            Assert.assertEquals("Distance (integer) is invalid [ 10.0 ]", error.getErrorMessage());
        } catch (RequestException ex) {
            Assert.fail();
        }
    }

    @Test
    public void testBasicSearch() throws RequestException {
        EventbriteClient client = new EventbriteClient(getCredentials(),
                new ResourceBasedHttpClient("/SearchResult-10.json"));
        SearchRequest request = new SearchRequest();

        request.setCity("San Francisco");
        request.setWithin(10);
        request.setWithinUnit(WithinUnit.Miles);
        request.setSearchDate(new LabelSearchDate(DateLabel.Today));
        request.setAllCategories(
                Category.Conferences,
                Category.Conventions,
                Category.Entertainment,
                Category.Fairs,
                Category.Food,
                Category.Music,
                Category.Performances,
                Category.Recreation,
                Category.Sales,
                Category.Seminars,
                Category.Sports,
                Category.Social,
                Category.Tradeshows,
                Category.Travel);

        EventsResult result = client.search(request);

        Assert.assertNotNull(result);

        List<EventData> eventData = result.getEvents();

        Assert.assertNotNull(eventData);
        Assert.assertEquals(11, eventData.size()); // 10 Events + 1 Summary
        Assert.assertTrue(eventData.get(0) instanceof EventSummaryData);

        EventSummaryData summary = (EventSummaryData) eventData.get(0);
        Assert.assertEquals(1922, summary.getTotalItems().intValue());
        Assert.assertEquals(5620119930L, summary.getFirstEvent().longValue());
        Assert.assertEquals(6113316093L, summary.getLastEvent().longValue());
        Assert.assertEquals(10, summary.getNumShowing().intValue());
    }
}
