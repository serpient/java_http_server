package http_server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class RouterTest {
    Router router;
    HashMap<String, HashMap<String, Callback>> collection;
    String req = "REQ";
    String res = "RES";

    String anonymousFn_True_Result = "true";
    Callback anonymousFn_True = (String req, String res) -> {
        System.err.println(res); return anonymousFn_True_Result;
    };

    String anonymousFn_False_Result = "false";
    Callback anonymousFn_False = (String req, String res) -> {
        System.err.println(res); return anonymousFn_False_Result;
    };

    @Before
    public void initializeRouter() {
        router = new Router();
        collection = router.collection();
    }

    @After
    public void cleanUpRouter() {
        collection.clear();
    }

    @Test
    public void Router_Stores_Collection_Of_Methods_and_Routes() {
        HashMap<String, HashMap<String, Callback>> testRoutesCollection = new HashMap<>();
        Router router = new Router();
        HashMap<String, HashMap<String, Callback>> collection = router.collection();

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

        String callbackResult = collection.get("/get_with_body").get("GET").run(req, res);

        assertEquals(anonymousFn_True_Result, callbackResult);
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

        String get_with_body_callback_result = collection.get("/get_with_body").get("GET").run(req, res);
        String hello_callback_result = collection.get("/hello").get("GET").run(req, res);

        assertEquals(anonymousFn_True_Result, get_with_body_callback_result);
        assertEquals(anonymousFn_False_Result, hello_callback_result);
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

        String post_callback_result = collection.get("/get_with_body").get("POST").run(req, res);

        String head_callback_result = collection.get("/get_with_body").get("HEAD").run(req, res);

        String get_callback_result = collection.get("/get_with_body").get("GET").run(req, res);

        assertEquals(anonymousFn_True_Result, post_callback_result);
        assertEquals(anonymousFn_False_Result, head_callback_result);
        assertEquals(anonymousFn_False_Result, get_callback_result);
    }

    @Test
    public void Given_Route_And_Method_Router_Can_Find_The_Matching_Callback() {
        router.get("/get_with_body", anonymousFn_False);
        String returnedValue = router.response("GET", "/get_with_body", req, res);
        assertEquals(anonymousFn_False_Result, returnedValue);
    }
}
