package http_server;

import http_standards.MIMETypes;
import http_standards.Methods;
import http_standards.RequestCreator;
import http_standards.Stringer;
import java.nio.file.Paths;
import java.util.HashMap;
import mocks.MockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RouterTest {
    Router router;
    HashMap<String, HashMap<String, Callback>> collection;
    String request_line = "POST /echo_body HTTP/1.1" + Stringer.crlf;
    String user_agent = "User-Agent: HTTPTool/1.0" + Stringer.crlf;
    String content_type = "Content-Type: text/plain" + Stringer.crlf;
    String content_length = "Content-Length: 47" + Stringer.crlf;
    String body = Stringer.crlf + "Here are all my favorite movies:\n" + "- Harry Potter";
    String request = request_line + user_agent + content_type + content_length + body;
    Request req = RequestCreator.from(request);
    Response res = new Response(router, req);

    String anonymousFn_True_Result = "true";
    Callback anonymousFn_True = (Request req, Response res) -> {
        res.setBody(anonymousFn_True_Result.getBytes(), MIMETypes.plain);
    };

    String anonymousFn_False_Result = "false";
    Callback anonymousFn_False = (Request req, Response res) -> {
        res.setBody(anonymousFn_False_Result.getBytes(), MIMETypes.plain);
    };

    @BeforeEach
    public void initializeRouter() {
        router = new Router();
        router.setRepository(new MockRepository("/public"));
        collection = router.getRouter();
    }

    @AfterEach
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

        assertEquals("[GET, OPTIONS]", collection.get("/get_with_body").keySet().toString());
    }

    @Disabled
    @Test
    public void Calling_Callback_From_Collection_Matches_Lambda_On_Creation() {
        router.get("/get_with_body", anonymousFn_True);

        collection.get("/get_with_body").get("GET").run(req, res);
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

        assertEquals("[HEAD, OPTIONS]", collection.get("/get_with_body").keySet().toString());
    }


    @Test
    public void Post_Adds_New_Post_Route_In_Collection() {
        router.post("/get_with_body", anonymousFn_True);

        assertEquals("[POST, OPTIONS]", collection.get("/get_with_body").keySet().toString());
    }


    @Test
    public void Route_Can_Have_Multiple_Methods() {
        router.post("/get_with_body", anonymousFn_True);
        router.head("/get_with_body", anonymousFn_False);
        router.get("/get_with_body", anonymousFn_False);

        assertEquals("[HEAD, POST, GET, OPTIONS]", collection.get("/get_with_body").keySet().toString());
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

    @Test
    public void Router_Can_Set_A_Public_Directory_Route() {
        router.directory("/public");

        assertEquals(true, collection.get("/public").containsKey("GET"));
    }

    @Test
    public void Router_can_find_next_next_largest_id_for_a_parent_route() {
        router.get("/dog/1", (Request request, Response response) -> {
            response.setFile(request.getRoute());
        });

        router.get("/dog/3", (Request request, Response response) -> {
            response.setFile(request.getRoute());
        });

        router.post("/dog/5", (Request request, Response response) -> {

        });

        assertEquals("/dog/6", router.getUniqueRoute("/dog"));
    }

    @Test
    public void Router_can_set_first_resource_id_for_a_parent_route() {
        assertEquals("/dog/1", router.getUniqueRoute("/dog"));
    }

    @Test
    public void Router_can_save_resources() {
        router.basePath(Paths.get(System.getProperty("user.dir")));
        router.directory("/public");
        router.saveResource("/dog/1", "html", "DELETE ME".getBytes());

        assertEquals(true, router.getMethodCollection("/dog/1").containsKey(Methods.get));
        assertEquals(true, router.getMethodCollection("/dog/1.html").containsKey(Methods.get));
        assertEquals(true, router.getMethodCollection("/public/dog/1").containsKey(Methods.get));
        assertEquals(true, router.getMethodCollection("/public/dog/1.html").containsKey(Methods.get));
    }

    @Test
    public void Router_can_delete_resources_and_routes() {
        router.basePath(Paths.get(System.getProperty("user.dir")));
        router.directory("/public");
        router.saveResource("/delete_me", "txt", "DELETE ME".getBytes());
        router.deleteResource("/delete_me", "txt");

        assertEquals(false, router.getMethodCollection("/delete_me").containsKey(Methods.get));
        assertEquals(false, router.getMethodCollection("/delete_me.txt").containsKey(Methods.get));
        assertEquals(false, router.getMethodCollection("/public/delete_me").containsKey(Methods.get));
        assertEquals(false, router.getMethodCollection("/public/delete_me.txt").containsKey(Methods.get));
    }

    @Test
    public void router_can_be_created_with_different_directory_paths() {
        router = new Router("/images");
        router.setRepository(new MockRepository("/images"));
        router.saveResource("/dog/1", "html", "DELETE ME".getBytes());
        collection = router.getRouter();

        assertEquals(true, collection.get("/images").containsKey("GET"));
        assertEquals(false, collection.containsKey("/public"));
        assertEquals(true, router.getMethodCollection("/dog/1").containsKey(Methods.get));
        assertEquals(true, router.getMethodCollection("/images/dog/1").containsKey(Methods.get));
    }

    @Test
    public void router_can_get_method_collections_for_subroutes() {
        router.directory("/public");

        router.patch("/contacts/:id", (Request request, Response response) -> {
        });
        
        assertEquals(true, router.getMethodCollection("/contacts/1").containsKey(Methods.patch));
        assertEquals(true, router.getMethodCollection("/contacts/2").containsKey(Methods.patch));
    }
}
