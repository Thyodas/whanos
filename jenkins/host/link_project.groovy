freeStyleJob("\$DISPLAY_NAME") {
    description("\$DESCRIPTION")
    steps {
        shell("echo 'Hello \$DISPLAY_NAME!'")
    }
    // properties {
    //     githubProjectUrl("https://github.com/\$GITHUB_NAME")
    // }
    // scm {
    //     git {
    //         remote {
    //             github("\$GITHUB_NAME", 'ssh')
    //             credentials('github_ssh_key')
    //         }
    //     }
    // }
    // triggers {
    //     githubPush()
    //     cron("0 6,12,21 * * *")
    // }
    // wrappers {
    //     preBuildCleanup()
    //     timeout {
    //         failBuild()
    //         abortBuild()
    //         absolute(5)
    //     }
    // }
    // publishers {
    //     gitHubCommitStatusSetter {
    //         statusResultSource {
    //             defaultStatusResultSource()
    //         }
    //     }
    // }
    // steps {
    //     shell("make fclean")
    //     shell("make")
    //     shell("""
    //         set +e
    //         make tests_run
    //         set -e
    //     """)
    //     shell("make clean")
    // }
}
