# The universal Makefile for Groovy DSL projects.
APP=odrl

# Nothing below this point should be changed.

VERSION=$(shell cat VERSION)
NAME=$(APP)-$(VERSION)
JAR=$(NAME).jar
RESOURCES=src/test/resources
DIST=target/$(APP)

help:
	@echo
	@echo "Available goals are:"
	@echo
	@echo "      clean : Clean removes all artifacts from previous builds."
	@echo "        jar : Creates the odrl-$(VERSION).jar file."
	@echo "    release : Creates zip archives of the jar and start script."
	@echo "    install : Copies the jar and startup script to the user's bin directory."
	@echo "     upload : uploads the zip files to the ANC web server."
	@echo "       help : Displays this help message."
	@echo
	
jar:
	mvn package
	
clean:
	mvn clean
	
install:
	cp target/$(JAR) $(HOME)/bin
	cat $(RESOURCES)/$(APP) | sed 's|__JAR__|$(HOME)/bin/$(JAR)|' > $(HOME)/bin/$(APP)
	
debug:
	@echo "Current version is $(VERSION)"
	
release:
	#mvn clean package
	if [ -d $(DIST) ] ; then rm -rf $(DIST) ; fi
	if [ -f target/$(NAME).zip ] ; then rm target/$(NAME).zip ; fi
	if [ ! -f target/$(JAR) ] ; then mvn clean package ; fi

	mkdir $(DIST)
	cat $(RESOURCES)/$(APP) | sed 's|__JAR__|$(APP)-$(VERSION).jar|' > $(DIST)/$(APP)
	chmod u+x $(DIST)/$(APP)
	cp target/$(JAR) $(DIST)
	cd target ; zip -r $(APP) $(APP) ; cp $(APP).zip $(NAME).zip ; mv $(APP).zip $(APP)-latest.zip
	echo "Release ready."
	
upload:
	if [ -e $(DIST)/$(NAME).zip ] ; then scp -P 22022 $(DIST)/$(NAME).zip suderman@anc.org:/home/www/anc/downloads ; fi
	if [ -e $(DIST)/$(APP)-latest.zip ] ; then scp -P 22022 $(DIST)/$(APP)-latest.zip suderman@anc.org:/home/www/anc/downloads ; fi
	echo "Release complete."

