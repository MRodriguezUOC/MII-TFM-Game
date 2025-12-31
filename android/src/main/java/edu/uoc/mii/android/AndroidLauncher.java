package edu.uoc.mii.android;

import android.os.Bundle;
import android.os.Build;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import edu.uoc.mii.Main;
import edu.uoc.mii.utils.DummyPlatformInput;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Launches the Android application.
 */
public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (Build.VERSION.SDK_INT < 25){
            android.util.Log.w("SSL_FIX", "Android antiguo detectado (" + Build.VERSION.SDK_INT + "). Aplicando parche SSL.");
            trustAllCertificates();
        }
        
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.
        initialize(new Main(new DummyPlatformInput()), configuration);
    }

    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
