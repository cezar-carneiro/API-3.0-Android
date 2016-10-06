package cieloecommerce.sdk.ecommerce.request;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import cieloecommerce.sdk.Environment;
import cieloecommerce.sdk.Merchant;
import cieloecommerce.sdk.ecommerce.Sale;

/**
 * Abstraction to reuse most of the code that send and receive the HTTP messages.
 *
 * @param <T> the AsyncTask expects 3 params and we can only anticipate 2 of them.
 */
public abstract class AbstractSaleRequest<T> extends AsyncTask<T, Void, Sale> {
    final Environment environment;
    private final Merchant merchant;
    private CieloRequestException exception;

    AbstractSaleRequest(Merchant merchant, Environment environment) {
        this.merchant = merchant;
        this.environment = environment;
    }

    /**
     * This is an async task, so we can't deal with exceptions as usual.
     *
     * @return true if we've any exception thrown
     */
    public boolean hasException() {
        return exception != null;
    }

    /**
     * If any exception was thrown during the async task, this will return that exception
     *
     * @return the exception, if any
     */
    public CieloRequestException getException() {
        return exception;
    }

    /**
     * Send the HTTP request to Cielo with the mandatory HTTP Headers set
     *
     * @param method The POST, PUT, GET request method
     * @param url    The endpoint em resource path
     * @return the HTTP response returned by Cielo
     * @throws IOException yeah, deal with it
     */
    Sale sendRequest(String method, URL url) throws IOException {
        return sendRequest(method, url, null);
    }

    /**
     * Send the HTTP request to Cielo with the mandatory HTTP Headers set
     *
     * @param method The POST, PUT, GET request method
     * @param url    The endpoint em resource path
     * @param body   The request body, if any
     * @return the HTTP response returned by Cielo
     * @throws IOException yeah, deal with it
     */
    Sale sendRequest(String method, URL url, String body) throws IOException {
        Log.d("Cielo SDK", "HTTP Method: " + method);
        Log.d("Cielo SDK", "URL: " + url.toString());

        HttpsURLConnection connection = null;
        connection = (HttpsURLConnection) url.openConnection();

        connection.setRequestMethod(method);

        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Accept-Encoding", "gzip");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "CieloEcommerce/3.0 Android SDK");
        connection.setRequestProperty("MerchantId", merchant.getId());
        connection.setRequestProperty("MerchantKey", merchant.getKey());
        connection.setRequestProperty("RequestId", UUID.randomUUID().toString());

        connection.setDoInput(true);
        connection.setUseCaches(false);

        if (body != null) {
            Log.d("Cielo SDK", "Request Body: " + body);

            connection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());

            dataOutputStream.writeBytes(body);
            dataOutputStream.flush();
            dataOutputStream.close();
        }

        int statusCode = connection.getResponseCode();

        InputStream inputStream = connection.getInputStream();

        BufferedReader responseReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        String responseBody;

        while ((line = responseReader.readLine()) != null) {
            responseBuilder.append(line);
        }

        responseReader.close();
        connection.disconnect();

        responseBody = responseBuilder.toString();

        Log.d("Cielo SDK", "Response Body: " + responseBody);

        return parseResponse(statusCode, responseBody);
    }

    /**
     * Just decode the JSON into a Sale or create the exception chain to be thrown
     *
     * @param statusCode   The status code of response
     * @param responseBody The response sent by Cielo
     * @return An instance of Sale or null
     */
    private Sale parseResponse(int statusCode, String responseBody) {
        Sale sale = null;
        Gson gson = new Gson();

        switch (statusCode) {
            case 200:
            case 201:
                sale = gson.fromJson(responseBody, Sale.class);
                break;
            case 400:
                CieloError[] errors = gson.fromJson(responseBody, CieloError[].class);

                for (CieloError error : errors) {
                    Log.v("Cielo SDK", "Error [" + error.getCode() + "] " + error.getMessage());

                    exception = new CieloRequestException(error.getMessage(), error, exception);
                }

                break;
            case 404:
                exception = new CieloRequestException("Not found", new CieloError(404, "Not found"), exception);
                break;
            default:
                Log.d("Cielo SDK", "Unknown HTTP Status " + statusCode);
        }

        return sale;
    }
}
