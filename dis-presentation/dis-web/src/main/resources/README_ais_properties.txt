
ais.properties configuration file,

must be placed inside a jboss module , at:

[JBOSS_HOME]/modules

or layered modules folder:
[JBOSS_HOME]/modules/system/layers/base

create inside jboss modules directory, a folder path like this must be created:
com/discovery/lais/main/

and create inside this files:
- ais.properties  
	(Copy from sample file /dis-web/src/main/resources/ais.properties_atJboss)

- module.xml


Module xml shoud have this content:

<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.0" name="com.discovery.lais">
    <resources>
        <resource-root path="."/>
    </resources>
</module>



