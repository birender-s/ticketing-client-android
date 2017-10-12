#!/bin/bash
sudo add-apt-repository ppa:ondrej/php
sudo apt-get update
sudo apt-get install -y apache2
sudo ufw allow in "Apache Full"
sudo apt-get install -y mysql-server

# After running this command, select (Y), option 2, then (Y) for the rest of the prompts.
sudo mysql_secure_installation

sudo apt install -y php5.6 libapache2-mod-php5.6 php5.6-curl php5.6-gd php5.6-mbstring php5.6-mcrypt php5.6-mysql php5.6-xml php5.6-xmlrpc

# This will, and should, return a "does not exist!" message.
sudo a2dismod php7.0

# This will, and should, return a "already enabled" message.
sudo a2enmod php5.6

sudo systemctl restart apache2

apt-get update && \
apt-get install -qy \
git \
libicu-dev \
libmcrypt-dev \
libpng-dev \
libxml2-dev \
npm && \
apt-get clean

apt-get install -y php5.6-gd
apt-get install -y  php5.6-intl 
apt-get install -y php5.6-mbstring
apt-get install -y php5.6-mcrypt
apt-get install -y  php5.6-opcache
apt-get install -y  php5.6-pdo-sql
apt-get install -y  php5.6-soap
apt-get install -y  php5.6-zip
apt-get install -y  php5.6-curl
apt-get install -y  php5.6-dom
apt-get install -y  php5.6-zip

curl -sS https://getcomposer.org/installer | php && \
mv composer.phar /usr/local/bin/composer  

mkdir -p /var/www/diamante
cd /var/www/diamante/
git clone -b 2.0-api-fix https://github.com/birender-s/diamantedesk-application.git .
cp php.ini /etc/php/5.6/apache2/
cp php.ini  /etc/php/5.6/cli/
cd /var/www/diamante/
composer install 
ln -s /usr/bin/nodejs /usr/bin/node && \
npm install -g grunt-cli bower
mkdir -p /var/www/diamante/web/uploads/
chown -R root:root /var/www/diamante/web /var/www/diamante/app/attachment /var/www/diamante/app/attachments /var/www/diamante/app/cache /var/www/diamante/app/config/parameters.yml /var/www/diamante/app/logs
chmod  -R 0777 /var/www/diamante/web /var/www/diamante/app/attachment /var/www/diamante/app/attachments /var/www/diamante/app/cache /var/www/diamante/app/config/parameters.yml /var/www/diamante/app/logs
rm -rf /var/www/html && ln -s /var/www/diamante/web /var/www/html
a2enmod rewrite
sudo service apache2 stop
cp 000-default.conf /etc/apache2/sites-enabled/000-default.conf
sudo service apache2 start

