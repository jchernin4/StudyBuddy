package StudyBuddy;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

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

    public void registerUser(String email) {
        MongoCollection<Document> usersCollection = db.getCollection(Settings.MONGO_USERS_COLLECTION_NAME);
        Document userDoc = new Document("email", email);
        usersCollection.insertOne(userDoc);
    }
}
