#! /usr/bin/env bash
echo "Create the file repository configuration:"
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'

echo " Import the repository signing key:"
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -

echo "Update the package lists:"
sudo apt-get update

echo " Install the latest version of PostgreSQL."
echo " If you want a specific version, use 'postgresql-12' or similar instead of 'postgresql':"
sudo apt-get -y install postgresql-10