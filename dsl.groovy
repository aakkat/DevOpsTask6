job("task6_job1"){
description("The First Job: Downloading content from GitHub")
        
scm{
github('aakkat/DevOpsTask6', 'master')
}
triggers {
scm('* * * * *')
}
steps {
shell('''rm -rvf /root/task3/*
cp -p dsl.groovy /root/task3/
cp -p index.html /root/task3/
cp -p index.php /root/task3
cp -p ca.crt /root/
cp -p client.crt /root/
cp -p client.key /root/
mkdir .kube
cp myinfo .kube/config
''')
}
}

job('task6_job2'){
description("The Second Job: Deploying respective webpages on the server")

triggers {  
upstream('task6_job1', 'SUCCESS')
}
steps {
shell ('''if sudo ls /root/task3 | grep .html
then
if sudo kubectl get deployment | grep webserver
then
echo "The Web Deployment is already running"
else
sudo kubectl create -f /root/kube/webserver.yml
sleep 6
if sudo kubectl get pods | grep web
then
a=$(sudo kubectl get pods -o 'jsonpath={.items[0].metadata.name}')
sudo kubectl cp /root/task3/index.html $a:/var/www/html
else
echo "Cannot copy the HTML code"
fi
fi
else
echo "The code is not for HTML"
fi
if sudo ls /root/task3 | grep .php
then
if sudo kubectl get deployment | grep phpserver
then
echo "The PHP Deployment is already running"
else
sudo kubectl create -f /root/kube/phpserver.yml
sleep 6
if kubectl get pods | grep php
then
b=$(sudo kubectl get pods -o 'jsonpath={.items[0].metadata.name}')
sudo kubectl cp /root/task3/index.php $b:/var/www/html
else
echo "Cannot copy the PHP code"
fi
fi
else
echo "The code is not for PHP"
fi''')
}
}

job("task6_job3"){
description("The Third Job: Testing the environments")

triggers {
upstream('task6_job2','SUCCESS')
}

steps {
shell ('''if sudo kubectl get pods | grep webserver
then
web_status_code=$(curl -o /dev/null -s -w "%{http_code}" 192.168.99.102:31000)
if [[ $web_status_code == 200 ]]
then
echo "The webserver is running fine"
else
echo "Something is wrong with the Web Server"
exit 1
fi
else
echo "No webserver running"
fi
if sudo kubectl get pods | grep phpserver
then
php_status_code=$(curl -o /dev/null -s -w "%{http_code}" 192.168.99.102:32000)
if [[ $php_status_code == 200 ]]
then
echo "The PHP server is working fine"
else
echo "Something is wrong with the PHP server"
exit 1
fi
else
echo "No PHP server running"
fi''')
}


publishers {
extendedEmail {
recipientList('aakashkathunia@gmail.com')
defaultSubject('Something is wrong with the build')
defaultContent('The testing has been failed. Please Check!!')
contentType('text/html')
triggers {
beforeBuild()
stillUnstable {
subject('Subject')
content('Body')
sendTo {
developers()
requester()
culprits()
}
}
}
}
}
}
