# Centos7添加系统服务
- create tibet.service file. eg:
```unit file (systemd)
[Unit]
Description=tibet
After=network.target remote-fs.target nss-lookup.target

[Service]
Type=forking
ExecStart=/home/admin/run_java.sh start
ExecStop=/home/admin/run_java.sh stop
User=admin
Group=admin

[Install]
WantedBy=multi-user.target
```
- move tibet.service to /usr/lib/systemd/system/
- chmod +754 /usr/lib/systemd/system/tibet.service
- systemctl enable tibet.service
