package de.masaki;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.masaki.objects.*;
import de.masaki.threads.SessionCheckerThread;
import de.masaki.utils.HWID;
import lombok.Getter;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class KeyAuth {
    private final Gson gson = new Gson();

    protected final String appname;
    protected final String ownerid;
    protected final String version;
    protected final String url;

    protected String sessionid;

    protected boolean initialized;

    protected UserData userData;


    public KeyAuth(String appname, String ownerid, String version, String url) {
        this.appname = appname;
        this.ownerid = ownerid;
        this.version = version;
        this.url = url;
    }

    public KeyAuth(String appname, String ownerid, String version) {
        this(appname, ownerid, version, "https://keyauth.win/api/1.2/");
    }

    /*
    Authentication methods
     */

    public InitResponse init() {
        OkHttpClient client = new OkHttpClient();

        InitResponse initResponse;

        try {
            RequestBody body = new FormBody.Builder()
                    .add("type", "init")
                    .add("ver", version)
                    .add("name", appname)
                    .add("ownerid", ownerid)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Unexpected code " + response);
                }

                assert response.body() != null;

                String responseBody = response.body().string();

                if(responseBody.contains("KeyAuth_Invalid")){
                    return new InitResponse(false, "Invalid KeyAuth Information", null);
                }

                JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);

                if (responseJson.has("success") && responseJson.get("success").getAsBoolean()) {
                    sessionid = responseJson.get("sessionid").getAsString();
                    this.initialized = true;
                    initResponse = new InitResponse(true, "Successfully initialized", responseJson);
                } else {
                    if (responseJson.has("message")) {
                        initResponse = new InitResponse(false, responseJson.get("message").getAsString(), responseJson);
                    } else {
                        initResponse = new InitResponse(false, "Unknown error", responseJson);
                    }
                }
            }

        } catch (IOException e) {
            initResponse = new InitResponse(false, "Network error", null);
        } catch (JsonParseException e) {
            initResponse = new InitResponse(false, "JSON parsing error", null);
        } catch (Exception e) {
            e.printStackTrace();
            initResponse = new InitResponse(false, "Unexpected error", null);
        } finally {
            client.dispatcher().executorService().shutdown();
        }

        return initResponse;
    }

    public LoginResult login(String username, String password) {
        if (!initialized) {
            return new LoginResult(username, false, "Please initialize first");
        }

        OkHttpClient client = new OkHttpClient();

        try {
            String hwid = HWID.getHWID();

            RequestBody body = new FormBody.Builder()
                    .add("type", "login")
                    .add("username", username)
                    .add("pass", password)
                    .add("hwid", hwid)
                    .add("sessionid", sessionid)
                    .add("name", appname)
                    .add("ownerid", ownerid)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new LoginResult(username, false, "Unexpected code " + response);
                }

                assert response.body() != null;

                String responseBody = response.body().string();
                JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);

                if (!responseJson.get("success").getAsBoolean()) {
                    if (responseJson.has("message")) {
                        return new LoginResult(username, false, "Login failed: " + responseJson.get("message").getAsString());
                    }else {
                        return new LoginResult(username, false, "Login failed: Unknown error");
                    }
                } else {
                    userData = new UserData(username, true, "Login successful", responseJson);
                    return new LoginResult(username, true, "Login successful");
                }
            }

        } catch (IOException e) {
            return new LoginResult(username, false, "Error during login request");
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(username, false, "Unexpected error");
        } finally {
            client.dispatcher().executorService().shutdown();
        }
    }

    public RegisterResult register(String username, String password, String key, String email) {
        if (!initialized) {
            return new RegisterResult(username, false, "Please initialize first");
        }

        OkHttpClient client = new OkHttpClient();

        try {
            String hwid = HWID.getHWID();

            RequestBody body = new FormBody.Builder()
                    .add("type", "register")
                    .add("username", username)
                    .add("pass", password)
                    .add("key", key)
                    .add("hwid", hwid)
                    .add("sessionid", sessionid)
                    .add("name", appname)
                    .add("ownerid", ownerid)
                    .add("email", email)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new RegisterResult(username, false, "Unexpected code " + response);
                }

                assert response.body() != null;

                String responseBody = response.body().string();
                JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);

                if (!responseJson.get("success").getAsBoolean()) {
                    if (responseJson.has("message")) {
                        return new RegisterResult(username, false, "Registration failed: " + responseJson.get("message").getAsString());
                    } else {
                        return new RegisterResult(username, false, "Registration failed: Unknown error");
                    }
                } else {
                    return new RegisterResult(username, true, "Registration successful");
                }
            }

        } catch (IOException e) {
            return new RegisterResult(username, false, "Error during registration request");
        } catch (Exception e) {
            e.printStackTrace();
            return new RegisterResult(username, false, "Unexpected error");
        } finally {
            client.dispatcher().executorService().shutdown();
        }
    }

    public LogoutResult logout() {
        if (!initialized) {
            return new LogoutResult(false, "Please initialize first");
        }

        OkHttpClient client = new OkHttpClient();

        try {
            RequestBody body = new FormBody.Builder()
                    .add("type", "logout")
                    .add("sessionid", sessionid)
                    .add("name", appname)
                    .add("ownerid", ownerid)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new LogoutResult(false, "Unexpected code " + response);
                }

                assert response.body() != null;

                String responseBody = response.body().string();
                JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);

                if (!responseJson.get("success").getAsBoolean()) {
                    if (responseJson.has("message")) {
                        return new LogoutResult(false, "Logout failed: " + responseJson.get("message").getAsString());
                    } else {
                        return new LogoutResult(false, "Logout failed: Unknown error");
                    }
                } else {
                    return new LogoutResult(true, "Logout successful");
                }
            }

        } catch (IOException e) {
            return new LogoutResult(false, "Error during logout request");
        } catch (Exception e) {
            e.printStackTrace();
            return new LogoutResult(false, "Unexpected error");
        } finally {
            client.dispatcher().executorService().shutdown();
        }
    }

    public SessionResponse checkSession() {


        if (!initialized) {
            return new SessionResponse(false, "Please initialize first");
        }

        OkHttpClient client = new OkHttpClient();

        try {
            RequestBody body = new FormBody.Builder()
                    .add("type", "check")
                    .add("name", appname)
                    .add("ownerid", ownerid)
                    .add("sessionid", sessionid)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new SessionResponse(false, "Unexpected code " + response);
                }

                assert response.body() != null;

                String responseBody = response.body().string();
                JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);

                if (!responseJson.has("success")) {
                    return new SessionResponse(false, "Session invalid: Unknown error");
                }

                if (!responseJson.get("success").getAsBoolean()) {
                    if (responseJson.has("message")) {
                        return new SessionResponse(false, "Session invalid: " + responseJson.get("message").getAsString());
                    } else {
                        return new SessionResponse(false, "Session invalid: Unknown error");
                    }
                } else {
                    return new SessionResponse(true, "Session valid");
                }


            } catch (IOException e) {
                return new SessionResponse(false, "Error during session check request");
            } catch (Exception e) {
                e.printStackTrace();
                return new SessionResponse(false, "Unexpected error");
            }
        } finally {
            client.dispatcher().executorService().shutdown();
        }
    }

    public LicenseLoginResult licenseLogin(String key) {
        if (!initialized) {
            return new LicenseLoginResult(false, "Please initialize first");
        }

        OkHttpClient client = new OkHttpClient();

        try {
            String hwid = HWID.getHWID();

            RequestBody body = new FormBody.Builder()
                    .add("type", "license")
                    .add("key", key)
                    .add("sessionid", sessionid)
                    .add("name", appname)
                    .add("ownerid", ownerid)
                    .add("hwid", hwid)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new LicenseLoginResult(false, "Unexpected code " + response);
                }

                assert response.body() != null;

                String responseBody = response.body().string();
                JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);

                if (!responseJson.get("success").getAsBoolean()) {
                    if (responseJson.has("message")) {
                        return new LicenseLoginResult(false, "License login failed: " + responseJson.get("message").getAsString());
                    } else {
                        return new LicenseLoginResult(false, "License login failed: Unknown error");
                    }
                } else {
                    return new LicenseLoginResult(true, "License login successful");
                }
            }

        } catch (IOException e) {
            return new LicenseLoginResult(false, "Error during license login request");
        } catch (Exception e) {
            e.printStackTrace();
            return new LicenseLoginResult(false, "Unexpected error");
        } finally {
            client.dispatcher().executorService().shutdown();
        }
    }

    public void startSessionCheckerThread(long intervalInMillies){
        SessionCheckerThread sessionCheckerThread = new SessionCheckerThread(this, intervalInMillies);
        sessionCheckerThread.start();
    }

    public void startSessionCheckerThread(long intervalInMillies, String exitMessage){
        SessionCheckerThread sessionCheckerThread = new SessionCheckerThread(this, intervalInMillies, exitMessage);
        sessionCheckerThread.start();
    }

    /*
    Feature methods
    */

    public BanResult ban(String reason) {
        if (!initialized) {
            return new BanResult(false, "Please initialize first");
        }

        OkHttpClient client = new OkHttpClient();

        try {
            RequestBody body = new FormBody.Builder()
                    .add("type", "ban")
                    .add("sessionid", sessionid)
                    .add("name", appname)
                    .add("reason", reason)
                    .add("ownerid", ownerid)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new BanResult(false, "Unexpected code " + response);
                }

                assert response.body() != null;

                String responseBody = response.body().string();
                JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);

                if (!responseJson.get("success").getAsBoolean()) {
                    if (responseJson.has("message")) {
                        return new BanResult(false, "Ban failed: " + responseJson.get("message").getAsString());
                    } else {
                        return new BanResult(false, "Ban failed: Unknown error");
                    }
                } else {
                    return new BanResult(true, "Ban successful");
                }
            }

        } catch (IOException e) {
            return new BanResult(false, "Error during ban request");
        } catch (Exception e) {
            e.printStackTrace();
            return new BanResult(false, "Unexpected error");
        } finally {
            client.dispatcher().executorService().shutdown();
        }
    }

    public LogResult createLog(String message) {
        if (!initialized) {
            return new LogResult(false, "Please initialize first");
        }

        if (message.length() > 275) {
            return new LogResult(false, "Log message exceeds the maximum length of 275 characters");
        }

        OkHttpClient client = new OkHttpClient();

        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String pcuser = System.getProperty("user.name") + " (" + osName + " " + osVersion + ")";
        // Convert pcuser to UTF-8 bytes
        byte[] pcuserBytes = pcuser.getBytes(StandardCharsets.UTF_8);
        pcuser = new String(pcuserBytes, StandardCharsets.UTF_8);


        try {
            RequestBody body = new FormBody.Builder()
                    .add("type", "log")
                    .add("pcuser", pcuser)
                    .add("message", message)
                    .add("sessionid", sessionid)
                    .add("name", appname)
                    .add("ownerid", ownerid)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new LogResult(false, "Unexpected code " + response);
                }

                assert response.body() != null;

                String responseBody = response.body().string();
                JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);

                if (!responseJson.get("success").getAsBoolean()) {
                    if (responseJson.has("message")) {
                        return new LogResult(false, "Log failed: " + responseJson.get("message").getAsString());
                    } else {
                        return new LogResult(false, "Log failed: Unknown error");
                    }
                } else {
                    return new LogResult(true, "Log successful");
                }
            }

        } catch (IOException e) {
            return new LogResult(false, "Error during log request");
        } catch (Exception e) {
            e.printStackTrace();
            return new LogResult(false, "Unexpected error");
        } finally {
            client.dispatcher().executorService().shutdown();
        }
    }

    //⚠️You must run this function first before retrieving any other data on a user or the application ⚠️
    public FetchStatsResponse fetchStats() {
        if (!initialized) {
            return new FetchStatsResponse(false, "Please initialize first", null);
        }

        OkHttpClient client = new OkHttpClient();

        try {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url))
                    .newBuilder()
                    .addQueryParameter("type", "fetchStats")
                    .addQueryParameter("name", appname)
                    .addQueryParameter("ownerid", ownerid)
                    .addQueryParameter("sessionid", sessionid);

            String requestUrl = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(requestUrl)
                    .get()
                    .build();


            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new FetchStatsResponse(false, "Unexpected code " + response, null);
                }

                assert response.body() != null;

                String responseBody = response.body().string();
                JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);

                if (!responseJson.get("success").getAsBoolean()) {
                    if (responseJson.has("message")) {
                        return new FetchStatsResponse(false, "Failed to fetch stats: " + responseJson.get("message").getAsString(), null);
                    } else {
                        return new FetchStatsResponse(false, "Failed to fetch stats: Unknown error", null);
                    }
                } else {
                    JsonObject stats = responseJson.getAsJsonObject("appinfo");
                    return new FetchStatsResponse(true, "Stats fetched successfully", stats);
                }
            }

        } catch (IOException e) {
            return new FetchStatsResponse(false, "Error during fetch stats request", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new FetchStatsResponse(false, "Unexpected error", null);
        } finally {
            client.dispatcher().executorService().shutdown();
        }
    }

    public GlobalVariableResult fetchGlobalVariable(String varid) {
        if (!initialized) {
            return new GlobalVariableResult(false, "Please initialize first", null);
        }

        OkHttpClient client = new OkHttpClient();

        try {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url))
                    .newBuilder()
                    .addQueryParameter("type", "var")
                    .addQueryParameter("varid", varid)
                    .addQueryParameter("name", appname)
                    .addQueryParameter("ownerid", ownerid)
                    .addQueryParameter("sessionid", sessionid);

            String requestUrl = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(requestUrl)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new GlobalVariableResult(false, "Unexpected code " + response, null);
                }

                assert response.body() != null;

                String responseBody = response.body().string();
                JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);

                if (!responseJson.get("success").getAsBoolean()) {
                    String errorMessage = responseJson.has("message") ? responseJson.get("message").getAsString() : "Unknown error";
                    return new GlobalVariableResult(false, "Failed to fetch variable: " + errorMessage, null);
                } else {
                    return new GlobalVariableResult(true, "Variable fetched successfully", responseJson.get("message").getAsString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new GlobalVariableResult(false, "Error during fetch global variable request", null);
        } finally {
            client.dispatcher().executorService().shutdown();
        }
    }


}