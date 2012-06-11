=========================
CDO Master Server Example
=========================

This project contains the launch configuration "ExampleMasterServer" that
can be used to start a new OSGi process with a CDO master repository.

Before you actually run this master server you should review the server
configuration file /config/cdo-server.xml in this project. In particular
you may want to adjust the following settings:

- Acceptor port
	Default is 2036

- H2 datasource URL
	Default is a /database folde in the current directory, i.e., this project

- ID generation location
	Default is STORE, should be changed to CLIENT for all offline scenarios!

If the server is running you can point your web browser to
http://localhost:7777 in order to introspect the repository.
Please note that this introspection facility should not be used in production!
