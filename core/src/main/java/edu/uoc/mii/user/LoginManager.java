package edu.uoc.mii.user;

import edu.uoc.mii.conf.GameConfig;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import edu.uoc.mii.conf.PreferenceManager;

/**
 *
 * @author Marco Rodriguez
 */
public class LoginManager {

    private static LoginManager instance;

    private String sessionCookie = null;
    private String username = "";
    private String password = "";
    private String email = "";
    private boolean isLogged = false;

    private final Json json;
    
    public interface Callback {

        void onSuccess();

        void onError(String message);
    }    

    private LoginManager() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
    }

    public static LoginManager getInstance() {
        if (instance == null) {
            instance = new LoginManager();
        }
        return instance;
    }

    public void register(String username, String password, String email, final Callback callback) {
        this.setUsername(username);
        this.setPassword(password);
        this.setEmail(email);
        Gdx.app.log("LoginManager", "Register ini");
        RegisterRequest requestData = new RegisterRequest(username, password, email);
        String requestJson = "";
        try {
            requestJson = json.toJson(requestData);
        } catch (Exception e) {
            Gdx.app.log("LoginManager", "Ex: " + e.toString());
        }
        Gdx.app.log("LoginManager", "Request: " + requestJson);
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .url(GameConfig.API_URL + "/auth/register")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .content(requestJson)
                .build();

        Gdx.app.log("LoginManager", "Intentando registrar a: " + username);
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();

                if (statusCode == 200 || statusCode == 201) {
                    saveCredentials(username, password);;
                    Gdx.app.log("LoginManager", "Registrado OK");
                    Gdx.app.postRunnable(callback::onSuccess);
                } else {
                    // Login failed (401, 403, etc.)
                    onFail("Error: " + statusCode);
                }
            }

            @Override
            public void failed(Throwable t) {
                onFail("Error: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                onFail("Cancelado");
            }

            private void onFail(String msg) {
                Gdx.app.error("LoginManager", msg);
                Gdx.app.postRunnable(() -> callback.onError(msg));
            }
        });
    }

    public void login(final Callback callback) {
        login(username, password, callback);
    }

    public void login(String username, String password, final Callback callback) {
        this.setUsername(username);
        this.setPassword(password);

        LoginRequest requestData = new LoginRequest(username, password);
        String requestJson = json.toJson(requestData);

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .url(GameConfig.API_URL + "/auth/login")
                .header("Content-Type", "application/json") // ¡Importante!
                .header("Accept", "application/json")
                .content(requestJson)
                .build();

        Gdx.app.log("LoginManager", "Intentando login con: " + username);

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String result = httpResponse.getResultAsString();
                Gdx.app.log("LoginManager", result);
                int statusCode = httpResponse.getStatus().getStatusCode();

                if (statusCode == 200 || statusCode == 201) {
                    isLogged = true;
                    saveCredentials(username, password);

                    if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
                        String rawCookie = httpResponse.getHeader("Set-Cookie");
                        if (rawCookie != null) {
                            sessionCookie = rawCookie.split(";")[0];

                            Gdx.app.log("LoginManager", "Login OK. Cookie guardada: " + sessionCookie);
                            Gdx.app.postRunnable(callback::onSuccess);
                        } else {
                            onFail("Login OK pero el servidor no envió cookie");
                        }
                    } else {
                        Gdx.app.log("LoginManager", "Cookie gestionada por el navegador (WebGL)");
                        Gdx.app.postRunnable(callback::onSuccess);
                    }
                } else {
                    // Login failed (401, 403, etc.)
                    if (statusCode == 403) {
                        PreferenceManager.getInstance().setValidCredentials(false);
                    }
                    onFail("Error: " + statusCode);
                }
            }

            @Override
            public void failed(Throwable t) {
                onFail("Error de conexión: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                onFail("Cancelado");
            }

            // Helper to return to the main thread in case of error
            private void onFail(String msg) {
                Gdx.app.error("LoginManager", msg);
                Gdx.app.postRunnable(() -> callback.onError(msg));
            }
        });
    }

    public void logout() {
        Gdx.app.log("LoginManager", "Intentando logout de: " + username);
        isLogged = false;        
        PreferenceManager.getInstance().setValidCredentials(isLogged);
        //TODO: call api lougout.
        Gdx.app.log("LoginManager", "Usuario deslogeado");
    }    
    
    private void saveCredentials(String username, String password) {
        PreferenceManager.getInstance().setUsernamePassword(username, password);
        PreferenceManager.getInstance().setValidCredentials(isLogged);
    }

    public void addSessionCookie(HttpRequestBuilder builder) {
        if (sessionCookie != null) {
            builder.header("Cookie", sessionCookie);
        }
    }

    public boolean isLoggedIn() {
        return isLogged;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
