FROM jenkins/jenkins:latest
COPY ./my_marvin.yml .
COPY ./plugins.txt /usr/share/jenkins/ref/plugins.txt
COPY ./job_dsl.groovy .
COPY ./link_project.groovy .
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt
