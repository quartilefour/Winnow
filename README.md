# GFN (Winnow)
Gene Function Navigation Tool

## Project Structure

### Local Setup
 To configure the system to start on your local machine, make sure you have docker agent running on your laptop. Local setup is orchestrated using doker-compose(https://docs.docker.com/compose/gettingstarted/).
 
 Environment consists of 3 containers.
 * postgres container as db running on port 5432.
 * adminer container to view the contents of the db. The container is running adminer UI at http://127.0.0.1:8090, make sure to change the Database type to postgres(defaults to mysql)
 * webapp container that runs the django app. The Django app can be accessed in UI at http://127.0.0.1:8000
 
 To start all the containers we first need to build them. (All the docker-compose commands should be run from the directory which has docker-compose.yml file). The webapp container requires the java code to be compiled and packaged as a war file. You can either build the war file explicitly by running the following commands
 
 `cd code/winnow` followed by `./gradlew clean build`
 
 Then to build the 3 containers run the below command, it will take a few minutes the first time this command is run as it has to download a bunch of dependent images.
 
 `docker-compose build`
 
 Alternatively you can use the helper script `build-code-containers.sh` which will do the above steps for you.
 
 Once all the images are successfully built to bring up the all the 3 containers run the command.
 
 `docker-compose up`
 
 Alternatively, if you are actively working on the django app and only want to run db and adminer containers, run the command
 
 `docker-compose up db adminer` 
 
 To tear down the setup, you can do `control + c` on the shell where the docker-compose command was run or run the command `docker-compose down` in a different shell.
 
 The containers when started using the docker-compose script start in a bridge network gfn_default. To start a container outside of docker-compose but still connect to the gfn_default start the container with the following command
 
 `docker run --network gfn_default -p 8080:8080 -t cscie599/gs-spring-boot-docker`
    
## Code Organization
* code/winnow/ - springboot backend app
* code/ingest/ - Data ingest and compute
* code/winnow-ui/ - ReactJS front end
* deploy/local/ - Contains the files required to bring up the docker containers.
* sql/ - Contains the SQL file with which the db container is initialized.

#### Java Local Environment 
For local development, non-Docker in your editor set an additional vm option `-Dspring.profiles.active=dev` This would enable the server to pick up springboot application-dev.properties 


Recommendation is still to run the DB in the container. To connect to the DB running in the container, add the following entry in the `/etc/hosts` file

`127.0.0.1	db`

### ReactJS UI
```
winnow-ui
├── package.json
├── package-lock.json
├── public
└── src
```

#### Run UI locally
```shell script
cd code/winnow-ui
npm install
npm start
```


### Production Setup ###
