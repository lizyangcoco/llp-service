package mf.build;

def vagrantUp(workdir,branch){
    dir(workdir + "/../vagrantUp"){
        DEVOPS_REPO_URL = 'git@github.houston.softwaregrp.net:ITSMA-X/devops-x.git'
        gt = new mf.devops.Github()
        gt.checkoutCode(DEVOPS_REPO_URL,true,branch,"")
        sh '''
          df -i
          cd docker/vagrant
          if [ "`ls .vagrant/machines/default`" = "virtualbox" ] ; then
            ./killVirtualBox.sh
          else
            vagrant destroy
          fi
          md5sum .vagrantuser
          cp -f .vagrantuser.ci.snb .vagrantuser
          ls -l .vagrantuser
          md5sum .vagrantuser
    
          [ -n "${BOX_NAME}" ] && sed -i "s|config.vm.box =.*|config.vm.box = ${BOX_NAME} |" Vagrantfile
          [ -n "${BOX_URL}" ] && sed -i "s|config.vm.box_url =.*|config.vm.box_url = ${BOX_URL} |" Vagrantfile
          [ -n "${REGISTER_TAG}" ] && sed -i "s|tag:.*|tag: "${REGISTER_TAG}" |" .vagrantuser
          chmod +x vagrantDesicion.sh
          id
          ./vagrantDesicion.sh --provider=managed
          git reset --hard
      '''
    }
}

def vagrantUp(workdir,branch,pgVersion){
    dir(workdir + "/../vagrantUp"){
        DEVOPS_REPO_URL = 'git@github.houston.softwaregrp.net:ITSMA-X/devops-x.git'
        gt = new mf.devops.Github()
        gt.checkoutCode(DEVOPS_REPO_URL,true,branch,"")
        if (pgVersion == "pg10") {
            sh """
                cd docker/vagrant
                md5sum Vagrantfile
                cp -f Vagrantfile.${pgVersion} Vagrantfile
                md5sum Vagrantfile
            """
        }
        sh '''
          df -i
          cd docker/vagrant
          if [ "`ls .vagrant/machines/default`" = "virtualbox" ] ; then
            ./killVirtualBox.sh
          else
            vagrant destroy
          fi
          md5sum .vagrantuser
          cp -f .vagrantuser.ci.snb .vagrantuser
          ls -l .vagrantuser
          md5sum .vagrantuser
    
          [ -n "${BOX_NAME}" ] && sed -i "s|config.vm.box =.*|config.vm.box = ${BOX_NAME} |" Vagrantfile
          [ -n "${BOX_URL}" ] && sed -i "s|config.vm.box_url =.*|config.vm.box_url = ${BOX_URL} |" Vagrantfile
          [ -n "${REGISTER_TAG}" ] && sed -i "s|tag:.*|tag: "${REGISTER_TAG}" |" .vagrantuser
          chmod +x vagrantDesicion.sh
          id
          ./vagrantDesicion.sh --provider=managed
          git reset --hard
      '''
    }
}
