! notepad-app

This is a simple notepad application for Android OS.


!! Build Targets

http://maven-android-plugin-m2site.googlecode.com/svn/plugin-info.html

* mvn compile - compile sources.
* mvn package - build apk package.
* mvn clean   -  cleaning generated files.
* mvn install - install packages to maven local repository.

* mvn android:deploy - install apk to device or emulator.
* mvn android:generate-sources


!! Release Build

Define following properties in 'android-release' profile.
* sign.keystore
* sign.alias
* sign.storepass
* sign.keypass

Set up profile in ~/.m2/settings.xml 
------------------------------------------------------------
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd"
>
	<profiles>
		<profile>
			<id>android-release</id>
			<properties>
				<sign.keystore>some/keystore/path/hello.keystore</sign.keystore>
				<sign.storepass>hello.keystore.pass</sign.storepass>
				<sign.alias>hello</sign.alias>
				<sign.keypass>hello.pass</sign.keypass>
			</properties>
		</profile>
	</profiles>
</settings>
------------------------------------------------------------
