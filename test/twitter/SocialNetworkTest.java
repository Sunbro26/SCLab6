/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */
    
    @Test
    public void testEmptyListOfTweets() {
        List<Tweet> tweets = new ArrayList<>();
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("Expected an empty graph for an empty list of tweets", followsGraph.isEmpty());
    }
    
    @Test
    public void testTweetsWithoutMentions() {
        Tweet tweet = new Tweet(1, "user1", "Just a random tweet with no mentions.", Instant.now());
        List<Tweet> tweets = Arrays.asList(tweet);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("Expected an empty graph for tweets without mentions", followsGraph.isEmpty());
    }

    @Test
    public void testSingleMention() {
        Tweet tweet = new Tweet(1, "user1", "Hello @user2", Instant.now());
        List<Tweet> tweets = Arrays.asList(tweet);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        
        assertTrue("Expected user1 to follow user2", 
                    followsGraph.containsKey("user1") && followsGraph.get("user1").contains("user2"));
    }

    @Test
    public void testMultipleMentions() {
        Tweet tweet = new Tweet(1, "user1", "Hello @user2 and @user3", Instant.now());
        List<Tweet> tweets = Arrays.asList(tweet);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("Expected user1 to follow user2 and user3", 
                    followsGraph.containsKey("user1") && 
                    followsGraph.get("user1").containsAll(Arrays.asList("user2", "user3")));
    }

    @Test
    public void testMultipleTweetsFromOneUser() {
        Tweet tweet1 = new Tweet(1, "user1", "Hello @user2", Instant.now());
        Tweet tweet2 = new Tweet(2, "user1", "Hi again @user3", Instant.now());
        List<Tweet> tweets = Arrays.asList(tweet1, tweet2);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("Expected user1 to follow user2 and user3", 
                    followsGraph.containsKey("user1") && 
                    followsGraph.get("user1").containsAll(Arrays.asList("user2", "user3")));
    }

    @Test
    public void testEmptyGraphForInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertTrue("Expected no influencers for an empty graph", influencers.isEmpty());
    }

    @Test
    public void testSingleUserWithoutFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("user1", new HashSet<>());
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue("Expected no influencers when user has no followers", influencers.isEmpty());
    }

    @Test
    public void testSingleInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("user1", new HashSet<>(Arrays.asList("user2")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("Expected user2 to be the only influencer", Arrays.asList("user2"), influencers);
    }

    @Test
    public void testMultipleInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("user1", new HashSet<>(Arrays.asList("user2", "user3")));
        followsGraph.put("user4", new HashSet<>(Arrays.asList("user2")));

        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("Expected user2 to have the most followers", "user2", influencers.get(0));
        assertEquals("Expected user3 to have the second most followers", "user3", influencers.get(1));
    }

    @Test
    public void testTiedInfluence() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("user1", new HashSet<>(Arrays.asList("user2")));
        followsGraph.put("user3", new HashSet<>(Arrays.asList("user4")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        // Since user2 and user4 both have 1 follower, they should appear in some order
        assertTrue("Expected user2 and user4 to be tied in influence",
                    influencers.containsAll(Arrays.asList("user2", "user4")));
    }

}
