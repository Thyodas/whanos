FROM jenkins/agent:latest

USER root

COPY --chown=jenkins ./images/ /app/images
COPY --chown=jenkins ./jenkins/detect-language /app/
COPY --chown=jenkins ./jenkins/helm/ /app/helm

RUN chown jenkins:jenkins /app

USER jenkins
