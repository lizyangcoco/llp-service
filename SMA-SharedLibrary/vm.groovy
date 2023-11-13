package mf.devops;

def snapshotManage(vmList, operation, snapshot){
    withCredentials([string(credentialsId: 'github-ci-writable-token', variable: 'TOKEN')]) {
        retry(3) {
            for (String vm_name : vmList){
                sh """
                curl -k -H "Authorization: token ${TOKEN}" https://raw.github.houston.softwaregrp.net/DEVOPS-X/DevOps-NG/master/devops-jenkins-pipeline/scripts/managementvm.py \
                -o managementvm.py
                python managementvm.py -n "${vm_name}" -o "${operation}" -s "${snapshot}"
                """
            }
        }
    }
}

return this
