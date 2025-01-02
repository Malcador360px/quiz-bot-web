package com.web.quiz_bot.util;

import com.web.quiz_bot.request.Request;
import javax.net.ssl.HttpsURLConnection;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import java.net.URI;

public class RequestUtil {

    private RequestUtil() {}

    public static byte[] sendRequest(Request request, URI serverUrl) {
        if (serverUrl == null) {
            System.err.println("No server available");
            return null;
        }
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        WebClient client = WebClient.create();
        try {
            return client.post().uri(serverUrl)
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request.toString()).retrieve().bodyToMono(byte[].class).block();
        } catch (WebClientRequestException ce) {
            return null;
        }
    }
}
