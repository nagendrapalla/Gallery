# Palla's Imgur Gallery API


### Prerequisites
- [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) (minimum 17)
- [Docker](https://www.docker.com/products/docker-desktop/) Desktop


#### After Successful Installation, run the below command to install required tools
  ```docker-compose up -d```

### Imgur Developer API Setup
- https://api.imgur.com/oauth2/addclient
- Register an Application by providing ```Application Name & Email``` in the below link
- Save Generated Client ID & Client Secret for future usage
- Trigger the below url by replacing **client_id** response in browser as a GET call to fetch Access Token & Refresh Token 
- ``` https://api.imgur.com/oauth2/authorize?client_id={{YOUR_CLIENT_ID}}&response_type=token```
- After Successful Login, you'll be redirected to new page. Copy the entire url in the new screen and extract access token and refresh token for future usage
- Now you have ```Client ID, Client Secret, Refresh Token ```
- Replace above details in application.yml file


### Jenkins Setup CI/CD Pipeline
- Install Jenkins locally
- Replace both ```palla4you, ${password}``` with your docker username & password in **_JenkinsFile_**
- Create a file with Jenkins Pipeline Script