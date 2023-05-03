package server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import util.DBUtil;

public class HttpVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> fut) {
        vertx.createHttpServer()
                .requestHandler(r -> handler(r))
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }

    @Override
    public void stop(){
        DBUtil.getInstance().disconnectCB();
    }

    private void handler(HttpServerRequest req){
            req.bodyHandler(event ->{
                JsonObject body=new JsonObject(event.toString());
                if(!"/register".equals(req.path())&&!"/deregister".equals(req.path())){
                    req.response().setStatusCode(404).end("not found");
                }
                else if(body.getString("user-id")==null){
                    req.response().setStatusCode(500).end("invalid payload");
                }

                if("/register".equals(req.path())){
                    JsonObject json=DBUtil.getInstance().get(body.getString("user-id"));
                    System.out.println(body.getString("user-id"));
                    System.out.println(json);
                    if(json!=null)
                        req.response().setStatusCode(500).end("user already exists");
                    else
                    {
                        DBUtil.getInstance().set(body.getString("user-id"),new JsonObject()
                                .put("user-id",body.getString("user-id"))
                                .put("timestamp",""+System.currentTimeMillis()));
                        req.response().end(new JsonObject().put("result","user added successfully").toString());
                    }
                }
                else if("/deregister".equals(req.path())){
                    JsonObject json=DBUtil.getInstance().get(body.getString("user-id"));
                    if(json==null)
                        req.response().setStatusCode(500).end("user does not exist");
                    else
                    {
                        DBUtil.getInstance().delete(body.getString("user-id"));
                        req.response().end(new JsonObject().put("result","user-id : "+body.getString("user-id")+" deleted from records").toString());
                    }
                }
            });
        }
}
