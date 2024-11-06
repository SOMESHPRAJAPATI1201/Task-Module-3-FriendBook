package in.webkorps.main.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.webkorps.main.config.TemplateAndResolverConfig;
import in.webkorps.main.dto.WrapperUser;
import in.webkorps.main.entity.Following;
import in.webkorps.main.entity.PostComments;
import in.webkorps.main.entity.User;
import in.webkorps.main.service.FollowingServices;
import in.webkorps.main.service.PostServices;
import in.webkorps.main.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RestCommunityController {

    @Autowired
    private PostServices postServices;

    @Autowired
    private UserServices userServices;

    @Autowired
    private FollowingServices followingServices;

    Map<String, Object> responseMap = new HashMap<>();


    @GetMapping("/getAllByIds")
    public ResponseEntity<?> getUserByIds(@RequestParam List<Integer> userIds) {
        return new ResponseEntity<>(postServices.allPostsByIDs(userIds), HttpStatus.OK);
    }

    @PostMapping(value = "/getFollowing", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getFollowingUsers(@RequestParam Integer userId, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        responseMap.put("user_id", userId);
        User user = userServices.getById(userId);
        responseMap.put("name", user.getFirstname() + " " + user.getLastname());
        responseMap.put("userList", followingServices.getFollowings(userId));
        if (acceptHeader.contains("application/json")) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("following", responseMap));
        }
    }

    @PostMapping(value = "/setFollowing", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getFollowingUsers(@RequestBody Map<String,Object> responseBody, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        Following following1 = new ObjectMapper().convertValue(responseBody.get("following"), Following.class);
        String redirectPage = (String) responseBody.get("redirectPage");
        Integer userId = following1.getUserId();
        Following following = followingServices.SetFollowing(following1.getFollowerId(), userId);
        User user = userServices.getById(userId);
        responseMap.put("name", user.getFirstname() + " " + user.getLastname());
        responseMap.put("user_id", userId);
        responseMap.put("follower_Id", following.getFollowerId());
        List<WrapperUser> wrapperUsers = userServices.getFollowingUsersList(userId);
        User follower = userServices.getById(following1.getFollowerId());
        if (wrapperUsers.size() != 0) {
            for (WrapperUser user1 : wrapperUsers) {
                if (followingServices.getByUserIdAndFollowerId(userId, user1.getUserId()) == null) {
                    user1.setFollowStatus(Following.FollowStatus.FOLLOW.toString());
                } else {
                    user1.setFollowStatus(followingServices.getByUserIdAndFollowerId(userId, user1.getUserId()).getFollowStatus().toString());
                }
            }
            responseMap.put("userList", wrapperUsers);
            if (acceptHeader.contains("application/json")) {
                responseMap.put("Message", following.getFollowStatus());
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
            } else {
                if (followingServices.getByUserIdAndFollowerId(following1.getUserId(), following1.getFollowerId()) == null) {
                    responseMap.put("followerCount", followingServices.getFollowers1(following1.getFollowerId()).size());
                    responseMap.put("postCount", postServices.getMyPostsList(following1.getFollowerId()).size());
                    responseMap.put("followingCount", followingServices.getFollowings(following1.getFollowerId()).size());
                    responseMap.put("user",follower);
                    responseMap.put("alert", "REQUESTED");
                    responseMap.put("userList", followingServices.getFollowings(userId));
                    responseMap.put("postList", new ArrayList<>());
                    return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf(redirectPage, responseMap));
                } else {
                    follower.setFollowStatus(followingServices.getByUserIdAndFollowerId(following1.getUserId(), following.getFollowerId()).getFollowStatus().toString());
                    responseMap.put("followerCount", followingServices.getFollowers1(following1.getFollowerId()).size());
                    responseMap.put("postCount", postServices.getMyPostsList(following1.getFollowerId()).size());
                    responseMap.put("followingCount", followingServices.getFollowings(following1.getFollowerId()).size());
                    responseMap.put("user",follower);
                    responseMap.put("alert", follower.getFollowStatus().toString());
                    responseMap.put("userList", followingServices.getFollowings(userId));
                    if(followingServices.getByUserIdAndFollowerId(following1.getUserId(), following.getFollowerId()).getFollowStatus().toString().equalsIgnoreCase("UNFOLLOW")){
                        responseMap.put("postList", postServices.getMyPostsList(following.getFollowerId()));
                    }else{
                        responseMap.put("postList",new ArrayList<>());
                    }
                    return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf(redirectPage, responseMap));
                }
            }
        } else {
            responseMap.put("name", user.getFirstname() + " " + user.getLastname());
            responseMap.put("alert", "No Records Found.");
            responseMap.put("postList", postServices.allPosts());
            responseMap.put("userList", followingServices.getFollowing1(userId));
            responseMap.put("followerRequestCount", followingServices.getFollowerCount(userId));
            responseMap.put("followingRequestCount", followingServices.getFollowingCount((userId)));
            responseMap.put("RequestCount", followingServices.getRequestCount(userId));
            redirectPage = "home";
            if (followingServices.getByUserIdAndFollowerId(following1.getUserId(), following.getFollowerId()) == null && redirectPage.equalsIgnoreCase("userProfilePost")) {
                responseMap.put("followerCount", followingServices.getFollowers1(following.getUserId()).size());
                responseMap.put("postCount", postServices.getMyPostsList(following.getUserId()).size());
                responseMap.put("followingCount", followingServices.getFollowings(following.getUserId()).size());
                responseMap.put("postList",new ArrayList<>());
                responseMap.put("user", follower);
                redirectPage = "userProfilePost";
            }else{
                responseMap.put("followerCount", followingServices.getFollowers1(following.getUserId()).size());
                responseMap.put("postCount", postServices.getMyPostsList(following.getUserId()).size());
                responseMap.put("followingCount", followingServices.getFollowings(following.getUserId()).size());
                if(followingServices.getByUserIdAndFollowerId(following1.getUserId(), following.getFollowerId()).getFollowStatus().toString().equalsIgnoreCase("UNFOLLOW")){
                    responseMap.put("postList", postServices.getMyPostsList(following.getFollowerId()));
                }else{
                    responseMap.put("postList",new ArrayList<>());
                }
                follower.setFollowStatus(followingServices.getByUserIdAndFollowerId(following1.getUserId(), following.getFollowerId()).getFollowStatus().toString());
                responseMap.put("user", follower);
                redirectPage = "userProfilePost";
            }
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf(redirectPage, responseMap));
        }
    }

    @PostMapping(value = "/setFollowing1", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getFollowing1Users(@RequestPart("following") Following following1, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        Integer userId = following1.getUserId();
        Following following = followingServices.SetFollowing(following1.getFollowerId(), userId);
        User user = userServices.getById(userId);
        responseMap.put("name", user.getFirstname() + " " + user.getLastname());
        responseMap.put("user_id", userId);
        responseMap.put("follower_Id", following.getFollowerId());
        List<WrapperUser> wrapperUsers = userServices.getFollowingUsersList(userId);
        if (wrapperUsers.size() != 0) {
            for (WrapperUser user1 : wrapperUsers) {
                if (followingServices.getByUserIdAndFollowerId(userId, user1.getUserId()) == null) {
                    user1.setFollowStatus(Following.FollowStatus.FOLLOW.toString());
                } else {
                    user1.setFollowStatus(followingServices.getByUserIdAndFollowerId(userId, user1.getUserId()).getFollowStatus().toString());
                }
            }
            responseMap.put("userList", wrapperUsers);
            if (acceptHeader.contains("application/json")) {
                responseMap.put("Message", following.getFollowStatus());
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
            } else {
                responseMap.put("name", user.getFirstname() + " " + user.getLastname());
                responseMap.put("alert", following.getFollowStatus());
                responseMap.put("postList", postServices.allPosts());
                responseMap.put("userList", followingServices.getFollowing1(userId));
                responseMap.put("followerRequestCount", followingServices.getFollowerCount(userId));
                responseMap.put("followingRequestCount", followingServices.getFollowingCount((userId)));
                responseMap.put("RequestCount", followingServices.getRequestCount(userId));
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("home", responseMap));
            }
        } else {
            responseMap.put("name", user.getFirstname() + " " + user.getLastname());
            responseMap.put("alert", "No Records Found.");
            responseMap.put("postList", postServices.allPosts());
            responseMap.put("userList", followingServices.getFollowing1(userId));
            responseMap.put("followerRequestCount", followingServices.getFollowerCount(userId));
            responseMap.put("followingRequestCount", followingServices.getFollowingCount((userId)));
            responseMap.put("RequestCount", followingServices.getRequestCount(userId));
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("home", responseMap));
        }
    }

    @PostMapping(value = "/setFollowing2", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> setFollowing2(@RequestPart("following") Following following, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        Following following1 = followingServices.SetFollowing(following.getFollowerId(), following.getUserId());
        User user = userServices.getById(following.getUserId());
        if (getFollowers(following1.getUserId()).size() != 0) {
            responseMap.put("name", user.getFirstname() + " " + user.getLastname());
            responseMap.put("user_id", following1.getUserId());
            responseMap.put("follower_Id", following1.getFollowerId());
            responseMap.put("userList", getFollowers(following1.getUserId()));
            if (acceptHeader.contains("application/json")) {
                responseMap.put("Message", following.getFollowStatus());
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
            } else {
                responseMap.put("alert", following.getFollowStatus());
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("followers", responseMap));
            }
        }else{
            responseMap.put("postList", postServices.allPosts());
            responseMap.put("name", user.getFirstname() + " " + user.getLastname());
            responseMap.put("user_id", user.getUserId());
            responseMap.put("followerRequestCount", followingServices.getFollowerCount(user.getUserId()));
            responseMap.put("followingRequestCount", followingServices.getFollowingCount((user.getUserId())));
            responseMap.put("RequestCount", followingServices.getRequestCount(user.getUserId()));
            responseMap.put("userList", followingServices.getFollowing1(following.getUserId()));
            responseMap.put("alert", "Nothing to show.");
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("home", responseMap));
        }
    }

    @GetMapping("/getFollowings")
    public ResponseEntity<?> getFollowings(@RequestParam Integer userId) {
        return new ResponseEntity<>(followingServices.getFollowing(userId), HttpStatus.OK);
    }

    @PostMapping(value = "/getFollowers", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getFollowers(@RequestParam Integer userId, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        responseMap.put("user_id", userId);
        User user = userServices.getById(userId);
        responseMap.put("name", user.getFirstname() + " " + user.getLastname());
        responseMap.put("userList", getFollowers(userId));
        if (acceptHeader.contains("application/json")) {
            responseMap.put("Message", "Followers Fetched");
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            responseMap.put("alert", "Followers Fetched");
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("followers", responseMap));
        }
    }

    @PostMapping(value = "/getFollowersRequest", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getFollowersRequest(@RequestPart User user, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        User user1 = userServices.getById(user.getUserId());
        responseMap.put("user_id", user.getUserId());
        responseMap.put("name", user.getFirstname() + " " + user.getLastname());
        responseMap.put("userList", followingServices.getFilteredFollowers(user.getUserId()));
        if (acceptHeader.contains("application/json")) {
            responseMap.put("Message", "Request Fetched");
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            responseMap.put("alert", "Requests Fetched");
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("notifications", responseMap));
        }
    }

    @PostMapping(value = "/approveRequest", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> approveRequest(@RequestPart Following following, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        followingServices.approveRequest(following.getFollowerId(), following.getUserId());
        User user = userServices.getById(following.getUserId());
        if (acceptHeader.contains("application/json")) {
            responseMap.clear();
            responseMap.put("user_id", following.getUserId());
            responseMap.put("name", user.getFirstname() + " " + user.getLastname());
            responseMap.put("userList", followingServices.getFilteredFollowers(user.getUserId()));
            responseMap.put("Message", "Requested Approved");
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            if (getFollowers(following.getUserId()).size() == 0) {
                responseMap.put("postList", postServices.allPosts());
                responseMap.put("name", user.getFirstname() + " " + user.getLastname());
                responseMap.put("user_id", user.getUserId());
                responseMap.put("followerRequestCount", followingServices.getFollowerCount(user.getUserId()));
                responseMap.put("followingRequestCount", followingServices.getFollowingCount((user.getUserId())));
                responseMap.put("RequestCount", followingServices.getRequestCount(user.getUserId()));
                responseMap.put("userList", followingServices.getFollowing1(following.getUserId()));
                responseMap.put("alert", "Nothing to show.");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("home", responseMap));
            } else {
                responseMap.put("user_id", following.getUserId());
                responseMap.put("name", user.getFirstname() + " " + user.getLastname());
                responseMap.put("userList", followingServices.getFilteredFollowers(user.getUserId()));
                responseMap.put("alert", "Followers Fetched");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("notifications", responseMap));
            }
        }
    }

    @PostMapping(value = "/approveRequest2", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> approveRequest2(@RequestPart Following following, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        followingServices.approveRequest(following.getFollowerId(), following.getUserId());
        User user = userServices.getById(following.getUserId());
        if (acceptHeader.contains("application/json")) {
            responseMap.clear();
            responseMap.put("user_id", user.getUserId());
            responseMap.put("name", user.getFirstname() + " " + user.getLastname());
            responseMap.put("userList", followingServices.getFilteredFollowers(user.getUserId()));
            responseMap.put("Message", "Requested Approved");
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            if (getFollowers(user.getUserId()).size() == 0) {
                responseMap.put("postList", postServices.allPosts());
                responseMap.put("name", user.getFirstname() + " " + user.getLastname());
                responseMap.put("user_id", user.getUserId());
                responseMap.put("followerRequestCount", followingServices.getFollowerCount(user.getUserId()));
                responseMap.put("followingRequestCount", followingServices.getFollowingCount((user.getUserId())));
                responseMap.put("RequestCount", followingServices.getRequestCount(user.getUserId()));
                responseMap.put("userList", followingServices.getFollowing1(user.getUserId()));
                responseMap.put("alert", "Nothing to show.");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("home", responseMap));
            } else {
                responseMap.put("user_id", user.getUserId());
                responseMap.put("name", user.getFirstname() + " " + user.getLastname());
                responseMap.put("userList", getFollowers(user.getUserId()));
                responseMap.put("alert", "Followers Fetched");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("followers", responseMap));
            }
        }
    }

    @PostMapping(value = "/rejectRequest", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> rejectFollowingRequest(@RequestParam Integer followerId, @RequestParam Integer userId, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        followingServices.rejectRequest(followerId, userId);
        User user = userServices.getById(userId);
        if (acceptHeader.contains("application/json")) {
            responseMap.clear();
            responseMap.put("user_id", userId);
            responseMap.put("name", user.getFirstname() + " " + user.getLastname());
            responseMap.put("userList", followingServices.getFilteredFollowers(user.getUserId()));
            responseMap.put("Message", "Requested Rejected");
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            if (getFollowers(userId).size() == 0) {
                responseMap.put("postList", postServices.allPosts());
                responseMap.put("name", user.getFirstname() + " " + user.getLastname());
                responseMap.put("user_id", user.getUserId());
                responseMap.put("followerRequestCount", followingServices.getFollowerCount(userId));
                responseMap.put("followingRequestCount", followingServices.getFollowingCount((userId)));
                responseMap.put("RequestCount", followingServices.getRequestCount(userId));
                responseMap.put("userList", followingServices.getFollowing1(userId));
                responseMap.put("alert", "User Request Rejected Nothing To Show.");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("home", responseMap));
            } else {
                responseMap.put("user_id", userId);
                responseMap.put("name", user.getFirstname() + " " + user.getLastname());
                responseMap.put("userList", followingServices.getFilteredFollowers(user.getUserId()));
                responseMap.put("alert", "Followers Fetched");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("notifications", responseMap));
            }
        }
    }

    public List<User> getUserFollowers(Integer userId) {
        List<User> allUsers = userServices.getUserList();
        List<User> renderUsers = new ArrayList<>();
        List<Following> followers = followingServices.getFollowers(userId);
        for (Following fellows : followers) {
            for (User user : allUsers) {
                if (fellows.getUserId().equals(user.getUserId())) {
                    if (fellows.getFollowStatus().toString().equalsIgnoreCase("UNFOLLOW")) {
                        user.setFollowStatus(fellows.getFollowStatus().toString());
                        if (followingServices.getByUserIdAndFollowerId(userId, fellows.getUserId()) != null) {
                            user.setFollowBackStatus(followingServices.getByUserIdAndFollowerId(userId, fellows.getUserId()).getFollowStatus().toString());
                        } else {
                            user.setFollowBackStatus(Following.FollowStatus.FOLLOW.toString());
                        }
                        renderUsers.add(user);
                    }
                }
            }
        }
        return renderUsers;
    }



    public List<WrapperUser>  getFollowers(Integer userId) {
        List<WrapperUser> allUsers = userServices.getUsersList();
        List<WrapperUser> renderUsers = new ArrayList<>();
        List<Following> followers = followingServices.getFollowers(userId);
        for (Following fellows : followers) {
            for (WrapperUser user : allUsers) {
                if (fellows.getUserId().equals(user.getUserId())) {
                    if (fellows.getFollowStatus().toString().equalsIgnoreCase("UNFOLLOW")) {
                        user.setFollowStatus(fellows.getFollowStatus().toString());
                        if (followingServices.getByUserIdAndFollowerId(userId, fellows.getUserId()) != null) {
                            user.setFollowBackStatus(followingServices.getByUserIdAndFollowerId(userId, fellows.getUserId()).getFollowStatus().toString());
                        } else {
                            user.setFollowBackStatus(Following.FollowStatus.FOLLOW.toString());
                        }
                        renderUsers.add(user);
                    }
                }
            }
        }
        return renderUsers;
    }

}

