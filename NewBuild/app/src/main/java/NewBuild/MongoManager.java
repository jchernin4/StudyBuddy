package NewBuild;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoManager {
    private static MongoManager instance;
    private final MongoDatabase db;

    public static MongoManager getInstance() {
        if (instance == null) {
            instance = new MongoManager();
        }

        return instance;
    }

    private MongoManager() {
        MongoClient mongo = new MongoClient(new MongoClientURI(Settings.MONGO_URI));
        db = mongo.getDatabase(Settings.MONGO_DATABASE_NAME);
    }

    public void registerUser(String firstname, String lastname, String email, String username, String password) {
        MongoCollection<Document> usersCollection = db.getCollection(Settings.MONGO_USERS_COLLECTION_NAME);

        String hashed = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        Document userDoc = new Document("firstname", firstname)
                .append("lastname", lastname)
                .append("email", email)
                .append("username", username)
                .append("password", hashed);
        usersCollection.insertOne(userDoc);
    }

    public boolean verifyPassword(String email, String password) {
        MongoCollection<Document> usersCollection = db.getCollection(Settings.MONGO_USERS_COLLECTION_NAME);
        Document userDoc = usersCollection.find(Filters.eq("email", email)).first();

        return BCrypt.verifyer().verify(password.toCharArray(), userDoc.getString("password")).verified;
    }

    public void createGroup(String name) {
        MongoCollection<Document> groupsCollection = db.getCollection(Settings.MONGO_GROUPS_COLLECTION_NAME);
        Document groupDoc = new Document("name", name).append("messages", new ArrayList<Document>());
        groupsCollection.insertOne(groupDoc);
    }

    public Document getGroup(String groupName) {
        MongoCollection<Document> groupsCollection = db.getCollection(Settings.MONGO_GROUPS_COLLECTION_NAME);

        return groupsCollection.find(Filters.eq("name", groupName)).first();
    }

    public FindIterable<Document> getGroupList() {
        MongoCollection<Document> groupsCollection = db.getCollection(Settings.MONGO_GROUPS_COLLECTION_NAME);

        return groupsCollection.find();
    }

    public void addMessage(String groupname, String message, String username) {
        MongoCollection<Document> groupsCollection = db.getCollection(Settings.MONGO_GROUPS_COLLECTION_NAME);

        Document groupDoc = groupsCollection.find(Filters.eq("name", groupname)).first();
        List<Document> messages = groupDoc.getList("messages", Document.class);

        messages.add(new Document("username", username).append("message", message));

        Document updateDoc = new Document("$set", new Document("messages", messages));
        groupsCollection.updateOne(Filters.eq("name", groupname), updateDoc);
    }
}
