FROM jenkins/jenkins:latest
COPY ./my_marvin.yml .
COPY ./plugins.txt /usr/share/jenkins/ref/plugins.txt
COPY ./job_dsl.groovy .

ENV DOCKER_OPTS --insecure-registry=localhost:5001

RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt
