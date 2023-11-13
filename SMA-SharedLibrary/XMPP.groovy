package mf.build;

def buildXMPP(workdir,INFRA_XMPP_VERSION,branch,BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG) {
    dir(workdir + "/xmpp") {
        def XMPP_REPO_URL = "git@github.houston.softwaregrp.net:ITSMA-X/xmpp.git"
        def ROOT_POM = 'pom.xml'
        def PARAMS_XMPP_1 = "-DINFRA_XMPP_VERSION=" + INFRA_XMPP_VERSION + " -DBVBVB_VERSION=" + BVBVB_VERSION + " -Pbvbvb -N"
        def PARAMS_XMPP_2 = "-DskipTests=true -Dbuild-installer=true -Dcontent.validation.skip=true " +
                "-e post-clean -Dtarget-repo=" + MVN_REPO + " -nsu"
        gt = new mf.devops.Github()
        gt.checkoutCode(XMPP_REPO_URL, true, branch, '')
        def mavenCommands = []
        mavenCommands.add("mvn -f ${ROOT_POM} validate ${PARAMS_XMPP_1}")
        mavenCommands.add("mvn -f ${ROOT_POM} deploy ${PARAMS_XMPP_2}")
        def mvn = new mf.devops.Mvn()
        mvn.runMavens(mavenCommands, JDK)
        if(MAKE_TAG) {
            gt.makeTag(INFRA_XMPP_VERSION)
        }
    }
}
