package mf.build;

def buildPPO(workdir,branch,PPO_VERSION,BVBVB_VERSION,MVN_REPO,JDK) {
    def PPO_REPO_URL = 'git@github.houston.softwaregrp.net:ITSMA-X/ppo.git'
    gt = new mf.devops.Github()
    dir(workdir + "/ppo") {
        def PARAMS_PPO_1 = "-DPPO_VERSION=" + PPO_VERSION + " -DBVBVB_VERSION=" + BVBVB_VERSION + " -Pbvbvb -N"
        def PARAMS_PPO_2 = "-DskipTests=true -Dbuild-installer=true -Dcontent.validation.skip=true " +
                "-e post-clean deploy -Dtarget-repo=" + MVN_REPO + " -nsu"
        gt.checkoutCode(PPO_REPO_URL, true,branch,'')

        def commands = []
        commands.add("mvn -f pom.xml validate " + PARAMS_PPO_1)
        commands.add("mvn -f pom.xml " + PARAMS_PPO_2)

        def mvn = new mf.devops.Mvn()
        mvn.runMavens(commands,JDK)
    }
}

return this