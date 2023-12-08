folder ('Whanos base images') {
    description('Folder for Whanos base images.')
}

folder ('Projects') {
    description('Folder for projects.')
}

languages = ["c", "java", "javascript", "python", "befunge"]

languages.each { language ->
    freeStyleJob("Whanos base images/whanos-$language") {
        label("image-builder")
        def path = "/app/images/$language/Dockerfile.base"
        def build_path = "/app/to_build"

        steps {
            shell("mkdir $build_path && cp $path $build_path/Dockerfile")
            dockerBuilderPublisher {
                dockerFileDirectory("$build_path")
                cloud("docker")
                fromRegistry {
                    url("")
                    credentialsId("")
                }
                tagsString("localhost:5001/whanos-$language:latest")
                pushOnSuccess(true)
                pushCredentialsId("")
                cleanImages(false)
                cleanupWithJenkinsJobDelete(false)
                pull(true)
            }
        }
    }
}

freeStyleJob("Whanos base images/Build all base images") {
	publishers {
		downstream(
			languages.collect { language -> "Whanos base images/whanos-$language" }
		)
	}
}

freeStyleJob('link-project') {
    label("image-builder")
    parameters {
        stringParam('GITHUB_NAME', null, 'GitHub repository owner/repo_name (e.g.: "EpitechIT31000/chocolatine")')
        stringParam('DISPLAY_NAME', '', 'Display name of the project')
        stringParam('DESCRIPTION', '', 'Description of the project')
    }
    steps {
        dsl {
            text('''
                freeStyleJob("Projects/\$DISPLAY_NAME") {
                label("image-builder")
                description("\$DESCRIPTION")
                properties {
                    githubProjectUrl("https://github.com/\$GITHUB_NAME")
                }
                scm {
                    git {
                        remote {
                            github("\$GITHUB_NAME", 'ssh')
                            credentials('github_ssh_key')
                        }
                    }
                }
                triggers {
                    scm("H * * * *")
                }
                steps {
                    shell("echo DETECTED_LANGUAGE=`/app/detect-language .` >> /app/env")
                    environmentVariables {
                        propertiesFile('/app/env')
                    }

                    conditionalSteps {
                        condition {
                            not {
                                fileExists('Dockerfile', BaseDir.WORKSPACE)
                            }
                        }
                        steps {
                            shell("cp /app/images/\\$DETECTED_LANGUAGE/Dockerfile.standalone Dockerfile")
                        }
                    }

                    dockerBuilderPublisher {
                        dockerFileDirectory(".")
                        cloud("docker")
                        fromRegistry {
                            url("localhost:5001")
                            credentialsId("")
                        }
                        tagsString("localhost:5001/whanos-\$DISPLAY_NAME-\\$DETECTED_LANGUAGE:\\$BUILD_NUMBER")
                        pushOnSuccess(true)
                        pushCredentialsId("")
                        cleanImages(false)
                        cleanupWithJenkinsJobDelete(false)
                        pull(true)
                    }
                }
            }

            ''')
        }
    }
}