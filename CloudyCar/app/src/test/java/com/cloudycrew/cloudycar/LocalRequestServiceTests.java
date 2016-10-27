package com.cloudycrew.cloudycar;

import com.cloudycrew.cloudycar.fileservices.IFileService;
import com.cloudycrew.cloudycar.models.Point;
import com.cloudycrew.cloudycar.models.Route;
import com.cloudycrew.cloudycar.models.requests.AcceptedRequest;
import com.cloudycrew.cloudycar.models.requests.ConfirmedRequest;
import com.cloudycrew.cloudycar.models.requests.PendingRequest;
import com.cloudycrew.cloudycar.models.requests.Request;
import com.cloudycrew.cloudycar.requeststorage.LocalRequestService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by George on 2016-10-25.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalRequestServiceTests {
    @Mock
    private IFileService fileService;
    private LocalRequestService localRequestService;

    private PendingRequest pendingRequest;
    private AcceptedRequest acceptedRequest;
    private ConfirmedRequest confirmedRequest;

    @Before
    public void setUp() {
        Route route = new Route(new Point(0, 0), new Point(0, 0));

        pendingRequest = new PendingRequest("rider", route);
        acceptedRequest = pendingRequest.acceptRequest("driver");
        confirmedRequest = acceptedRequest.confirmRequest();

        localRequestService = new LocalRequestService(fileService);
    }

    @Test
    public void test_createRequest_ifFileDoesNotExist_thenReturnsNull() {
        when(fileService.loadFileAsString(Constants.ELASTIC_SEARCH_BASE_URL)).thenReturn(null);

        List<Request> expectedRequestList = new ArrayList<>();
        List<Request> actualRequestList = localRequestService.getRequests();

        assertEquals(expectedRequestList, actualRequestList);
    }
}