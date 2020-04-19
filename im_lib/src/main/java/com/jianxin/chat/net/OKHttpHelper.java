package com.jianxin.chat.net;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jianxin.chat.net.base.ResponseData;
import com.jianxin.chat.net.base.StatusResult;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by imndx on 2017/12/15.
 */

public class OKHttpHelper {
    private static final Map<String, List<okhttp3.Cookie>> cookieStore = new ConcurrentHashMap<>();

    private static WeakReference<Context>  AppContext;
    public static void init(Context context) {
        AppContext = new WeakReference<>(context);
    }
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl url, List<okhttp3.Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                    if (AppContext != null && AppContext.get() != null) {
                        SharedPreferences sp = AppContext.get().getSharedPreferences("WFC_OK_HTTP_COOKIES", 0);
                        Set<String>  set = new HashSet<>();
                        for (okhttp3.Cookie k:cookies) {
                            set.add(gson.toJson(k));
                        }
                        sp.edit().putStringSet(url.host(), set).apply();
                    }
                }

                @Override
                public List<okhttp3.Cookie> loadForRequest(HttpUrl url) {
                    List<okhttp3.Cookie> cookies = cookieStore.get(url.host());
                    if (cookies == null) {
                        if (AppContext != null && AppContext.get() != null) {
                            SharedPreferences sp = AppContext.get().getSharedPreferences("WFC_OK_HTTP_COOKIES", 0);
                            Set<String>  set = sp.getStringSet(url.host(), new HashSet<>());
                            cookies = new ArrayList<>();
                            for (String s:set) {
                                okhttp3.Cookie cookie = gson.fromJson(s, okhttp3.Cookie.class);
                                cookies.add(cookie);
                            }
                            cookieStore.put(url.host(), cookies);
                        }
                    }

                    return cookies;
                }
            })
            .build();

    private static Gson gson = new Gson();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static <T> void get(final String url, Map<String, String> params, final Callback<T> callback) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (params != null) {
            HttpUrl.Builder builder = httpUrl.newBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            httpUrl = builder.build();
        }

        final Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onFailure("-1", e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(url, call, response, callback);
            }
        });

    }

    public static <T> void post(final String url, Map<String, Object> param, final Callback<T> callback) {
        RequestBody body = RequestBody.create(JSON, gson.toJson(param));
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onFailure("-1", e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(url, call, response, callback);

            }
        });
    }

    public static <T> void put(final String url, Map<String, String> param, final Callback<T> callback) {
        RequestBody body = RequestBody.create(JSON, gson.toJson(param));
        final Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onFailure("-1", e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(url, call, response, callback);

            }
        });
    }

    public static <T> void upload(String url, Map<String, String> params, File file, MediaType mediaType, final Callback<T> callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(mediaType, file));

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onFailure("-1", e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(url, call, response, callback);
            }
        });
    }

    private static <T> void handleResponse(String url, Call call, okhttp3.Response response, Callback<T> callback) {
        if (callback != null) {
            if (!response.isSuccessful()) {
                callback.onFailure(response.code()+"", response.message());
                return;
            }

            Type type;
            Type[] types = callback.getClass().getGenericInterfaces();
            type = ((ParameterizedType) types[0]).getActualTypeArguments()[0];

            if (type.equals(Void.class)) {
                callback.onSuccess((T) null);
                return;
            }

            if (type.equals(String.class)) {
                try {
                    callback.onSuccess((T) response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }


            try {
                StatusResult statusResult;
                if (type instanceof Class && type.equals(StatusResult.class)) {
                    statusResult = gson.fromJson(response.body().string(), StatusResult.class);
                    callback.onSuccess((T) statusResult);
                } else {
                    ResponseData<T> wrapper = gson.fromJson(response.body().string(), new ResultType(type));
                    if (wrapper == null) {
                        callback.onFailure("-1", "response is null");
                        return;
                    }
                    if (wrapper.isSuccess() && wrapper.getData() != null) {
                        callback.onSuccess(wrapper.getData());
                    } else {
                        callback.onFailure(wrapper.getCode(), wrapper.getMessage());
                    }
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                callback.onFailure("-1", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                callback.onFailure("-1", e.getMessage());
            }
        }
    }

    private static class ResultType implements ParameterizedType {
        private final Type type;

        public ResultType(Type type) {
            this.type = type;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{type};
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public Type getRawType() {
            return ResponseData.class;
        }
    }
}
