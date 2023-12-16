# Install Ansible with pip
First things first, make sure you have Python and pip installed. If not, install them.

```bash
sudo apt update
sudo apt install python3 python3-pip
```
Now, let's install Ansible using pip.

```bash
pip3 install ansible
```
# Install Git

You'll need Git to clone your GitHub repository.

```bash
sudo apt install git
```
# Clone the GitHub Repository

Navigate to the directory where you want to clone the repo and run:

```bash
git clone https://github.com/EpitechPromo2026/B-CPP-500-STG-5-2-rtype-remi.mergen.git
```

# Create Ansible Inventory
Create a file named inventory.ini:


```ini
[your-server]
your-server-ip ansible_ssh_user=your-ssh-user ansible_ssh_private_key_file=/path/to/your/private/key ansible_become_pass=your-sudo-password
```
Secure Sensitive Data with Ansible Vault
# Create a Vault File

```bash
ansible-vault create vault.yml
# Add Sensitive Data to Vault
```
Add your sensitive data to vault.yml:


```yaml
---
database_password: your-secure-password
api_key: your-api-key
```
Reference Vault Data in Playbook

# Run the Ansible Playbook
Now, run your playbook:

```bash
ansible-playbook --ask-vault-pass playbook.yml -i inventory.ini
```
This will execute the tasks defined in your playbook on the specified hosts.


# How to Write an Ansible Playbook
An Ansible playbook is a YAML file that defines a set of tasks to be executed on remote hosts. Each task represents a single action, such as installing a package, copying a file, or restarting a service. Let's break down the basic structure:

## YAML Format:
Ansible playbooks are written in YAML. YAML is a human-readable data serialization format. Ensure proper indentation as YAML relies on indentation to define the structure.

## Play Definition:
A playbook can have one or more plays. A play defines a set of tasks to be executed on a specific group of hosts. It typically consists of a name, a list of hosts, and a set of tasks.

```yaml
---
- name: My First Play
  hosts: your-server
  become: true

  tasks:
    # Tasks go here
```
## Tasks:
Tasks are the building blocks of a playbook. Each task specifies a module to be executed on the remote host. Modules are Ansible's way of performing actions. Here's an example task:


```yaml
tasks:
  - name: Update apt cache
    apt:
      update_cache: yes
```
In this task, the apt module is used to update the package cache on Debian-based systems.

## Variables:
Playbooks can use variables to make them more flexible and reusable. Variables can be defined at various levels, such as at the play or task level. Here's an example of using a variable:


```yaml
---
- name: Use Variable
  hosts: your-server
  become: true

  vars:
    app_name: myapp

  tasks:
    - name: Ensure the app directory exists
      file:
        path: /opt/{{ app_name }}
        state: directory
```
In this example, the app_name variable is defined at the play level and then used in the task.
