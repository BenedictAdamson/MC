# Dockerfile for the use in the Jenkinsfile for the MC project,
# to set up the build environment for Jenkins to use.

# © Copyright Benedict Adamson 2018-22.
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

FROM debian:11

ARG JENKINSUID
ARG JENKINSGID

RUN apt-get -y update && apt-get -y install \
   maven \
   openjdk-17-jdk-headless
RUN apt-get remove -y openjdk-11-jre-headless

# Setup users and groups
RUN groupadd -g ${JENKINSGID} jenkins
RUN useradd -c "Jenkins user" -g ${JENKINSGID} -M -N -u ${JENKINSUID} jenkins

WORKDIR /home/jenkins/agent