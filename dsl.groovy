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
cp -rvf * /root/task3/
'''
)
}
}
