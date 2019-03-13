#!/bin/bash
sudo  mkdir -p /var/cache/apt/archives/partial
cat /etc/apt/sources.list | sed s/us.archive/old-releases/g > /tmp/hola
sudo mv /tmp/hola /etc/apt/sources.list
sudo reboot
sudo apt-get update
sudo apt-get install -y gcc
sudo apt-get install -y portmap
sudo apt-get install -y ant
