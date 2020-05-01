![Winnow](./src/img/logo.png)
# Winnow UI
## ReactJS
* NodeJS v12
* Code Base: `code/winnow-ui`
* Structure Overview:
    ```
    winnow-ui
    ├── jest.config.json    Test Suite configuration
    ├── package.json        Package dependencies
    ├── package-lock.json   Package dependencies
    ├── public              Static content
    └── src                 Application code
    ```

### Local
* Dependencies
  - `node` version 12.xx.xx
  - `winnow-core` application needs to be running to provide API endpoints.
* Tests
  - Run Test Suite
    ```shell script
      npm install
      npm run test -- --coverage --watchAll=false -u
    ```
  - Coverage Reports
  
    `./coverage/lcov-report/index.html`
* Run
    ```shell script
    npm install
    npm run start
    ```
    Browser should automatically open to http://localhost:3000/
    
### Production Deployment
* Build UI production bundle
  ```shell script
    npm install
    npm run build
   ```
  
* Create an S3 bucket: `winnow-ui-XXXXX`
  - Set to Public Access by deselecting all checkboxes for 'Block all public access'
  - Set to Static Website Hosting by selecting 'Use this bucket to host a website'. Fill in `index.html` for both the Index and Error Documents.
  - Create Bucket Policy to allow S3 Object access:
  ```metadata json
  {
    "Version":"2012-10-17",
    "Statement":[
      { "Sid":"AddPerm",
        "Effect":"Allow",
        "Principal":"*",
        "Action": [
          "s3:GetObject"
        ],
        "Resource":[
          "arn:aws:s3:::winnow-us-XXXXX/*"
        ]
      }
    ]
  }
  ```
  - Upload the contents of `./build/` to the root of the S3 bucket above.
  
* Create CloudFront Distribution
  - Select Web delivery.
  - Set Original Domain Name to the S3 bucket Endpoint URL
  - Set Restrict Bucket Access to No
  - Set Default Root Object to `index.html`
  - Set Viewer Protocol Policy to HTTP to Redirect to HTTPS.
  - Set Alternate Domain Names (CNAMEs) to your chosen fully qualified domain name (FQDN)
  - Set SSL Certificate to the certificate matching the FQDN above.
  - Click Create Distribution to create the distribution.


### UI Design & Code
* JSON.stringify is used in labels and titles to display nested Object data.
