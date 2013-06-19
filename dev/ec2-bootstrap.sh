# Run this script to configure an instance for harvesting and bulkloading.

# install a few things

sudo apt-get update
sudo apt-get -y install screen zip unzip git sqlite3
http://s3tools.org/repo/deb-all/stable/s3cmd_1.0.0.orig.tar.gz
tar -xvf s3cmd_1.0.0.orig.tar.gz
cd s3cmd-1.0.0
sudo python setup.py install
cd

# Setup for git
git config --global user.name "David Bloom"
git config --global user.email "dbloom@vertnet.org"

# generate ssh key
ssh-keygen -t rsa -N "" -f /home/$USER/.ssh/id_rsa -C "dbloom@vertnet.org"
sudo chmod 644 /home/$USER/.ssh/id_rsa

# Add github to known_hosts
echo "github.com,207.97.227.239 ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAq2A7hRGmdnm9tUDbO9IDSwBK6TbQa+PXYPCPy6rbTrTtw7PHkccKrpp0yVhp5HdEIcKr6pLlVDBfOLX9QUsyCOV0wzfjIJNlGEYsdlLJizHhbn2mUjvSAHQqZETYP81eFzLQNnPHt4EVVUh7VfDESU84KezmD5QlWpXLmvU31/yMf+Se8xhHTvKSCZIFImWwoG6mbUoWf9nzpIoaSjB+weqqUUmpaaasXVal72J+UX2B+2RPW3RcT0eOzQgqlJL3RKrTJvdsjE3JEAvGq3lGHSZXy28G3skua2SmVi/w4yCE6gbODqnTWlg7+wC604ydGXA8VJiS5ap43JXiUFFAaQ==" >> /home/$USER/.ssh/known_hosts

# install Java
sudo apt-get -y install openjdk-7-jre
sudo apt-get -y install openjdk-7-jdk

# make ~/bin directory, add to PATH
mkdir ~/bin
echo "export PATH=/home/$USER/bin:${PATH}" >> ~/.bashrc

# install lein
cd ~/bin
wget https://raw.github.com/technomancy/leiningen/stable/bin/lein
chmod u+x lein
./lein
cd ~/

# install app engine sdk
cd bin
wget http://googleappengine.googlecode.com/files/google_appengine_1.8.0.zip
unzip google_appengine_1.8.0.zip
echo "export PATH=/home/$USER/bin/google_appengine:${PATH}" >> ~/.bashrc
cd

# simple uberjarring via uj command
echo "alias uj='lein do deps, compile :all, uberjar'" >> /home/$USER/.bashrc

# clone projects
git clone git://github.com/VertNet/gulo.git
git clone git://github.com/VertNet/webapp.git

# configure EBS volume
sudo mkfs -t ext3 /dev/xvdb
sudo mkdir /mnt/beast
sudo mount /dev/xvdb /mnt/beast
sudo chown $USER:$USER /mnt/beast

# configure credentials

echo "Configuring CartoDB. Please have your credentials ready and press 'enter' to continue."
read na
echo "Oauth key:"
read OAUTH_KEY
echo "Oauth secret:"
read OAUTH_SECRET
echo "Username:"
read USERNAME
echo "Password:"
read CDB_PASSWORD
echo "API key:"
read API_KEY
 
echo "{
  \"key\": \"$OAUTH_KEY\",
  \"secret\": \"$OAUTH_SECRET\",
  \"user\": \"$USERNAME\",
  \"password\": \"$CDB_PASSWORD\",
  \"api_key\": \"$API_KEY\"
}" > ~/gulo/resources/creds.json

echo "Configuring AWS. Please have your credentials ready and press 'enter' to continue. Note that backslashes in your AWS credentials may cause errors."
read na
echo "Access key:"
read ACCESS_ID
echo
echo "Secret key:"
read SECRET_KEY
echo
 
echo "{
        \"access-id\": \"$ACCESS_ID\",
        \"secret-key\": \"$SECRET_KEY\"
}" > ~/gulo/resources/aws.json
 
echo "Keep those AWS credentials handy for configuring s3cmd. Press 'enter' to continue"
 
s3cmd --configure

# configure app engine credentials

echo "Please enter your App Engine email address: "
read EMAIL
echo "export EMAIL=$EMAIL" >> ~/.bashrc

echo "Please enter your App Engine password: "
read GAE_PASSWORD
echo "export GAE_PASSWORD=$GAE_PASSWORD" >> ~/.bashrc
echo "Credentials are now set up."

# uberjar gulo
cd gulo
uj
cd

echo "Instance configured - go have a beer to celebrate!"
