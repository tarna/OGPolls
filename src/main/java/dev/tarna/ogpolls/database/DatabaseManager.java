package dev.tarna.ogpolls.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bson.UuidRepresentation;

@Getter
public class DatabaseManager {
    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> pollsCollection;

    public DatabaseManager(String uri, String databaseName) {
        this.client = MongoClients.create(
            MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(
                    new ConnectionString(uri)
                ).build()
        );
        this.database = client.getDatabase(databaseName);
        this.pollsCollection = database.getCollection("polls");
    }
}
