import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for collecting comments from youtube using the YouTube API
 *
 * @author Kostas Platis
 * @Date 21/3/2016
 *
 */
public class YTCommentsCollector {

    private static YouTube youtube;


    public ArrayList<String> collectComments() {

        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");
        ArrayList<String> videoIDs = fetchVideoIds();   //fetching the videoIDs
        ArrayList<String> allComments = new ArrayList<String>();

        try {
            //getting the credentials from Auth Class
            Credential credential = Auth.authorize(scopes, "commentthreads");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName("fix_the_fixing").build();

            for (String videoID : videoIDs) {     //for every video ID
                System.out.println("\n===============\n" + videoID + "\n==============\n");
                //gets the comment thread list response
                CommentThreadListResponse commentThreadListResponse = youtube.commentThreads().list("snippet").setVideoId(videoID).setTextFormat("plainText").setMaxResults(100L).execute();
                List<CommentThread> videoComments = commentThreadListResponse.getItems();   //gets the comments from the list
                System.out.println("Number of comments: " + videoComments.size());

                for (CommentThread videoComment : videoComments) {    //for every comment

                    CommentSnippet snippet = videoComment.getSnippet().getTopLevelComment().getSnippet();
                    allComments.add(snippet.getTextDisplay());     //adds the comment
                    System.out.println("  - Author: " + snippet.getAuthorDisplayName());
                    System.out.println("  - Comment: " + snippet.getTextDisplay());
                    System.out.println("\n-------------------------------------------------------------\n");

                    /*      Comment's replies part

                    // Will use this thread as parent to new reply.
                    String parentId = videoComment.getId();
                    System.out.println("parentId = "+parentId);

                    // Call the YouTube Data API's comments.list method to retrieve
                    // existing comment
                    // replies.
                    CommentListResponse commentsListResponse = youtube.comments().list("snippet").setParentId(parentId).setTextFormat("plainText").execute();
                    List<Comment> commentReplies = commentsListResponse.getItems();

                    if (commentReplies.isEmpty()) {
                        System.out.println("This comment does not have replies...");
                    } else {
                        // Print information from the API response.
                        System.out.println("\n================== Returned Comment Replies ==================\n");
                        for (Comment commentReply : commentReplies) {
                            CommentSnippet commentSnippet = commentReply.getSnippet();
                            System.out.println("  - Author: " + commentSnippet.getAuthorDisplayName());
                            System.out.println("  - Comment: " + commentSnippet.getTextDisplay());
                            allComments.add(commentSnippet.getTextDisplay());
                            System.out.println("\n-------------------------------------------------------------\n");
                        }


                    }

                    */


                }

        }
        } catch (GoogleJsonResponseException e){
            e.getDetails();
        } catch(IOException e){
            e.printStackTrace();
        }

        return allComments;

    }


        /**
     * Method responsible for fetching video ids from the videoIDs.txt file
     *
     * @return a list with the
     */
    private ArrayList<String> fetchVideoIds(){

        String filename = "./resources/VideoIDs.txt";

        ArrayList<String> videoIDs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {    //for every line of the file
            String line;
            while ((line = br.readLine()) != null) {
                videoIDs.add(line);     //adds the videoID in the list
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return videoIDs;

    }

}
