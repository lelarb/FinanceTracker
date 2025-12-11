package financeapp;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public class TransactionManager {
    private final MongoCollection<Document>collection;

    public TransactionManager(){
        MongoDatabase db = MongoDBConnection.getDatabase();
        collection = db.getCollection("transactions");
    }

    public void addTransaction(Transaction t){
        collection.insertOne(t.toDocument());
    }

    public ArrayList<Transaction> getAllTransactions(){
        ArrayList<Transaction> list = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().iterator();

        while (cursor.hasNext()){
            Document d = cursor.next();
            list.add(new Transaction(
                    d.getObjectId("_id"),
                    d.getString("Vrsta"),
                    d.getDouble("Iznos"),
                    d.getString("Opis"),
                    d.getString("Kategorija")
            ));
        }
        return list;
    }

public double getTotalIncome(){
        double total = 0;
        for(Transaction t : getAllTransactions()){
            if("Prihod".equals(t.getType())){
                total += t.getAmount();
            }
        }
        return total;
}

public double getTotalExpense(){
        double total = 0;
        for (Transaction t : getAllTransactions()){
            if ("Rashod".equals(t.getType())){
                total += t.getAmount();
            }
        }
        return total;
}

public void updateTransaction(Transaction t){
        collection.updateOne(
                new Document("_id", t.getId()),
                new Document("$set", new Document("Vrsta", t.getType())
                        .append("Iznos", t.getAmount())
                        .append("Opis", t.getDescription())
                        .append("Kategorija", t.getCategory()))
        );
}

public void deleteTransaction(ObjectId id){
        collection.deleteOne(new Document("_id", id));
}
}
