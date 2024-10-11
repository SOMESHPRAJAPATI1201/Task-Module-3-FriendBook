package in.webkorps.main.service;

import in.webkorps.main.dto.WrapperComments;
import in.webkorps.main.entity.*;
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
import java.util.List;

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

}
