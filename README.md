## Google OAuth Authorization Code Flow

This is an example Java microservice that implements the OAuth 2.0 authorization code flow to autorize with Google services.
The Authorization Code grant type is used by confidential and public clients to exchange an authorization code for an access token.

The Authorization Code Flow is commonly used in server-side web applications. It ensures sensitive tokens, such as access tokens, are not exposed to the client and are securely exchanged between the Authorization Server and the microservice.

Key Steps:

1. Authorization Request: The client requests an authorization code from the Authorization Server.

2. Authorization Code Exchange: The microservice exchanges the authorization code for an access token.

3. Access Resource: The microservice uses the access token to call a protected resource.
