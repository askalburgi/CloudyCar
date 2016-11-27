package com.cloudycrew.cloudycar.driversummary;

import com.cloudycrew.cloudycar.ViewController;
import com.cloudycrew.cloudycar.controllers.RequestController;
import com.cloudycrew.cloudycar.models.requests.ConfirmedRequest;
import com.cloudycrew.cloudycar.models.requests.PendingRequest;
import com.cloudycrew.cloudycar.observables.IObserver;
import com.cloudycrew.cloudycar.requeststorage.IRequestStore;
import com.cloudycrew.cloudycar.users.IUserPreferences;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by George on 2016-11-05.
 */
public class DriverSummaryController extends ViewController<IDriverSummaryView> {
    private RequestController requestController;
    private IUserPreferences userPreferences;
    private IRequestStore requestStore;

    /**
     * Instantiates a new Driver summary controller.
     *
     * @param requestController the request controller
     * @param userPreferences   the user preferences
     * @param requestStore      the request store
     */
    public DriverSummaryController(RequestController requestController, IUserPreferences userPreferences, IRequestStore requestStore) {
        this.requestController = requestController;
        this.requestStore = requestStore;
        this.userPreferences = userPreferences;
    }

    /**
     * Refresh requests a driver is involved in asynchronously.
     */
    public void refreshRequests() {
        dispatchShowLoading();
        requestController.refreshRequests();
    }

    @Override
    public void attachView(IDriverSummaryView view) {
        super.attachView(view);
        requestStore.addObserver(requestStoreObserver);
    }

    @Override
    public void detachView() {
        super.detachView();
        requestStore.removeObserver(requestStoreObserver);
    }

    private IObserver<IRequestStore> requestStoreObserver = new IObserver<IRequestStore>() {
        @Override
        public void notifyUpdate(IRequestStore observable) {
            dispatchDisplayConfirmedRequests(getRequestsConfirmedForDriver());
            dispatchDisplayAcceptedRequests(getRequestsAcceptedByDriver());
        }
    };

    private List<ConfirmedRequest> getRequestsConfirmedForDriver() {
        return Observable.from(requestStore.getRequests(ConfirmedRequest.class))
                         .filter(new Func1<ConfirmedRequest, Boolean>() {
                             @Override
                             public Boolean call(ConfirmedRequest r) {
                                 return r.getDriverUsername().equals(userPreferences.getUserName());
                             }
                         })
                         .toList()
                         .toBlocking()
                         .firstOrDefault(new ArrayList<>());
    }

    private List<PendingRequest> getRequestsAcceptedByDriver() {
        return Observable.from(requestStore.getRequests(PendingRequest.class))
                         .filter(new Func1<PendingRequest, Boolean>() {
                             @Override
                             public Boolean call(PendingRequest r) {
                                 return r.hasBeenAcceptedBy(userPreferences.getUserName());
                             }
                         })
                         .toList()
                         .toBlocking()
                         .firstOrDefault(new ArrayList<>());
    }

    private void dispatchShowLoading() {
        if (getView() != null) {
            getView().displayLoading();
        }
    }

    private void dispatchDisplayConfirmedRequests(List<ConfirmedRequest> confirmedRequests) {
        if (getView() != null) {
            getView().displayConfirmedRequests(confirmedRequests);
        }
    }

    private void dispatchDisplayAcceptedRequests(List<PendingRequest> acceptedRequests) {
        if (getView() != null) {
            getView().displayAcceptedRequests(acceptedRequests);
        }
    }
}
