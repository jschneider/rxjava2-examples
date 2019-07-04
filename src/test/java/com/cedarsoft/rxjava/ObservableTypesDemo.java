package com.cedarsoft.rxjava;

import org.junit.jupiter.api.*;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class ObservableTypesDemo {
  @Test
  void testTypes() throws Exception {
    Observable<Integer> observable = Observable.fromArray(1, 2, 3);

    Maybe<Integer> maybe = observable.firstElement();
    maybe.subscribe(new MaybeObserver<Integer>() {
      @Override
      public void onSubscribe(Disposable d) {
      }

      @Override
      public void onSuccess(Integer integer) {
      }

      @Override
      public void onError(Throwable e) {
      }

      @Override
      public void onComplete() {
      }
    });

    Single<Integer> single = maybe.toSingle(7);
    single.subscribe(new SingleObserver<Integer>() {
      @Override
      public void onSubscribe(Disposable d) {
      }

      @Override
      public void onSuccess(Integer integer) {
      }

      @Override
      public void onError(Throwable e) {
      }
    });

    Completable completable = single.ignoreElement();
    completable.subscribe(new CompletableObserver() {
      @Override
      public void onSubscribe(Disposable d) {
      }

      @Override
      public void onComplete() {
      }

      @Override
      public void onError(Throwable e) {
      }
    });
  }

  @Test
  void testFlowableObservable() throws Exception {
    Observable<Integer> observable = Observable.fromArray(1, 2, 3);

    Flowable<Integer> flowable = observable.toFlowable(BackpressureStrategy.ERROR);
  }

  @Test
  void testFlowableCreate() throws Exception {
    Flowable<String> flowable = Flowable.create(new FlowableOnSubscribe<String>() {
      @Override
      public void subscribe(FlowableEmitter<String> emitter) throws Exception {
        long numberOfRequestedItems = emitter.requested();
      }
    }, BackpressureStrategy.BUFFER);
  }
}
