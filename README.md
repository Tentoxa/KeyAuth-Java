#  KeyAuth-JAVA-API Remastered

**KeyAuth-JAVA-API Remastered** is a Java library that simplifies integration of the [KeyAuth](https://keyauth.cc/) authentication system into your applications. 

**Key Features:**

-  Secure and modern libraries to minimize vulnerabilities.
-  Efficient session checking using background threads for optimal performance.
-  Easy-to-use object-based results for simplified handling.
-  Using latest KeyAuth api.

**Future Plans:**
-  Add License Key Login / Register

**Important Note:**

- ⚠️ **No data encryption** is currently implemented.

**Getting Started:**

1. **Installation (Maven):**

   - Add the jitpack.io repository to your `pom.xml`:

     ```xml
     <repositories>
         <repository>
             <id>jitpack.io</id>
             <url>https://jitpack.io</url>
         </repository>
     </repositories>
     ```

   - Add the KeyAuth-Java dependency:

     ```xml
     <dependency>
         <groupId>com.github.Tentoxa</groupId>
         <artifactId>KeyAuth-Java</artifactId>
         <version>1.1</version>
     </dependency>
     ```

2. **Initialization:**

   - Create a `KeyAuth` object using your KeyAuth credentials:

     ```java
     KeyAuth keyAuthAPI = new KeyAuth("your_app_name", "your_owner_id", "your_version");
     keyAuthAPI.init()
     ```

3. **Usage:**

   - **Login:**

     ```java
     LoginResult result = keyAuthAPI.login(username, password);

     if (result.isSuccess()) {
         System.out.println("Login successful!");
     } else {
         System.out.println("Login failed: " + result.getMessage());
     }
     ```

   - **Session Checking:**

     - **Recommended:** Use a background thread to periodically check session validity:

       ```java
       keyAuthAPI.startSessionCheckerThread(10000); // Check every 10 seconds
       ```
