#! /bin/sh
rm -f config/application.properties
cp config/application-template.properties config/application.properties 
cp manifest.yml.template manifest.yml
cp manifest.yml.template manifest-integration.yml
