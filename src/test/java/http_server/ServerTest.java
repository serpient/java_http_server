package http_server;

import html_builder.HTMLBuilder;
import http_standards.Headers;
import http_standards.MIMETypes;
import http_standards.Parser;
import http_standards.Stringer;
import mocks.MockClientSocket;
import mocks.MockRepository;
import mocks.MockRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.Repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    Router router;

    private String runSessionAndRetrieveResponse(String request) {
        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        return mockClientSocket.getSentData();
    }

    @BeforeEach
    public void cleanRouter() {
        Repository mockRepository = new MockRepository("/public");
        mockRepository.writeFile("./public/Home.html", MIMETypes.html, "<!DOCTYPE html>\n".getBytes());
        mockRepository.writeFile("./public/TurtleTab.txt", MIMETypes.plain, "TurtleTabs a Google".getBytes());
        mockRepository.writeFile("./public/water.png", MIMETypes.png, "water image".getBytes());
        mockRepository.writeFile("./public/japan.png", MIMETypes.png, "japan image".getBytes());
        router = new MockRouter(mockRepository).getApp();
    }

    @Test
    public void GET_Request_Is_Responded_With_Headers_And_No_Body() {
        String request = "GET /simple_get HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals(true, Parser.getHeaders(response).containsKey("Date"));
        assertEquals(true, Parser.getHeaders(response).containsKey("Server"));
        assertEquals(null, Parser.getBody(response));
    }

    @Test
    public void HEAD_Request_Is_Responded_With_Headers_Only() {
        String request = "HEAD /simple_get HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals(true, Parser.getHeaders(response).containsKey("Date"));
        assertEquals(true, Parser.getHeaders(response).containsKey("Server"));
        assertEquals(null, Parser.getBody(response));
    }


    @Test
    public void GET_Request_Is_Responded_With_Headers_And_Body() {
        String request = "GET /harry_potter HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("text/plain", Parser.getHeaders(response).get("Content-Type"));
        assertEquals("48", Parser.getHeaders(response).get("Content-Length"));
        assertEquals("Here are all my favorite movies:\n" + "- Harry Potter", Parser.getBody(response));
    }

    @Test
    public void HEAD_request_is_responded_with_headers_only_even_if_body_exists() {
        String request = "HEAD /get_with_body HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("text/plain", Parser.getHeaders(response).get("Content-Type"));
        assertEquals("48", Parser.getHeaders(response).get("Content-Length"));
        assertEquals(null, Parser.getBody(response));
    }


    @Test
    public void GET_request_is_responded_with_404_when_resource_invalid() {
        String request = "GET /resource_not_found HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("404", Parser.getStatusCode(response));
    }

    @Test
    public void POST_request_is_responded_with_headers_and_echoed_post_value() {
        String request_line = "POST /echo_body HTTP/1.1" + Stringer.crlf;
        String content_type = "Content-Type: text/plain" + Stringer.crlf;
        String content_length = "Content-Length: 47" + Stringer.crlf;
        String body = Stringer.crlf + "Here are all my favorite movies:\n" + "- Harry Potter";
        String request = request_line + content_type + content_length + body;
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("201", Parser.getStatusCode(response));
        assertEquals("/echo_body", Parser.getHeaders(response).get("Location"));
    }

    @Test
    public void OPTIONS_request_is_responded_with_current_methods_available_On_route() {
        String request = "OPTIONS /method_options HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("OPTIONS, GET, HEAD", Parser.getHeaders(response).get("Allow"));
        assertEquals(null, Parser.getBody(response));
    }

    @Test
    public void valid_route_but_invalid_method_is_responded_with_405_not_allowed() {
        String request = "GET /get_with_body HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("405", Parser.getStatusCode(response));
        assertEquals("OPTIONS, HEAD", Parser.getHeaders(response).get("Allow"));
    }

    @Test
    public void redirected_route_is_responded_with_301_and_new_route() {
        router.setPort(1234);
        String request = "GET /redirect HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("301", Parser.getStatusCode(response));
        assertEquals("http://127.0.0.1:1234/simple_get", Parser.getHeaders(response).get("Location"));
    }


    @Test
    public void navigating_to_static_directory_generates_HTML_directory() {
        String request = "GET /public HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("text/html", Parser.getHeaders(response).get("Content-Type"));
        assertEquals(true, Parser.getBody(response).startsWith("<!DOCTYPE html>"));
    }

    @Test
    public void navigating_to_base_route_redirects_to_static_directory() {
        router.setPort(1234);
        String request = "GET / HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("301", Parser.getStatusCode(response));
        assertEquals("http://127.0.0.1:1234/public", Parser.getHeaders(response).get("Location"));
    }

    @Test
    public void navigating_to_base_path_redirects_to_static_directory() {
        router.setPort(5000);
        String request = "GET / HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("301", Parser.getStatusCode(response));
        assertEquals("http://127.0.0.1:5000/public", Parser.getHeaders(response).get("Location"));
    }

    @Test
    public void navigating_to_directory_file_sends_back_file() {
        String request = "GET /public/Home.html HTTP/1.1";
        String body = "<!DOCTYPE html>";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("text/html", Parser.getHeaders(response).get("Content-Type"));
        assertEquals(body, Parser.getBody(response));
    }

    @Test
    public void navigating_to_directory_image_sends_back_image() {
        String request = "GET /public/water.png HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("image/png", Parser.getHeaders(response).get("Content-Type"));
    }

    @Test
    public void post_request_can_save_resource_under_new_route() {
        String content_type = "Content-Type: text/plain" + Stringer.crlf;
        String content_length = "Content-Length: 16" + Stringer.crlf;
        String body = Stringer.crlf + "Dog Breed: Corgi";

        assertAll("post request",
            () -> {
                String request_line = "POST /dog HTTP/1.1" + Stringer.crlf;
                String request = request_line + content_type + content_length + body;
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("201", Parser.getStatusCode(response));
                assertEquals("/dog/1", Parser.getHeaders(response).get("Location"));
                assertEquals(null, Parser.getBody(response));
            },
            () -> {
                String request = "GET /dog/1 HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("200", Parser.getStatusCode(response));
                assertEquals(body.trim(), Parser.getBody(response));
            }
        );
    }

    @Test
    public void post_request_can_save_html_files_types() {
        String content_type = "Content-Type: text/html" + Stringer.crlf;
        String content_length = "Content-Length: 16" + Stringer.crlf;
        HTMLBuilder html = new HTMLBuilder();
        html.append("<div>Dog Breed: Maine Coon>/div>");
        String body = Stringer.crlf + html.generate();

        assertAll("post request",
            () -> {
                String request_line = "POST /dog HTTP/1.1" + Stringer.crlf;
                String request = request_line + content_type + content_length + body;
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("201", Parser.getStatusCode(response));
                assertEquals("/dog/1", Parser.getHeaders(response).get("Location"));
                assertEquals(null, Parser.getBody(response));
            },
            () -> {
                String request = "GET /dog/1 HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("200", Parser.getStatusCode(response));
                assertEquals("text/html", Parser.getHeaders(response).get("Content-Type"));
                assertEquals(body.trim(), Parser.getBody(response));
            },
            () -> {
                String request = "GET /dog/1.html HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("200", Parser.getStatusCode(response));
                assertEquals("text/html", Parser.getHeaders(response).get("Content-Type"));
                assertEquals(body.trim(), Parser.getBody(response));
            }
        );
    }

    @Test
    public void post_request_with_no_body_does_not_create_a_resource() {
        assertAll("post request",
            () -> {
                String request = "POST /dog HTTP/1.1" + Stringer.crlf;
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("204", Parser.getStatusCode(response));
                assertEquals(null, Parser.getBody(response));
                assertEquals(null, Parser.getHeaders(response).get(Headers.location));
            },
            () -> {
                String request = "GET /dog/1 HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("404", Parser.getStatusCode(response));
            }
        );
    }

    @Test
    public void put_request_can_save_to_resource_route() {
        String content_type = "Content-Type: text/html" + Stringer.crlf;
        String content_length = "Content-Length: 16" + Stringer.crlf;
        HTMLBuilder html = new HTMLBuilder();
        html.append("<div>Cat Breed: Maine Coon>/div>");
        String body = Stringer.crlf + html.generate();

        assertAll("put request",
            () -> {
                String request_line = "PUT /cat/1 HTTP/1.1" + Stringer.crlf;
                String request = request_line + content_type + content_length + body;
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("201", Parser.getStatusCode(response));
                assertEquals(null, Parser.getBody(response));
            },
            () -> {
                String request = "GET /cat/1 HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("200", Parser.getStatusCode(response));
                assertEquals("text/html", Parser.getHeaders(response).get("Content-Type"));
                assertEquals(body.trim(), Parser.getBody(response));
            }
        );
    }

    @Test
    public void put_request_can_overwrite_existing_resource() {
        HTMLBuilder replaced_html = new HTMLBuilder();
        replaced_html.append("Hello kitty!");
        String replaced_body = Stringer.crlf + replaced_html.generate();

        assertAll("put request",
            () -> {
                String content_type = "Content-Type: text/html" + Stringer.crlf;
                String content_length = "Content-Length: 16" + Stringer.crlf;
                HTMLBuilder html = new HTMLBuilder();
                html.append("<div>Cat Breed: Maine Coon>/div>");
                String body = Stringer.crlf + html.generate();
                String request_line = "PUT /cat/1 HTTP/1.1" + Stringer.crlf;
                String user_agent = "User-Agent: HTTPTool/1.0" + Stringer.crlf;
                String request = request_line + user_agent + content_type + content_length + body;
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("201", Parser.getStatusCode(response));
            },
            () -> {
                String request_line = "PUT /cat/1 HTTP/1.1" + Stringer.crlf;
                String replaced_content_type = "Content-Type: text/plain" + Stringer.crlf;
                String replaced_content_length = "Content-Length: 16" + Stringer.crlf;
                String request = request_line + replaced_content_type + replaced_content_length + replaced_body;
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("201", Parser.getStatusCode(response));
            },
            () -> {
                String request = "GET /cat/1 HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("200", Parser.getStatusCode(response));
                assertEquals("text/plain", Parser.getHeaders(response).get("Content-Type"));
                assertEquals(replaced_body.trim(), Parser.getBody(response));
            }
        );
    }

    @Test
    public void delete_request_deletes_the_resource() {
        router.saveResource("/delete_me", "txt", "DELETE ME".getBytes());

        assertAll("delete request",
            () -> {
                String request = "GET /delete_me HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("200", Parser.getStatusCode(response));
                assertEquals("DELETE ME", Parser.getBody(response));
            },
            () -> {
                String request = "DELETE /delete_me HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("204", Parser.getStatusCode(response));
            },
            () -> {
                String request = "GET /delete_me.txt HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("404", Parser.getStatusCode(response));
            },
            () -> {
                String request = "GET /delete_me HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("404", Parser.getStatusCode(response));
            },
            () -> {
                String request = "GET /public/delete_me.txt HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("404", Parser.getStatusCode(response));
            },
            () -> {
                String request = "GET /public/delete_me HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("404", Parser.getStatusCode(response));
            }
        );
    }

    @Test
    public void server_can_handle_invalid_resource_deletion_in_callback() {
        router.delete("/cat/1", (Request request, Response response) -> {
            OperationResult result = router.deleteResource("/cat/1", "json");
            response.forDelete(result);
        });

        String request = "DELETE /cat/1 HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("500", Parser.getStatusCode(response));
    }

    @Test
    public void server_can_handle_request_parameters() {
        String route = "/multiple_parameters?message=Hello%20G%C3%BCnter&author=%40Mrs%20JK%20Rowling";
        String request = "GET " + route + " HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals(true, Parser.getBody(response).contains("author=@Mrs JK Rowling"));
        assertEquals(true, Parser.getBody(response).contains("message=Hello Günter"));
    }

    @Test
    public void server_can_handle_form_data_in_post_request() {
        assertAll("post form request",
            () -> {
                String requestLine = "POST /post_form HTTP/1.1" + Stringer.crlf;
                String content_type = "Content-Type: application/x-www-form-urlencoded" + Stringer.crlf;
                String body = Stringer.crlf + "firstname=%40lilgangwolf+What%27s+Up&lastname=%40u%24to";
                String request = requestLine + content_type + body;
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("201", Parser.getStatusCode(response));
                assertEquals("/post_form/1", Parser.getHeaders(response).get(Headers.location));
                assertEquals(true, Parser.getBody(response).contains("firstname=@lilgangwolf What's Up"));
                assertEquals(true, Parser.getBody(response).contains("lastname=@u$to"));
            },
            () -> {
                String request = "GET /post_form/1 HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("200", Parser.getStatusCode(response));
                assertEquals(true, Parser.getBody(response).contains("firstname=@lilgangwolf What's Up"));
                assertEquals(true, Parser.getBody(response).contains("lastname=@u$to"));
            },
            () -> {
                String requestLine = "POST /post_form HTTP/1.1" + Stringer.crlf;
                String content_type = "Content-Type: application/x-www-form-urlencoded" + Stringer.crlf;
                String body = Stringer.crlf + "firstname=%40lilgangwolf+What%27s+Up&lastname=%40u%24to";
                String request = requestLine + content_type + body;
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("201", Parser.getStatusCode(response));
                assertEquals("/post_form/2", Parser.getHeaders(response).get(Headers.location));
                assertEquals(true, Parser.getBody(response).contains("firstname=@lilgangwolf What's Up"));
                assertEquals(true, Parser.getBody(response).contains("lastname=@u$to"));
            },
            () -> {
                String request = "GET /post_form/2 HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("200", Parser.getStatusCode(response));
                assertEquals(true, Parser.getBody(response).contains("firstname=@lilgangwolf What's Up"));
                assertEquals(true, Parser.getBody(response).contains("lastname=@u$to"));
            },
            () -> {
                String request = "GET /public HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("200", Parser.getStatusCode(response));
                assertEquals(true, Parser.getBody(response).contains("post_form/2"));
            }
        );
    }

    @Test
    public void patch_request_can_apply_partial_modifications_to_a_resource() {
        String jsonString = "{  \n" +
                "   \"firstName\":\"Teddy\",\n" +
                "   \"lastName\":\"Roosevelt\",\n" +
                "   \"city\":\"Los Angeles\"\n" +
                "}";
        router.saveResource("/contacts/1", "json", jsonString);

        assertAll("patch request",
            () -> {
                String request = "GET /contacts/1 HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("200", Parser.getStatusCode(response));
                assertEquals(true, Parser.getBody(response).contains("{  \n" +
                        "   \"firstName\":\"Teddy\",\n" +
                        "   \"lastName\":\"Roosevelt\",\n" +
                        "   \"city\":\"Los Angeles\"\n" +
                        "}"));
            },
            () -> {
                String requestLine = "PATCH /contacts/1 HTTP/1.1" + Stringer.crlf;
                String content_type = "Content-Type: application/json-patch+json" + Stringer.crlf;
                String patchDocument = "[\n" +
                        "  { \"op\": \"replace\", \"path\": \"/firstName\", \"value\": \"Maria\" },\n" +
                        "  { \"op\": \"add\", \"path\": \"/school\", \"value\": \"MIT\" },\n" +
                        "]";
                String body = Stringer.crlf + patchDocument;
                String request = requestLine + content_type + body;
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("204", Parser.getStatusCode(response));
            },
            () -> {
                String request = "GET /contacts/1 HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);
                System.err.println(response);
                assertEquals("200", Parser.getStatusCode(response));
                assertEquals("{\"firstName\":\"Maria\",\"lastName\":\"Roosevelt\",\"city\":\"Los Angeles\",\"school\":\"MIT\"}", Parser.getBody(response));
            }
        );
    }

    @Test
    public void patch_request_can_handle_invalid_patch_document_formatting() {
        String jsonString = "{  \n" +
                "   \"firstName\":\"Teddy\",\n" +
                "   \"lastName\":\"Roosevelt\",\n" +
                "   \"city\":\"Los Angeles\"\n" +
                "}";
        router.saveResource("/contacts/1", "json", jsonString);

        assertAll("patch request",
            () -> {
                String requestLine = "PATCH /contacts/1 HTTP/1.1" + Stringer.crlf;
                String content_type = "Content-Type: application/json-patch+json" + Stringer.crlf;
                String invalidPatchDocument = "{ \"to\": \"replace\", \"path\": \"/firstName\", \"value\": " +
                        "\"Maria\" },\n" +
                        "  { \"op\": \"add\", \"path\": \"/school\", \"value\": \"MIT\" }";
                String body = Stringer.crlf + invalidPatchDocument;
                String request = requestLine + content_type + body;
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("400", Parser.getStatusCode(response));
            },
            () -> {
                String request = "GET /contacts/1 HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);
                System.err.println(response);
                assertEquals("200", Parser.getStatusCode(response));
                assertEquals(jsonString, Parser.getBody(response));
            }
        );
    }

    @Test
    public void patch_request_can_handle_invalid_path_in_patch_request() {
        String jsonString = "{  \n" +
                "   \"firstName\":\"Teddy\",\n" +
                "   \"lastName\":\"Roosevelt\",\n" +
                "   \"city\":\"Los Angeles\"\n" +
                "}";
        router.saveResource("/contacts/1", "json", jsonString);

        assertAll("patch request",
            () -> {
                String requestLine = "PATCH /contacts/1 HTTP/1.1" + Stringer.crlf;
                String content_type = "Content-Type: application/json-patch+json" + Stringer.crlf;
                String invalidPatchDocument = "[{ \"op\": \"replace\", \"path\": \"/non_existing_key\", " +
                        "\"value\":" +
                        " " +
                        "\"Maria\" }]";
                String body = Stringer.crlf + invalidPatchDocument;
                String request = requestLine + content_type + body;
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("409", Parser.getStatusCode(response));
            },
            () -> {
                String request = "GET /contacts/1 HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);
                System.err.println(response);
                assertEquals("200", Parser.getStatusCode(response));
                assertEquals(jsonString, Parser.getBody(response));
            }
        );
    }

    @Test
    public void patch_request_can_handle_non_existent_json_files() {
        String requestLine = "PATCH /contacts/1 HTTP/1.1" + Stringer.crlf;
        String content_type = "Content-Type: application/json-patch+json" + Stringer.crlf;
        String patchDocument = "[{ \"op\": \"replace\", \"path\": \"/key\", " +
                "\"value\":" +
                " " +
                "\"Maria\" }]";
        String body = Stringer.crlf + patchDocument;
        String request = requestLine + content_type + body;
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("500", Parser.getStatusCode(response));
    }

    @Test
    public void server_logs_request_and_response() {
        String request = "GET /harry_potter HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        List<String> files = router.getRepository().readDirectoryContents("./log");
        System.err.println(files.get(0));
        String logFile = new String(router.getRepository().readFile("./log/" + files.get(0)));
        assertEquals(true, logFile.contains(response));
        assertEquals(true, logFile.contains(request));
    }

    @Test
    public void saver_can_apply_parent_changes_to_child_route() {
        String jsonString = "{  \n" +
                "   \"firstName\":\"Teddy\",\n" +
                "   \"lastName\":\"Roosevelt\",\n" +
                "   \"city\":\"Los Angeles\"\n" +
                "}";
        router.saveResource("/contacts/2", "json", jsonString);
        router.patch("/contacts/:id", (Request request, Response response) -> {
            OperationResult updateResult = router.updateJSONResource(request.getRoute(), request.getBody());
            response.forPatch(updateResult);
        });

        String requestLine = "PATCH /contacts/2 HTTP/1.1" + Stringer.crlf;
        String content_type = "Content-Type: application/json-patch+json" + Stringer.crlf;
        String patchDocument = "[\n" +
                "  { \"op\": \"replace\", \"path\": \"/firstName\", \"value\": \"Maria\" },\n" +
                "  { \"op\": \"add\", \"path\": \"/school\", \"value\": \"MIT\" },\n" +
                "]";
        String body = Stringer.crlf + patchDocument;
        String request = requestLine + content_type + body;
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("204", Parser.getStatusCode(response));
    }
}
