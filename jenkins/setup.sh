#!/bin/bash

mkdir -p ./secrets

read -r -p "Enter 'admin' password (ENTER to send): " PASSWORD
echo -n "$PASSWORD" > "secrets/admin_password"

read -r -p "Enter 'builder' password (ENTER to send): " PASSWORD
echo -n "$PASSWORD" > "secrets/builder_password"

read -r -p "Enter github token (ENTER to send): " TOKEN
echo -n "$TOKEN" > "secrets/github_token"

echo "Enter github ssh private key (CTRL-D after newline to send):"
SSH_KEY=$(cat)
echo -n "$SSH_KEY" > "secrets/github_ssh_key"

docker compose build
