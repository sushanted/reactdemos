package sr.reactdemos.mono.construct;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import reactor.core.publisher.Mono;

public class MonoUsing {

  public static void main(final String[] args) {

    final Callable<HttpURLConnection> queryGenerator = () -> {
      try {
        return (HttpURLConnection) new URL("https://www.google.com/search?q=mono").openConnection();
      } catch (final MalformedURLException e) {
        e.printStackTrace();
      } catch (final IOException e) {
        e.printStackTrace();
      }
      return null;
    };

    System.out.println(
        Mono.using(//
            queryGenerator, //
            conn -> {
              System.out.println("Creating connection");
              try {
                // Creating a mono out of the connection
                return Mono.just(conn.getResponseCode());
              } catch (final IOException e) {
                e.printStackTrace();
              }
              return Mono.empty();
            }, //
            conn -> {
              System.out.println("Cleaning up");
              conn.disconnect();
            }//
        )//
            .block());

  }

}
