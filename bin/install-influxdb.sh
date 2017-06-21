distribution=$(lsb_release -si | awk '{print tolower($0)}')
if [ $distribution = "ubuntu" ] || [ $distribution = "Debian" ]; then
	wget -N https://dl.influxdata.com/influxdb/releases/influxdb_1.1.0_amd64.deb
	echo "installing influxdb.."
	sudo dpkg -i influxdb_1.1.0_amd64.deb
	echo "Changing default meta port 8088 to 8882"
	sudo sed -i '1s/^/bind-address = \":8882\"\n/' /etc/influxdb/influxdb.conf
	echo "Disabling anonymous reporting"
	sudo sed -i 's/reporting-disabled = false/reporting-disabled = true/' /etc/influxdb/influxdb.conf
	echo "Disabling influxdb meta data storing"
	sudo sed -i 's/store-enabled = true/store-enabled = false/' /etc/influxdb/influxdb.conf
	sudo service influxdb start
	echo "Creating user 'root' with password 'root'"
	wget "http://localhost:8086/query" --post-data "q=CREATE%20USER%20root%20WITH%20PASSWORD%20%27root%27%20WITH%20ALL%20PRIVILEGES"
elif [ $distribution = "redhat" ] || [ $distribution = "centos" ] || [ $distribution = "amazonami" ] || [ $distribution = "AmazonAMI" ]; then
	wget -N https://dl.influxdata.com/influxdb/releases/influxdb-1.1.0.x86_64.rpm
	echo "installing influxdb.."
	sudo yum localinstall -y influxdb-1.1.0.x86_64.rpm
	echo "Changing default meta port 8088 to 8882"
	sudo sed -i '1s/^/bind-address = \":8882\"\n/' /etc/influxdb/influxdb.conf
	sudo service influxdb start
	echo "Creating user 'root' with password 'root'"
	wget "http://localhost:8086/query" --post-data "q=CREATE%20USER%20root%20WITH%20PASSWORD%20%27root%27%20WITH%20ALL%20PRIVILEGES"
else
	echo "Sorry, unable to install influxdb on your system. You can install using binary package [https://s3.amazonaws.com/influxdb/influxdb_0.9.6.1_x86_64.tar.gz]."
fi
