// script to delete secrets

def secretsDir = new File('/run/secrets')
assert secretsDir.exists()
def result = secretsDir.deleteDir()
assert result
