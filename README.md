# Quick Start
Example usage of http_server from an application
```
package application;
import http_server.Request;
import http_server.Response;
import http_server.Server;
import http_server.Router;

public class App {
    static Router app;
    static Server server;

    public static void main(String args[]) {
        app = createRouter();

        server = new Server(5000, app);
        server.start();
    }

    private static void createRouter() {
        Router app = new Router();

        // creating a directory
        app.staticDirectory("/public");


        // creating routes
        app.get("/dog", (Request request, Response response) -> {
            String uniqueRoute = app.getUniqueRoute(request.getRoute());
            String resourceRoute = app.saveResource(
                                        uniqueRoute, 
                                        request.getContentFileType(),
                                        request.getBody()
                                    );
            response.successfulPost(resourceRoute);
        });

        return app;
    }
}

```
# Defining Routes
Users can define a route and a callback that let's them customize the final response. Request and Response objects are available in the route callback.
* **Request** allows users access to the request method, body and route from the callback.
* **Response** allows users to pre-define the status, add custom headers, and edit the response body.

### Router Methods
| **Method**                                                         | **Returns** |                                                                                                                                                                   **Description** |
|--------------------------------------------------------------------|-------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| get(String route, Callback handler)                                | void        | Creates a GET method for the defined route                                                                                                                                        |
| head(String route, Callback handler)                               | void        | Creates a HEAD method for the defined route                                                                                                                                       |
| post(String route, Callback handler)                               | void        | Creates a POST method for the defined route                                                                                                                                       |
| put(String route, Callback handler)                                | void        | Creates a PUT method for the defined route                                                                                                                                        |
| delete(String route, Callback handler)                             | void        | Creates a DELETE method for the defined route                                                                                                                                     |
| all(String route, Callback handler)                                | void        | Creates all methods for the defined route                                                                                                                                         |
| staticDirectory(String directoryPath)                              | void        | Links to an existing directory from which resources can be read from / written to                                                                                                 |
| saveResource(String resourcePath, String fileType, byte[] content) | void        | Saves a resource to the previously defined static directory and creates the necessary GET & DELETE routes to access the resource. Returns the resource path.                      |
| saveResource(String resourcePath, String fileType, String content) | void        | Saves a resource to the previously defined static directory and creates the necessary GET & DELETE routes to access the resource. Returns the resource path.                      |
| deleteResource(String resourcePath, String fileType)               | void        | Deletes a resource from the defined static directory.                                                                                                                             |
| getUniqueRoute(String route)                                       | String      | Given a route such as "/dog", this will look through child paths and return the next unique route. For example, "/dog/2" would be returned if "/dog/1" is already being utilized. |

### Request Methods
| Method               	| Returns             	| Description                               	|
|----------------------	|-------------------------	|-------------------------------------------	|
| getMethod()          	| String                  	| Request method                            	|
| getBody()            	| String                  	| Request body                              	|
| getRoute()           	| String                  	| Request route                             	|
| getHeaders()         	| HashMap<String, String> 	| Request headers                           	|
| getParameters()      	| HashMap<String, String> 	| Request parameters from the route, if any 	|
| getContentFileType() 	| String                  	| File Type of Request body content, if any 	|

### Response Methods
| Method                                           	| Returns   	| Description                                                                      	|
|--------------------------------------------------	|---------------	|----------------------------------------------------------------------------------	|
| setStatus(String status)                         	| void          	| Sets the Response status                                                         	|
| getStatus()                                      	| String        	| Returns Response status                                                          	|
| setHeader(String headerName, String headerValue) 	| void          	| Sets a Response header                                                           	|
| getHeaders()                                     	| LinkedHashMap 	| Returns a hash map of Response headers                                           	|
| getBody()                                        	| byte[]        	| Returns the Response body                                                        	|
| sendFile(String filePath)                        	| void          	| Given a valid file path, Response sets the file, content-type and content-length 	|
| sendBody(byte[] bodyContent, String contentType) 	| void          	| Response sets the body content, content-type and content-length                  	|
| sendBody(String bodyContent, String contentType) 	| void          	| Response sets the body content, content-type and content-length                  	|
| redirect(String redirectedRoute)                 	| void          	| Response sets the status and headers needed to handle a redirected route         	|

# HTTP Server Local Development Setup
### 1. Install Java
Check if Java is present: `java -version`

This project is currently running `java version "12.0.1" 2019-04-16`.
If not present, follow these [instructions](https://www.notion.so/Setting-Up-Java-Environment-1a48792fb5c6403bbb430c882e411226#3b7fec7b6e6d4f06a82dca4afcf31081).

### 2. Install Gradle
`brew install gradle`
Verify you have it installed with `gradle -v`

### 3. Clone repo
`git clone https://github.com/serpient/java_http_server.git`

### 4. Run Build or Test
Within project folder, run `./gradlew build` to see the status

Within project folder, run `./gradlew test` to run the tests

### 5. Run the server from project directory
**Option 1: Default port 5000**
```
gradle run
```

**Option 2: Custom port and valid directory**
```
 gradle run --args="-p 3000 -d /test"
```

**Option 3: Custom port and valid directory from jar**
```
gradle build
java -jar build/libs/application.jar -p 2000 -d /files
```

**Arguments**
- `-p` for Port
- `-d` for Directory path


# Testing
#### Testing Multiple Requests
- To test simultaneous client sessions with a benchmark, start App.main() and then run `ab -r -n 1000 -c 50
http://localhost:5000/` in another terminal.

#### Testing with Postman
1. Install Postman
2. Open terminal, navigate to project directory, then run `gradle run`
3. Open Postman and run the different routes defined in the Application:

#### Testing with the Acceptance Tests
1. Clone the [acceptance tests repository](https://github.com/8thlight/http_server_spec) and follow install
instructions
2. Open terminal, navigate to project directory, then run `gradle run`
3. Open terminal, navigate to acceptance tests directory, then run `bundle exec spinach`