
all : package

#################### build

compile : 
	mvn $@

package : 	
	mvn $@

install :
	mvn $@

rebuild :
	mvn clean package

#################### document

site : 
	mvn site

#################### project
dist : 
	mvn assembly:single

clean : 
	mvn $@
