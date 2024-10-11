package in.webkorps.main.controllers;

import in.webkorps.main.dto.WrapperUser;
import in.webkorps.main.entity.Following;
import in.webkorps.main.entity.PostComments;
import in.webkorps.main.entity.User;
import in.webkorps.main.service.FollowingServices;
import in.webkorps.main.service.PostServices;
import in.webkorps.main.service.UserServices;
import in.webkorps.main.utlls.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CommunityController {

    @Autowired
    private PostServices postServices;

    @Autowired
    private UserServices userServices;

    @Autowired
    private FollowingServices followingServices;


    @GetMapping("/getAll")
    public ResponseEntity<?> getUserByIds(@RequestParam List<Integer> userIds) {
        return new ResponseEntity<>(postServices.allPostsByIDs(userIds), HttpStatus.OK);
    }

    @PostMapping("/getFollowing")
    public String getUsers2(@RequestParam Integer userId, Model model) {
        try {
            model.addAttribute("user_id", userId);
            User user = userServices.getById(userId);
            model.addAttribute("name", user.getFirstname() + " " + user.getLastname());
            List<WrapperUser> followings = userServices.getFollowingUsersList(userId);
            for (WrapperUser user1 : followings) {
                if (followingServices.getByUserIdAndFollowerId(userId, user1.getUserId()) == null) {
                    user1.setFollowStatus(Following.FollowStatus.FOLLOW.toString());
                } else {
                    user1.setFollowStatus(followingServices.getByUserIdAndFollowerId(userId, user1.getUserId()).getFollowStatus().toString());
                }
            }
            model.addAttribute("userList", followings);
            return "following";
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            return returnProfileByUserId(model, userId, "Unable to get following.");
        }
    }

    @PostMapping("/setFollowing")
    public String setFollowing(@RequestParam Integer followerId, @RequestParam Integer userId, Model model) {
        try {
            Following following = followingServices.SetFollowing(followerId, userId);
            User user = userServices.getById(userId);
            model.addAttribute("name", user.getFirstname() + " " + user.getLastname());
            model.addAttribute("user_id", userId);
            List<WrapperUser> wrapperUsers = userServices.getFollowingUsersList(userId);
            for (WrapperUser user1 : wrapperUsers) {
                if (followingServices.getByUserIdAndFollowerId(userId, user1.getUserId()) == null) {
                    user1.setFollowStatus(Following.FollowStatus.FOLLOW.toString());
                } else {
                    user1.setFollowStatus(followingServices.getByUserIdAndFollowerId(userId, user1.getUserId()).getFollowStatus().toString());
                }
            }
            model.addAttribute("userList", wrapperUsers);
            model.addAttribute("alert", "Request Sent");
            return "following";
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            return returnProfileByUserId(model, userId, "Something went wrong.");
        }
    }

    @PostMapping("/setFollowing2")
    public String setFollowing2(@RequestParam Integer followerId, @RequestParam Integer userId, Model model) {
        try {
            followingServices.SetFollowing(followerId, userId);
            User user = userServices.getById(userId);
            model.addAttribute("name", user.getFirstname() + " " + user.getLastname());
            List<WrapperUser> followers = getFollowers(userId);
            if (getFollowers(userId).size() != 0) {
                model.addAttribute("user_id", userId);
                for (WrapperUser user1 : followers) {
                    if (followingServices.getByUserIdAndFollowerId(userId, user1.getUserId()) == null) {
                        user1.setFollowBackStatus(Following.FollowStatus.FOLLOW.toString());
                    } else {
                        user1.setFollowBackStatus(followingServices.getByUserIdAndFollowerId(userId, user1.getUserId()).getFollowStatus().toString());
                    }
                }
                model.addAttribute("userList", followers);
                model.addAttribute("alert", "Request Sent");
                return "followers";
            } else {
                returnProfileByUserId(model, userId, "User Unfollowed Nothing To Show.");
                return "Profile";
            }
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            return returnProfileByUserId(model, userId, "Something went wrong.");
        }
    }

    @GetMapping("/getFollowings")
    public ResponseEntity<?> getFollowings(@RequestParam Integer userId) {
        return new ResponseEntity<>(followingServices.getFollowing(userId), HttpStatus.OK);
    }

    @PostMapping("/getFollowers")
    public String getFollowers(@RequestParam Integer userId, Model model) {
        try {
            model.addAttribute("user_id", userId);
            User user = userServices.getById(userId);
            model.addAttribute("name", user.getFirstname() + " " + user.getLastname());
            model.addAttribute("userList", getFollowers(userId));
            model.addAttribute("alert", "Followers Fetched");
            return "followers";
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            return returnProfileByUserId(model, userId, "Something went wrong.");
        }
    }

    @PostMapping("/approveRequest")
    public String getFollowers(@RequestParam Integer followerId, @RequestParam Integer userId, Model model) {
        try {
            followingServices.approveRequest(followerId, userId);
            User user = userServices.getById(userId);
            if (getFollowers(userId).size() == 0) {
                return returnProfileByUserId(model, userId, "User Unfollowed Nothing To Show.");
            } else {
                model.addAttribute("user_id", userId);
                model.addAttribute("name", user.getFirstname() + " " + user.getLastname());
                model.addAttribute("userList", getFollowers(userId));
                model.addAttribute("alert", "Followers Fetched");
                return "followers";
            }
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            return returnProfileByUserId(model, userId, "Something went wrong.");
        }
    }


    public List<WrapperUser> getFollowers(Integer userId) {
        List<WrapperUser> allUsers = userServices.getUsersList();
        List<WrapperUser> renderUsers = new ArrayList<>();
        List<Following> followers = followingServices.getFollowers(userId);
        for (Following fellows : followers) {
            for (WrapperUser user : allUsers) {
                if (fellows.getUserId().equals(user.getUserId())) {
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
        return renderUsers;
    }

    public String returnProfileByUserId(Model model, Integer userId, String alert) {
        User user = userServices.getById(userId);
        model.addAttribute("postList", postServices.allPosts());
        model.addAttribute("name", user.getFirstname() + " " + user.getLastname());
        model.addAttribute("user_id", user.getUserId());
        model.addAttribute("comments", new PostComments());
        model.addAttribute("followerRequestCount", followingServices.getFollowerCount(userId));
        model.addAttribute("alert", alert);
        return "Profile";
    }


}

