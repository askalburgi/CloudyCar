package com.cloudycrew.cloudycar;

/**
 * Created by George on 2016-10-12.
 */

import com.cloudycrew.cloudycar.models.Point;
import com.cloudycrew.cloudycar.models.Route;
import com.cloudycrew.cloudycar.models.User;
import com.cloudycrew.cloudycar.models.requests.PendingRequest;
import com.cloudycrew.cloudycar.models.requests.Request;
import com.cloudycrew.cloudycar.requeststorage.IRequestStore;
import com.cloudycrew.cloudycar.search.ISearchService;
import com.cloudycrew.cloudycar.search.SearchContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchingTests {
    @Mock
    private IRequestStore requestStore;
    @Mock
    private ISearchService searchService;

    private PendingRequest request1;
    private PendingRequest request2;
    private String testDescription;

    @Before
    public void set_up() {
        testDescription = "test description";
        Point startingPoint1 = new Point(48.1472373, 11.5673969,testDescription);
        Point endingPoint1 = new Point(48.1258551, 11.5121003,testDescription);

        Route route1 = new Route(startingPoint1,endingPoint1);

        Point startingPoint2 = new Point(53.5225, 113.6242,testDescription);
        Point endingPoint2 = new Point(53.5232, 113.5263,testDescription);

        Route route2 = new Route(startingPoint2,endingPoint2);

        User user= new User("SomeUser");
        double price = 2.5;

        request1 = new PendingRequest(user.getUsername(), route1, price);
        request2 = new PendingRequest(user.getUsername(), route2, price);

        when(requestStore.getRequests()).thenReturn(Arrays.asList(request1, request2));
    }

    @Test
    public void test_searchByGeoLocation_ifThereAreNoMatchingResults_thenReturnsEmptyList() {
        SearchContext searchContext = new SearchContext().withLocation(0 , 0, 5);

        List<PendingRequest> searchResults = searchService.search(searchContext);

        assertTrue(searchResults.isEmpty());
    }

    @Test
    public void test_searchByGeoLocation_ifThereAreMatchingResults_thenReturnsResults() {
        SearchContext searchContext = new SearchContext().withLocation(0 , 0, 5);

        List<PendingRequest> expectedSearchResults = Arrays.asList(request1);
        List<PendingRequest> searchResults = searchService.search(searchContext);

        assertEquals(expectedSearchResults, searchResults);
    }

    @Test
    public void test_searchByKeyword_ifThereAreNoMatchingResults_thenReturnsEmptyList() {
        SearchContext searchContext = new SearchContext().withKeyword("expensive");

        List<PendingRequest> searchResults = searchService.search(searchContext);

        assertTrue(searchResults.isEmpty());
    }

    @Test
    public void test_searchByKeyword_ifThereAreMatchingResults_thenReturnsResults() {
        SearchContext searchContext = new SearchContext().withKeyword("cheap");

        List<PendingRequest> expectedSearchResults = Arrays.asList(request2);
        List<PendingRequest> actualSearchResults = searchService.search(searchContext);

        assertEquals(expectedSearchResults, actualSearchResults);
    }
}
