# Android At a Glance

A glance for all necessary topics in ANDROID.

## Topics

* [Services](#services) - Services

### Services

![Services XML Attributes](./images/services.png?raw=true "Services XML Attributes in Manifest")

•	Normally service will run on application process that to in main thread. So if we want to create a service in new separate process:
    •   We can use <b>isolatedProcess</b> = true : separate special process will be created
    •   We can give name in <b>process</b> attribute as : if name starts with “.” Then service will be created in separate process and it can be accessible with in application or if name starts with small letter then service is created in separate process and that will be global
•	Service can be accessible for other applications along with current application components. That is via giving <b>exported</b> attribute true. Default value will depends upon using <b>intent-filter</b> in service tag in manifest file. If intent-filter is used so that it can be called from other applications also via implicit intents.
•	By default service can be instantiated, if we want to remove it we can use <b>enabled</b> attribute as false.
•	If we want our service to run before unlocking device we can use <b>directBootAware</b>.
•	There are three types of services:
    •   Foreground service : should show notification
    •   Background service : runs in background
        •	Normal service ( sequential on UI thread itself )
        •	IntentService ( Multithreading )
    •   Bound service : client – server architecture
        •   Local service using Binder ( sequential, same in app process )
        •   Using Messenger ( Sequential, isolated special process )
        •   AIDL ( Multithreading, isolated process )

