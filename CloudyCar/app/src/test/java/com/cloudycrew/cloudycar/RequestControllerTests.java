package com.cloudycrew.cloudycar;

import com.cloudycrew.cloudycar.controllers.RequestController;
import com.cloudycrew.cloudycar.models.Location;
import com.cloudycrew.cloudycar.models.Route;
import com.cloudycrew.cloudycar.models.User;
import com.cloudycrew.cloudycar.models.requests.CompletedRequest;
import com.cloudycrew.cloudycar.models.requests.ConfirmedRequest;
import com.cloudycrew.cloudycar.models.requests.PendingRequest;
import com.cloudycrew.cloudycar.models.requests.Request;
import com.cloudycrew.cloudycar.requeststorage.IRequestService;
import com.cloudycrew.cloudycar.requeststorage.IRequestStore;
import com.cloudycrew.cloudycar.scheduling.ISchedulerProvider;
import com.cloudycrew.cloudycar.scheduling.TestSchedulerProvider;
import com.cloudycrew.cloudycar.users.IUserPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Created by George on 2016-10-12.
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestControllerTests {
    @Mock
    private IRequestStore requestStore;
    @Mock
    private IRequestService requestService;
    @Mock
    private IUserPreferences userPreferences;

    private ISchedulerProvider schedulerProvider;

    private RequestController requestController;

    private String riderUsername;
    private String driverUsername;

    private User rider;
    private User driver;
    private String testDescription;
    private String requestDescription;

    private PendingRequest request1;
    private PendingRequest request2;
    private PendingRequest acceptedRequest1;
    private ConfirmedRequest confirmedRequest1;
    private CompletedRequest completedRequest1;

    @Before
    public void set_up() {
        riderUsername = "janedoedoe";
        driverUsername = "driverdood";
        testDescription = "test description";
        requestDescription = "description";

        rider = new User(riderUsername);
        driver = new User(driverUsername);

        Location startingLocation = new Location(48.1472373, 11.5673969,testDescription );
        Location endingLocation = new Location(48.1258551, 11.5121003,testDescription );

        Route route = new Route(startingLocation, endingLocation);

        double price = 3.50;

        request1 = new PendingRequest(riderUsername, route, price, requestDescription);
        request2 = new PendingRequest(riderUsername, route, price, requestDescription);

        acceptedRequest1 = request1.accept(driverUsername);

        confirmedRequest1 = acceptedRequest1.confirmRequest(driverUsername);
        completedRequest1 = confirmedRequest1.completeRequest();

        schedulerProvider = new TestSchedulerProvider();
        requestController = new RequestController(userPreferences, requestStore, requestService, schedulerProvider);
    }

    @Test
    public void test_createRequest_thenStoreContainsNewPendingRequest() {
        Location startingLocation = new Location(48.1472373, 11.5673969,testDescription );
        Location endingLocation = new Location(48.1258551, 11.5121003,testDescription );

        Route route = new Route(startingLocation, endingLocation);

        requestController.createRequest(route, 3.5, testDescription);

        verify(requestStore).addRequest(request1);
        verify(requestService).createRequest(request1);
    }

    @Test
    public void test_cancelRequest_deleteRequestIsCalledWithCorrectRequestId() {
        when(requestStore.getRequest(request1.getId())).thenReturn(request1);
        when(userPreferences.getUserName()).thenReturn(riderUsername);

        requestController.cancelRequest(request1.getId());

        verify(requestStore).deleteRequest(request1.getId());
        verify(requestService).deleteRequest(request1.getId());
    }

    @Test
    public void test_completeRequest_ifStoreDoesNotContainRequest_thenNothingHappens() {
        when(userPreferences.getUserName()).thenReturn(riderUsername);

        requestController.completeRequest(confirmedRequest1.getId());

        verify(requestStore, never()).updateRequest(any(Request.class));
        verify(requestService, never()).updateRequest(any(Request.class));
    }

    @Test
    public void test_completeRequest_ifStoreContainsRequest_thenUpdateRequestIsCalledWithTheExpectedCompletedRequest() {
        when(requestStore.getRequest(confirmedRequest1.getId(), ConfirmedRequest.class)).thenReturn(confirmedRequest1);
        when(userPreferences.getUserName()).thenReturn(riderUsername);

        requestController.completeRequest(confirmedRequest1.getId());

        verify(requestStore).updateRequest(completedRequest1);
        verify(requestService).updateRequest(completedRequest1);
    }

    @Test
    public void test_confirmRequest_ifStoreDoesNotContainRequest_thenNothingHappens() {
        when(userPreferences.getUserName()).thenReturn(riderUsername);

        requestController.confirmRequest(acceptedRequest1.getId(), driverUsername);

        verify(requestStore, never()).updateRequest(any(Request.class));
        verify(requestService, never()).updateRequest(any(Request.class));
    }

    @Test
    public void test_confirmRequest_ifStoreContainsRequest_thenUpdateRequestIsCalledWithTheExpectedConfirmedRequest() {
        when(requestStore.getRequest(acceptedRequest1.getId(), PendingRequest.class)).thenReturn(acceptedRequest1);
        when(userPreferences.getUserName()).thenReturn(driverUsername);

        requestController.confirmRequest(acceptedRequest1.getId(), driverUsername);

        verify(requestStore).updateRequest(confirmedRequest1);
        verify(requestService).updateRequest(confirmedRequest1);
    }

    @Test
    public void test_acceptRequest_ifStoreDoesNotContainRequest_thenNothingHappens() {
        when(userPreferences.getUserName()).thenReturn(riderUsername);

        requestController.acceptRequest(acceptedRequest1.getId());

        verify(requestStore, never()).addRequest(any(Request.class));
        verify(requestService, never()).createRequest(any(Request.class));
    }

    @Test
    public void test_acceptRequest_ifRequestExistsAndIsPending_thenStoreIsUpdatedWithTheAcceptedRequest() {
        when(requestStore.getRequest(request1.getId(), PendingRequest.class)).thenReturn(request1);
        when(userPreferences.getUserName()).thenReturn(driverUsername);

        requestController.acceptRequest(request1.getId());

        verify(requestStore).updateRequest(acceptedRequest1);
        verify(requestService).updateRequest(acceptedRequest1);
    }
}
