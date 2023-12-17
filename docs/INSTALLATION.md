# Installation

This document provides instructions on how to install the necessary dependencies for this project.

## Dependencies

- Ansible
- Ubuntu/Debian based distribution on the remote machine
- Kubespray (installation below)

## Installation Steps

## Clone this repository

```bash
git clone git@github.com:EpitechPromo2026/B-DOP-500-STG-5-1-whanos-guillaume.hein.git
cd B-DOP-500-STG-5-1-whanos-guillaume.hein
```

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
    sudo kubectl get pods -A
    ```

    It should show you something like this:

    ```txt
    NAMESPACE     NAME                                      READY   STATUS    RESTARTS   AGE
    kube-system   calico-kube-controllers-648dffd99-hdczv   1/1     Running   0          15m
    kube-system   calico-node-l2tk8                         1/1     Running   0          16m
    kube-system   coredns-77f7cc69db-hmvbr                  1/1     Running   0          14m
    kube-system   dns-autoscaler-595558c478-pbrhg           1/1     Running   0          14m
    kube-system   kube-apiserver-node1                      1/1     Running   1          17m
    kube-system   kube-controller-manager-node1             1/1     Running   2          17m
    kube-system   kube-proxy-m566w                          1/1     Running   0          16m
    kube-system   kube-scheduler-node1                      1/1     Running   1          17m
    kube-system   nodelocaldns-6rkzg                        1/1     Running   0          14m
    ```


### Configure server via Ansible

Now return to this repository and follow the instructions below.
We will now deploy Jenkins and docker on the remote machine.

1. Create your vault
    > Note: `ansible-vault` will store your GitHub SSH key and the credentials for jenkins.

    Store your vault password (be careful not to push it online!)

    ```bash
    echo "your-vault-password" > ./ansible/password
    ```

    The vault should look like this in `vault.yml`:

    ```yaml
    ---
    jenkins_admin_password: "admin"
    jenkins_github_token: "ghp_*******************"
    ansible_become_pass: ssh_user_password
    jenkins_github_ssh_key: LS0tLS1CRUdJTiBPUEVOU1NIIFBSSVZBVEUgS0VZLS0t...
    ```

    Where:
    - `jenkins_admin_password` is the password for the admin user of jenkins
    - `jenkins_github_token` is your GitHub token that has access to the repositories you want to use and webhooks
    - `ansible_become_pass` is the password for the user that will be created (or already exists) on the remote machine
    - `jenkins_github_ssh_key` is your GitHub SSH key (encoded in **base64**)

    > Note: You can use the following command to encode your SSH key in base64: `base64 -i key_file`

    Then create your vault:

    ```bash
    ansible-vault create ./ansible/vault.yml --vault-password-file ./ansible/password
    ```

2. Create your inventory

    ```yml
    all:
        hosts:
            10.211.55.4: # IP of the remote machine
                ansible_ssh_user: USERNAME # Username of the remote machine
                ansible_ssh_private_key_file: ~/.ssh/ssh_private_key # Path to the private key of the remote machine
    ```

    > Note: Don't forget to upload your SSH public key to the remote machine.

3. Run the playbook

    ```bash
    ansible-playbook -i inventory.yml playbook.yml --vault-password-file password
    ```

    > Note: If you get errors related to 'permissions' try to run the command with `--become` or/and `--become-user=root` flags.

4. Access Jenkins

    Use your web browser to access Jenkins at `http://HOST_IP:8080` where `HOST_IP` is the IP of the remote machine.

    Login using the credentials you provided in the vault.

5. Skip installation wizard

    Once logged in click on the `Skip and continue as admin` button.

6. Approve Script

   Once on the dashboard, click on the `Manage Jenkins` button on the left, then click on `Manage Plugins`.

   Go to the security category and click on `In-process script Approval`.

   Click on the `Approve` button for the Groovy script.

6. Create base images

   For your projects to be containerized, you need to create base images for them.

   To do so go to the main page `dashboard` and go to folder `Whanos base images`, then click on job `Build all base images`.

   > Note: You can also build them one by one by clicking on the job related to your project language, e.g. `whanos-javascript`.

7. Deploy your app!

    Go to the main dashboard and click on job  `link-project`, then click on `Build with Parameters`.

    Fill in the parameters and click on `Build`.

    The parameters are:
    - `GITHUB_NAME`: GitHub repository owner/repo_name (e.g.: "EpitechIT31000/chocolatine")
    - `DISPLAY_NAME`: Display name of the project
    - `DESCRIPTION`: Description of the project

    > Note: If you want to deploy a private repository, you need to add a github SSH key.
    > This SSH key was setup before in the vault.

## Creating a Whanos Compatible App

To ensure your application is compatible with the Whanos infrastructure, follow these guidelines based on the language of your application:

### General Repository Structure

- Place your application's source code and resources in an `app` directory at the root of the repository.
- Include a `whanos.yml` file at the root of the repository if deployment in a Kubernetes cluster is required.
- Include a `Dockerfile` at the root of the repository if containerization is required.


### Specific Language Requirements

#### C

- **Detection:** Contains a `Makefile` at the root.
- **Build:** Use `make` for compilation.
- **Execution:** Run the compiled binary, expected to be named `compiled-app`.

#### Java

- **Detection:** Contains `pom.xml` in the `app` directory.
- **Build:** Use `mvn package`.
- **Execution:** Run with `java -jar app.jar`.
- **Output:** Compiled `app.jar` in a `target` subdirectory.

#### JavaScript

- **Detection:** Contains `package.json` at the root.
- **Build:** N/A.
- **Execution:** Use `node .`.
- **Note:** No compilation step required.

#### Python

- **Detection:** Contains `requirements.txt` at the root.
- **Build:** N/A.
- **Execution:** Use `python -m app`.
- **Note:** No compilation step required.

#### Befunge

- **Detection:** Contains a single `main.bf` file in the `app` directory.
- **Build & Execution:** Free choice, to be executed from the root.

### Whanos.yml File Specifications

If deployment in Kubernetes is needed, the `whanos.yml` should specify:

- **replicas:** Number of replicas (default: 1).
- **resources:** Resource needs according to Kubernetes specifications (default: none).
- **ports:** List of integer ports needed by the container (default: none).

Here is an example of a `whanos.yml` file:

```yaml
deployment:
  replicas: 3
  resources:
    limits:
      memory: "128M"
    requests:
      memory: "64M"
  ports:
    - 3000
```

> Note: The `deployment` key is required.

### Dockerfile Specifications

If containerization is needed, the `Dockerfile` should pull from the `whanos/base` image.

Here is an example of a `Dockerfile`:

```dockerfile
FROM whanos-javascript

RUN npm install -g typescript@4.4.3

RUN tsc

RUN find . -name "*.ts" -type f -not -path "./node_modules/*" -delete
```

> Note: The `FROM` instruction is required.
> Pull from the images as they are named in the `Whanos base images` folder in Jenkins.


### Accessing the Application Externally

- If `whanos.yml` defines ports, they must be accessible externally. The external port does not have to match the internal port.

### Troubleshooting

- If your application fails to build or deploy, check the Jenkins logs for errors.
- Ensure that your `whanos.yml` is correctly formatted and follows the specifications.
- For issues related to specific languages or technologies, refer to their respective documentation for troubleshooting tips.
