package in.webkorps.main.service;

import in.webkorps.main.dto.WrapperComments;
import in.webkorps.main.entity.PostComments;
import in.webkorps.main.entity.Posts;
import in.webkorps.main.repository.CommentsRepo;
import in.webkorps.main.repository.LikesRepo;
import in.webkorps.main.repository.PostsRepo;
import in.webkorps.main.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentsServices {

    @Autowired
    private PostsRepo postsRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private LikesRepo likesRepo;

    @Autowired
    private CommentsRepo commentsRepo;


    public ResponseEntity<?> setComment(PostComments postComments){
        postComments.setFirstName(userRepo.findById(postComments.getUserId()).get().getFirstname());
        postComments.setLocalDateTime(LocalDateTime.now());
        return new ResponseEntity<>(commentsRepo.save(postComments), HttpStatus.OK);
    }

    public String deleteComment(Integer id,Integer postId,Integer userId){
            Posts posts = postsRepo.findById(postId).get();
            System.out.println(posts.getPostId());
            PostComments comments = commentsRepo.findById(id).get();
            System.out.println(comments.getUserId());
            if(comments.getUserId()==userId || posts.getUser().getUserId()==userId){
                commentsRepo.deleteById(id);
                return "Comment Deleted Successfully.";
            }else{
                return "Comment deletion failed.You can delete it when you commented it or comment is on your post.";
            }
    }

    public PostComments setComment(String comment,Integer userId,Integer postId){
        PostComments postComments = new PostComments();
        postComments.setComment(comment);
        postComments.setUserId(userId);
        postComments.setPost(postsRepo.findById(postId).get());
        postComments.setFirstName(userRepo.findById(postComments.getUserId()).get().getFirstname());
        postComments.setLocalDateTime(LocalDateTime.now());
        return commentsRepo.save(postComments);
    }

    public List<WrapperComments> getCommentsList(Integer id){
        List<PostComments> comments = commentsRepo.findByPostPostId(id);
        List<WrapperComments> wrapperComments = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for(PostComments comm : comments){
            String formattedDate = comm.getLocalDateTime() != null ? comm.getLocalDateTime().format(formatter) : "";
            comm.setFormattedDate(formattedDate);
            wrapperComments.add(new WrapperComments(comm.getId(),comm.getUserId(),id, comm.getFirstName(), comm.getComment(),comm.getFormattedDate()));
        }
        return wrapperComments;
    }

    public Map<String,Object> editComment(Integer userId, Integer postId, Integer commentId,String comment) {
        Map<String,Object> map = new HashMap<>();
        Posts posts = postsRepo.findById(postId).get();
        PostComments comments = commentsRepo.findById(commentId).get();
        if(comments.getUserId()==userId || posts.getUser().getUserId()==userId){
            if(comment==null || comment.equalsIgnoreCase("")){
                map.put("mesg","Invalid Comment Input.");
                return map;
            }else{
                comments.setLocalDateTime(LocalDateTime.now());
                comments.setComment(comment);
                commentsRepo.save(comments);
                map.put("mesg","Comment Updated Successfully.");
                return map;
            }
        }else{
            map.put("mesg","Sorry, you can't edit others comment whether it's on your post.");
            return map;
        }
    }
}
