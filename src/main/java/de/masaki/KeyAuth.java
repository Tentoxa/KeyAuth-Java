package de.masaki;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.masaki.objects.InitResponse;
import de.masaki.objects.LoginResult;
import de.masaki.objects.SessionResponse;
import de.masaki.objects.UserData;
import de.masaki.threads.SessionCheckerThread;
import de.masaki.utils.HWID;
import lombok.Getter;
import okhttp3.*;

import java.io.IOException;

public class KeyAuth {
    private final Gson gson = new Gson();

    protected final String appname;
    protected final String ownerid;
    protected final String version;
    protected final String url;

    protected String sessionid;

    protected boolean initialized;

    @Getter
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

    public void startSessionCheckerThread(long intervalInMillies){
        SessionCheckerThread sessionCheckerThread = new SessionCheckerThread(this, intervalInMillies);
        sessionCheckerThread.start();
    }

    public void startSessionCheckerThread(long intervalInMillies, String exitMessage){
        SessionCheckerThread sessionCheckerThread = new SessionCheckerThread(this, intervalInMillies, exitMessage);
        sessionCheckerThread.start();
    }

}