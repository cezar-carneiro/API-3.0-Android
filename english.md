# API-3.0-Android

SDK API-3.0 Android

## Key features

* [x] Credit Card Payments.
* [x] Recurrencing payments.
    * [x] Authorized at first recurrence.
    * [x] With authorization from the first recurrence.
* [x] Payments by debit card.
* [x] Payments with boleto.
* [x] Payments with electronic transfer.
* [x] Cancelation of authorization.
* [x] Payment consulting.
 
## Limitation

By involving the application user interface, SDK only works as a framework when creating transactions.
In cases that authorization is direct, there is no limitation; but in cases that authentication is required, or any
user redirection, the developer must use SDK to generate payments, with the link returned by Cielo,
to provide the user redirection.

## Setup

To use SDK in your project in Android Studio, just import the module following the steps below:

### Method 1

1. Open your project in the app in Android Studio, as usual.
2. Download the SDK, either via git or through the download button in the repository.
3. In the menu `File`/`New`, select `Import Module`.
4. In `Source Directory`, inform `{DIRECTORY WHERE YOU CLONED REPOSITORY FROM}/sdk` (;
    * In `Module name` you must see `:sdk`.
5. Click in `Finish`.
6. The Gradle will sync automatically; the end, the SDK will be available for use.

### Method 2

1. Open your project in the app in Android Studio, as usual.
2. Download the SDK, either via git or through the download button in the repository.
3. Create a directory named `sdk` at root of directory of your project and copy SDK to this directory.
4. Edit the file `settings.gradle` to contain a reference to SDK; it must be similar to: `include ':app', ':sdk'`.
5. Do `build/clean`, close the project and then open it again.
6. Edit `build.gradle` e add SDK to your dependencies: `compile project(':sdk')`.

## Using SDK

SDK uses a AsyncTask to send HTTP requests; to create a simple payment with credit card SDK, you just need:

### Create a payment with a credit card:

```java
// ...
// Configure seu merchant
Merchant merchant = new Merchant("MERCHANT ID", "MERCHANT KEY");

// Crie uma instância de Sale informando o ID do pagamento
Sale sale = new Sale("ID do pagamento");

// Crie uma instância de Customer informando o nome do cliente
Customer customer = sale.customer("Comprador Teste");

// Crie uma instância de Payment informando o valor do pagamento
Payment payment = sale.payment(15700);

// Crie  uma instância de Credit Card utilizando os dados de teste
// esses dados estão disponíveis no manual de integração
payment.creditCard("123", "Visa").setExpirationDate("12/2018")
                                 .setCardNumber("0000000000000001")
                                 .setHolder("Fulano de Tal");

// Crie o pagamento na Cielo
try {
    // Configure o SDK com seu merchant e o ambiente apropriado para criar a venda
    sale = new CieloEcommerce(merchant, Environment.SANDBOX).createSale(sale);

    // Com a venda criada na Cielo, já temos o ID do pagamento, TID e demais
    // dados retornados pela Cielo
    String paymentId = sale.getPayment().getPaymentId();

    // Com o ID do pagamento, podemos fazer sua captura, se ela não tiver sido capturada ainda
    sale = new CieloEcommerce(merchant, Environment.SANDBOX).captureSale(paymentId, 15700, 0);

    // E também podemos fazer seu cancelamento, se for o caso
    sale = new CieloEcommerce(merchant, Environment.SANDBOX).cancelSale(paymentId, 15700);
} catch (ExecutionException | InterruptedException e) {
    // Como se trata de uma AsyncTask, será preciso tratar ExecutionException e InterruptedException
    e.printStackTrace();
} catch (CieloRequestException e) {
    // Em caso de erros de integração, podemos tratar o erro aqui.
    // os códigos de erro estão todos disponíveis no manual de integração.
    CieloError error = e.getError();

    Log.v("Cielo", error.getCode().toString());
    Log.v("Cielo", error.getMessage());

    if (error.getCode() != 404) {
        Log.e("Cielo", null, e);
    }
}
// ...
```

## Manual

For more information about integration with Cielo API 3.0, check the manual at: [Integração API 3.0](https://developercielo.github.io/Webservice-3.0/)
