package in.webkorps.main.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.webkorps.main.config.TemplateAndResolverConfig;
import in.webkorps.main.dto.*;
import in.webkorps.main.entity.Following;
import in.webkorps.main.entity.PostComments;
import in.webkorps.main.entity.Posts;
import in.webkorps.main.entity.User;
import in.webkorps.main.service.*;
import org.hibernate.Internal;
import org.hibernate.engine.transaction.jta.platform.internal.SynchronizationRegistryBasedSynchronizationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RestPostController {

    @Autowired
    private PostServices postServices;

    @Autowired
    private LikesServices likesServices;

    @Autowired
    private UserServices userServices;

    @Autowired
    private CommentsServices commentsServices;

    @Autowired
    private FollowingServices followingServices;

    Map<String, Object> responseMap = new HashMap<>();

    @PostMapping(value = "/addPost", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> AddPost2(@RequestPart("post") Posts posts, @RequestPart("postImage") MultipartFile file, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) throws IOException {
        System.out.println(posts.getPostName());
        Integer userId = posts.getUser().getUserId();
        Posts post = postServices.savePosts(posts.getPostName(), file, userId);
        responseMap.clear();
        if (acceptHeader.contains("application/json")) {
            responseMap.put("Message", "Post Added Successfully.");
            responseMap.put("Post Id", post.getPostId());
            responseMap.put("User Id", userId);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            responseMap.put("postList", postServices.getPostsList(userId));
            responseMap.put("user_id", userId);
            responseMap.put("alert", "Post Added Successfully.");
            responseMap.put("name", userServices.getById(userId).getFirstname() + " " + userServices.getById(userId).getLastname());
            responseMap.put("followerRequestCount", followingServices.getFollowerCount(userId));
            responseMap.put("followingRequestCount", followingServices.getFollowingCount((userId)));
            responseMap.put("RequestCount", followingServices.getRequestCount(userId));
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("home", responseMap));
        }
    }

    @GetMapping("/getLike/{postId}")
    public ResponseEntity<List<WrapperLike>> getLikesByPostId(@PathVariable Integer postId) {
        List<WrapperLike> likes = likesServices.getLike(postId);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @GetMapping("/getPosts/{userId}")
    public ResponseEntity<List<Posts>> getPostsByUserId(@PathVariable Integer userId) {
        List<Posts> postsList = postServices.getPostsList(userId);
        return new ResponseEntity<>(postsList, HttpStatus.OK);
    }

    @PostMapping(value = "/getMyPosts", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getMyPosts(@RequestParam Integer userId, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        User user = userServices.getById(userId);
        responseMap.put("user", user);
        responseMap.put("name", user.getFirstname() + " " + user.getLastname());
        responseMap.put("user_id", userId);
        responseMap.put("userList", followingServices.getFollowing1(userId));
        responseMap.put("followerRequestCount", followingServices.getFollowerCount(userId));
        responseMap.put("followingRequestCount", followingServices.getFollowingCount((userId)));
        responseMap.put("RequestCount", followingServices.getRequestCount(userId));
        if (acceptHeader.contains("application/json")) {
            responseMap.clear();
            responseMap.put("alert", "Your Posts Has Been Fetched Succesfully.");
            responseMap.put("postList", postServices.getFilterPostsList(postServices.getMyPostsList(userId)));
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            responseMap.put("postList", postServices.getMyPostsList(userId));
            responseMap.put("alert", "Your Posts Has Been Fetched Succesfully.");
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("myPost", responseMap));
        }
    }

    @PostMapping(value = "/getPostsById", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPostsById(@RequestPart("following") Following following, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        User user1 = userServices.getById(following.getUserId());
        User follower = userServices.getById(following.getFollowerId());
        responseMap.put("user", follower);
        responseMap.put("name", user1.getFirstname() + " " + user1.getLastname());
        responseMap.put("user_id", user1.getUserId());
        if (acceptHeader.contains("application/json")) {
            responseMap.clear();
            responseMap.put("alert", "Your Posts Has Been Fetched Succesfully.");
            responseMap.put("postList", postServices.getFilterPostsList(postServices.getMyPostsList(follower.getUserId())));
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            responseMap.put("postList", postServices.getMyPostsList(follower.getUserId()));
            responseMap.put("alert", "User Has Been Fetched Succesfully.");
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("userProfilePost", responseMap));
        }
    }


    @GetMapping("/getUsersPost/{userId}")
    public ResponseEntity<List<WrapperPosts2>> getPostsByUserId2(@PathVariable Integer userId) {
        return new ResponseEntity<>(postServices.getFilterPostsList(postServices.getPostsList(userId)), HttpStatus.OK);
    }

    @PostMapping(value = "/addComment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> setComment(@RequestBody Map<String, Object>  responseBody, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        PostComments postComments = new ObjectMapper().convertValue(responseBody.get("postComments"), PostComments.class);
        String redirectPage = (String) responseBody.get("redirectPage");
        Integer postId = postComments.getPost().getPostId();
        Integer userId = postComments.getUserId();
        String comment = postComments.getComment();
        PostComments comments = commentsServices.setComment(comment, userId, postId);
        responseMap.clear();
        if (acceptHeader.contains("application/json")) {
            responseMap.put("Message", "Comment Added Successfully.");
            responseMap.put("UserId", comments.getUserId());
            responseMap.put("Post Id", postId);
            responseMap.put("Comment", comments.getComment());
            responseMap.put("User Name", comments.getFirstName());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            if (responseBody.get("followerId") == null) {
                responseMap.put("postList", postServices.getPostsList(userId));
            } else {
                Integer followerId = Integer.parseInt((String)responseBody.get("followerId"));
                responseMap.put("user", userServices.getById(followerId));
                responseMap.put("postList", postServices.getMyPostsList(followerId));
            }
            responseMap.put("user_id", userId);
            responseMap.put("userList", followingServices.getFollowing1(userId));
            responseMap.put("alert", "Comment Added Successfully.");
            responseMap.put("name", userServices.getById(userId).getFirstname() + " " + userServices.getById(userId).getLastname());
            responseMap.put("followerRequestCount", followingServices.getFollowerCount(userId));
            responseMap.put("followingRequestCount", followingServices.getFollowingCount((userId)));
            responseMap.put("RequestCount", followingServices.getRequestCount(userId));
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf(redirectPage, responseMap));
        }
    }

    @PostMapping(value = "/deleteComment",consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> deleteComment(@RequestBody Map<String, Object>  responseBody, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        PostComments postComments = new ObjectMapper().convertValue(responseBody.get("postComments"), PostComments.class);
        String redirectPage = (String) responseBody.get("redirectPage");
        Integer id = postComments.getId();
        Integer userId = postComments.getUserId();
        Integer postId = postComments.getPost().getPostId();
        String response = commentsServices.deleteComment(id, postId, userId);
        responseMap.clear();
        if (acceptHeader.contains("application/json")) {
            responseMap.put("Message", response);
            responseMap.put("UserId", userId);
            responseMap.put("Post Id", postId);
            responseMap.put("User Name", userServices.getById(userId).getFirstname());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            if (responseBody.get("followerId") == null) {
                responseMap.put("postList", postServices.getPostsList(userId));
            } else {
                Integer followerId = Integer.parseInt((String)responseBody.get("followerId"));
                responseMap.put("user", userServices.getById(followerId));
                responseMap.put("postList", postServices.getMyPostsList(followerId));
            }
            responseMap.put("user_id", userId);
            responseMap.put("alert", response);
            responseMap.put("userList", followingServices.getFollowing1(userId));
            responseMap.put("name", userServices.getById(userId).getFirstname() + " " + userServices.getById(userId).getLastname());
            responseMap.put("followerRequestCount", followingServices.getFollowerCount(userId));
            responseMap.put("followingRequestCount", followingServices.getFollowingCount((userId)));
            responseMap.put("RequestCount", followingServices.getRequestCount(userId));
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf(redirectPage, responseMap));
        }
    }

    @PostMapping(value = "/editComment",consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> editComment(@RequestBody Map<String, Object>  responseBody, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        PostComments postComments = new ObjectMapper().convertValue(responseBody.get("postComments"), PostComments.class);
        String redirectPage = (String) responseBody.get("redirectPage");
        Integer userId = postComments.getUserId();
        Integer postId = postComments.getPost().getPostId();
        Integer commentId = postComments.getId();
        String comment = postComments.getComment();
        Map<String, Object> response = commentsServices.editComment(userId, postId, commentId, comment);
        responseMap.clear();
        if (response.get("mesg").toString().equalsIgnoreCase("Comment Fetched.")) {
            if (acceptHeader.contains("application/json")) {
                responseMap.put("Message", response.get("mesg"));
                responseMap.put("UserId", userId);
                responseMap.put("Post Id", postId);
                responseMap.put("comment", response.get("comment"));
                responseMap.put("User Name", userServices.getById(userId).getFirstname());
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
            } else {
                if (responseBody.get("followerId") == null) {
                    responseMap.put("postList", postServices.getPostsList(userId));
                } else {
                    Integer followerId = Integer.parseInt((String)responseBody.get("followerId"));
                    responseMap.put("user", userServices.getById(followerId));
                    responseMap.put("postList", postServices.getMyPostsList(followerId));
                }
                responseMap.put("user_id", userId);
                responseMap.put("alert", response.get("mesg"));
                responseMap.put("userList", followingServices.getFollowing1(userId));
                responseMap.put("name", userServices.getById(userId).getFirstname() + " " + userServices.getById(userId).getLastname());
                responseMap.put("followerRequestCount", followingServices.getFollowerCount(userId));
                responseMap.put("followingRequestCount", followingServices.getFollowingCount((userId)));
                responseMap.put("RequestCount", followingServices.getRequestCount(userId));
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf(redirectPage, responseMap));
            }
        } else {
            if (acceptHeader.contains("application/json")) {
                responseMap.put("Message", response.get("mesg"));
                responseMap.put("UserId", userId);
                responseMap.put("Post Id", postId);
                responseMap.put("User Name", userServices.getById(userId).getFirstname());
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
            } else {
                if (responseBody.get("followerId") == null) {
                    responseMap.put("postList", postServices.getPostsList(userId));
                } else {
                    Integer followerId = Integer.parseInt((String)responseBody.get("followerId"));
                    responseMap.put("user", userServices.getById(followerId));
                    responseMap.put("postList", postServices.getMyPostsList(followerId));
                }
                responseMap.put("user_id", userId);
                responseMap.put("alert", response.get("mesg"));
                responseMap.put("userList", followingServices.getFollowing1(userId));
                responseMap.put("name", userServices.getById(userId).getFirstname() + " " + userServices.getById(userId).getLastname());
                responseMap.put("followerRequestCount", followingServices.getFollowerCount(userId));
                responseMap.put("followingRequestCount", followingServices.getFollowingCount((userId)));
                responseMap.put("RequestCount", followingServices.getRequestCount(userId));
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf(redirectPage, responseMap));
            }
        }
    }

    @PostMapping(value = "/like", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> likeUnlikePost(@RequestBody Map<String, Object> requestBody, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        Posts posts = new ObjectMapper().convertValue(requestBody.get("posts"), Posts.class);
        String redirectPage = (String) requestBody.get("redirectPage");
        System.out.println(posts.getPostId() + " " + posts.getUser().getUserId() + " " + redirectPage + " " + requestBody.get("followerId"));
        Integer userId = posts.getUser().getUserId();
        Integer postId = posts.getPostId();
        boolean isLiked = likesServices.clickLike(userId, postId);
        responseMap.put("user_id", userId);
        responseMap.put("name", userServices.getById(userId).getFirstname() + " " + userServices.getById(userId).getLastname());
        responseMap.put("followerRequestCount", followingServices.getFollowerCount(userId));
        responseMap.put("followingRequestCount", followingServices.getFollowingCount((userId)));
        responseMap.put("RequestCount", followingServices.getRequestCount(userId));
        if (requestBody.get("followerId") == null) {
            responseMap.put("postList", postServices.getPostsList(userId));
        } else {
            Integer followerId = (Integer) requestBody.get("followerId");
            responseMap.put("user", userServices.getById(followerId));
            responseMap.put("postList", postServices.getMyPostsList(followerId));
        }
        if (acceptHeader.contains("application/json")) {
            responseMap.clear();
            responseMap.put("postId", postId);
            responseMap.put("user_id", userId);
            if (isLiked) {
                responseMap.put("alert", "Post Liked");
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
            } else {
                responseMap.put("alert", "Post Unliked");
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
            }
        } else {
            if (isLiked) {
                responseMap.put("alert", "Post Liked");
                responseMap.put("userList", followingServices.getFollowing1(userId));
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf(redirectPage, responseMap));
            } else {
                responseMap.put("alert", "Post Unliked");
                responseMap.put("userList", followingServices.getFollowing1(userId));
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf(redirectPage, responseMap));
            }
        }
    }
}
