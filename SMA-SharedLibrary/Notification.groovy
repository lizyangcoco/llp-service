package mf.devops

def sendEmail(to, subject, body, mimeType = "text/html") {
    emailext to: to,
            subject: subject,
            body: body,
            mimeType: mimeType
}

def commonFailureNotification(to,subject){
    def body = """
        <p><font color='red' size='5'>Failed Pipeline: ${currentBuild.fullDisplayName}</font></p> <br>
        Something is wrong with ${env.BUILD_URL}
    """
    sendEmail(to, subject, body)
}

return this
