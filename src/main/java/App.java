import io.vertx.core.Vertx;
import server.HttpVerticle;
import util.DBUtil;

public class App {
    public static void main(String[] args) {
        DBUtil.getInstance().initCouchbase();
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(HttpVerticle.class.getName());
    }
}
