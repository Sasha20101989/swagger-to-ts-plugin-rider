package com.rubius.plugins;

import com.intellij.openapi.ui.Messages;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

public class NetworkUtils {
    public static boolean isUrlAccessible(String urlString) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URI(urlString).toURL().openConnection();
            connection.setRequestMethod("HEAD");
            return connection.getResponseCode() >= 200 && connection.getResponseCode() < 400;
        } catch (IOException | URISyntaxException e) {
            return false;
        }
    }
}