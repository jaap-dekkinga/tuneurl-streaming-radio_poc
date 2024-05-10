#! /bin/bash

sudo apt update
sudo apt install -y apache2

# checking and giving permission
# sudo ufw app list
sudo ufw allow 'Apache'
# sudo ufw status


sudo systemctl status apache2
export MY_URL=(curl -s4 icanhazip.com)


# sudo systemctl stop apache2
sudo systemctl start apache2
# sudo systemctl restart apache2
# sudo systemctl reload apache2
# sudo systemctl disable apache2
# sudo systemctl enable apache2

sudo mkdir /var/www/tuneurl
sudo chmod -R 755 /var/www/tuneurl
