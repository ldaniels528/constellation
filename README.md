# Constellation

An advanced 2D drawing, drafting and design package

### Objective

To create a state of the art 2D drawing package in 100% pure Java (Java2D and Swing interface).

### Features

1. Advanced geometry: lines, circles, ellipses, and Bezier curves
1. Collaboration
	* The Concurrent Design module enables users to concurrently create/update models (joint session)
	* The Remote Assistance module enables users to remotely control a user's session
1. Customization
	* A scripting language which can be used to create user commands and functions
	* A Java-based API will be exposed to create new types of geometric elements and functions.
1. Supports embedded images (jpeg, gif, png, tiff)
1. Integrated Help & Online Documentation
1. IGES format import

### Upcoming Features

1. Versioning System
	* Retain revisions of models for restoration based on version number or date
	* Support customized curves through the evaluation of user defined functions.

### Build Requirements

* Java 1.7+
* Maven 3.0+

### Building the code

    $ mvn clean package
      
### Running the tests

    $ mvn test   

### Run the application

	$ java -jar constellation.jar
