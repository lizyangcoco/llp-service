package mf.devops

def gitConfigNameAndEmail(String name, String email) {
    sh """
    git config --global user.name ${name}
    git config --global user.email ${email}
   """
}

def checkoutCode(String url, boolean force, String branch, String credentialsId) {
    gitConfigNameAndEmail('SMAX-Jenkins', 'smax-jenkins@microfocus.com')
    if (force) {
        deleteDir()
    }
    checkout([
            $class                           : 'GitSCM',
            branches                         : [[name: branch]],
            doGenerateSubmoduleConfigurations: false,
            extensions                       : [
                    [$class: 'CheckoutOption', timeout: 60],
                    [$class: 'PruneStaleBranch']
            ],
            submoduleCfg                     : [],
            userRemoteConfigs                : [
                    [credentialsId: credentialsId,
                     url          : url,
                     refspec      : '+refs/pull/*:refs/remotes/origin/pr/*'
                    ]
            ]
    ])
}

def getRepoURL(repository) {
    switch (repository.toLowerCase()) {
        case ~/.*itsma.*/:
            return "git@github.houston.softwaregrp.net:ITSMA-X/itsma-x.git"
        case ~/.*contain.*/:
            return "git@github.houston.softwaregrp.net:ITSMA-X/containerizing.git"
        case ~/.*jmx.*/:
            return "git@github.houston.softwaregrp.net:ITSMA-X/jmx-ui.git"
        case ~/.*xmpp.*/:
            return "git@github.houston.softwaregrp.net:ITSMA-X/xmpp.git"
        case ~/.*tomcat.*/:
            return "git@github.houston.softwaregrp.net:ITSMA-X/tomcat.git"
        case ~/.*chat.*/:
            return "git@github.houston.softwaregrp.net:ITSMA-X/chat-ui.git"
        case ~/.*mobile.*/:
            return "git@github.houston.softwaregrp.net:ITSMA-X/mobile-x.git"
        case ~/.*ng.*/:
            return "git@github.houston.softwaregrp.net:DEVOPS-X/DevOps-NG.git"
        case ~/.*script.*/:
            return "git@github.houston.softwaregrp.net:SMA-RnD/releasedScripts.git"
        case ~/.*registry.*/:
            return "git@github.houston.softwaregrp.net:SMA-RnD/suite-registry.git"
        case ~/.*data.*/:
            return "git@github.houston.softwaregrp.net:SMA-RnD/itsma-suite-data.git"
        default:
            echo "please select correct repository"
            return null
    }
}

def makeBranch(String branchName) {
    sh """
    git checkout -b ${branchName}
    git push origin ${branchName}
    """
}

def makeTag(String tagName) {
    sh """
    git tag ${tagName}
    git push origin ${tagName}
    """
}

def rmBranch(String branchName) {
    sh """
    git checkout ${branchName}
    git checkout master
    git branch -D ${branchName}
    git push origin :${branchName}
    """
}

def rmTag(String tagName) {
    sh """
    git tag -d ${tagName}
    git push origin :refs/tags/${tagName}
    """
}

def createBranch(repository, sourceBranch, targetBranch) {
    if (repository.contains('git@github')){
        repoURL =  repository
    }else {
        repoURL = getRepoURL(repository)
    }
    withCredentials([string(credentialsId: 'itsmaci-rw', variable: 'CREDENTIAL_PASSWORD')]) {
        checkoutCode(repoURL, true, sourceBranch, "${CREDENTIAL_PASSWORD}")
        makeBranch(targetBranch)
    }
}

def createTag(repository, sourceTag, targetTag) {
    if (repository.contains('git@github')){
        repoURL =  repository
    }else {
        repoURL = getRepoURL(repository)
    }

    withCredentials([string(credentialsId: 'itsmaci-rw', variable: 'CREDENTIAL_PASSWORD')]) {
        checkoutCode(repoURL, true, sourceTag, "${CREDENTIAL_PASSWORD}")
        makeTag(targetTag)
    }

}

def deleteBranch(repository, branchName) {
    if (branchName == 'master' ) {
        echo 'you are not allowed to delete master branch'
        return
    }
    repoURL = getRepoURL(repository)
    withCredentials([string(credentialsId: 'itsmaci-rw', variable: 'CREDENTIAL_PASSWORD')]) {
        checkoutCode(repoURL, true, 'master', "${CREDENTIAL_PASSWORD}")
        rmBranch(branchName)
    }
}

def deleteTag(repository, tagName) {
    if (tagName == 'master' ) {
        echo 'you are not allowed to delete master branch'
        return
    }
    repoURL = getRepoURL(repository)
    withCredentials([string(credentialsId: 'itsmaci-rw', variable: 'CREDENTIAL_PASSWORD')]) {
        checkoutCode(repoURL, true, 'master', "${CREDENTIAL_PASSWORD}")
        rmTag(tagName)
    }
}

def makeRelease(tag_name, target_commitish, name, body, draft, prerelease, repos, owners, repo) {
    withCredentials([string(credentialsId: 'itsmaci-rw', variable: 'CREDENTIAL_PASSWORD')]) {
        def res = ''
        if (repos.endsWith("/")) {
            repos = repos.substring(0, repos.length() - 1)
        }
        release_body = '\'{"tag_name":"' + tag_name +
                '","target_commitish":"' + target_commitish +
                '","name":"' + name +
                '","body":"' + body +
                '","draft":' + draft +
                ',"prerelease":' + prerelease +
                '}\''
        url = repos + "/api/v3/repos/" + owners + "/" + repo + "/releases?access_token=${CREDENTIAL_PASSWORD}"

        cmd = "curl -X POST -d $release_body $url"
        res = sh returnStdout: true, script: "$cmd"
        echo "------------response------------"
        echo res
        return res
    }
}

return this
