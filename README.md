<h2>Summary</h2>

This file synchronization package was a project for a course in Distributed Systems at the University of Melbourne and is an example of a multi-threaded client-server program using a pre-specified message-passing protocol. It handles the synchronization of a file between client and server and can be executed with either client or server pushing (but neither client nor server can push and pull at the same time).

The package has already been compiled as two JAR executables, syncclient.jar and syncserver.jar. The source code in the two packages is identical due to the need to both push and pull. It could also be re-compiled with Client and Server as the main classes.

The client is by default in push mode, the source sending data blocks to the destination, and the default block size is 1024. If the file at the destination has changed and is not what it is expected to be by the source, an exception will be thrown and the synchronization should still result as expected.

<h2>Usage</h2>

Usage on Server
$ java -jar syncserver.jar -file filename

Usage on Client
$ java -jar syncclient.jar -file filename -host hostname [-direction push/pull] [-blocksize blocksize]

<h3>For more information</h3>
See detailed_readme.txt.