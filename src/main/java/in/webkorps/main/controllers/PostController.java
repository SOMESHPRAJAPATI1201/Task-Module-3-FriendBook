package in.webkorps.main.controllers;

import in.webkorps.main.dto.WrapperLike;
import in.webkorps.main.entity.*;
import in.webkorps.main.service.*;
import in.webkorps.main.utlls.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Controller
public class PostController {

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


    @PostMapping("/addPost")
    public String getAddPost(@RequestParam("content") String content, @RequestParam("profileImage") MultipartFile file, @RequestParam("userId") Integer userId, Model model) throws IOException {
        try {
            postServices.savePosts(content, file, userId);
            return getProfilePage(model,userId,"Post Added Successfully");
        }catch (Exception e){
            Logger.LOGGER.info(e.getMessage());
            return getProfilePage(model,userId,"Unable to add post");
        }
    }

    @GetMapping("/getLike/{postId}")
    public ResponseEntity<List<WrapperLike>> getLikesByPostId(@PathVariable Integer postId) {
        List<WrapperLike> likes = likesServices.getLike(postId);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @PostMapping("/like")
    public String likeUnlikePost(@RequestParam Integer userId, @RequestParam Integer postId,Model model) {
        try {
            if(likesServices.clickLike(userId,postId)){
                return getProfilePage(model,userId,"Post Liked");
            }else{
                model.addAttribute("likeCount",likesServices.getLike(postId));
                return getProfilePage(model,userId,"Post Unliked");
            }
        } catch (Exception e) {
            Logger.LOGGER.info(e.getMessage());
            return getProfilePage(model,userId,"Something went wrong.");
        }
    }

    @PostMapping("/addComment")
    public String setComment(@ModelAttribute("comments") PostComments postComments, Model model){
        try {
            commentsServices.setComment(postComments);
            getProfilePage(model, postComments.getUserId(), "Comment Added Successfully.");
            return "Profile";
        }catch (Exception e){
            Logger.LOGGER.info(e.getMessage());
            return getProfilePage(model,postComments.getUserId(),"Unable to add comment.");
        }
    }

    public String getProfilePage(Model model, Integer userId, String alert){
        model.addAttribute("postList", postServices.getPostsList(userId));
        model.addAttribute("user_id", userId);
        model.addAttribute("comments", new PostComments());
        model.addAttribute("alert", alert);
        model.addAttribute("name", userServices.getById(userId).getFirstname()+" "+userServices.getById(userId).getLastname());
        model.addAttribute("followerRequestCount", followingServices.getFollowerCount(userId));
        return "Profile";
    }

}
