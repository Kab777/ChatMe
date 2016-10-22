package com.junyu.IMBudget.rxJava;

import rx.Observer;

/**
 * Created by Junyu on 10/19/2016.
 */

public abstract class SimpleObserver<T> implements Observer<T> {

    @Override public void onCompleted() {

    }

    @Override public void onError(Throwable e) {

    }

    @Override public void onNext(T t) {

    }

}
