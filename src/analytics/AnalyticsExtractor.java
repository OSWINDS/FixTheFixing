package analytics;

import mongo.MongoConnector;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.StringJoiner;

/**
 * Created by sifantid on 5/5/2016.
 */
public class AnalyticsExtractor {
    private static MongoConnector mc;

    /**
     * Class constructor
     * @param collectionName The name of the specific collection to be accessed
     */
    public AnalyticsExtractor(String collectionName) {
        mc = new MongoConnector("localhost", 27017, collectionName);
    }

    public void getHashtagFrequencies() {
        writeToTagcloudFile(calculateFrequencies("hashtags","twitter"),"hashtag_frequencies.txt");
    }

    public void getTwitterMentionFrequencies() {
        writeToTagcloudFile(calculateFrequencies("mentions","twitter"),"mentions_frequencies.txt");
    }

    public void getLocationFrequencies() {
        writeToTagcloudFile(calculateFrequenciesSimple("geo","twitter"),"location_frequencies.txt");
        writeToTagcloudFile(calculateFrequenciesSimple("location","youtube"),"location_frequencies_youtube.txt");
    }

    private HashMap<String,Integer> calculateFrequenciesSimple(String field, String medium) {
        HashMap<String,Integer> frequencies = new HashMap<>();
        HashMap<ObjectId,JSONObject> tweets_comments;
        if(medium.equals("twitter")) {
            tweets_comments = mc.getTweets();
        } else {
            tweets_comments = mc.getComments();
        }

        for(JSONObject tweet_comment : tweets_comments.values()) {
            String key = tweet_comment.getString(field);
            if(!key.isEmpty()) {
                frequencies.putIfAbsent(key, 0);
                frequencies.computeIfPresent(key, (k, v) -> v + 1);
            }
        }
        return frequencies;
    }

    /**
     * Calculates the frequencies of given field for a specific collection of tweets
     * @param field The field we want to calculate the frequency of
     * @return Pairs of field values and frequencies
     */
    private HashMap<String,Integer> calculateFrequencies(String field, String medium) {
        HashMap<String,Integer> frequencies = new HashMap<>();
        HashMap<ObjectId,JSONObject> tweets_comments;
        if(medium.equals("twitter")) {
            tweets_comments = mc.getTweets();
        } else {
            tweets_comments = mc.getComments();
        }

        for(JSONObject tweet_comment : tweets_comments.values()) {
            String[] fieldValues = tweet_comment.getString(field).split(" ");
            if(!fieldValues[0].isEmpty()) {
                for (String fieldValue : fieldValues) {
                    frequencies.putIfAbsent(fieldValue, 0);
                    frequencies.computeIfPresent(fieldValue, (k, v) -> v + 1);
                }
            }
        }
        return frequencies;
    }

    private void writeToTagcloudFile(HashMap<String,Integer> map, String filename) {
        String path = "out\\" + filename;
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path), "utf-8"))) {
            for(String s : map.keySet()) {
                String key = s.replace(", ",";");
                writer.write(key + " , " + map.get(s));
                writer.write(System.lineSeparator());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test function
     */
    public void analyze() {
        HashMap<String,Integer> frequencies = calculateFrequencies("hashtags","twitter");
        for(String hashtag : frequencies.keySet()) {
            System.out.println("Hashtag: " + hashtag + ": " + frequencies.get(hashtag));
        }
    }
}