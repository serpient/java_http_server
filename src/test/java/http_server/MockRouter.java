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
        router.get("/simple_get", (String req, String res) -> "");

        router.head("/simple_get", (String req, String res) -> "");

        router.get("/get_with_body", (String req, String res) -> {
            return "Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n";
        });

        router.head("/get_with_body", (String req, String res) -> {
            return "Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n";
        });

        router.post("/echo_body", (String req, String res) -> "");
    }
}
