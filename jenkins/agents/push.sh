#!/bin/sh
docker build -t whanos-agent:latest -f ./jenkins/agents/Dockerfile .
docker tag whanos-agent:latest localhost:5001/whanos-agent:latest
docker push localhost:5001/whanos-agent:latest
