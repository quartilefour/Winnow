# GFN (Winnow)
Gene Function Navigation Tool

## Project Structure

### Local Setup
 To configure the system to start on your local machine, make sure you have docker agent running on your laptop. Local setup is orchestrated using doker-compose(https://docs.docker.com/compose/gettingstarted/).
 
 Environment consists of 3 containers.
 * postgres container as db.cscie599.com running on port 5432.
 * adminer container to view the contents of the db. The container is running adminer UI at http://127.0.0.1:8090, make sure to change the Database type to postgres(defaults to mysql)
 * webapp container that runs the springboot backend app. The springboot backend app can be accessed in UI at http://127.0.0.1:8000
 * ftpfiledownloader container to download ftp files from various sources.
 * bulkingestion container to insert into postgres from the compressed files downloaded to S3 by ftpfiledownloader container.
 
 To start all the containers we first need to build them. (All the docker-compose commands should be run from the directory which has docker-compose.yml file). The webapp container requires the java code to be compiled and packaged as a war file. You can either build the war file explicitly by running the following commands
 
 `cd code/winnow` followed by `./gradlew clean build`
 
 Then to build the 3 containers go to the root folder <GFN> and run the below command, it will take a few minutes the first time this command is run as it has to download a bunch of dependent images.
 
 `docker-compose build`
 
 Alternatively you can run the helper script `build-code-containers.sh` in the root folder <GFN> which will build the code and also create all the relevant containers in a single step.
 
 Once all the images are successfully built to bring up the all the 3 containers run the command.
 
 `docker-compose up`
 
 Alternatively, if you are actively working on the web backend app and only want to run db and adminer containers, run the command
 
 `docker-compose up db.cscie599.com adminer.cscie599.com` 
 
 To tear down the setup, you can do `control + c` on the shell where the docker-compose command was run or run the command `docker-compose down` in a different shell.
 
 The containers when started using the docker-compose script use a bridge network gfn_default. To start a container outside of docker-compose but still connect to the gfn_default network start the container with the following command
 
 `docker run --network gfn_default -p 8080:8080 -t cscie599/gs-spring-boot-docker`
 
 To start the ftpdownloader container on your local machine, create the folder `tmp-docker`, `tmp-docker/extracted`, `tmp-docker/raw` on your local machine and run the following command, please do change the location of the source folder.
 
 `docker run --network gfn_default --mount type=bind,source=<path to the folder>/tmp-docker,target=/data gfn_ftpapp.cscie99.com:latest`
    
## Code Organization
* code/winnow/ - springboot backend app
* code/ingest/ - Data ingest and compute
* code/winnow-ui/ - ReactJS front end
* deploy/local/ - Contains the files required to bring up the docker containers.
* sql/ - Contains the SQL file with which the db container is initialized.
* test-data/ - Contains an example file with a subset of data for each of the datasets to be ingested for the analysis.

#### Java Local Environment 
For local development, non-Docker in your editor set an additional vm option `-Dspring.profiles.active=dev` and also pass the program argument `--input.directory=<Path to test-data/extracted folder on your local machine **(do have the file seperator at the end)**>`. This would enable the server to pick up springboot application-dev.properties 

Recommendation is still to run the DB in the container. To connect to the DB running in the container, add the following entry in the `/etc/hosts` file

`127.0.0.1	db.cscie599.com`

See [here](./code/winnow/HELP.md) for some documentation around springboot.

### ReactJS UI
```
winnow-ui
├── jest.config.json
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
After ```npm start```, a browser window should automatically open to http://localhost:3000/


### Production Setup ###
* Create a S3 bucket `cscie599`. This bucket will be used to store raw and processed data. Keep all the default settings for the bucket.

* Create a new VPC with a subnet having access to internet.
  - Provide `10.0.0.0/16` as the IPv4 CIDR. Make sure `DNS resolution` and `DNS hostnames` is enabled.
  - Create a subnet with IPv4 CIDR 10.0.0.0/24.
  - Create a new internet gateway and attach it to the VPC created above.
  - Setup route table association as follows
    - 10.0.0.0/16 -> local
    - 0.0.0.0/0   -> <internet-gateway>

* Create a cento 7 VM with an EBS attached. Install postgres on the VM by following the following instructions.
  - Add yum repository for postgres by running the command `sudo yum install https://download.postgresql.org/pub/repos/yum/11/redhat/rhel-7-x86_64/pgdg-centos11-11-2.noarch.rpm`
  - Download and install the postgres11 by running the command `sudo yum -y install postgresql11-server postgresql11`
  - Initialize postgre by running the command `sudo /usr/pgsql-11/bin/postgresql-11-setup initdb`
  - Start the postgres service `sudo systemctl start postgresql-11`
  - Configure the postgres service to start automatically at restart `sudo systemctl start postgresql-11`
  - Edit postgresql.conf to change the listen address to all ip addresses. `sudo vi /var/lib/pgsql/11/data/postgresql.conf`
  - Edit pg_hba.conf and use trust for authentication `sudo vi /var/lib/pgsql/11/data/pg_hba.conf`
  - Restart postgres service `sudo systemctl restart postgresql-11`
  - Change password for postgres user 
    - By first logging in without password `sudo su - postgres`
    - Setting up a password `psql -c "alter user postgres with password '<password to be set>'"`
  - Create a new user admin
    - Run the following command `createuser admin`
    - Set password for admin user `psql -c "alter user admin with password '<password to be set>'"`
  - Create DB gfn `createdb gfn -O admin`
  - exit from shell
  - Connect to postgres db using the superuser (postgres) credentials `psql -U postgres -h localhost -d gfn -W` when prompted for password enter the password.
  - Give user admin access to all schemas `GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO admin;` and exit
  - Create schema on postgres by running the command `psql -U admin gfn -W < create_schema.sql` enter the password when prompted.

* IAM setup
  - Create a new user cscie599 with 2 permissions `AmazonEC2ContainerRegistryFullAccess` and `AmazonS3FullAccess`
  - Create a new role, cscie599ECSS3 to be used across different AWS services with the following policies
    - AmazonEKSClusterPolicy
    - AmazonS3FullAccess
    - AmazonElasticMapReduceforEC2Role
    - AWSCodeDeployRoleForECS
    - AWSCodeDeployRoleForECSLimited
    - AmazonECS_FullAccess
    - AmazonEKSServicePolicy
    - AmazonECSTaskExecutionRolePolicy
    - AmazonEC2ContainerServiceforEC2Role
     
* Create a custom AMI and mounting S3 bucket.
      
* Uploading containers.
  - FTPFileDownload container setup
    -Setup a container repository on AWS for running the AWS job e.g `228205745268.dkr.ecr.us-east-1.amazonaws.com/cscie590`. To push the FTPdownloader image run the following 2 commands in sequence after succesfully running the `build-code-containers.sh` script.

      `docker tag gfn_ftpapp.cscie99.com:latest 228205745268.dkr.ecr.us-east-1.amazonaws.com/cscie590:latest`

      `docker push 228205745268.dkr.ecr.us-east-1.amazonaws.com/cscie590:latest`
      
  - Bulk Ingestion container
    -Setup another container repository on AWS for running the AWS job e.g `228205745268.dkr.ecr.us-east-1.amazonaws.com/cscie599-ingester`. To push the Ingestionjob image run    
      
      `docker tag gfn_ingesterapp.cscie99.com:latest 228205745268.dkr.ecr.us-east-1.amazonaws.com/cscie599-ingester:latest`
      
      `docker push 228205745268.dkr.ecr.us-east-1.amazonaws.com/cscie599-ingester:latest`

* AWS batch setup
  - Compute Environment
  - Job queue
  - Job definition
  - Launching a job