/*
 * Copyright 2014-2019 Igor Almeida.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package com.example.muzei.muzeiapod;

import android.net.Uri;
import android.text.Html;
import android.util.Log;

import com.google.android.apps.muzei.api.provider.MuzeiArtProvider;
import com.google.android.apps.muzei.api.provider.Artwork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApodNasaArtSource extends MuzeiArtProvider {
    private static final String TAG = "ApodNasaForMuzei";
    private static final String API_URL = "https://science.nasa.gov/wp-json/wp/v2/apod-basic/";
    private static final int TIMEOUT_MILLIS = 20_000;

    @Override
    public void onLoadRequested(boolean initial) {
        HttpURLConnection connection = null;
        try {
            Uri apiUri = Uri.parse(API_URL).buildUpon()
                    .appendQueryParameter("per_page", "10")
                    .appendQueryParameter("_fields", "date,title,permalink,media_type,hdurl,credit")
                    .build();
            connection = (HttpURLConnection) new URL(apiUri.toString()).openConnection();
            connection.setConnectTimeout(TIMEOUT_MILLIS);
            connection.setReadTimeout(TIMEOUT_MILLIS);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "NASA Science APOD endpoint returned HTTP " + responseCode);
                return;
            }

            JSONArray entries = new JSONArray(readResponse(connection.getInputStream()));
            for (int i = 0; i < entries.length(); i++) {
                JSONObject apod = entries.getJSONObject(i);
                if (!"image".equals(apod.optString("media_type"))) {
                    continue;
                }

                String imageUrl = apod.optString("hdurl", "");
                if (imageUrl.isEmpty()) {
                    continue;
                }

                String date = apod.getString("date");
                String permalink = apod.getString("permalink");
                String credit = Html.fromHtml(apod.optString("credit", ""),
                        Html.FROM_HTML_MODE_LEGACY).toString().trim();
                String byline = credit.isEmpty() ? date : date + " · " + credit;
                setArtwork(new Artwork.Builder()
                        .title(apod.getString("title"))
                        .byline(byline)
                        .persistentUri(Uri.parse(imageUrl))
                        .token(date)
                        .webUri(Uri.parse(permalink))
                        .build());
                return;
            }
            Log.e(TAG, "NASA Science APOD response contained no image with an image URL.");
        } catch (IOException | JSONException | IllegalArgumentException e) {
            Log.e(TAG, "Failed to load NASA Science APOD artwork.", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readResponse(InputStream inputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

}