package mf.build;

def buildChatUI(workdir,branch,CHAT_UI_VERSION,BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG) {
    def CHAT_UI_REPO_URL = 'git@github.houston.softwaregrp.net:ITSMA-X/chat-ui.git'
    gt = new mf.devops.Github()
    dir(workdir + "/chatui") {
        def ROOT_CHAT_UI_POM = 'pom.xml'
        def PARAMS_CHAT_UI_1 = "-DCHAT_UI_VERSION=" + CHAT_UI_VERSION + " -DBVBVB_VERSION=" + BVBVB_VERSION + " -Pbvbvb -N"
        def PARAMS_CHAT_UI_2 = "-DskipTests=true -Dbuild-installer=true -Dcontent.validation.skip=true " +
                "-e post-clean deploy  -Dtarget-repo=" + MVN_REPO + " -nsu"
        gt.checkoutCode(CHAT_UI_REPO_URL, true,branch,"")
        def commands = []
        commands.add("mvn -f " + ROOT_CHAT_UI_POM + " validate " + PARAMS_CHAT_UI_1)
        commands.add("mvn -f " + ROOT_CHAT_UI_POM + " " + PARAMS_CHAT_UI_2)
        def mvn = new mf.devops.Mvn()
        mvn.runMavens(commands,JDK)
        if(MAKE_TAG){
            gt.makeTag(CHAT_UI_VERSION)
        }
    }
}
