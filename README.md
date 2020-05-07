# Jira Service

This *Spring Boot Application* is implemented to interact with Jira and explore its API.

# Structure
This app has a package called **auth** which uses [OAuth](https://en.wikipedia.org/wiki/OAuth) version 1.0 to authenticate
with the Jira instance. Since Jira uses a 3-legged OAuth, I had to grant my app access on the Jira instance through application
links.  
There are two main clients:  
1- `OAuthClient`: this is the gate to the auth package and the main class I use to authenticate with.  
2- `JiraClient`: this is the class that I use to send my HTTP requests to Jira API.

`ClientMain` is where I interact with this two clients to authenticate first and then send my request to Jira API.

# Running The App
This application is authenticated with our Jira Cloud instance.In order to do that I did generate an RSA public/private 
key pair and then created an application link in Jira using that key (more on this can be found [here](https://developer.atlassian.com/cloud/jira/platform/jira-rest-api-oauth-authentication/)).
 In order to run this application you need to define the following environment variables:   
`JIRA_HOME`: something like this: `https://venasolutions.atlassian.net/`  
`JIRA_URL`: something like this: `https://venasolutions.atlassian.net/rest/api/3`  
`PRIVATE_KEY`: The key you used to verify your app on your jira instance  
`SPREADSHEET_ID`: Id of the Google sheet where you wanna insert Sprint report  
  
Once you run the app, it gives you a link that you need to click on. This brings up a web-page where you should click on
"allow" to get a verification code. Copy that verification code and paste it into command-line and press enter. Now your
app and its session is authenticated and you can proceed with using Jira's API.

# Google Sheets Integration
I created a new "Cloud Platform project" [here](https://developers.google.com/sheets/api/quickstart/java). This generated
a set of credentials specifically for this project; talk to Mustafa and he can give them to you. 

The first time you run the application, it'll open in the browser and ask you to authenticate; click ok. 

This will create an entry in the `/tokens` folder. If you need to re-authenticate or change the permissions required by
the application, delete the tokens folder to re-auth next time the service starts.

From there, create a spreadsheet with the columns "Committed, Injected, Inflated, Removed, Completed, Incomplete" (in any order, doesn't have to be continuous) and paste the sheet Id
into `SheetsTest.java`. The sheets Id can be found in the URL; it'll look something like `17guP0zy95DkAGKhEpExeNUyjMNB_BoJnNIy3yeothMU`