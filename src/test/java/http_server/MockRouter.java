package http_server;

public class MockRouter {
    Router router;

    public MockRouter() {
        this.router = new Router();
    }

    public Router getRouter() {
        initializeRoutes();
        System.err.println(router);
        return router;
    }

    private void initializeRoutes() {
        router.get("/simple_get", (Request request, Response response) -> {});

        router.head("/simple_get", (Request request, Response response) -> {});

        router.get("/get_with_body", (Request request, Response response) -> {
            response.body("Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n");
        });

        router.head("/get_with_body", (Request request, Response response) -> {
            response.body("Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n");
        });

        router.post("/echo_body", (Request request, Response response) -> {});
    }
}
