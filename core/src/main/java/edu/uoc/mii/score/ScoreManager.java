package edu.uoc.mii.score;

import edu.uoc.mii.conf.GameConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import edu.uoc.mii.user.LoginManager;

/**
 *
 * @author Marco Rodriguez
 */
public class ScoreManager {

    private static ScoreManager instance;
    private final Json json;
    private ScoreEntry score;
    private int nTries = 0;

    public interface Callback {

        void onSuccess(String json);

        void onError(String message);
    }

    private ScoreManager() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        Gdx.app.log("ScoreManager", "URL: " + GameConfig.API_URL + "/scores");
    }

    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    public void newScore() {
        this.score = new ScoreEntry(LoginManager.getInstance().getUsername(), 0);
    }

    public void addPoint(int points) {
        Gdx.app.log("ScoreManager", "Current points: " + this.score.getPoints() + ", addPoint: " + points);
        this.score.addPoints(points);
    }
    
    public int getPoints() {
        return score.getPoints();
    }    
    
    public void setPoints(int points) {
        score.setPoints(points);
    }
      
    public void fetchScores(Callback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .url(GameConfig.API_URL + "/scores")
                .timeout(10 * 1000)
                .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String jsonString = httpResponse.getResultAsString();

                Gdx.app.log("API_DEBUG", "Status Code: " + statusCode);
                Gdx.app.log("API_DEBUG", "Raw Response: " + jsonString);

                Gdx.app.postRunnable(() -> callback.onSuccess(jsonString));
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("API_DEBUG", "sendHttpRequest failed", t);
                Gdx.app.log("API_DEBUG", "sendHttpRequest failed: " + t.toString());
                callback.onError("Error de conexión :(");
            }

            @Override
            public void cancelled() {
                // Request cancelled
            }
        });
    }

    public void submitScore(final Callback callback) {
        if (!LoginManager.getInstance().isLoggedIn()) {
            if (callback != null) {
                callback.onError("No estás logueado. No se puede enviar la puntuación.");
            }
            return;
        }

        ScoreSubmission submission = new ScoreSubmission(score.getUsername(), score.getPoints());
        String requestJson = json.toJson(submission);

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .url(GameConfig.API_URL + "/scores")
                .header("Content-Type", "application/json");

        LoginManager.getInstance().addSessionCookie(requestBuilder);

        requestBuilder.content(requestJson);

        Gdx.app.log("SCORE", "Enviando puntuación: " + score.getPoints());

        Gdx.net.sendHttpRequest(requestBuilder.build(), new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();

                if (statusCode >= 200 && statusCode < 300) {
                    nTries = 0;
                    Gdx.app.log("SCORE", "¡Puntuación guardada!");
                    if (callback != null) {
                        Gdx.app.postRunnable(() -> callback.onSuccess(null));
                    }
                } else {
                    // It could be 401 if the cookie expired
                    nTries++;
                    if (nTries < 3) {
                        Gdx.app.error("SCORE", "Error servidor: " + statusCode + ", retrying...");
                        if (statusCode == 401 || statusCode == 403) {
                            LoginManager.getInstance().login(new LoginManager.Callback() {
                                @Override
                                public void onSuccess() {
                                    submitScore(callback);
                                }

                                @Override
                                public void onError(String message) {
                                    if (callback != null) {
                                        Gdx.app.postRunnable(() -> callback.onError("Error al guardar: " + statusCode));
                                    }
                                }
                            });
                        } else {
                            submitScore(callback);
                        }
                    } else {
                        Gdx.app.error("SCORE", "Error servidor: " + statusCode);
                        if (callback != null) {
                            Gdx.app.postRunnable(() -> callback.onError("Error al guardar: " + statusCode));
                        }
                    }
                }
            }

            @Override
            public void failed(Throwable t
            ) {
                Gdx.app.error("SCORE", "Fallo de red", t);
                if (callback != null) {
                    Gdx.app.postRunnable(() -> callback.onError("Error de conexión"));
                }
            }

            @Override
            public void cancelled() {
                // ...
            }
        });
    }
}
