#![Winnow](./src/img/logo-xs.png) Winnow UI
    
| Browser | Version | Support |
| :------- | :-------: | :-------: |
| Chrome | &ge;48 | &#10004; |
| Edge | &ge;44 | &#10004; |
| Firefox | &ge;72 | &#10004; |
| Internet Explorer | Any | &#10060; |
| Opera | &ge; 68 | &#10004; |
| Safari | &ge; 13 | &#10004; |
    
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
        ├── components      Custom React components
        ├── constants       Winnow constants
        ├── context         Auth context
        ├── img             Images
        ├── routes          Winnow entry points
        ├── service         Winnow application services
        └── tests           Application tests
    ```
### Local
* Dependencies
  - `node` version 12.xx.xx
  - `winnow-core` application needs to be running to provide API endpoints.
  - Browser support
    - Compatibility can be updated in `./public/index.html`
    - Browser detection script from: [SO](https://stackoverflow.com/questions/52736265/can-i-render-warning-message-if-users-browser-is-not-supported)
    
* Tests

  - Testing Framework: Jest with Enzyme
  - Run Test Suite
    ```shell script
      npm install
      npm run test -- --coverage --watchAll=false -u
    ```
  - Coverage Reports
  
    `./coverage/lcov-report/index.html`
  - Test Deployed Build bundle
    ```shell script
      npm -g install serve
      npm run build
      serve -s build
    ```
    The build bundle will be served at http://localhost:5000/
* Run
    ```shell script
    npm install
    npm run start
    ```
    Browser should automatically open to http://localhost:3000/
    
### Production Deployment
* Dependencies
  - `node` version 12.xx.xx
  
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
