job('task6_job2'){
description("The Second Job: Deploying respective webpages on the server")

    steps {
         remoteShell('root@192.168.56.107:22') {
             command('echo Hello', 'echo World!')
}
}
}
