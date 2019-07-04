package com.cedarsoft.rxjava;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Demo4Throttle {
  @Test
  void testThrotteling() throws Exception {
    fetchLetters()
      .throttleLatest(500L, TimeUnit.MILLISECONDS, true)
      .observeOn(Schedulers.io()) //Maybe a ui thread?
      .subscribe(new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(String s) {
          LOG.info("onNext: " + s);
        }

        @Override
        public void onError(Throwable e) {
          e.printStackTrace();
        }

        @Override
        public void onComplete() {
          LOG.info("Completed");
        }
      });

    Thread.sleep(50000);
  }

  public Observable<String> fetchLetters() {
    return Observable.create(new ObservableOnSubscribe<String>() {
      @Override
      public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              for (int i = 0; i < 50; i++) {
                Thread.sleep(100);
                String nextValue = Character.toString((char) (i + 65));
                LOG.info("Emmitting " + nextValue);
                emitter.onNext(nextValue);
              }

              emitter.onComplete();
            }
            catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          }
        }, "Letter-Producer-Thread").start();
      }
    });
  }


  private static final Logger LOG = LoggerFactory.getLogger(Demo1Basics.class.getName());
}
