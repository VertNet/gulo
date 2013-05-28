# configure EMR cluster for use with VertNet projects

# install some helpful utilities
sudo apt-get install -y  screen, s3cmd, zip, unzip

# Setup for git
git config --global user.name "Whizbang Systems"
git config --global user.email "admin@whizbangsystems.net"

# generate ssh key
ssh-keygen -t rsa -N "" -f /home/hadoop/.ssh/id_rsa -C "admin@whizbangsystems.net"
sudo chmod 644 /home/hadoop/.ssh/id_rsa

# Add github to known_hosts
echo "github.com,207.97.227.239 ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAq2A7hRGmdnm9tUDbO9IDSwBK6TbQa+PXYPCPy6rbTrTtw7PHkccKrpp0yVhp5HdEIcKr6pLlVDBfOLX9QUsyCOV0wzfjIJNlGEYsdlLJizHhbn2mUjvSAHQqZETYP81eFzLQNnPHt4EVVUh7VfDESU84KezmD5QlWpXLmvU31/yMf+Se8xhHTvKSCZIFImWwoG6mbUoWf9nzpIoaSjB+weqqUUmpaaasXVal72J+UX2B+2RPW3RcT0eOzQgqlJL3RKrTJvdsjE3JEAvGq3lGHSZXy28G3skua2SmVi/w4yCE6gbODqnTWlg7+wC604ydGXA8VJiS5ap43JXiUFFAaQ==" >> /home/hadoop/.ssh/known_hosts


# simple leiningen install via 'li'
echo "alias li='cd /home/hadoop/bin; wget https://raw.github.com/technomancy/leiningen/stable/bin/lein; chmod u+x lein; ./lein; cd /home/hadoop;'" >> /home/hadoop/.bashrc

# simple uberjarring
echo "alias uj='lein do deps, compile :all, uberjar'" >> /home/hadoop/.bashrc

git clone git://github.com/VertNet/gulo.git
git clone git://github.com/MapofLife/teratorn.git
git clone git://github.com/VertNet/webapp.git

source ~/.bashrc

sudo chown hadoop /mnt/
mkdir /mnt/beast
sudo chown hadoop:hadoop /mnt/beast
