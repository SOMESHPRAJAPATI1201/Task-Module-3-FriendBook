package in.webkorps.main.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.webkorps.main.config.TemplateAndResolverConfig;
import in.webkorps.main.dto.WrapperLike;
import in.webkorps.main.dto.WrapperPosts2;
import in.webkorps.main.entity.Following;
import in.webkorps.main.entity.PostComments;
import in.webkorps.main.entity.Posts;
import in.webkorps.main.entity.User;
import in.webkorps.main.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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

    @PostMapping(value = "/getPostsById", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPostsById(@RequestPart("following") Following following, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        User user1 = userServices.getById(following.getUserId());
        User follower = userServices.getById(following.getFollowerId());
        responseMap.put("followerCount", followingServices.getFollowers1(follower.getUserId()).size());
        responseMap.put("postCount", postServices.getMyPostsList(follower.getUserId()).size());
        responseMap.put("followingCount", followingServices.getFollowings(following.getFollowerId()).size());
        responseMap.put("name", user1.getFirstname() + " " + user1.getLastname());
        responseMap.put("user_id", user1.getUserId());
        if (user1.getUserId() == follower.getUserId()) {
            follower.setFollowStatus("none");
            if (acceptHeader.contains("application/json")) {
                responseMap.clear();
                responseMap.put("alert", "Your Posts Has Been Fetched Succesfully.");
                responseMap.put("postList", postServices.getFilterPostsList(postServices.getMyPostsList(follower.getUserId())));
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
            } else {
                responseMap.put("user", follower);
                responseMap.put("postList", postServices.getMyPostsList(follower.getUserId()));
                responseMap.put("alert", "User Has Been Fetched Succesfully.");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("userProfilePost", responseMap));
            }
        }else if (followingServices.getByUserIdAndFollowerId(user1.getUserId(), follower.getUserId()) == null) {
            if (acceptHeader.contains("application/json")) {
                responseMap.clear();
                responseMap.put("alert", "You have to follow user to see his posts.");
                responseMap.put("postList", new ArrayList<>());
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
            } else {
                responseMap.put("user", follower);
                responseMap.put("postList", new ArrayList<>());
                responseMap.put("alert", "You have to follow user to see his posts.");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("userProfilePost", responseMap));
            }
        } else {
            follower.setFollowStatus(followingServices.getByUserIdAndFollowerId(user1.getUserId(), follower.getUserId()).getFollowStatus().toString());
            if (acceptHeader.contains("application/json")) {
                responseMap.clear();
                responseMap.put("alert", "Your Posts Has Been Fetched Succesfully.");
                responseMap.put("postList", postServices.getFilterPostsList(postServices.getMyPostsList(follower.getUserId())));
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
            } else {
                responseMap.put("user", follower);
                if (followingServices.getByUserIdAndFollowerId(user1.getUserId(), follower.getUserId()).getFollowStatus().toString().equalsIgnoreCase("UNFOLLOW")) {
                    responseMap.put("postList", postServices.getMyPostsList(follower.getUserId()));
                } else {
                    responseMap.put("postList", new ArrayList<>());
                }
                responseMap.put("alert", "User Has Been Fetched Succesfully.");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("userProfilePost", responseMap));
            }
        }

    }


    @GetMapping("/getUsersPost/{userId}")
    public ResponseEntity<List<WrapperPosts2>> getPostsByUserId2(@PathVariable Integer userId) {
        return new ResponseEntity<>(postServices.getFilterPostsList(postServices.getPostsList(userId)), HttpStatus.OK);
    }

    @PostMapping(value = "/addComment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> setComment(@RequestBody Map<String, Object> responseBody, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) throws Throwable {
        PostComments postComments = new ObjectMapper().convertValue(responseBody.get("postComments"), PostComments.class);
        postComments.toString();
        String redirectPage = (String) responseBody.get("redirectPage");
        Integer postId = postComments.getPost().getPostId();
        Integer userId = postComments.getUserId();
        String comment = postComments.getComment();
        PostComments comments = commentsServices.setComment(comment, userId, postId);
        responseMap.clear();
        responseMap.put("name", userServices.getById(userId).getFirstname() + " " + userServices.getById(userId).getLastname());
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
                Integer followerId = Integer.parseInt((String) responseBody.get("followerId"));
                if (userId == followerId) {
                    userServices.getById(followerId).setFollowStatus("none");
                    if (acceptHeader.contains("application/json")) {
                        responseMap.clear();
                        responseMap.put("alert", "Comment Added Succesfully.");
                        responseMap.put("postList", postServices.getFilterPostsList(postServices.getMyPostsList(followerId)));
                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
                    } else {
                        responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                        responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                        responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                        responseMap.put("user", userServices.getById(followerId));
                        responseMap.put("postList", postServices.getMyPostsList(followerId));
                        responseMap.put("user_id", userId);
                        responseMap.put("alert", "Comment Added Succesfully.");
                        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("userProfilePost", responseMap));
                    }
                }else if (followingServices.getByUserIdAndFollowerId(userId, followerId) == null) {
                    responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                    responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                    responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                    responseMap.put("user", userServices.getById(followerId));
                    responseMap.put("user_id", userId);
                    responseMap.put("postList", new ArrayList<>());
                } else {
                    User follower = userServices.getById(followerId);
                    follower.setFollowStatus(followingServices.getByUserIdAndFollowerId(userId, followerId).getFollowStatus().toString());
                    responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                    responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                    responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                    responseMap.put("user_id", userId);
                    responseMap.put("user", follower);
                    responseMap.put("alert", "Comment Added Succesfully.");
                    if (followingServices.getByUserIdAndFollowerId(userId, followerId).getFollowStatus().toString().equalsIgnoreCase("UNFOLLOW")) {
                        responseMap.put("postList", postServices.getMyPostsList(followerId));
                    } else {
                        responseMap.put("postList", new ArrayList<>());
                    }
                }
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

    @DeleteMapping(value = "/deleteComment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> deleteComment(@RequestBody Map<String, Object> responseBody, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        PostComments postComments = new ObjectMapper().convertValue(responseBody.get("postComments"), PostComments.class);
        String redirectPage = (String) responseBody.get("redirectPage");
        Integer id = postComments.getId();
        Integer userId = postComments.getUserId();
        Integer postId = postComments.getPost().getPostId();
        String response = commentsServices.deleteComment(id, postId, userId);
        responseMap.clear();
        responseMap.put("name", userServices.getById(userId).getFirstname() + " " + userServices.getById(userId).getLastname());
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
                Integer followerId = (Integer) responseBody.get("followerId");
                if (userId == followerId) {
                    userServices.getById(followerId).setFollowStatus("none");
                    responseMap.put("user_id", userId);
                    if (acceptHeader.contains("application/json")) {
                        responseMap.clear();
                        responseMap.put("alert", response);
                        responseMap.put("postList", postServices.getFilterPostsList(postServices.getMyPostsList(followerId)));
                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
                    } else {
                        responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                        responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                        responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                        responseMap.put("user", userServices.getById(followerId));
                        responseMap.put("postList", postServices.getMyPostsList(followerId));
                        responseMap.put("alert", response);
                        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("userProfilePost", responseMap));
                    }
                }else if (followingServices.getByUserIdAndFollowerId(userId, followerId) == null) {
                    responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                    responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                    responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                    responseMap.put("user", userServices.getById(followerId));
                    responseMap.put("user_id", userId);
                    responseMap.put("postList", new ArrayList<>());
                } else {
                    User follower = userServices.getById(followerId);
                    follower.setFollowStatus(followingServices.getByUserIdAndFollowerId(userId, followerId).getFollowStatus().toString());
                    responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                    responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                    responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                    responseMap.put("user", follower);
                    responseMap.put("user_id", userId);
                    responseMap.put("alert", response);
                    if (followingServices.getByUserIdAndFollowerId(userId, followerId).getFollowStatus().toString().equalsIgnoreCase("UNFOLLOW")) {
                        responseMap.put("postList", postServices.getMyPostsList(followerId));
                    } else {
                        responseMap.put("postList", new ArrayList<>());
                    }
                }
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

    @PutMapping(value = "/editComment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> editComment(@RequestBody Map<String, Object> responseBody, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        PostComments postComments = new ObjectMapper().convertValue(responseBody.get("postComments"), PostComments.class);
        postComments.toString();
        String redirectPage = (String) responseBody.get("redirectPage");
        Integer userId = postComments.getUserId();
        Integer postId = postComments.getPost().getPostId();
        Integer commentId = postComments.getId();
        String comment = postComments.getComment();
        Map<String, Object> response = commentsServices.editComment(userId, postId, commentId, comment);
        responseMap.clear();
        responseMap.put("name", userServices.getById(userId).getFirstname() + " " + userServices.getById(userId).getLastname());
        boolean mesg = response.get("mesg").toString().equalsIgnoreCase("Comment Updated Successfully.");
        if (mesg) {
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
                    Integer followerId =  (Integer) responseBody.get("followerId");
                    if (userId == followerId) {
                        responseMap.put("user_id", userId);
                        userServices.getById(followerId).setFollowStatus("none");
                        if (acceptHeader.contains("application/json")) {
                            responseMap.clear();
                            responseMap.put("alert",response.get("mesg"));
                            responseMap.put("postList", postServices.getFilterPostsList(postServices.getMyPostsList(followerId)));
                            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
                        } else {
                            responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                            responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                            responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                            responseMap.put("user", userServices.getById(followerId));
                            responseMap.put("postList", postServices.getMyPostsList(followerId));
                            responseMap.put("alert",response.get("mesg"));
                            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("userProfilePost", responseMap));
                        }
                    }else if (followingServices.getByUserIdAndFollowerId(userId, followerId) == null) {
                        responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                        responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                        responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                        responseMap.put("user", userServices.getById(followerId));
                        responseMap.put("postList", new ArrayList<>());
                    } else {
                        User follower = userServices.getById(followerId);
                        follower.setFollowStatus(followingServices.getByUserIdAndFollowerId(userId, followerId).getFollowStatus().toString());
                        responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                        responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                        responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                        responseMap.put("alert",response.get("mesg"));
                        responseMap.put("user", follower);
                        if (followingServices.getByUserIdAndFollowerId(userId, followerId).getFollowStatus().toString().equalsIgnoreCase("UNFOLLOW")) {
                            responseMap.put("postList", postServices.getMyPostsList(followerId));
                        } else {
                            responseMap.put("postList", new ArrayList<>());
                        }
                    }
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
                responseMap.put("user_id", userId);
                if (responseBody.get("followerId") == null) {
                    responseMap.put("postList", postServices.getPostsList(userId));
                } else {
                    Integer followerId = (Integer) responseBody.get("followerId");
                    if (userId == followerId) {
                        userServices.getById(followerId).setFollowStatus("none");
                        if (acceptHeader.contains("application/json")) {
                            responseMap.clear();
                            responseMap.put("alert",response.get("mesg"));
                            responseMap.put("postList", postServices.getFilterPostsList(postServices.getMyPostsList(followerId)));
                            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
                        } else {
                            responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                            responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                            responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                            responseMap.put("user", userServices.getById(followerId));
                            responseMap.put("postList", postServices.getMyPostsList(followerId));
                            responseMap.put("alert",response.get("mesg"));
                            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("userProfilePost", responseMap));
                        }
                    }else if (followingServices.getByUserIdAndFollowerId(userId, followerId) == null) {
                        responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                        responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                        responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                        responseMap.put("user", userServices.getById(followerId));
                        responseMap.put("postList", new ArrayList<>());
                    } else {
                        User follower = userServices.getById(followerId);
                        follower.setFollowStatus(followingServices.getByUserIdAndFollowerId(userId, followerId).getFollowStatus().toString());
                        responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                        responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                        responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                        responseMap.put("user", follower);
                        responseMap.put("alert",response.get("mesg"));
                        if (followingServices.getByUserIdAndFollowerId(userId, followerId).getFollowStatus().toString().equalsIgnoreCase("UNFOLLOW")) {
                            responseMap.put("postList", postServices.getMyPostsList(followerId));
                        } else {
                            responseMap.put("postList", new ArrayList<>());
                        }
                    }
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
            if (userId == followerId) {
                userServices.getById(followerId).setFollowStatus("none");
                if (acceptHeader.contains("application/json")) {
                    responseMap.clear();
                    responseMap.put("alert", isLiked == true ? "Post Liked" : "Post Unliked");
                    responseMap.put("postList", postServices.getFilterPostsList(postServices.getMyPostsList(followerId)));
                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
                } else {
                    responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                    responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                    responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                    responseMap.put("user", userServices.getById(followerId));
                    responseMap.put("postList", postServices.getMyPostsList(followerId));
                    responseMap.put("alert", isLiked == true ? "Post Liked" : "Post Unliked");
                    return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("userProfilePost", responseMap));
                }
            }else if (followingServices.getByUserIdAndFollowerId(userId, followerId) == null) {
                responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                responseMap.put("user", userServices.getById(followerId));
                responseMap.put("postList", new ArrayList<>());
            } else {
                User follower = userServices.getById(followerId);
                follower.setFollowStatus(followingServices.getByUserIdAndFollowerId(userId, followerId).getFollowStatus().toString());
                responseMap.put("followerCount", followingServices.getFollowers1(followerId).size());
                responseMap.put("postCount", postServices.getMyPostsList(followerId).size());
                responseMap.put("followingCount", followingServices.getFollowings(followerId).size());
                responseMap.put("alert", isLiked == true ? "Post Liked" : "Post Unliked");
                responseMap.put("user", follower);
                if (followingServices.getByUserIdAndFollowerId(userId, followerId).getFollowStatus().toString().equalsIgnoreCase("UNFOLLOW")) {
                    responseMap.put("postList", postServices.getMyPostsList(followerId));
                } else {
                    responseMap.put("postList", new ArrayList<>());
                }
            }
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
                System.out.println(redirectPage + " Liked");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf(redirectPage, responseMap));
            } else {
                responseMap.put("alert", "Post Unliked");
                responseMap.put("userList", followingServices.getFollowing1(userId));
                System.out.println(redirectPage + " Unliked");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf(redirectPage, responseMap));
            }
        }
    }
}
