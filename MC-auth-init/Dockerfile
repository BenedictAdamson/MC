# Dockerfile for the MC-auth-init module,
# to initialise authentication information.

# © Copyright Benedict Adamson 2020.
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

FROM jboss/keycloak:11.0.2
ARG VERSION
LABEL version="${VERSION}"
LABEL description="The Mission Command game authentication initialisation job."
LABEL maintainer="badamson@spamcop.net"
COPY src/main/resources/mc-auth-init /opt/jboss/keycloak/bin
ENTRYPOINT [ "/opt/jboss/keycloak/bin/mc-auth-init" ]