package mf.build;

def buildTomcat(workdir,INFRA_TOMCAT_VERSION,branch,BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG) {
    dir(workdir + "/tomcat") {
        def TOMCAT_REPO_URL = "git@github.houston.softwaregrp.net:ITSMA-X/tomcat.git"
        def ROOT_POM = 'pom.xml'
        def PARAMS_TOMCAT_1 = "-DINFRA_TOMCAT_VERSION=" + INFRA_TOMCAT_VERSION + " -DBVBVB_VERSION=" + BVBVB_VERSION + " -Pbvbvb -N"
        def PARAMS_TOMCAT_2 = "-DskipTests=true -Dbuild-installer=true -Dcontent.validation.skip=true " +
                "-Dtarget-repo=" + MVN_REPO + " -e post-clean -nsu"
        gt = new mf.devops.Github()
        gt.checkoutCode(TOMCAT_REPO_URL, true, branch, "")
        def mavenCommands = []
        mavenCommands.add("mvn -f ${ROOT_POM} validate ${PARAMS_TOMCAT_1}")
        mavenCommands.add("mvn -f ${ROOT_POM} deploy ${PARAMS_TOMCAT_2}")
        def mvn = new mf.devops.Mvn()
        mvn.runMavens(mavenCommands,JDK)
        if(MAKE_TAG) {
            gt.makeTag(INFRA_TOMCAT_VERSION)
        }
    }
}
