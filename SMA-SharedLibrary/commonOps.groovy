package mf.cdf
/**
 * function:  use cdf script kube-start.sh to start CDF
 * @param mHosts :master nodes list
 * @param wHosts :worker nodes list
 */

def kubeStartNodes(String nodes) {
    String[] list
    list = nodes.split(' ')
    batch = [:]
    for (String n : list) {
        sh """
                sshpass -p iso*help ssh -o "StrictHostKeyChecking no" root@"${n}" "/opt/kubernetes/bin/kube-start.sh"
            """
    }
}
def kubeStopNodes(String nodes) {
    String[] list
    list = nodes.split(' ')
    batch = [:]
    for (String n : list) {
       try{
           sh """
                sshpass -p iso*help ssh -o "StrictHostKeyChecking no" root@"${n}" '/opt/kubernetes/bin/kube-stop.sh -y | grep "CDF services stopped"'
            """
       } catch(error){
           echo '*' * 80
           echo "run kube-stop.sh command failed"
           echo '*' * 80
       }
    }
}

def startCDF(String mHosts,String wHosts){
    echo "prepare to start CDF master nodes $mHosts"
    kubeStartNodes(mHosts)
    echo "prepare to start CDF work nodes $wHosts"
    kubeStartNodes(wHosts)
}
def stopCDF(String mHosts,String wHosts){
    echo "prepare to stop CDF work nodes $wHosts"
    kubeStopNodes(wHosts)
    echo "prepare to stop CDF master nodes $mHosts"
    kubeStopNodes(mHosts)
}
/**
 * function:  use cdf script kube-status.sh to check whether CDF is ready
 * @param masterIP: first master node host
 * @return
 */
def checkCDFStatus(String masterIP){
    echo "here is the masterIP ${masterIP}"
    def CDFstatus = 'ok'
    try {
        retry(10){
            sh """
            sshpass -p iso*help ssh -o "StrictHostKeyChecking no" root@"${masterIP}" "/opt/kubernetes/bin/kube-status.sh"
            """
            sleep time: 1 ,unit: 'MINUTES'
        }
    }catch(error){
        echo '*' * 80
        echo " check CDF status failed."
        echo '*' * 80
        CDFstatus = 'failed'
        sh 'exit 1'
        return CDFstatus
    }finally{
        echo "end of check CDF status"
    }
}

/**
 * function: update CDF vault on first master
 * @param masterIP
 * @return
 */
def updateCDFVault(String masterIP){

    def result=0
    try{
        echo '*' * 80
        echo "try to update CDF vault"
        retry(2){
            sh """
            sshpass -p iso*help ssh -o "StrictHostKeyChecking no" root@"${masterIP}" "/opt/kubernetes/bin/update_kubevaulttoken"
            """
            sleep time: 3,unit: 'MINUTES'
        }

    }catch(error){
        echo '*' * 80
        echo "update CDF vault failed"
        echo '*' * 80
        result=1
        throw error
        sh 'exit 1'
    }finally{
        return result
    }
}
/**
 * function:
 * @param masterIP : first master node ip
 * @param namespace
 * @return
 */
def startSuite(masterIP,namespace){
    sh """
    sshpass -p iso*help ssh -o "StrictHostKeyChecking no" root@"${masterIP}" "/opt/kubernetes/scripts/cdfctl.sh runlevel set -l UP -n ${namespace}"   
    """

}
def stopSuite(masterIP,namespace){
    sh """
    sshpass -p iso*help ssh -o "StrictHostKeyChecking no" root@"${masterIP}" "/opt/kubernetes/scripts/cdfctl.sh runlevel set -l DOWN -n ${namespace}"   
    """
}
/**
 * function: upgrade from current version to @param CDFVersion
 * @param mHosts: master nodes list
 * @param wHosts: worker nodes list
 * @param CDFversion: cdf version list
 * @return
 */

def parseCDFVersion(String updateCDFVersion){
    def ret = []
    final pattern = ~/^(\d+).(\d+).(\d+)/
    def match =updateCDFVersion.trim()=~pattern
    if (match.find()){
        ret[0]=match.group(1).toInteger()
        ret[1]=match.group(2).toInteger()
        ret[2]=match.group(3).toInteger()
    }
    return ret
}

def removeDir(String[] nodes){
    for(String node: nodes){
        sh """
               sshpass -p "iso*help" ssh -o "StrictHostKeyChecking no" root@"${node}" "rm -rf /tmp/cdf"
               """
    }
}

