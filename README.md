# Palla's Imgur Gallery API

## Authors

- [Nagendra Palla](https://github.com/nagendrapalla)

### Prerequisites

- [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) (minimum 17)
- [Docker](https://www.docker.com/products/docker-desktop/) Desktop

#### After Successful Installation, run the below command to install required tools

```docker-compose up -d```

### Imgur Developer API Setup

- https://api.imgur.com/oauth2/addclient
- Register an Application by providing ```Application Name & Email``` in the below link
- Save Generated Client ID & Client Secret for future usage
- Trigger the below url by replacing **client_id** response in browser as a GET call to fetch Access Token & Refresh
  Token
- ``` https://api.imgur.com/oauth2/authorize?client_id={{YOUR_CLIENT_ID}}&response_type=token```
- After Successful Login, you'll be redirected to new page. Copy the entire url in the new screen and extract access
  token and refresh token for future usage
- Now you have ```Client ID, Client Secret, Refresh Token ```
- Replace above details in application.yml file

### Jenkins Setup CI/CD Pipeline

- Install Jenkins locally
- Replace both ```palla4you, ${password}``` with your docker username & password in **_JenkinsFile_**
- Create a file with Jenkins Pipeline Script
- It will generate docker image and push it to Docker HUB
- Now Docker Image is ready to run...

### Available API's to trigger

#### url: [POST] http://localhost:8080/auth/register
Request Payload:

```json
{
  "username": "palla",
  "password": "palla123"
}
```

Response Payload:

```json lines
{
  "userId": 1,
  "username": "palla"
}
```

&nbsp;

#### url: [POST] http://localhost:8080/auth/login

Request Payload:

``` json
{ "username": "palla", "password": "palla123" }
```

Response Payload:

``` json
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYWxsYSIsImlhdCIMTY4Mjk5Njk4MCwiZXhwIjoxNjgyOTk4NzgwfQ.YCdp8srkMTrJWfCW_8A4ErxzD0I5lLOC9yLG9ULHPkI
``` 

&nbsp;

#### url: [POST] http://localhost:8080/image/upload/multiple

- Bearer
  Token: ```eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYWxsYSIsImlhdCIMTY4Mjk5Njk4MCwiZXhwIjoxNjgyOTk4NzgwfQ.YCdp8srkMTrJWfCW_8A4ErxzD0I5lLOC9yLG9ULHPkI```
- Request Form Data:

```json 
{
  "files": "Multipart Files",
  "username": "palla"
}
```

- Response Payload
```json
  [
    {
      "id": 1,
      "link": "https://i.imgur.com/V8yaitC.jpg"
    },
    {
      "id": 2,
      "link": "https://i.imgur.com/kfVrvNk.jpg"
    },
    {
      "id": 3,
      "link": "https://i.imgur.com/qwI5kLA.jpg"
    }
  ]
  ```

&nbsp;

#### url: [POST] http://localhost:8080/image/upload

- Bearer
  Token: ```eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYWxsYSIsImlhdCIMTY4Mjk5Njk4MCwiZXhwIjoxNjgyOTk4NzgwfQ.YCdp8srkMTrJWfCW_8A4ErxzD0I5lLOC9yLG9ULHPkI```
- Request Form Data:

```json 
{
  "file": "Multipart File",
  "username": "palla"
}
```

- Response Payload
```json
  [
    {
      "id": 1,
      "link": "https://i.imgur.com/V8yaitC.jpg"
    }
  ]
  ```

&nbsp;

#### url: [GET] http://localhost:8080/image/user/{username}

- Bearer
  Token: ```eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYWxsYSIsImlhdCIMTY4Mjk5Njk4MCwiZXhwIjoxNjgyOTk4NzgwfQ.YCdp8srkMTrJWfCW_8A4ErxzD0I5lLOC9yLG9ULHPkI```
- Response Payload
```json
  [
    {
      "id": 1,
      "link": "https://i.imgur.com/V8yaitC.jpg"
    },
    {
      "id": 2,
      "link": "https://i.imgur.com/kfVrvNk.jpg"
    },
    {
      "id": 3,
      "link": "https://i.imgur.com/qwI5kLA.jpg"
    }
  ]
```

&nbsp;

#### url: [GET] http://localhost:8080/image/{image_id}

- Bearer
  Token: ```eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYWxsYSIsImlhdCIMTY4Mjk5Njk4MCwiZXhwIjoxNjgyOTk4NzgwfQ.YCdp8srkMTrJWfCW_8A4ErxzD0I5lLOC9yLG9ULHPkI```
- Response Payload
```json 
  {
    "id": 1,
    "link": "https://i.imgur.com/V8yaitC.jpg"
  }  
```
&nbsp;

#### url: [DELETE] http://localhost:8080/image/{image_id}

- Bearer
  Token: ```eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYWxsYSIsImlhdCIMTY4Mjk5Njk4MCwiZXhwIjoxNjgyOTk4NzgwfQ.YCdp8srkMTrJWfCW_8A4ErxzD0I5lLOC9yLG9ULHPkI```
- Response Payload
``` 
  Image Deleted Successfully 
```
