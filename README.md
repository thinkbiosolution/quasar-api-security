## API security layer with OAuth login 
## Table of Contents <!-- omit in toc -->


- [About](#About-the-Platform)
- [Installation](#installation)
- [Prerequisites](#Prerequisites)
- [Installing Node.js](#Installing-Node.js)
- [Verifying the Installation](#Verifying-the-Installation)
- [Initializing the Node.js Application](#Initializing-the-Node.js-Application)
- [Running the Node.js Application](#Running-the-Node.js-Application)

## About the Platform

The API security layer was built to fulfill the following needs for all API and authentication requests used by the QuasaR-NGIN platform:
- Authentication - The security layer must authenticate the users and validate their identities before allowing them to access the voice API.
- Authorisation - The security layer must determine the level of access each user has and only allow them to perform actions they are authorized to perform.
- Confidentiality - The security layer must protect the confidentiality of sensitive information, such as passwords, by encrypting data transmission.
- Data privacy - The security layer must ensure that user data is kept private and is not shared with unauthorized third parties.
- Risk assessment - The security layer must assess the potential risks associated with using the voice API and implement measures to minimize those risks.
- Compliance - The security layer must comply with relevant data protection regulations, such as the EU General Data Protection Regulation (GDPR).


To fulfil the requirements, an API security layer was built to handle the authentication, authorization, logging and reporting of all API requests made on the QuasaR-NGIN platform. 

## Installation

### Prerequisites

### Installing Node.js

The first step is to install Node.js on your system. There are two ways to install Node.js on Ubuntu:

#### Method 1: Using the apt Package Manager
The easiest way to install Node.js on Ubuntu is to use the apt package manager. Use the following command to install Node.js:

```
sudo apt-get update
sudo apt-get install nodejs

```

#### Method 2: Using the Node.js Binary Distributions

Another way to install Node.js on Ubuntu is to download the binary distributions from the Node.js website. You can download the latest version of Node.js from the official website: [Link](https://nodejs.org/en/download/ "https://nodejs.org/en/download/").

Once you have downloaded the binary distributions, follow these steps to install Node.js:

1. Open a terminal and navigate to the directory where you have downloaded the binary distributions.

2. Use the following command to extract the contents of the downloaded archive:

```
tar -xvf node-<version>.tar.gz
```

3. Navigate to the extracted directory:

```
cd node-<version>
```

4. Use the following command to configure and install Node.js:
```
./configure
make
sudo make install
```

### Verifying the Installation
Once you have installed Node.js, you can verify the installation by running the following command:


``` 
node -v 
```

This will display the version of Node.js that you have installed on your system.

### Initializing the Node.js Application
Once you have installed Node.js, you can create a new Node.js application. Follow these steps to create a new Node.js application:

1. Open a terminal and navigate to the directory where you want to create your Node.js application.

2. Use the following command to create a new Node.js application:

```
npm init
```

This command will create a new `package.json` file in your current directory. The package.json file contains information about your Node.js application, such as its name, version, dependencies, and scripts.

3. Enter the information about your Node.js application, such as its name, version, and description. You can press `Enter` to use the default values for each prompt.

4. Once you have completed the prompts, a new `package.json` file will be created in your current directory.

### Running the Node.js Application
You can run the Node.js application by using the following command:

node <file-name>.js
Replace <file-name> with the name of your Node.js file.

For example, if your Node.js file is named app.js, you can run it using the following command:

```
node app.js
```


This will start your Node.js application and it will run until you stop it by pressing Ctrl + C.