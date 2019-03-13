#!/bin/bash

sudo apt update
sudo apt install -y nmon  sysstat  regina-rexx  unzip  openssh-server  vim ant net-tools

mkdir $HOME/si2
cd $HOME/si2

#Install java8
sudo add-apt-repository ppa:webupd8team/java
sudo apt update
sudo apt install -y oracle-java8-installer 

#Download and install glassfish
mkdir temp
cd temp

wget http://apache.cbox.biz//jmeter/binaries/apache-jmeter-5.0.tgz &
wget https://www.vmware.com/go/getplayer-linux &
wget http://download.java.net/glassfish/4.1.2/release/glassfish-4.1.2.zip 

sudo unzip glassfish-4.1.2.zip -d /opt
sudo chmod -R a+rwX /opt/glassfish4 

sudo apt install -y postgresql tora libqt4-sql-psql libpostgresql-jdbc-java 

tar -xzvf apache-jmeter-5.0.tgz
mv apache-jmeter-5.0 ../ &
export PATH="$PATH:$HOME/apache-jmeter-5.0/bin"


sudo chmod +x VMware-Player-15.0.2-10952284.x86_64.bundl
sudo sh VMware-Player-15.0.2-10952284.x86_64.bundl 

#tar –xzvf /opt/si2/si2srv.tgz
if [ ! -f si2srv.tgz ]; then
    echo "si2srv.tgz not found!"
else
    tar –xzvf si2srv.tgz
fi


#El glassfish de los labos es el 4 pero tambien tienen el 5, OJO!!
export J2EE_HOME=/opt/glassfish4/glassfish

cd ..
rm -rf temp

echo -e "Ya esta todo instalado\nRecuerda configurar la maquina virtual (si2:2019sid0s)\n\t- Dos conexiones (nat y bridge) \n\t- Vigila a que interfaz corresponde cada una dentro de la vm! \n\t- Usa ssh user@host\n\t- Ejecuta el script para el cambio de ips y macs (virtualip.sh <interface>)\n\nJmeter se puede ejecutar con 'jmeter.sh'\n"


