package mf.devops;


def signSingleFile(file_name, output_dir){
    sign_url = 'http://shcitsmaxdl01.hpeswlab.net:8081/nexus/service/local/repositories/thirdparty/content/org/MF/codesign/1.7.0/codesign-1.7.0-linux.gz'
    guid = '61854a4f-3b5c-4ea9-8d50-f8774df97f60'
    sh """
        mkdir -p ${output_dir}
        rm -rf ./${output_dir}/*
        wget -q ${sign_url}
        tar -zxf codesign-1.7.0-linux.gz
        cd Linux/CodeSign-CLI                        
    """
    dir("Linux/CodeSign-CLI"){
        echo """
            ./MicroFocusClient.sh -sign -guid ${guid} -in ${file_name} -out ${output_dir}/
        """
        sh """
            ./MicroFocusClient.sh -sign -guid ${guid} -in ${file_name} -out ${output_dir}/
        """
    }
}

return this
