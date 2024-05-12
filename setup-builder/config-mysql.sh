#! /bin/bash

sudo -i

sudo service mysql stop
systemctl set-environment MYSQLD_OPTS="--skip-grant-tables --skip-networking"
sudo service mysql start

mysql -u root < mysql-configs/reset-admin.sql


mysql -u root -p < mysql-configs/set-admin-password.sql

sudo service mysql stop
sudo service mysql start


cd db/

export PASSWORD=passWord@123

#sh ./setup-db.sh install
