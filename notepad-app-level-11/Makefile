RELEASE_PROFILE=android-release

all : package

#################### build

compile : 
	mvn $@

gen : 
	mvn android:generate-sources

package : 	
	mvn $@

release : 	
	mvn clean && mvn -P$(RELEASE_PROFILE) package 

install :
	mvn $@

deploy : 
	mvn package android:deploy

undeploy : 
	mvn android:undeploy

rebuild :
	mvn clean package

redeploy :
	mvn clean package android:deploy

#################### document
site :
	mvn $@

#################### emulator

start : 
	mvn android:emulator-start

stop : 
	mvn android:emulator-stop

####################
dist : 
	mvn assembly:assembly

clean : 
	mvn $@
