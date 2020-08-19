# MC-auth
© Copyright Benedict Adamson 2020.

MC-auth is the authentication server of the Mission Command game. It is a [Keycloak](https://www.keycloak.org/) server.

MC-auth is a Docker image, based on the official [`jboss/keycloak` image](https://hub.docker.com/r/jboss/keycloak). A Docker image for MC-auth is available from the  public repository
[https://hub.docker.com/r/benedictadamson/mc-auth](https://hub.docker.com/r/benedictadamson/mc-auth).

## Usage
Use of the `mc-auth` image is similar to use of the `jboss/keycloak` image. In particular:
* Set the environment variable `KEYCLOAK_PASSWORD` to indicate the password of the administrator.
* Set the environment variables `DB_VENDOR`, `DB_ADDR` and `DB_PORT` to indicate which database back-end to use.

However, the image sets the following environment variables used by the `jboss/keycloak` base image, so you will not be able to override these.
* `KEYCLOAK_USER=admin`, to specify the user name of the administrator.