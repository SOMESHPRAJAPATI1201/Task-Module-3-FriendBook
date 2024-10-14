package in.webkorps.main.controllers;

import in.webkorps.main.entity.PostComments;
import in.webkorps.main.entity.User;
import in.webkorps.main.service.FollowingServices;
import in.webkorps.main.service.PostServices;
import in.webkorps.main.service.UserServices;
import in.webkorps.main.utlls.Logger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;

@Controller
public class UsersControllers {

    @Autowired
    private UserServices userServices;

    @Autowired
    private PostServices postServices;

    @Autowired
    private FollowingServices followingServices;
    //Registration

    @GetMapping("/regPage")
    public String getHomePage(Model model) {
        try {
            model.addAttribute("user", new User());
            return "Register1";
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            model.addAttribute("alert", "Something went wrong try again.");
            model.addAttribute("user", new User());
            return "Register1";
        }
    }

    @PostMapping("/regForm")
    public String getRegisterForm(@ModelAttribute("user") User user, @RequestParam("profileImage") MultipartFile file, Model model) throws IOException {
        try {
            userServices.saveStudent(user,file);
            model.addAttribute("alert", "User registered Successfully.");
            model.addAttribute("user", new User());
            return "Login1";
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            if(e.getMessage().contains("Duplicate entry")){
                model.addAttribute("alert", "Email is already associated.");
            }else{
                model.addAttribute("alert", "Issue in user details.");
            }
            model.addAttribute("user", new User());
            return "Register1";
        }
    }

    //Login

    @GetMapping("/logPage")
    public String getLoginPage(Model model) {
        try {
            model.addAttribute("user", new User());
            return "Login1";
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            model.addAttribute("alert", "Issue In Registering User.");
            return "index";
        }
    }


    @PostMapping("/logForm")
    public String setLoginForm(@ModelAttribute("user") User user, Model model) {
        try {
            User student1 = userServices.loginUser(user);
            model.addAttribute("name", student1.getFirstname() + " " + student1.getLastname());
            model.addAttribute("postList", postServices.getPostsList(student1.getUserId()));
            model.addAttribute("user", student1);
            model.addAttribute("user_id", student1.getUserId());
            model.addAttribute("comments", new PostComments());
            model.addAttribute("alert", "Login Successfully");
            model.addAttribute("userList", userServices.getUsersList());
            model.addAttribute("followerRequestCount", followingServices.getFollowerCount(student1.getUserId()));
            return "Profile";
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            model.addAttribute("alert", "Failed to login(Invalid creds).");
            model.addAttribute("user", new User());
            return "Login1";
        }
    }

    @PostMapping("/getprofile")
    public String profile(@RequestParam Integer userId, @RequestParam String firstname, Model model) {
        try {
           return returnProfilePage(model,userId,"Post fetched successfully.");
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            model.addAttribute("alert", "Something went wrong.");
            model.addAttribute("user", new User());
            return "Login1";
        }
    }

    @PostMapping("/viewProfile")
    public String getUserProfile(@RequestParam Integer userId, Model model) {
        User user = userServices.getById(userId);
        try {
            if (user != null) {
                String base64Image = Base64.getEncoder().encodeToString((user.getImageFile()));
                model.addAttribute("name", user.getFirstname() + " " + user.getLastname());
                model.addAttribute("user", user);
                model.addAttribute("imageData", "data:image/png;base64," + base64Image);
            }
            return "my_profile";
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            return returnProfilePage(model, userId, "Unable to open My Profile.");
        }
    }

    @PostMapping("/updateProfile")
    public String getUpdatedProfile(@ModelAttribute("user") User user, @RequestParam("profileImage") MultipartFile file, Model model) throws IOException {
        User user1 = null;
        try {
            user1 = userServices.getById(user.getUserId());
            String mesg = userServices.updateUserInTransaction(user, file);
            String base64Image = Base64.getEncoder().encodeToString((user1.getImageFile()));
            model.addAttribute("name", user1.getFirstname() + " " + user1.getLastname());
            model.addAttribute("imageData", "data:image/png;base64," + base64Image);
            model.addAttribute("alert", mesg);
            return "my_profile";
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            return returnProfilePage(model, user.getUserId(), "Unable to update My Profile.");
        }
    }

    @GetMapping("/logout")
    public String getLogout(HttpServletRequest request, Model model) {
        try {
            HttpSession session = request.getSession();
            if (session != null) {
                session.invalidate();
            }
            model.addAttribute("alert", "Logged out successfully.");
            model.addAttribute("user", new User());
            return "Login1";
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            model.addAttribute("alert", "Faced issue in logout tried again.");
            model.addAttribute("user", new User());
            return "Login1";
        }
    }

    public String returnProfilePage(Model model, Integer userId, String alert) {
        User user = userServices.getById(userId);
        model.addAttribute("alert", alert);
        model.addAttribute("name", user.getFirstname() + " " + user.getLastname());
        model.addAttribute("postList", postServices.getPostsList(userId));
        model.addAttribute("user_id", userId);
        model.addAttribute("comments", new PostComments());
        model.addAttribute("userList", userServices.getUsersList());
        model.addAttribute("followerRequestCount", followingServices.getFollowerCount(userId));
        return "Profile";
    }

}
