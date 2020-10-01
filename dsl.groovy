job('task6_job2'){
description("The Second Job: Deploying respective webpages on the server")

steps {
shell {
('''if sudo ls /root/dev3 | grep .html
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
sudo kubectl cp /root/dev3/index.html $a:/var/www/html
else
echo "Cannot copy the HTML code"
fi
fi
else
echo "The code is not for HTML"
fi
if sudo ls /root/dev3 | grep .php
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
sudo kubectl cp /root/dev3/index.php $b:/var/www/html
else
echo "Cannot copy the PHP code"
fi
fi
else
echo "The code is not for PHP"
fi''')
}
}
}
