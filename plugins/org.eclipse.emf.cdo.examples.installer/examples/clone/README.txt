========================
CDO Clone Server Example
========================

This project contains the launch configuration "ExampleCloneServer" that
can be used to start a new standalone process with a CDO clone repository.

The clone will connect to a master server that accepts connections on
port 2036 and automatically replicate it whenever it's available.

Make sure that the master repository is configured with ID generation
location CLIENT and auditing, as well as branching!

If the server is running you can point your web browser to
http://localhost:7778 in order to introspect the repository.
Please note that this introspection facility should not be used in production!
