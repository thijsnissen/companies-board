package nl.thijsnissen.auth

final case class AuthenticationConfig(password: PasswordConfig, jwt: JwtConfig, csrf: CsrfConfig)
