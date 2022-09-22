# MC
The Mission Command game.

This project provides the core part of MC,
which is independent of the user interface (front-end) and repository (database)
used by the game.

See the related [MC-web project](https://github.com/BenedictAdamson/MC])
for the implementation of the user-interface and repository.

## License

© Copyright Benedict Adamson 2018-22.
 
![AGPLV3](https://www.gnu.org/graphics/agplv3-with-text-162x68.png)

MC is free software: you can redistribute it and/or modify
it under the terms of the
[GNU Affero General Public License](https://www.gnu.org/licenses/agpl.html)
as published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MC is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with MC.  If not, see <https://www.gnu.org/licenses/>.

## Components

The build system makes the software available as a collection of Java (JAR) archives.

## Public Repositories

MC is available from these public repositories:
* Source code: [https://github.com/BenedictAdamson/MC](https://github.com/BenedictAdamson/MC)

## Technologies Used

* Languages:
    * [Groovy](https://groovy-lang.org/)
    * [Java 17](https://docs.oracle.com/javase/17/)
    * [Typescript](https://www.typescriptlang.org/)
* Servers:
    * [mongoDB](https://www.mongodb.com/)
    * [NGINX](https://www.nginx.com/)
* Java Annotations:
    * [JCIP annotations](http://jcip.net/annotations/doc/net/jcip/annotations/package-summary.html)
    * [SpotBugs annotations](https://github.com/spotbugs/spotbugs/tree/master/spotbugs-annotations)
* Development environment:
    * [IntelliJ IDEA](https://www.jetbrains.com/idea/)
    * [Minikube](https://github.com/kubernetes/minikube)
* Software configuration management:
     * [Chartmuseum](https://github.com/helm/chartmuseum)
     * [Git](https://git-scm.com/)
     * [GitHub](https://github.com)
     * [Sonatype Nexus Repository Manager OSS](https://www.sonatype.com/nexus-repository-oss)
* Building:
    * [Docker](https://www.docker.com/)
    * [Jenkins](https://jenkins.io/)
    * [Maven](https://maven.apache.org/), including various Maven plugins provided by the Maven project
        * [Helm Maven Plugin](https://github.com/kiwigrid/helm-maven-plugin)
        * [JDeb Plugin](https://github.com/tcurdt/jdeb) for building Debian packages
        * [Maven Frontend Plugin](https://github.com/eirslett/frontend-maven-plugin)
        * [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/docs/2.1.3.RELEASE/maven-plugin/)
        * [SpotBugs Maven Plugin](https://spotbugs.github.io/spotbugs-maven-plugin/index.html)
        * [Docker Maven Plugin](https://github.com/fabric8io/docker-maven-plugin)
    * [Ubuntu](https://ubuntu.com)
    * [Debian](https://debian.org)
* Static analysis and testing:
    * [Jasmine](https://jasmine.github.io/index.html)
    * [JUnit 5](https://junit.org/junit5/)
    * [Java Hamcrest](http://hamcrest.org/JavaHamcrest/)
    * [MockServer](https://mock-server.com/)
    * [Open Test Alliance for the JVM](https://github.com/ota4j-team/opentest4j)
    * [PMD](https://pmd.github.io/)
    * [Spock](https://spockframework.org/)
    * [SpotBugs](https://spotbugs.github.io/)
* Frameworks:
    * [Angular](https://angular.io/)
    * [Spring Boot](http://spring.io/projects/spring-boot)
    * [Spring Framework](https://spring.io/projects/spring-framework)
    * [Spring Security](https://spring.io/projects/spring-security)
* Deployment:
    * [Docker Hub](https://hub.docker.com/)
    * [Helm](https://helm.sh/)
    * [Kubernetes](https://kubernetes.io/)
* Previously used:
    * [Eclipse IDE](https://www.eclipse.org/ide/)
    * [Cucumber](https://docs.cucumber.io/cucumber/):
    * [Cucumber JVM](https://docs.cucumber.io/installation/java/)
    * [Cucumber-Spring](https://docs.cucumber.io/cucumber/state/#spring)
    * [Cucumber Eclipse Plugin](https://marketplace.eclipse.org/content/cucumber-eclipse-plugin)
    * [Eclipse Docker Tooling](https://marketplace.eclipse.org/content/eclipse-docker-tooling)
    * [Eclipse Jenkins Editor](https://github.com/de-jcup/eclipse-jenkins-editor)
    * [Eclipse SpotBugs Plugin](https://marketplace.eclipse.org/content/spotbugs-eclipse-plugin)
    * [Eclipse Wild Web Developer](https://projects.eclipse.org/proposals/eclipse-wild-web-developer)
    * [Java 11](https://docs.oracle.com/javase/11/)
    * [Spring WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)
    * Spotify [Dockerfile Maven Plugin](https://github.com/spotify/dockerfile-maven)
