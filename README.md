[![Build Status](https://travis-ci.org/sebge2/i18n-tool.svg?branch=master)](https://travis-ci.org/sebge2/i18n-tool)

# I18n Tool
Tool scanning translations in a GitHub repository. A UI editor allows to find missing translations and fill them.


## Backend Endpoints

* /api/*
* /ws/*
* /auth/*
    * /auth/oauth2/authorize-client/{client-registration-id}
    * /auth/oauth2/code/{code}
    
## Angular Routes

* /login
* /logout/success


## Environment Setup
### Travis Configuration

The following environment variables are needed by this Travis-CI build:
* AWS_ACCESS_KEY=****
* AWS_SECRET_KEY=****
* DOCKER_PASSWORD=****
* DOCKER_USERNAME=****
* E2E_GIT_HUB_OAUTH_CLIENT=****
* E2E_GIT_HUB_OAUTH_SECRET=****
* E2E_GIT_HUB_REPO_NAME=i18n-tool
* E2E_GIT_HUB_REPO_USER_NAME=sebge2
* E2E_GIT_HUB_USER=**** 
* E2E_GIT_HUB_USER_PASSWORD=****
* E2E_GIT_HUB_WEBHOOK_SECRET=****
* E2E_SERVER_PORT=8080
* DEFAULT_ADMIN_PASSWORD=my-admin-password

### Amazon EB Configuration

The following environment variables must be setup on Amazon Elastic Bean Stalk:
* SERVER_PORT
* DOCKER_IMAGE_VERSION
* GIT_HUB_OAUTH_CLIENT
* GIT_HUB_OAUTH_SECRET
* GIT_HUB_REPO_USER_NAME
* GIT_HUB_REPO_NAME
* GIT_HUB_WEBHOOK_SECRET
* DEFAULT_ADMIN_PASSWORD (optional)