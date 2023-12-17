# Installation

This document provides instructions on how to install the necessary dependencies for this project.

## Dependencies

- Ansible
- Ubuntu/Debian based distribution on the remote machine
- Kubespray (installation below)

## Installation Steps

## Ansible

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


### Configure kubernetes on server via Kubespray and Ansible

1. Clone the kubespray repository

    ```bash
    git clone git@github.com:kubernetes-sigs/kubespray.git
    ```

    Then follow the instructions on the [kubespray repository](https://github.com/kubernetes-sigs/kubespray/blob/master/README.md) to deploy everything on the server.

    > Note: You can clone anywhere you want, even outside of this repository.

    If you need extra help here is more detailed instructions:

2. Copy their inventory sample

    Once you have clone the repo and you are within it, copy the inventory sample to a new folder.

    ```bash
    cp -rfp inventory/sample inventory/mycluster
    ```

    Here we copy to mycluster, it will store all the configuration files for our cluster.

3. Update Ansible inventory file with inventory builder

    ```bash
    declare -a IPS=(IP1 IP2 IP3)
    CONFIG_FILE=inventory/mycluster/hosts.yaml python3 contrib/inventory_builder/inventory.py ${IPS[@]}
    ```

    Where `IP1`, `IP2` and `IP3` are the IP addresses of the machines you want to deploy the cluster on.
    The python script will generate the `hosts.yaml` file in the `inventory/mycluster` folder.

4. (Optional) Review and change parameters under `inventory/mycluster/group_vars`

    ```bash
    cat inventory/mycluster/group_vars/all/all.yml
    cat inventory/mycluster/group_vars/k8s-cluster/k8s-cluster.yml
    ```
5. (Optional) Clean up previous deployments

    ```bash
    ansible-playbook -i inventory/mycluster/hosts.yaml --user root -b reset.yml
    ```

    > Note: If this command doesn't work, check the next instruction where more parameters will be detailed.

6. Deploy cluster

    ```bash
    ansible-playbook -i inventory/mycluster/hosts --user ubuntu --become --become-user=root cluster.yml --key-file=~/.ssh/Whanos.pem
    ```

    Where:
    - `-i` is the path to the inventory file
    - `--user` is the user of the remote machine
    - `--become` is used to become root
    - `--become-user` is the user to become (root)
    - `--key-file` is the path to the private key of the remote machine (for ssh)

    If you are using AWS EC2, you can use another host for it to work:
    ```txt
    [all]
    node1 ansible_host=IP

    [kube-master]
    node1

    [kube-node]
    node1

    [etcd]
    node1

    [k8s-cluster:children]
    kube-master
    kube-node
    ```

    And then run the same command but change the -i:
    ```bash
    ansible-playbook -i inventory/mycluster/new_hosts --user ubuntu --become --become-user=root cluster.yml --key-file=~/.ssh/Whanos.pem
    ```
    Where `new_hosts` is the path to the new inventory file.

7. Wait for it to finish deploying

    It will take several minutes to deploy the cluster, you can check the status of the deployment by ssh-ing into the remote machine and running the following command:

    ```bash
    kubectl get nodes
    ```

### Configure server via Ansible

2. 

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
