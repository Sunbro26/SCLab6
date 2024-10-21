/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
	
	public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
	    // Create a map to store the social network graph where keys are authors and values are sets of mentioned users.
	    Map<String, Set<String>> followsGraph = new HashMap<>();
	    
	    // Compile a regex pattern to match mentions in the form of "@username".
	    Pattern mentionPattern = Pattern.compile("@(\\w+)"); // \w matches word characters (A-Z, a-z, 0-9, _)

	    // Iterate through each tweet in the list.
	    for (Tweet tweet : tweets) {
	        // Get the author of the tweet and convert it to lowercase to ensure case-insensitivity.
	        String author = tweet.getAuthor().toLowerCase();
	        
	        // Create a matcher to find mentions within the tweet text.
	        Matcher matcher = mentionPattern.matcher(tweet.getText());
	        
	        // Find all the mentions in the tweet.
	        while (matcher.find()) {
	            // Extract the mentioned username (without the '@' symbol) and convert it to lowercase.
	            String mentionedUser = matcher.group(1).toLowerCase();
	            
	            // Ensure that a user doesn't follow themselves by checking if the author is not the mentioned user.
	            if (!mentionedUser.equals(author)) {
	                // If the author is not already in the map, add them with an empty set.
	                followsGraph.putIfAbsent(author, new HashSet<>());
	                
	                // Add the mentioned user to the set of users that the author follows.
	                followsGraph.get(author).add(mentionedUser);
	            }
	        }
	    }
	    
	    // Return the completed follows graph.
	    return followsGraph;
	}


    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
	
	public static List<String> influencers(Map<String, Set<String>> followsGraph) {
	    // Map to store the follower count for each user. 
	    // Key: User's username, Value: Number of followers the user has.
	    Map<String, Integer> followerCount = new HashMap<>();
	    
	    // Iterate through each set of followed users in the social network (followsGraph).
	    // This represents all the users that people are following.
	    for (Set<String> followed : followsGraph.values()) {
	        // For each user being followed, increase their follower count.
	        for (String followedUser : followed) {
	            // If the user has followers already, increment their count.
	            // Otherwise, set their initial count to 1.
	            followerCount.put(followedUser, followerCount.getOrDefault(followedUser, 0) + 1);
	        }
	    }
	    
	    // Create a list of all users (the keys in followerCount map).
	    List<String> influencers = new ArrayList<>(followerCount.keySet());
	    
	    // Sort the list of users by follower count in descending order.
	    // Comparator: For each pair of users 'a' and 'b', compare their follower counts.
	    influencers.sort((a, b) -> followerCount.get(b) - followerCount.get(a));
	    
	    // Return the sorted list of users (influencers), from highest to lowest follower count.
	    return influencers;
	}


}
