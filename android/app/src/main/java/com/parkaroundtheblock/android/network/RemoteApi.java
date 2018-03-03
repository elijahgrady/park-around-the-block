package com.parkaroundtheblock.android.network;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by jacobilin on 3/3/18.
 */

public class RemoteApi {
    private static RemoteApi instance;

    private List<Disposable> disposables;

    private RemoteApi() {
        disposables = new ArrayList<>();
    }

    public static RemoteApi getInstance() {
        if (instance == null) {
            synchronized (RemoteApi.class) {
                if (instance == null) {
                    instance = new RemoteApi();
                }
            }
        }

        return instance;
    }

    private void cleanupDisposables() {
        List<Disposable> newDisposables = new ArrayList<>();

        for (Disposable disposable : disposables) {
            if (!disposable.isDisposed()) {
                newDisposables.add(disposable);
            }
        }

        disposables = newDisposables;
    }

    public void cleanup() {
        for (Disposable disposable : disposables) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }

        disposables = new ArrayList<>();
    }

    public void call(Context context, Consumer<? super Response<Void>> onNext, Consumer<? super Throwable> onError) {
        cleanupDisposables();
        disposables.add(RequestManager.getDefault(context).call()
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(onNext, onError));
    }
}
