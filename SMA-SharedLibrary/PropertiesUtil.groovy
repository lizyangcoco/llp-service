/**
 * PropertiesUtil is used to generate parameterized
 * defaultValue
 * description
 * name
 * className is the type of parameter
 * @sample:
 *       @Library("sma-pipeline-library")
 *      PropertiesUtil p = new PropertiesUtil()
 *      p.addParamters(new PropertiesUtil('pass','desc','root','string'))
 *       .addParamters(new PropertiesUtil('pass1','desc1','machine','text'))
 *       .generateProperties()
 *
 */
class PropertiesUtil{

    List propertiesList =[] as String[]

    def defaultValue
    def description
    def name
    def className
    PropertiesUtil(){}
    PropertiesUtil(def defaultValue,def description,def name,def className){
        this.defaultValue = defaultValue
        this.description = description
        this.name = name
        this.className = className

    }

    def addParamters(PropertiesUtil params){
        def defaultValue = 'defaultValue'
        if(params.getClassName() == 'choice'){
            defaultValue = 'choices'
        }
        def temp  = params.getClassName()+'('+defaultValue+':"'+params.getDefaultValue()+'",description:"'+params.getDescription()+'",name:"'+params.getName()+'")'
        propertiesList.add(temp)
        return this

    }

    def generateParameters(){
        return  propertiesList.join(',')
    }

    
}
