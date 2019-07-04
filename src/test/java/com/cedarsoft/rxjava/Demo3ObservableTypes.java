package com.cedarsoft.rxjava;

import org.junit.jupiter.api.*;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class Demo3ObservableTypes {
  @Test
  void testObservable() {
    Observable<String> observable = getBestMovies();

    //n Elemente. Eventuell ein Fehler. Irgendwann onComplete
    observable.subscribe(new Observer<String>() {
      @Override
      public void onSubscribe(Disposable disposable) {
      }

      @Override
      public void onNext(String s) {
      }

      @Override
      public void onError(Throwable throwable) {
      }

      @Override
      public void onComplete() {
      }
    });
  }

  public Observable<String> getBestMovies() {
    return Observable.fromArray("The Godfather", "The Shawshank Redemption", "Pulp Fiction", "Star Wars", "The Dark Knight");
  }

  @Test
  void testSingle() {
    Single<String> single = getBestMovie();

    //Liefert einen Wert (onSuccess): Oder Fehler (onError)
    single.subscribe(new SingleObserver<String>() {
      @Override
      public void onSubscribe(Disposable disposable) {
      }

      @Override
      public void onSuccess(String s) {
      }

      @Override
      public void onError(Throwable throwable) {
      }
    });
  }

  public Single<String> getBestMovie() {
    return Single.just("The Godfather");
  }

  @Test
  void testMaybe() {
    Maybe<String> maybe = getBestMovieIfAvailable();

    //Liefert einen Wert (onSuccess) oder keinen Wert (onComplete) oder einen Fehler (onError)
    maybe.subscribe(new MaybeObserver<String>() {
      @Override
      public void onSubscribe(Disposable d) {
      }

      @Override
      public void onSuccess(String s) {
      }

      @Override
      public void onError(Throwable e) {
      }

      @Override
      public void onComplete() {
      }
    });
  }

  public Maybe<String> getBestMovieIfAvailable() {
    return Maybe.just("The Godfather");
  }


  @Test
  void testCompletable() {
    Completable completable = commit();

    //Liefert Erfolg (onComplete) oder einen Error (onError)
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

  public Completable commit() {
    return Completable.complete();
  }


  @Test
  void testConvertTypes() {
    Observable<String> observable = getBestMovies();

    Single<String> single1 = observable.singleOrError();
    Single<String> single2 = observable.single("Default Value");

    Maybe<String> maybe1 = observable.firstElement();
    Maybe<String> maybe2 = single1.toMaybe();

    Single<String> singleFromMaybe = maybe1.toSingle("Default Value");

    Completable completable1 = maybe1.ignoreElement();
    Completable completable2 = single1.ignoreElement();
  }


  @Test
  void testFlowable() {
    Flowable<String> flowable = getBestMoviesAsFlowable();

    flowable.subscribe(new Subscriber<String>() {
      @Override
      public void onSubscribe(Subscription s) {
        //Request elements when ready!
        s.request(3);
      }

      @Override
      public void onNext(String s) {
      }

      @Override
      public void onError(Throwable t) {
      }

      @Override
      public void onComplete() {
      }
    });
  }

  public Flowable<String> getBestMoviesAsFlowable() {
    return Flowable.fromArray("The Godfather", "The Shawshank Redemption", "Pulp Fiction", "Star Wars", "The Dark Knight");
  }

  @Test
  void testFlowableObservable() {
    Observable<String> observable = getBestMovies();

    //Was tun, wenn der Konsument nicht rechtzeitig neue Elemente verlangt?

    Flowable<String> flowable0 = observable.toFlowable(BackpressureStrategy.ERROR); //Exception werfen und abbrechen
    Flowable<String> flowable1 = observable.toFlowable(BackpressureStrategy.BUFFER); //Buffern (so lange der Speicher reicht)
    Flowable<String> flowable2 = observable.toFlowable(BackpressureStrategy.DROP); //Werte einfach verwerfen
    Flowable<String> flowable3 = observable.toFlowable(BackpressureStrategy.LATEST); //Werte verwerfen, falls es zu schnell gehen sollte, aber den jüngsten aufbewahren
    Flowable<String> flowable4 = observable.toFlowable(BackpressureStrategy.MISSING); //Für Spezial-Fälle

    //Das geht ohne weiteres. Wir liefern die Elemente einfach so schnell wie es geht
    Observable<String> observable1 = flowable4.toObservable();
  }
}
