package cieloecommerce.sdk.ecommerce.request;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

import cieloecommerce.sdk.Environment;
import cieloecommerce.sdk.Merchant;
import cieloecommerce.sdk.ecommerce.Sale;

/**
 * Capture or cancel a Sale
 */
public class UpdateSaleRequest extends AbstractSaleRequest<String> {
    private final String type;
    private Integer amount;
    private Integer serviceTaxAmount;

    public UpdateSaleRequest(String type, Merchant merchant, Environment environment) {
        super(merchant, environment);

        this.type = type;
    }

    @Override
    protected Sale doInBackground(String... params) {
        Sale sale = null;
        String paymentId = params[0];

        try {
            Uri.Builder uri = Uri.parse(environment.getApiUrl() + "1/sales/" + paymentId).buildUpon();

            uri.appendPath(type);

            if (amount != null) {
                uri.appendQueryParameter("amount", amount.toString());
            }

            if (serviceTaxAmount != null) {
                uri.appendQueryParameter("serviceTaxAmount", serviceTaxAmount.toString());
            }

            URL url = new URL(uri.build().toString());

            sale = sendRequest("PUT", url);
        } catch (IOException e) {
            Log.e("Cielo SDK", e.getLocalizedMessage(), e);
        }

        return sale;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setServiceTaxAmount(Integer serviceTaxAmount) {
        this.serviceTaxAmount = serviceTaxAmount;
    }
}