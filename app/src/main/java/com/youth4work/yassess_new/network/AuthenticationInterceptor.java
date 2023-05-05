package com.youth4work.yassess_new.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    private String authToken;
    public AuthenticationInterceptor(String token) {
        this.authToken = token;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder()
                .header("Authorization", "Bearer  "+ authToken)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Host", "ytestapi.youth4work.com")
                .method(original.method(), original.body());
        Request request = builder.build();
        return chain.proceed(request);
    }
}