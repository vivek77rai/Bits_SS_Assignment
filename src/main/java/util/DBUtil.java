package util;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import io.vertx.core.json.JsonObject;

import java.time.Duration;

public class DBUtil {
    private DBUtil(){}
    private static final DBUtil INSTANCE=new DBUtil();
    public static DBUtil getInstance(){
        return INSTANCE;
    }
    private Cluster cluster;
    private String bucketName = "user";
    private String username = "Administrator";
    private String password = "password";
    private Bucket bucket = null;
    public void initCouchbase(){
        try {
            cluster = Cluster.connect("localhost", username, password);
            cluster.waitUntilReady(Duration.ofSeconds(10));
            bucket = cluster.bucket(bucketName);
            bucket.waitUntilReady(Duration.ofSeconds(10));
            Collection collection = bucket.defaultCollection();
        } catch (Exception e ) {
            System.out.println("Ooops!... something went wrong");
            e.printStackTrace();
            throw e;
        }
    }

    public void set(String key, JsonObject value){
        bucket.defaultCollection().upsert(key, value.toString());
    }
    public JsonObject get(String key){
        try{
            GetResult getResult=bucket.defaultCollection().get(key);
            return new JsonObject(bucket.defaultCollection().get(key).contentAs(String.class));
        }
        catch (Exception e){
            System.out.println("Exception occured");
            e.printStackTrace();
            return null;
        }
    }
    public void delete(String key){
        bucket.defaultCollection().remove(key);
    }

    public void disconnectCB(){
        if(cluster != null) {
            cluster.disconnect();
        }
    }
}
