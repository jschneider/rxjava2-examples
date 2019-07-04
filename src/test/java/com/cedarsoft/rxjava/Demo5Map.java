package com.cedarsoft.rxjava;

import java.io.InputStream;
import java.net.URL;

import org.junit.jupiter.api.*;

import com.google.common.io.ByteStreams;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import javafx.util.Pair;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Demo5Map {
  public Observable<String> getUrlsAsString() {
    return Observable.fromArray("https://cedarsoft.com", "https://cedarsoft.de");
  }

  @Test
  void demo1FetchUrls() throws Exception {
    getUrlsAsString()
      .map(new Function<String, URL>() {
        @Override
        public URL apply(String s) throws Exception {
          return new URL(s);
        }
      })
      .map(new Function<URL, InputStream>() {
        @Override
        public InputStream apply(URL url) throws Exception {
          return url.openStream();
        }
      })
      .map(new Function<InputStream, String>() {
        @Override
        public String apply(InputStream inputStream) throws Exception {
          return new String(ByteStreams.toByteArray(inputStream));
        }
      })
      .subscribe(new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(String s) {
          System.out.println("-------------------");
          System.out.println("s = " + s.length() + " -- " + s.hashCode());
          System.out.println("-------------------");
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onComplete() {
        }
      });
  }

  @Test
  void demo1FetchUrlsLambda() throws Exception {
    getUrlsAsString()
      .map(URL::new)
      .map(URL::openStream)
      .map(inputStream -> new String(ByteStreams.toByteArray(inputStream)))
      .subscribe(new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(String s) {
          System.out.println("-------------------");
          System.out.println("s = " + s.length() + " -- " + s.hashCode());
          System.out.println("-------------------");
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onComplete() {
        }
      });
  }

  @Test
  void demo2FetchUrlsWithPairs() throws Exception {
    getUrlsAsString()
      .map(URL::new)
      .map(url -> new Pair<>(url, url.openStream()))
      .map(pair -> new Pair<>(pair.getKey(), new String(ByteStreams.toByteArray(pair.getValue()))))
      .subscribe(new Observer<Pair<URL, String>>() {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(Pair<URL, String> pair) {
          String s = pair.getValue();
          System.out.println("-------------------");
          System.out.println(pair.getKey());
          System.out.println("s = " + s.length() + " -- " + s.hashCode());
          System.out.println("-------------------");
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onComplete() {
        }
      });
  }
}
