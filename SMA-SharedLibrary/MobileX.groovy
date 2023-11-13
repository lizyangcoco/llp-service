package mf.build;

def buildMobileX(workdir,branch,SAW_VERSION,MOBILE_GATEWAY_VERSION,BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG) {
    def MOBILE_GATEWAY_REPO_URL = 'git@github.houston.softwaregrp.net:ITSMA-X/mobile-x.git'
    gt = new mf.devops.Github()
    dir(workdir + "/mobile") {
        def ROOT_MOBILE_POM = 'pom.xml'
        def PARAMS_MOBILE_1 = "-DSAW_VERSION=" + SAW_VERSION + "  -DMOBILE_GATEWAY_VERSION=" + MOBILE_GATEWAY_VERSION + " -DBVBVB_VERSION=" + BVBVB_VERSION + " -Pbvbvb -N"
        def PARAMS_MOBILE_2 = "-DskipTests=true -Dbuild-installer=true -Dcontent.validation.skip=true " +
                "-e post-clean deploy -Dtarget-repo=" + MVN_REPO + " -nsu"
        gt.checkoutCode(MOBILE_GATEWAY_REPO_URL, true,branch,'')
        def commands = []
        commands.add("mvn -f " + ROOT_MOBILE_POM + " validate " + PARAMS_MOBILE_1)
        commands.add("mvn -f " + ROOT_MOBILE_POM + " " + PARAMS_MOBILE_2)
        def mvn = new mf.devops.Mvn()
        mvn.runMavens(commands,JDK)
        if(MAKE_TAG) {
            gt.makeTag(MOBILE_GATEWAY_VERSION)
        }
    }
}
