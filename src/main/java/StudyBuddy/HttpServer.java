package StudyBuddy;

import io.javalin.Javalin;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
            ctx.header("Access-Control-Allow-Origin", "*");

            Document doc = Document.parse(ctx.body());

            System.out.println(doc.toJson());

            MongoManager.getInstance().registerUser(doc.getString("email"));
        });
    }
}
