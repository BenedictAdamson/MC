# MC-auth-init
© Copyright Benedict Adamson 2020.

MC-auth-init initialises authentication data for the Mission Command game.

MC-auth-init is a Docker image, based on the official [`jboss/keycloak` image](https://hub.docker.com/r/jboss/keycloak).
A Docker image for MC-auth-init is available from the  public repository
[https://hub.docker.com/r/benedictadamson/mc-auth-init](https://hub.docker.com/r/benedictadamson/mc-auth-init).

## Usage
The Docker image runs a (non-daemon) program, which accepts the following command-line arguments:
* *host*: the DNS-name or IP address of the authentication (Keycloak) server.
* *port*: the TCP/IP port that the authentication server listens on.

The image uses the following environment variables:
* `KEYCLOAK_PASSWORD` to indicate the password of the administrator

## Initial state
This module sets up the following.
* It creates a *realm* named `MC`.
* In that realm it creates a *client-id* named `mc-ui`.
* It adds the Keycloak root administrator (`admin`) as a user to that realm.