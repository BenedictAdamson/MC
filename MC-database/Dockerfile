# Dockerfile for the MC-database project,
# to produce the MC database server in a container.

# © Copyright Benedict Adamson 2018-20.
# 
# This file is part of MC.
#
# MC is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# MC is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with MC-des.  If not, see <https://www.gnu.org/licenses/>.
#

FROM mongo:4.2
ARG VERSION
LABEL version="${VERSION}"
LABEL description="The Mission Command game database server."
LABEL maintainer="badamson@spamcop.net"
ENV MONGO_INITDB_DATABASE=mc MONGO_INITDB_ROOT_USERNAME=admin
RUN mkdir /data/db/home-monogdb
RUN ln -s  /data/db/home-monogdb /home/monogdb
COPY src/main/resources/docker-entrypoint-initdb.d/initdb-mc.js /docker-entrypoint-initdb.d/initdb-mc.js
COPY src/main/resources/docker-entrypoint-initdb.d/inituser-mc.sh /docker-entrypoint-initdb.d/inituser-mc.sh
