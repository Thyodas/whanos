folder ('Whanos base images') {
    description('Folder for Whanos base images.')
}

folder ('Projects') {
    description('Folder for projects.')
}

languages = ["c", "java", "javascript", "python", "befunge"]

languages.each { language ->
	freeStyleJob("Whanos base images/whanos-$language") {
		steps {
			shell("echo 'Hello $language!'")
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
    parameters {
        stringParam('DISPLAY_NAME', '', 'Display name of the project')
        stringParam('DESCRIPTION', '', 'Description of the project')
    }
    steps {
        dsl {
            external('./link_project.groovy')
        }
    }
}