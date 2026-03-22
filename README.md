# Companies Board

Adapted from [ZIO Rite of Passage](https://rockthejvm.com/p/zio-rite-of-passage) using [Ports and Adapters](https://alistair.cockburn.us/hexagonal-architecture/) architecture.
Common functionality, like [authentication](code/libs/auth/src/main/scala/nl/thijsnissen/auth), the [HTTP server](code/libs/http/src/main/scala/nl/thijsnissen/http), the [database-](code/libs/db/src/main/scala/nl/thijsnissen/db) and the [E-mail client](code/libs/email/src/main/scala/nl/thijsnissen/email), is
moved to separate library modules.

## Running Locally
Use the following integrations for local testing only. First, run `docker-compose up -d`.

### Mailpit

To use Mailpit, add `docker/mailpit/cert.pem` to your JVM `cacerts` with the following command:
```
keytool -cacerts -import -alias <some_descriptive_name> -file <path_to_cert.pem>
```

### Stripe

To setup local testing of the Stripe payments webhook, set `PAYMENT_PROVIDER_STRIPE_WEBHOOK_SECRET` in your `.env`
to the value obtained by running `docker/stripe/webhook_secret.sh`.

### Ollama

The LLM functionality uses `Ollama`. Make sure to specify the model to use as `LLM_CLIENT_OLLAMA_HTTP_MODEL`.
