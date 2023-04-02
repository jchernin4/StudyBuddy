package NewBuild;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.bson.Document;
import org.eclipse.jetty.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HttpServer {
    private static HttpServer instance;
    public MongoManager mongoManager;
    private final Javalin app;

    public static HttpServer getInstance() {
        if (instance == null) {
            instance = new HttpServer();
        }
        return instance;
    }

    private HttpServer() {
        mongoManager = MongoManager.getInstance();
        app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(CorsPluginConfig::anyHost);
            });
        }).start(Settings.HTTP_SERVER_PORT);
    }

    public void start() {
        app.before("*", ctx -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss a");
            LocalDateTime now = LocalDateTime.now(ZoneId.of("US/Eastern"));
            System.out.println("[LOG] " + dtf.format(now) + " | " + ctx.method() + " request to " + ctx.fullUrl()
                    + " from userAgent: " + ctx.userAgent() + " and IP: " + ctx.ip());
        });

        app.post("/signup", ctx -> {
            Document doc = Document.parse(ctx.body());
            System.out.println(doc.toJson());
            MongoManager.getInstance().registerUser(doc.getString("firstname"), doc.getString("lastname"), doc.getString("email"), doc.getString("username"), doc.getString("password"));
        });

        app.post("/login", ctx -> {
            Document doc = Document.parse(ctx.body());

            boolean verified = MongoManager.getInstance().verifyPassword(doc.getString("email"), doc.getString("password"));

            // The security here is terrible, I know, but we're just trying to get a working site for the competition
            if (verified) {
                ctx.status(HttpStatus.OK_200);
                return;
            }

            ctx.status(HttpStatus.FORBIDDEN_403);
        });

        app.post("/groups/create", ctx -> {
            Document doc = Document.parse(ctx.body());
            System.out.println(doc.toJson());
            MongoManager.getInstance().createGroup(doc.getString("name"));
        });

        app.get("/groups/list", ctx -> {
            FindIterable<Document> list = MongoManager.getInstance().getGroupList();
            ArrayList<String> names = new ArrayList<String>();
            MongoCursor<Document> cursor = list.cursor();

            while (cursor.hasNext()) {
                names.add(cursor.next().getString("name"));
            }

            Document listDoc = new Document("list", names);
            ctx.result(listDoc.toJson());
        });

        app.get("/groups/{name}/messages", ctx -> {
            String groupName = ctx.pathParam("name");
            Document groupDoc = MongoManager.getInstance().getGroup(groupName);

            ctx.result(groupDoc.toJson());
        });

        app.post("/groups/{name}/messages", ctx -> {
            Document doc = Document.parse(ctx.body());
            String groupName = ctx.pathParam("name");

            MongoManager.getInstance().addMessage(groupName, doc.getString("message"), doc.getString("username"));

            ctx.status(HttpStatus.CREATED_201);
        });
    }
}
