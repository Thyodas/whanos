FROM jenkins/agent:latest

USER root

COPY --chown=jenkins ./images/ /app/images
COPY --chown=jenkins ./jenkins/detect-language /app/
COPY --chown=jenkins ./helm/ /app/

RUN chown jenkins:jenkins /app

RUN curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

USER jenkins
