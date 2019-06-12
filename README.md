
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

# Testing Request / Responses with Postman
### 1. Install Postman
### 2. Start the server
### 3. Try a POST request to `localhost:5000/echo_body` and add text to the body
### 4. See the server response

# Testing Multiple Requests
- To test the simultaneous client sessions with a benchmark, start App.main() and then run `ab -r -n 1000 -c 50
http://localhost:5000/` in another terminal.

