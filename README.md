
# HTTP Server Requirements
- Write an HTTP server from scratch.

# Local Development Setup
### 1. Install Java
Check if Java is present: `java -version`

If not present, follow these [instructions](https://www.notion.so/Setting-Up-Java-Environment-1a48792fb5c6403bbb430c882e411226#3b7fec7b6e6d4f06a82dca4afcf31081).

### 2. Install Gradle
`brew install gradle`
Verify you have it installed with `gradle -v`

### 3. Clone repo
`git clone https://github.com/serpient/java_http_server.git`

### 4. Run Build or Test
Within project folder, run `./gradlew build` to see the status

Within project folder, run `./gradlew test` to run the tests

### 5. Run the server
#### Option 1: Default port 5000
Run `gradle run` to start the server

#### Option 2: Custom post
Run `gradle run --args=1111`. Replace '1111' with your own custom port

# Server and Application logic is separated
Project's entry point is from application side now. An example application that define routes and start the http server:
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

        app.get("/get_with_body", (Request request, Response response) -> {
            res.body("Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n");
        });

        app.head("/get_with_body", (Request request, Response response) -> {
            res.body("Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n");
        });

        app.post("/echo_body", (Request request, Response response) -> {});

        app.all("/redirect", (Request request, Response response) -> {
            response.redirect("/simple_get");
        });

        return app;
    }
}

```
# User Defined Routes and Callback
Users can define a route and a callback that let's them customize a response
### Example Routes:
```
app.head("/simple_get", (Request request, Response response) -> {});

app.get("/get_with_body", (Request request, Response response) -> {
        res.body("Here are all my favorite movies:\n" + "- Harry Potter\n");
});
```
### Request and Response objects are available in the route callback.
* Request allows users access to the request method, body and route from the callback.
* Response allows users to pre-define the status, add custom headers, and edit the response body.

# Testing Multiple Requests
- To test the simultaneous client sessions with a benchmark, start App.main() and then run `ab -r -n 1000 -c 50
http://localhost:5000/` in another terminal.

# Testing with Postman
### Install Postman
### Open terminal, navigate to project directory, then run `gradle run`
### Open Postman and run the different routes currently available:
```
GET /simple_get
HEAD /simple_get
GET /get_with_body
HEAD /get_with_body
POST /echo_body
```
# Testing with the Acceptance Tests
### Clone the [acceptance tests repository](https://github.com/8thlight/http_server_spec) and follow install instructions

### Open terminal, navigate to project directory, then run `gradle run`
### Open terminal, navigate to acceptance tests directory, then run `bundle exec spinach`