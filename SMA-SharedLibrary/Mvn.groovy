package mf.devops;
import groovy.transform.Field

@Field logging = new mf.devops.Logging()
@Field MAVEN_TOOL = 'maven-3.3.9'
@Field MAVEN_SETTINGS_PATH = '/u/maas_jenkins/.m2/settings.xml'
@Field MAVEN_LOCAL_REPO = '.repository'

def runMaven(command,JDK){
    runMaven(command,MAVEN_TOOL,MAVEN_SETTINGS_PATH,MAVEN_LOCAL_REPO,JDK,'')
}

def runMavens(commands,JDK){
    runMavens(commands,MAVEN_TOOL,MAVEN_SETTINGS_PATH,MAVEN_LOCAL_REPO,JDK,'')
}

def runMaven(command,MAVEN_TOOL,MAVEN_SETTINGS_PATH,MAVEN_LOCAL_REPO,JDK,MAVEN_JVM_OPTS){
    withMaven(jdk: JDK, maven: MAVEN_TOOL, mavenLocalRepo: MAVEN_LOCAL_REPO, mavenOpts: MAVEN_JVM_OPTS, mavenSettingsFilePath: MAVEN_SETTINGS_PATH, publisherStrategy: 'EXPLICIT') {
        command = command + " -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=error"
        logging.outputParameter('run mvn',command)
        sh "${command}"
    }
}

def runMavens(commands,MAVEN_TOOL,MAVEN_SETTINGS_PATH,MAVEN_LOCAL_REPO,JDK,MAVEN_JVM_OPTS){
    withMaven(jdk: JDK, maven: MAVEN_TOOL, mavenLocalRepo: MAVEN_LOCAL_REPO, mavenOpts: MAVEN_JVM_OPTS, mavenSettingsFilePath: MAVEN_SETTINGS_PATH, publisherStrategy: 'EXPLICIT') {
        for(String command in commands ){
            command = command + " -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=error"
            logging.outputParameter('run mvn',command)
            sh "${command}"
        }
    }
}


return this