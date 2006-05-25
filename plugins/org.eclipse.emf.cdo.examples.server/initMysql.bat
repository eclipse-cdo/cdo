@echo off

rem mysql -u test -ptest -e "drop database if exists cdoTest; create database cdoTest" mysql

set ROOTPW=root
set USER=test@localhost
set PASSWORD=test
set DATABASE=cdoTest

mysql -u root -p%ROOTPW% -e "drop database if exists %DATABASE%" mysql
mysql -u root -p%ROOTPW% -e "create database %DATABASE%" mysql
rem mysql -u root -p%ROOTPW% -e "create user %USER% identified by '%PASSWORD%'" mysql
mysql -u root -p%ROOTPW% -e "grant all privileges on %DATABASE%.* to %USER% identified by '%PASSWORD%'" mysql
