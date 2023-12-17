# Installation

This document provides instructions on how to install the necessary dependencies for this project.

## Dependencies

- Ansible
- Ubuntu/Debian based distribution on the remote machine

## Installation Steps

1. Install Ansible

    ### Linux

    ```bash
    sudo apt update
    sudo apt install python3 python3-pip
    pip3 install ansible
    ```

    ### MacOS

    ```bash
    brew install ansible
    ```

    ### Windows

    ```bash
    pip install ansible
    ```

2. Go in the ansible directory and Store your vault password (be careful not to push it online!)

    ```bash
    cd ansible
    echo "your-vault-password" > ./ansible/password
    ```

3. Create your vault
    > Note: `ansible-vault` will store your GitHub SSH key and the credentials for jenkins.

    ```bash
    ansible-vault create ./ansible/vault.yml --vault-password-file ./ansible/password
    ```
    The vault should look like this:

    ```yaml
    ---
    jenkins_admin_password: "admin"
    jenkins_github_ssh_key: "-----BEGIN OPENSSH PRIVATE KEY-----\n
    b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABlwAAAAdzc2gtcn\n
    ......... (your ssh key) .........\n
    8sFzVJnUsf1y35qc1e10KxZy4v0zr5PdfGMqkdtTUkADXo3WV6lLlkHr04pK75LHnhxtOR\n
    MAfJyfueSIzs8DAAAAEGd1aWxsYXVtZUBwb3Atb3MBAg==\n
    -----END OPENSSH PRIVATE KEY-----\n
    "
    jenkins_github_token: "ghp_*******************"
    ansible_become_pass: ssh_user_password
    ```

    Where:
    - `jenkins_admin_password` is the password for the admin user of jenkins
    - `jenkins_github_ssh_key` is your GitHub SSH key
    - `jenkins_github_token` is your GitHub token that has access to the repositories you want to use and webhooks
    - `ansible_become_pass` is the password for the user that will be created (or already exists) on the remote machine

4. Create your inventory

    ```yml
    all:
        hosts:
            10.211.55.4: # IP of the remote machine
                ansible_ssh_user: USERNAME # Username of the remote machine
                ansible_ssh_private_key_file: ~/.ssh/ssh_private_key # Path to the private key of the remote machine
    ```

    > Note: Don't forget to upload your SSH public key to the remote machine.

5. Run the playbook

    ```bash
    ansible-playbook -i inventory.yml playbook.yml --vault-password-file password
    ```

6. Access Jenkins

    Use your web browser to access Jenkins at `http://HOST_IP:8080` where `HOST_IP` is the IP of the remote machine.

    Login using the credentials you provided in the vault.

7. Skip installation wizard

    Once logged in click on the `Skip and continue as admin` button.

8. Approve Script

   Once on the dashboard, click on the `Manage Jenkins` button on the left, then click on `Manage Plugins`.

   Go to the security category and click on `In-process script Approval`.

   Click on the `Approve` button for the Groovy script.
