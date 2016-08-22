# API-3.0-Android

SDK API-3.0 Android

## Principais recursos

* [x] Pagamentos por cartão de crédito.
* [x] Pagamentos recorrentes.
    * [x] Com autorização na primeira recorrência.
    * [x] Com autorização a partir da primeira recorrência.
* [x] Pagamentos por cartão de débito.
* [x] Pagamentos por boleto.
* [x] Pagamentos por transferência eletrônica.
* [x] Cancelamento de autorização.
* [x] Consulta de pagamentos.
 
## Limitações

Por envolver a interface de usuário da aplicação, o SDK funciona apenas como um framework para criação das transações. Nos casos onde a autorização é direta, não há limitação; mas nos casos onde é necessário a autenticação ou qualquer tipo de redirecionamento do usuário, o desenvolvedor deverá utilizar o SDK para gerar o pagamento e, com o link retornado pela Cielo, providenciar o redirecionamento do usuário.

## Instalação

Para utilizar o SDK em seu projeto no Android Studio, basta importar o módulo seguindo os passos abaixo:

### Método 1

1. Abra normalmente o projeto de seu app no Android Studio.
2. Faça download do SDK, seja via git ou através do botão download no repositório.
3. No menu `File`/`New`, selecione `Import Module`.
4. Em `Source Directory`, informe `{DIRETÓRIO ONDE CLONOU O REPOSITÓRIO}/sdk`;
    * Em `Module name` deverá aparecer `:sdk`.
5. Clique em `Finish`.
6. O Gradle fará a sincronia automaticamente; ao fim, o SDK estará disponível para uso.

### Método 2

1. Abra normalmente o projeto de seu app no Android Studio.
2. Faça download do SDK, seja via git ou através do botão download no repositório.
3. Crie um diretório chamado `sdk` na raiz do diretório do seu projeto e copie o SDK para esse diretório.
4. Edite o arquivo `settings.gradle` para que contenha a referência para o SDK; ficará mais ou menos assim: `include ':app', ':sdk'`.
5. Faça um `build/clean`, feche o projeto e, então, abra-o novamente.
6. Edite seu `build.gradle` e adicione o SDK às suas dependências: `compile project(':sdk')`.


## Utilizando o SDK

O SDK utiliza uma AsyncTask para enviar as requisições HTTP; para criar um pagamento simples com cartão de crédito com o SDK, basta fazer:

### Criando um pagamento com cartão de crédito

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

Para mais informações sobre a integração com a API 3.0 da Cielo, vide o manual em: [Integração API 3.0](https://developercielo.github.io/Webservice-3.0/)
