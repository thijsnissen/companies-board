# Companies Board

Adapted from [ZIO Rite of Passage](https://rockthejvm.com/p/zio-rite-of-passage) using [Ports and Adapters](https://alistair.cockburn.us/hexagonal-architecture/) architecture.
Common functionality, like [authentication](code/libs/auth/src/main/scala), the [HTTP server](code/libs/http/src/main/scala), the [database-](code/libs/db/src/main/scala) and the [E-mail client](code/libs/email/src/main/scala), is
moved to separate library modules.

## Running Locally
Use the following integrations for local testing only. First, run `docker-compose up -d`.

### Mailpit

To use Mailpit, run the following script to generate a self-signed TLS certificate and import it into your JVM `cacerts`:

```bash
docker/mailpit/generate-cert.sh [alias]
```
Optionally pass an alias for the JVM cacerts entry (default: `mailpit-local`):

### Stripe

To setup local testing of the Stripe payments webhook, set `PAYMENT_PROVIDER_STRIPE_WEBHOOK_SECRET` in your `.env`
to the value obtained by running:

```bash
docker/stripe/webhook_secret.sh
```

### Ollama

The LLM functionality uses `Ollama`. Make sure to specify the model to use as `LLM_CLIENT_OLLAMA_HTTP_MODEL`.
