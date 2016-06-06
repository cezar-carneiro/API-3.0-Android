package cieloecommerce.sdk.ecommerce.request;

import android.util.Log;

import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

import cieloecommerce.sdk.Environment;
import cieloecommerce.sdk.Merchant;
import cieloecommerce.sdk.ecommerce.Sale;

/**
 * Create any kind of sale
 */
public class CreateSaleRequest extends AbstractSaleRequest<Sale> {
    public CreateSaleRequest(Merchant merchant, Environment environment) {
        super(merchant, environment);
    }

    @Override
    protected Sale doInBackground(Sale... params) {
        Sale sale = null;

        try {
            String url = environment.getApiUrl() + "1/sales/";
            HttpPost request = new HttpPost(url);

            request.setEntity(new StringEntity(new GsonBuilder().create().toJson(params[0])));

            HttpResponse response = sendRequest(request);

            sale = readResponse(response);
        } catch (IOException e) {
            Log.e("Cielo SDK", e.getLocalizedMessage(), e);
        }

        return sale;
    }
}
