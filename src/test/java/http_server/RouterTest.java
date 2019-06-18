package http_server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class RouterTest {
    Router router;
    HashMap<String, HashMap<String, Callback>> collection;
    String crlf = "\r\n";
    String request_line = "POST /echo_body HTTP/1.1" + crlf;
    String user_agent = "User-Agent: HTTPTool/1.0" + crlf;
    String content_type = "Content-Type: text/plain" + crlf;
    String content_length = "Content-Length: 47" + crlf;
    String body = crlf + "Here are all my favorite movies:\n" + "- Harry Potter";
    String request = request_line + user_agent + content_type + content_length + body;
    RequestParser parser = new RequestParser(request);
    Request req = new Request(parser.method(), parser.route(), parser.body(), parser.headers());
    Response res = new Response(req, new Router());

    String anonymousFn_True_Result = "true";
    Callback anonymousFn_True = (Request req, Response res) -> {
        res.setBody(anonymousFn_True_Result);
    };

    String anonymousFn_False_Result = "false";
    Callback anonymousFn_False = (Request req, Response res) -> {
        res.setBody(anonymousFn_False_Result);
    };

    @Before
    public void initializeRouter() {
        router = new Router();
        collection = router.getRouter();
    }

    @After
    public void cleanUpRouter() {
        collection.clear();
    }

    @Test
    public void Router_Stores_Collection_Of_Methods_and_Routes() {
        HashMap<String, HashMap<String, Callback>> testRoutesCollection = new HashMap<>();
        Router router = new Router();
        HashMap<String, HashMap<String, Callback>> collection = router.getRouter();

        assertEquals(testRoutesCollection, collection);
    }

    @Test
    public void Get_Adds_New_Get_Route_In_Collection() {
        router.get("/get_with_body", anonymousFn_True);

        assertEquals("[GET]", collection.get("/get_with_body").keySet().toString());
    }

    @Test
    public void Calling_Callback_From_Collection_Matches_Lambda_On_Creation() {
        router.get("/get_with_body", anonymousFn_True);

        collection.get("/get_with_body").get("GET").run(req, res);

        assertEquals(anonymousFn_True_Result, res.getBody());
    }

    @Test
    public void A_Method_Can_Have_Multiple_Routes() {
        router.get("/get_with_body", anonymousFn_True);
        router.get("/another_get", anonymousFn_True);
        router.get("/hello", anonymousFn_True);

        assertEquals("[/get_with_body, /another_get, /hello]", collection.keySet().toString());
    }

    @Test
    public void Each_Route_In_a_Method_Has_Its_Own_Callback() {
        router.get("/get_with_body", anonymousFn_True);
        router.get("/hello", anonymousFn_False);

        Callback get_with_body_callback = collection.get("/get_with_body").get("GET");
        Callback hello_callback = collection.get("/hello").get("GET");

        assertEquals(anonymousFn_True, get_with_body_callback);
        assertEquals(anonymousFn_False, hello_callback);
    }

    @Test
    public void Head_Adds_New_Head_Route_In_Collection() {
        router.head("/get_with_body", anonymousFn_True);

        assertEquals("[HEAD]", collection.get("/get_with_body").keySet().toString());
    }


    @Test
    public void Post_Adds_New_Post_Route_In_Collection() {
        router.post("/get_with_body", anonymousFn_True);

        assertEquals("[POST]", collection.get("/get_with_body").keySet().toString());
    }


    @Test
    public void Route_Can_Have_Multiple_Methods() {
        router.post("/get_with_body", anonymousFn_True);
        router.head("/get_with_body", anonymousFn_False);
        router.get("/get_with_body", anonymousFn_False);

        assertEquals("[HEAD, POST, GET]", collection.get("/get_with_body").keySet().toString());
    }

    @Test
    public void Route_Can_Have_Multiple_Methods_With_Different_Callbacks() {
        router.post("/get_with_body", anonymousFn_True);
        router.head("/get_with_body", anonymousFn_False);
        router.get("/get_with_body", anonymousFn_False);

        Callback post_callback = collection.get("/get_with_body").get("POST");

        Callback head_callback= collection.get("/get_with_body").get("HEAD");

        Callback get_callback = collection.get("/get_with_body").get("GET");

        assertEquals(anonymousFn_True, post_callback);
        assertEquals(anonymousFn_False, head_callback);
        assertEquals(anonymousFn_False, get_callback);
    }

    @Test
    public void Given_Route_And_Method_Router_Can_Find_The_Matching_Callback() {
        router.get("/get_with_body", anonymousFn_False);
        assertEquals(anonymousFn_False, collection.get("/get_with_body").get("GET"));
    }
}
