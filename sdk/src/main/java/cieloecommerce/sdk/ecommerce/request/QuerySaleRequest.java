package cieloecommerce.sdk.ecommerce.request;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

import cieloecommerce.sdk.Environment;
import cieloecommerce.sdk.Merchant;
import cieloecommerce.sdk.ecommerce.Sale;

/**
 * Query a Sale by it's paymentId
 */
public class QuerySaleRequest extends AbstractSaleRequest<String> {
    public QuerySaleRequest(Merchant merchant, Environment environment) {
        super(merchant, environment);
    }

    @Override
    protected Sale doInBackground(String... params) {
        Sale sale = null;
        String paymentId = params[0];

        try {
            String url = environment.getApiUrl() + "1/sales/" + paymentId;

            HttpGet request = new HttpGet(url);
            HttpResponse response = sendRequest(request);

            sale = readResponse(response);
        } catch (IOException e) {
            Log.e("Cielo SDK", e.getLocalizedMessage(), e);
        }

        return sale;
    }
}