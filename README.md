# KeyAuth-JAVA-API Remastered

**KeyAuth-JAVA-API** is a Java library designed for seamless integration of [KeyAuth](https://keyauth.cc/) into your applications.

### Features:
- Utilizes modern libraries to ensure security and mitigate vulnerabilities.
- Threaded session checking for efficient checking sessions.
- Simplified result handling using objects for ease of use.

### Additional Information:
- **No encryption** is applied to the data flow. This may be added in future

### Getting Started
To integrate **KeyAuth-JAVA-API** into your project, follow these steps:

1. **Installation**: Add the library to your project dependencies.

2. **Initialization**: Initialize the API with your KeyAuth credentials.
```java
KeyAuth keyAuthAPI = new KeyAuth(appname, ownerid, version);
```

3. **Usage**: Start using the API to authenticate users and manage sessions.
```java
LoginResult result = keyAuthAPI.login(username, password);
if(result.isSuccess()){
    System.out.println("Login successful!");
} else {
    System.out.println("Login failed: " + result.getMessage());
}

// Start a background thread to periodically check and maintain session validity every x milliseconds
keyAuthAPI.startSessionCheckerThread(10000);
```
