package mf.devops
import mf.build.*

def fallback_script(){
    return ["Script Error"].toString()
}

def generateRandomName(){
    String name = "choice-parameter"
    def random = new Random()
    name = name + (String)random.nextLong()
    return name
}

def generateStringParameter(String defaultValue,String description,String name){
    Map parameters = [
            $class:'StringParameterDefinition',
            defaultValue:defaultValue,
            description: description,
            name: name
    ]

    return parameters
}

def generateBooleanParameter(Boolean defaultValue,String description,String name){
     Map parameters = [
        $class:'BooleanParameterDefinition',
        defaultValue: defaultValue,
        description: description,
        name: name
]
    return parameters
}

def generateGitDefinitionParamter(String useRepository,String branch,String defaultValue,String description,String name){
    Map parameters = [
            $class: 'GitParameterDefinition',
            branch: branch,
            branchFilter: '.*',
            defaultValue: defaultValue,
            description: description,
            name: name,
            quickFilterEnabled: false,
            selectedValue: 'NONE',
            sortMode: 'NONE',
            tagFilter: '*',
            type: 'PT_BRANCH',
            useRepository:useRepository
    ]
    return parameters
}

def generateDynamicReferenceParameter(String name, String reference, String type, String description, String script){
    String randomName = generateRandomName()
    String fallback = fallback_script()
    Map parameters =
            [
                    $class: 'DynamicReferenceParameter',
                    choiceType: type,
                    description: description,
                    name: name,
                    omitValueField: true,
                    randomName: randomName,
                    referencedParameters: reference,
                    script: [$class: 'GroovyScript',
                             fallbackScript: [classpath: [], sandbox: false, script: fallback],
                             script: [classpath: [], sandbox: false, script: script]
                    ]
            ]
    return parameters
}

def generateCascadeChoiceParameter(String name, String reference, String type, String description, String script){
    String randomName = generateRandomName()
    String fallback = fallback_script()
    Map parameters =
            [
                    $class: 'CascadeChoiceParameter',
                    choiceType: type,
                    description: description,
                    filterLength: 1,
                    filterable: false,
                    name: name,
                    randomName: randomName,
                    referencedParameters: reference,
                    script: [$class: 'GroovyScript',
                             fallbackScript: [classpath: [], sandbox: false, script: fallback],
                             script: [classpath: [], sandbox: false, script: script]
                    ]
            ]
    return parameters
}

def generateActiveChoiceParameter(String name,String description, String script){
    String randomName = generateRandomName()
    String fallback = fallback_script()
    Map parameters =
            [
                    $class: 'ChoiceParameter',
                    choiceType: "PT_SINGLE_SELECT",
                    description: description,
                    name: name,
                    randomName: randomName,
                    script: [$class: 'GroovyScript',
                             fallbackScript: [classpath: [], sandbox: false, script: fallback],
                             script: [classpath: [], sandbox: false, script: script]
                    ]
            ]
    return parameters
}


def runWithMavenOpts(String command,String mavenTool,String mavenLocalRepo,String mavenSettingPath,String jvmOpts){
    withMaven(maven: mavenTool, mavenLocalRepo: mavenLocalRepo, mavenSettingsFilePath: mavenSettingPath,mavenOpts: jvmOpts, options: [artifactsPublisher(disabled: true), findbugsPublisher(disabled: true), openTasksPublisher(disabled: true)]) {
        runWithShell(command)
    }
}

def runWithMaven(String command,String mavenTool,String mavenLocalRepo,String mavenSettingPath, options=[]) {
    withMaven(maven: mavenTool, mavenLocalRepo: mavenLocalRepo, mavenSettingsFilePath: mavenSettingPath, publisherStrategy: 'EXPLICIT', options: options) {
        runWithShellMaven(command)
    }
}

def runWithMavenWithJDK(String command,String mavenTool,String jdk,String mavenSettingPath) {
    withMaven(maven: mavenTool, jdk: jdk, mavenSettingsFilePath: mavenSettingPath, options: [artifactsPublisher(disabled: true), findbugsPublisher(disabled: true), openTasksPublisher(disabled: true)]) {
        runWithShell(command)
    }
}

def runWithShell(String command){
    sh "${command}"
}

def runWithShellMaven(String command){
    command = command + " -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=error"
    sh "${command}"
}

def runWithMavens(commands,String mavenTool,String mavenLocalRepo,String mavenSettingPath, options=[]) {
    withMaven(maven: mavenTool, mavenLocalRepo: mavenLocalRepo, mavenSettingsFilePath: mavenSettingPath, publisherStrategy: 'EXPLICIT', options: options) {
        for(String command in commands){
            runWithShellMaven(command)
        }
    }
}

def buildChatUI(workdir,branch,CHAT_UI_VERSION,BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG){
    def chatui = new ChatUi()
    chatui.buildChatUI(workdir,branch,CHAT_UI_VERSION,BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG)
}

def buildJMXUI(workdir, artifactVersion,branch,BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG){
    def jmxui = new JMXUI()
    jmxui.buildJMXUI(workdir, artifactVersion,branch,BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG)
}

def buildMobileX(workdir,branch,SAW_VERSION,MOBILE_GATEWAY_VERSION,BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG) {
    def mobilex = new MobileX()
    mobilex.buildMobileX(workdir,branch,SAW_VERSION,MOBILE_GATEWAY_VERSION,BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG)
}

def buildTomcat(workdir, INFRA_TOMCAT_VERSION, branch, BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG) {
    def tomcat = new Tomcat()
    tomcat.buildTomcat(workdir, INFRA_TOMCAT_VERSION, branch, BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG)
}

def vagrantUp(workdir,branch){
    def vagrant = new VagrantUp()
    vagrant.vagrantUp(workdir,branch)
}

def vagrantUp(workdir,branch,pgVersion){
    def vagrant = new VagrantUp()
    vagrant.vagrantUp(workdir,branch,pgVersion)
}

def buildXMPP(workdir, INFRA_XMPP_VERSION, branch,BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG){
    def xmpp = new XMPP()
    xmpp.buildXMPP(workdir, INFRA_XMPP_VERSION, branch,BVBVB_VERSION,MVN_REPO,JDK,MAKE_TAG)
}
    return this