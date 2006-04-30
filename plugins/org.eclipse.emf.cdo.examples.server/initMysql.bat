@echo off

mysql -u test -ptest -e "drop database if exists cdoTest; create database cdoTest" mysql

rem set ROOTPW=rIstec0o
rem set USER=test@localhost
rem set PASSWORD=test
rem set DATABASE=cdoTest

rem mysql -u root -p%ROOTPW% -e "drop database if exists %DATABASE%" mysql
rem mysql -u root -p%ROOTPW% -e "create database %DATABASE%" mysql
rem mysql -u root -p%ROOTPW% -e "create user %USER% identified by '%PASSWORD%'" mysql
rem mysql -u root -p%ROOTPW% -e "grant all privileges on %DATABASE%.* to %USER%" mysql
