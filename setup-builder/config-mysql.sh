#! /bin/bash

sudo -i

systemctl stop mysql
systemctl set-environment MYSQLD_OPTS="--skip-grant-tables --skip-networking"

systemctl start mysql

mysql -u root
# TODO - This do not work straight from here, must pass command to mysql-client format
'''
USE mysql;
UPDATE user SET plugin='mysql_native_password' WHERE User='root';
FLUSH PRIVILEGES;
exit
'''


mysql -u root -p
# TODO - This do not work straight from here, must pass command to mysql-client format
'''
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'passWord@123';
COMMIT ;
exit
'''

sudo service mysql stop
sudo service mysql start

mysql --user=root --password=$PASSWORD

mysql> exit