def upgradeCDF(String mHosts,String wHosts,String CDFversion){

    String []mList
    String []wList
    mList=mHosts.split(' ')
    wList=wHosts.split(' ')
    masterIP=mList[0]
    CDFbaseVersion = CDFversion.substring(0,7)
    CDFUrl = ""
    SHC_ARTIFACTORY_BASE = "https://shcartifactory.swinfra.net/artifactory/"
    SVS_ARTIFACTORY_BASE = "https://svsartifactory.swinfra.net/artifactory/"
    def ver = parseCDFVersion(CDFversion) as int[]
    if (ver.size() == 3 && ver[0] * 100000 + ver[1] * 1000 + ver[2] >= 202002099) {
        CDFUrl="itom-buildoutput/cdf-daily-build/${CDFbaseVersion}/ITOM_Platform_Foundation_Standard_${CDFversion}.zip"
    } else {
        CDFUrl="itom-buildoutput/cdf-daily-build/${CDFbaseVersion}-upgrade/ITOM_Suite_Foundation_Upgrade_${CDFversion}.zip"
    }

    //get current ssh Dir
    String [] allNodes= mList.plus(wList)
    switch (CDFbaseVersion) {
        case "2019.05":
            //step0: get upgrade zip & unzip
            for(String node: allNodes)
                sh """
               sshpass -p "iso*help" ssh -o "StrictHostKeyChecking no" root@"${masterIP}" "mkdir -p /tmp/cdf"
               """
                sh """
                sshpass -p "iso*help" ssh -o "StrictHostKeyChecking no" root@"${node}" "wget -q ${ARTIFACTORY_BASE}${CDFUrl} && unzip ITOM_Suite_Foundation_Upgrade_${CDFversion}.zip "
                """
            //step1: master nodes : upgrade -g
            sh """
               sshpass -p "iso*help" ssh -o "StrictHostKeyChecking no" root@"${masterIP}" "/root/ITOM_Suite_Foundation_Upgrade_${CDFversion}/upgrade.sh -g"
               """
            //step 2: master&worker  nodes: upgrade -l
            for(String node : allNodes)
                sh """
               sshpass -p "iso*help" ssh -o "StrictHostKeyChecking no" root@"${node}" "/root/ITOM_Suite_Foundation_Upgrade_${CDFversion}/upgrade.sh -l"
               """
            //step3-1: master : upgrade -i -y -t /tmp
            for(String node : mList)
                sh """
               sshpass -p "iso*help" ssh -o "StrictHostKeyChecking no" root@"${masterIP}" "/root/ITOM_Suite_Foundation_Upgrade_${CDFversion}/upgrade.sh -i -y -t /tmp"
               """
            //step3-2: worker: upgrade -i -y -t /tmp
            for(String node : wList)
                if (node != masterIP){
                    sh """
               sshpass -p "iso*help" ssh -o "StrictHostKeyChecking no" root@"${node}" "/root/ITOM_Suite_Foundation_Upgrade_${CDFversion}/upgrade.sh -i -y -t /tmp"
               """
                }
            // step4: one nodes  upgrade -u -y -t /tmp
            sh """
               sshpass -p "iso*help" ssh -o "StrictHostKeyChecking no" root@"${masterIP}" "/root/ITOM_Suite_Foundation_Upgrade_${CDFversion}/upgrade.sh -u -y -t /tmp"
               """
            break
        default:
            def MASTER_CMD = ''
            def WORKER_CMD = ''
            for (String m : mList){
                if (MASTER_CMD == '') {
                    MASTER_CMD += '-f ' + m + ' '
                } else {
                    MASTER_CMD += '-m ' + m + ' '
                }
            }
            for (String w : wList){
                WORKER_CMD += '-w ' + w + ' '
            }
            retry(2) {
                try {
                    sh """
                        sudo pip install fabric==1.14.0 --proxy=http://web-proxy.houston.softwaregrp.net:8080
                    """
                } catch(e) {
                    sleep time: 1, unit: 'MINUTES'
                    throw e
                }
            }
            retry(2) {
                ARTIFACTORY_BASE = SHC_ARTIFACTORY_BASE
                try {
                    sh """
                        wget ${ARTIFACTORY_BASE}itom-buildoutput/itom-devops/sma-upgrade/${CDFbaseVersion}/cdbox_${CDFbaseVersion}.latest.tar
                        tar -xf cdbox_${CDFbaseVersion}.latest.tar 
                    """
                } catch(e) {
                    sleep time: 1, unit: 'MINUTES'
                    ARTIFACTORY_BASE = SVS_ARTIFACTORY_BASE
                    sh """
                        wget ${ARTIFACTORY_BASE}itom-buildoutput/itom-devops/sma-upgrade/${CDFbaseVersion}/cdbox_${CDFbaseVersion}.latest.tar
                        tar -xf cdbox_${CDFbaseVersion}.latest.tar 
                    """
                }
            }
           
                ARTIFACTORY_BASE = SHC_ARTIFACTORY_BASE
                try {
                    removeDir(allNodes)
                    sh """                 
                        cd cdbox
                        sudo python cdbox.py upgrade cdf ${MASTER_CMD} ${WORKER_CMD} -u root -p iso*help \
                        -l ${ARTIFACTORY_BASE}${CDFUrl}  --noBackup --clean  --logOnScreen
                    """
                } catch(e) {
                    print e
                    
                }
            
    }
}
return this;
