package mf.build;

def buildJMXUI(workdir,artifactVersion,branch,BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG) {
    dir(workdir + "/jmx_ui") {
        def JMX_UI_REPO_URL = "git@github.houston.softwaregrp.net:ITSMA-X/jmx-ui.git"
        def ROOT_POM = 'pom.xml'
        def PARAMS_JMX_UI_1 = "-DJMX_UI_TOOL_VERSION=" +  artifactVersion + " -DBVBVB_VERSION=" + BVBVB_VERSION + " -Pbvbvb -N"
        def PARAMS_JMX_UI_2 = "-DskipTests=true -Dbuild-installer=true -Dcontent.validation.skip=true " +
                "-e post-clean -Dtarget-repo=" + MVN_REPO + " -nsu"
        gt = new mf.devops.Github()
        gt.checkoutCode(JMX_UI_REPO_URL, true, branch, "")
        def mavenCommands = []
        mavenCommands.add("mvn -f ${ROOT_POM} validate ${PARAMS_JMX_UI_1}")
        mavenCommands.add("mvn -f ${ROOT_POM} deploy ${PARAMS_JMX_UI_2}")
        def mvn = new mf.devops.Mvn()
        mvn.runMavens(mavenCommands,JDK)
        if(MAKE_TAG) {
            gt.makeTag(artifactVersion)
        }
    }
}
