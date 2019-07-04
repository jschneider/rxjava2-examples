package com.cedarsoft.rxjava;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.*;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.BackpressureStrategy;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javafx.util.Pair;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Demo6FlatMap {
  @Test
  void testDemoFlatMap() throws Exception {
    //Szenario: Mehrere Files auf mehrere Server uploaden

    Observable<String> files = Observable.just("file1", "file2", "file3");

    long start = System.currentTimeMillis();

    files
      .observeOn(Schedulers.io())
      .flatMap(this::uploadToAllServers)
      .subscribe(new Observer<UploadResult>() {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(UploadResult uploadResult) {
          LOG.info("Upload finished: " + uploadResult);
        }

        @Override
        public void onError(Throwable e) {
          throw new RuntimeException(e);
        }

        @Override
        public void onComplete() {
          LOG.info("Finished after <" + (System.currentTimeMillis() - start) + " ms");
        }
      });

    Thread.sleep(10_000);
  }

  /**
   * Uploads a file to multiple servers and returns the status codes for each server
   */
  private Observable<UploadResult> uploadToAllServers(String file) throws MalformedURLException {
    Observable<URL> servers = Observable.fromArray(new URL("https://server1.com"), new URL("https://server2.com"));

    return servers
      .map(server -> {
        LOG.info("Uploading " + file + "  to " + server);
        Thread.sleep(1000);

        //Create the upload result
        return new UploadResult(server, file, 200);
      }).subscribeOn(Schedulers.io())
      ;
  }

  public static class UploadResult {
    private final URL server;

    private final String file;

    private final int statusCode;

    public UploadResult(URL server, String file, int statusCode) {
      this.server = server;
      this.file = file;
      this.statusCode = statusCode;
    }

    public URL getServer() {
      return server;
    }

    public String getFile() {
      return file;
    }

    public int getStatusCode() {
      return statusCode;
    }

    @Override
    public String toString() {
      return "UploadResult{" +
        "server=" + server +
        ", file='" + file + '\'' +
        ", statusCode=" + statusCode +
        '}';
    }
  }


  @Test
  void testParallelFileUpload() throws Exception {
    //Szenario: Mehrere Files auf mehrere Server uploaden

    Observable<String> files = Observable.just("file1", "file2", "file3");

    long start = System.currentTimeMillis();

    files
      .toFlowable(BackpressureStrategy.ERROR)
      .parallel(7)
      .runOn(Schedulers.io())
      .flatMap((String file) -> uploadToAllServers(file).toFlowable(BackpressureStrategy.BUFFER))
      .sequential()
      .subscribe(new FlowableSubscriber<UploadResult>() {
        @Override
        public void onSubscribe(Subscription s) {
          s.request(1000); //request all results
        }

        @Override
        public void onNext(UploadResult uploadResult) {
          LOG.info("Upload finished: " + uploadResult);
        }

        @Override
        public void onError(Throwable t) {
          t.printStackTrace();
        }

        @Override
        public void onComplete() {
          LOG.info("Finished after <" + (System.currentTimeMillis() - start) + " ms");
        }
      });

    Thread.sleep(10_000);
  }

  @Test
  void testCombine() throws Exception {
    long start = System.currentTimeMillis();

    Observable<String> files = Observable.just("file1", "file2", "file3");
    Observable<URL> servers = Observable.fromArray(new URL("https://server1.com"), new URL("https://server2.com")).cache();

    files
      .flatMap(file -> servers.map(server -> new Pair<>(server, file)))
      .flatMap(pair -> Single.fromCallable(new Callable<UploadResult>() {
        @Override
        public UploadResult call() throws Exception {
          Thread.sleep(1000);
          return new UploadResult(pair.getKey(), pair.getValue(), 200);
        }
      })
        .subscribeOn(Schedulers.io())
        .toObservable())
      .observeOn(Schedulers.io())
      .subscribe(new Observer<UploadResult>() {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(UploadResult uploadResult) {
          LOG.info("Upload finished: " + uploadResult);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {
          LOG.info("Finished after <" + (System.currentTimeMillis() - start) + " ms");
        }
      });


    Thread.sleep(10_000);
  }

  public Observable<String> getUrls() {
    return Observable.fromArray("https://cedarsoft.com", "https://cedarsoft.de");
  }

  private static final Logger LOG = LoggerFactory.getLogger(Demo2Threading.class.getName());
}
