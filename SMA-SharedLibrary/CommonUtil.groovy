package mf.devops

import groovy.json.JsonSlurperClassic
import groovy.transform.Field

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


@Field jenkinsUtil = new mf.devops.JenkinsUtil()

def parseStringToList(String var, String regex){
    List<String> var_list = var.substring(0,var.length()).split(regex).collect{it as String}
    for (int i = 0;i<var_list.size();i++){
        var_list[i] = "'"+var_list+"'"
    }
    return var_list
}

@NonCPS
def parseJsonText(String jsonText){
    final slurper = new JsonSlurperClassic()
    return new HashMap<>(slurper.parseText(jsonText))
}


def removeInvalidFlag(String origin){
    return origin.replace('[',"").replace(']',"")
}


def parseJsonInfoIntoMap(List jsonfile){
    Map<String,HashMap<String,String>> map = new HashMap<>()
    for (int i = 0; i < jsonfile.size(); i++) {
        String key = removeInvalidFlag(jsonfile[i].keySet().toString())
        Map valueMap = jsonfile[i].values()[0][0]
        map.put(key,valueMap)
    }
    return map
}

def parseArrayList(List list, String key, Boolean selected){
    List<String> lists = []
    if(selected){
        for (int i = 0; i < list.size(); i++) {
            Map map = (HashMap)list.get(i)
            lists.add("'"+map.get(key)+":selected'")

        }
    }else{
        for (int i = 0; i < list.size(); i++) {
            Map map = (HashMap)list.get(i)
            lists.add("'"+map.get(key)+"'")

        }
    }
    return lists
}


def replaceParameter(String file, String olds, String news){
    String result = "sed -i \'s/"+olds+"/"+news+"/g\'"+" "+file
    jenkinsUtil.runWithShell(result)
}

def sendEmail(to, subject, body, mimeType = "text/html") {
    emailext to: to,
            subject: subject,
            body: body,
            mimeType: mimeType
}




def getTime(){
    return  new Date()
}
int dayForWeek(Date date){
    int dayOfWeek
    try {
        Calendar c = Calendar.getInstance()
        c.setTime(date)
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayOfWeek = 7
        } else {
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1
        }
    }catch(Exception e){

    }
    return dayOfWeek
}


return this
