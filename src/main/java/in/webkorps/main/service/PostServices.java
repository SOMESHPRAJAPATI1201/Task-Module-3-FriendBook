package in.webkorps.main.service;

import in.webkorps.main.dto.WrapperComments;
import in.webkorps.main.entity.Following;
import in.webkorps.main.entity.PostComments;
import in.webkorps.main.entity.Posts;
import in.webkorps.main.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServices {

    @Autowired
    private PostsRepo postsRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private LikesRepo likesRepo;

    @Autowired
    private CommentsRepo commentsRepo;

    @Autowired
    private FollowingsRepo followingsRepo;


    public List<Posts> savePosts(String content, MultipartFile file, Integer integer) throws IOException {
        Posts posts = new Posts();
        posts.setPostName(content);
        posts.setImageFile(file.getBytes());
        posts.setImageName(file.getOriginalFilename());
        posts.setImageType(file.getContentType());
        posts.setEventDate(LocalDateTime.now());
        posts.setUser(userRepo.findById(integer).get());
        postsRepo.save(posts);
        return getFilteredList(postsRepo.findAll());
    }

    public List<Posts> allPosts() {
        return getFilteredList(postsRepo.findAll());
    }

    public List<Posts> getPostsList(Integer userId) {
        List<Following> list = followingsRepo.findByUserId(userId);
        List<Posts> posts = new ArrayList<>();
        for (Following follower : list) {
            if (follower.getFollowStatus().toString().equals("UNFOLLOW")) {
                posts.addAll(getFilteredList(postsRepo.findAllByUserUserId(follower.getFollowerId())));
            }
        }
        posts.addAll(getFilteredList(postsRepo.findAllByUserUserId(userId)));
        Collections.reverse(posts);
        return posts;
    }

    public List<Posts> allPostsByIDs(List<Integer> ids) {
        return getFilteredList(postsRepo.findAllByUserIdIn(ids));
    }

    public List<Posts> allPostsByID(Integer id) {
        return getFilteredList(postsRepo.findAllByUserUserId(id));
    }


    public List<Posts> getFilteredList(List<Posts> posts) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return posts.stream()
                .map(post -> {
                    if (post != null) {
                        String base64Image = post.getImageFile() != null
                                ? Base64.getEncoder().encodeToString(post.getImageFile())
                                : ""; // Handle null image case

                        String formattedDate = post.getEventDate() != null
                                ? post.getEventDate().format(formatter)
                                : "";

                        post.setLikeCount(post.getLikes() != null ? post.getLikes().size() : 0);
                        post.setFormattedDate(formattedDate);
                        post.setImageStringFile("data:image/png;base64," + base64Image);
                        List<PostComments> comments = commentsRepo.findByPostPostId(post.getPostId()).stream().map(
                                comment -> {
                                    if (comment != null) {
                                        String formattedDate1 = comment.getLocalDateTime() != null
                                                ? comment.getLocalDateTime().format(formatter)
                                                : "";
                                        comment.setFormattedDate(formattedDate1);
                                    }
                                    return comment;
                                }
                        ).collect(Collectors.toList());
                        post.setComments(comments);
                    }
                    return post;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


}
