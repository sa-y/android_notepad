
all : package

#################### build

compile : 
	mvn $@

gen : 
	mvn android:generate-sources

package : 	
	mvn $@


install :
	mvn $@

rebuild :
	mvn clean package


#################### project
dist : 
	mvn assembly:assembly

clean : 
	mvn $@
