# nexum-rpa-test:

-- -

### Test done as a step for the interview

* Configurations used to make: Linux Xubuntu, IntelliJ and Docker

## Instructions

* Verify the Java version (17). It needs to be compatible.

* Verify in the machine if it has Maven installed.

* Requires Docker installed (postgres is on docker-compose).

* Verify if the Chromedriver version are the same of your chrome installed (the chromedriver in the folder are the linux
  version 109).

* Before run the project, init the docker-compose.yml.

#### Use `docker-compose up` inside the project folder then, after that, the project will be able to run.

#### After this, use all in sequence:

* `mvn clean`
* `mvn install`
* `java -jar target/nexum-rpa-test-1.0.1.jar`

## Some observations:

### After making the code, I tried to make a Dockerfile to run the Jar file, but I couldn't do it in time (chromedriver was not found).

### During I'm realizing, I've noted that the CasasBahia's website get a protection about webScraping and all automations ("Automation framework is detected" in console).

<img src="casasBahiaProtection.png">

### After this, when you reload the page, you will get forever an access denied message.

<img src="casasBahiaProtection2.png">