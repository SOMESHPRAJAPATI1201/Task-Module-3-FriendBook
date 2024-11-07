package in.webkorps.main.service;

import in.webkorps.main.dto.WrapperLike;
import in.webkorps.main.entity.Likes;
import in.webkorps.main.entity.Posts;
import in.webkorps.main.repository.LikesRepo;
import in.webkorps.main.repository.PostsRepo;
import in.webkorps.main.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LikesServices {

    @Autowired
    private PostsRepo postsRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private LikesRepo likesRepo;


    public List<WrapperLike> getLike(Integer id){
        List<Likes> likeCount = likesRepo.findByPost_PostId(id);
        return likeCount.stream()
                .map(like -> new WrapperLike(like.getId(), like.getUserId(), id))
                .collect(Collectors.toList());
    }

    public int getLike(int id){
        return likesRepo.findByPost_PostId(id).size();
    }


    @Transactional
    public boolean clickLike(Integer userId, Integer postId){
        if (likesRepo.findByUserIdAndPostPostId(userId, postId)==null){
            Likes likes = new Likes();
            Posts posts = new Posts();
            posts.setPostId(postId);
            likes.setUserId(userId);
            likes.setPost(posts);
            likesRepo.save(likes);
            return true;
        }else{
            likesRepo.deleteByUserIdAndPostPostId(userId, postId);
            return false;
        }
    }

}
