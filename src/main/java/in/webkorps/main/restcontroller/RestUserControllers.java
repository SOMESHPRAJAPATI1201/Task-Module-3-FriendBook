package in.webkorps.main.restcontroller;

import in.webkorps.main.config.TemplateAndResolverConfig;
import in.webkorps.main.entity.User;
import in.webkorps.main.service.FollowingServices;
import in.webkorps.main.service.PostServices;
import in.webkorps.main.service.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RestUserControllers {

    @Autowired
    private UserServices userServices;

    @Autowired
    private PostServices postServices;

    @Autowired
    private FollowingServices followingServices;

    Map<String, Object> responseMap = new HashMap<>();
    //Registration

    @GetMapping(value = "/regPage", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getHomePage(@RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        if (acceptHeader.contains("application/json")) {
            responseMap.put("message", "Welcome to friendbook");
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("Register1", responseMap));
        }
    }

    @PostMapping(value = "/regForm", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getRegisterForm(@RequestPart("user") User user,
                                             @RequestPart("profileImage") MultipartFile profileImage,
                                             @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) throws IOException {
        responseMap.clear();
        Map<String,Object> response = userServices.saveStudent(user, profileImage);
        if(response.get("mesg").toString().equalsIgnoreCase("User Updated Successfully")) {
            if (acceptHeader.contains("application/json")) {
                responseMap.put("user", response.get("user"));
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
            } else {
                responseMap.put("alert", "User registered Successfully.");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("Login1", responseMap));
            }
        }else{
            if (acceptHeader.contains("application/json")) {
                responseMap.put("mesg", "Failed to register user");
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
            } else {
                responseMap.put("alert", "Failed to register user");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("Login1", responseMap));
            }
        }
    }

    ////Login
    @GetMapping(value = "/logPage", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getLoginPage(@RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) throws IOException {
        responseMap.clear();
        if (acceptHeader.contains("application/json")) {
            responseMap.put("message", "Welcome To Login Page");
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("Login1", responseMap));
        }
    }

    @PostMapping(value = "/logForm", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> setLoginForm(@RequestPart User user, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) throws IOException {
        responseMap.clear();
        if ((user.getUsername() != null && !user.getUsername().equals("")) && (user.getPassword() != null && !user.getUsername().equals(""))) {
            User response = userServices.loginUser(user.getUsername(), user.getPassword());
            if (response != null) {
                if (acceptHeader.contains("application/json")) {
                    responseMap.put("message", "User Login Successfully");
                    responseMap.put("user", userServices.getUsersById(response.getUserId()));
                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
                } else {
                    responseMap.put("name", response.getDisplayname());
                    responseMap.put("postList", postServices.getPostsList(response.getUserId()));
                    responseMap.put("user", response);
                    responseMap.put("user_id", response.getUserId());
                    responseMap.put("alert", "Login Successfully");
                    responseMap.put("userList", followingServices.getFollowing1(response.getUserId()));
                    responseMap.put("followerRequestCount", followingServices.getFollowerCount(response.getUserId()));
                    responseMap.put("followingRequestCount", followingServices.getFollowingCount((response.getUserId())));
                    responseMap.put("RequestCount", followingServices.getRequestCount(response.getUserId()));
                    return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("home", responseMap));
                }
            } else {
                if (acceptHeader.contains("application/json")) {
                    responseMap.put("message", "Invalid Creds User Not Found");
                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
                } else {
                    System.out.println(response.getUsername());
                    responseMap.put("alert", "User Login Failed.");
                    return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("Login1", responseMap));
                }
            }
        } else {
            if (acceptHeader.contains("application/json")) {
                responseMap.put("message", "Invalid Creds User Not Found");
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
            } else {
                responseMap.put("alert", "Invalid Creds User Not Found.");
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("Login1", responseMap));
            }
        }
    }


    @PostMapping(value = "/getHomePage", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getHomePage(@RequestParam Integer userId, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        if (acceptHeader.contains("application/json")) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(returnProfilePage(responseMap, userId, "Post Fetched Succesfully."));
        } else {
            responseMap.put("userList", followingServices.getFollowing1(userId));
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("home", returnProfilePage(responseMap, userId, "Post Fetched Succesfully.")));
        }
    }

    @PostMapping(value = "/viewProfile", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUserProfile(@RequestParam Integer userId, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        responseMap.clear();
        User user = userServices.getById(userId);
        responseMap.put("user", user);
        responseMap.put("user_Id", user.getUserId());
        if (acceptHeader.contains("application/json")) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            responseMap.put("name", user.getFirstname() + " " + user.getLastname());
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("my_profile", responseMap));
        }
    }

    @PostMapping(value = "/updateProfile", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUpdatedProfile(@RequestPart("user") User user,
                                               @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                               @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) throws IOException {
        responseMap.clear();
        System.out.println(user.getFirstname() + " " + user.getLastname() + " " + user.getUsername());
        String mesg;

        if (profileImage != null && !profileImage.isEmpty()) {
            mesg = userServices.updateUserInTransaction(user, profileImage);
        } else {
            mesg = userServices.updateUserInTransaction(user);
        }
        User user1 = userServices.getById(user.getUserId());
        responseMap.put("name", user1.getFirstname() + " " + user1.getLastname());
        if (user1 != null) {
            responseMap.put("user", user1);
        }
        responseMap.put("alert", mesg);
        if (acceptHeader.contains("application/json")) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("my_profile", responseMap));
        }
    }

    @GetMapping(value = "/logout", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getLogout(HttpServletRequest request, @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "text/html") String acceptHeader) {
        HttpSession session = request.getSession();
        responseMap.clear();
        if (session != null) {
            session.invalidate();
        }
        if (acceptHeader.contains("application/json")) {
            responseMap.put("message", "Logged out successfully.");
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
        } else {
            responseMap.put("alert", "Logged out successfully.");
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(TemplateAndResolverConfig.renderHtmlWithThymeleaf("Login1", responseMap));
        }
    }

    public Map<String, Object> returnProfilePage(Map<String, Object> model, Integer userId, String alert) {
        User user = userServices.getById(userId);
        model.put("alert", alert);
        model.put("user", user);
        model.put("userList", followingServices.getFollowing1(userId));
        model.put("name", user.getFirstname() + " " + user.getLastname());
        model.put("postList", postServices.getPostsList(userId));
        model.put("user_id", userId);
        model.put("userId", userId);
        responseMap.put("followerRequestCount", followingServices.getFollowerCount(userId));
        responseMap.put("followingRequestCount", followingServices.getFollowingCount((userId)));
        responseMap.put("RequestCount", followingServices.getRequestCount(userId));
        return model;
    }
}
