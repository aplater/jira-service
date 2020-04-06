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