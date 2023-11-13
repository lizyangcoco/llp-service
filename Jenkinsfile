@Library('sma-pipeline-library')
        vm = new mf.devops.vm()
jenkinsUtil = new mf.devops.JenkinsUtil()
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import groovy.json.*
pipeline {
    agent {
        node {
            label "win"
            customWorkspace "${env.JOB_NAME}/${env.BUILD_NUMBER}"
        }
   }
   stages {
       stage ("list directory") {
           steps {
               script {

                   def dir = sh 'ls'
                  println "overall dir list : " + dir

               }
           }
       }
 
       stage ("Which java") {
           steps {
               script {
                    def version = sh 'java -version'
                    println "java version : " + version
               }
           }
       }
   }
}