# MC-auth-db
© Copyright Benedict Adamson 2018-20.

MC-auth-db is a database server of the Mission Command game, used for recording information pertaining to user authentication and authorization. It is a [MariaDB](https://mariadb.org/) RDBMS.

MC-auth-db is a Docker image, based on the official [`mariadb` image](https://hub.docker.com/_/mariadb).
A Docker image for MC-auth-db is available from the  public repository
[https://hub.docker.com/r/benedictadamson/mc-database](https://hub.docker.com/r/benedictadamson/mc-auth-db).

## Usage
Use of the `mc-mc-auth-db` image is similar to use of the `mariadb` image. In particular:
* Set the environment variable `MYSQL_ROOT_PASSWORD` to initialise the password for the root user of the database.
* Set the environment variable `MYSQL_PASSWORD` to initialise the password used by the MC-auth component.
* The data is stored in the directory `/var/lib/mysql`, which should be a mounted volume.

However, the image sets the following environment variables used by the `mariadb` base image, so you will not be able to override these.
* `MYSQL_DATABASE=keycloak`, to create a specific database for use with the MC-auth component.
* `MYSQL_USER=admin`, to specify the user name for use by that component.