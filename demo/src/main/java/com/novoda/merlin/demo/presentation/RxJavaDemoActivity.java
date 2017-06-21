package com.novoda.merlin.demo.presentation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.novoda.merlin.MerlinsBeard;
import com.novoda.merlin.NetworkStatus;
import com.novoda.merlin.demo.R;
import com.novoda.merlin.demo.connectivity.display.NetworkStatusDisplayer;
import com.novoda.merlin.demo.connectivity.display.NetworkStatusSnackbarDisplayer;
import com.novoda.merlin.rxjava.MerlinObservable;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class RxJavaDemoActivity extends Activity {

    private NetworkStatusDisplayer networkStatusDisplayer;
    private MerlinsBeard merlinsBeard;
    private CompositeSubscription subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        View rootView = findViewById(R.id.root);
        networkStatusDisplayer = new NetworkStatusSnackbarDisplayer(getResources(), rootView);
        merlinsBeard = MerlinsBeard.from(this);
        subscriptions = new CompositeSubscription();

        findViewById(R.id.current_status).setOnClickListener(networkStatusOnClick);
        findViewById(R.id.wifi_connected).setOnClickListener(wifiConnectedOnClick);
        findViewById(R.id.mobile_connected).setOnClickListener(mobileConnectedOnClick);
        findViewById(R.id.network_subtype).setOnClickListener(networkSubtypeOnClick);
    }

    private final View.OnClickListener networkStatusOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (merlinsBeard.isConnected()) {
                networkStatusDisplayer.displayConnected();
            } else {
                networkStatusDisplayer.displayDisconnected();
            }
        }
    };

    private final View.OnClickListener wifiConnectedOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (merlinsBeard.isConnectedToWifi()) {
                networkStatusDisplayer.displayConnected();
            } else {
                networkStatusDisplayer.displayDisconnected();
            }
        }
    };

    private final View.OnClickListener mobileConnectedOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (merlinsBeard.isConnectedToMobileNetwork()) {
                networkStatusDisplayer.displayConnected();
            } else {
                networkStatusDisplayer.displayDisconnected();
            }
        }
    };

    private final View.OnClickListener networkSubtypeOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            networkStatusDisplayer.displayNetworkSubtype(merlinsBeard.getMobileNetworkSubtypeName());
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Subscription merlinSubscription = MerlinObservable.from(this)
                .distinctUntilChanged()
                .subscribe(new NetworkAction(networkStatusDisplayer));
        subscriptions.add(merlinSubscription);
    }

    @Override
    protected void onPause() {
        super.onPause();
        networkStatusDisplayer.reset();
        subscriptions.clear();
    }

    private static class NetworkAction implements Action1<NetworkStatus> {

        private final NetworkStatusDisplayer networkStatusDisplayer;

        NetworkAction(NetworkStatusDisplayer networkStatusDisplayer) {
            this.networkStatusDisplayer = networkStatusDisplayer;
        }

        @Override
        public void call(NetworkStatus networkStatus) {
            if (networkStatus.isAvailable()) {
                networkStatusDisplayer.displayConnected();
            } else {
                networkStatusDisplayer.displayDisconnected();
            }
        }
    }

}
