package in.webkorps.main.service;

import in.webkorps.main.dto.WrapperComments;
import in.webkorps.main.dto.WrapperLike;
import in.webkorps.main.dto.WrapperPosts2;
import in.webkorps.main.entity.Following;
import in.webkorps.main.entity.Likes;
import in.webkorps.main.entity.PostComments;
import in.webkorps.main.entity.Posts;
import in.webkorps.main.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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


    public Posts savePosts(String content, MultipartFile file, Integer integer) throws IOException {
        Posts posts = new Posts();
        posts.setPostName(content);
        if (!file.isEmpty()) {
            File uploadDir = new File("C:\\Users\\Dell\\Documents\\FriendBook\\posts\\");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Use a unique filename or handle existing files
            String fileName = file.getOriginalFilename();
            File serverFile = new File(uploadDir, fileName);

            // Ensure you handle the case where the file might already exist
            if (serverFile.exists()) {
                // Handle duplicate files, for example, by appending a timestamp or incrementing a counter
                String newFileName = System.currentTimeMillis() + "_" + fileName; // Append current time to make it unique
                serverFile = new File(uploadDir, newFileName);
            }

            try (FileOutputStream fos = new FileOutputStream(serverFile)) {
                fos.write(file.getBytes());
                System.out.println("File saved successfully: " + serverFile.getAbsolutePath());
            }
        } else {
            System.out.println("File is empty");
        }
        posts.setImageName(file.getOriginalFilename());
        posts.setEventDate(LocalDateTime.now());
        posts.setUser(userRepo.findById(integer).get());
        return postsRepo.save(posts);
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

    public List<Posts> getMyPostsList(Integer userId) {
        List<Posts> posts = new ArrayList<>();
        posts.addAll(getFilteredList(postsRepo.findAllByUserUserId(userId)));
        Collections.reverse(posts);
        return posts;
    }

    public List<WrapperPosts2> getFilterPostsList(List<Posts> postsList){
        List<WrapperPosts2> wrapperPosts = new ArrayList<>();
        List<WrapperComments> wrapperComments = null;
        List<WrapperLike> wrapperLikes1 = null;
        for (Posts post : postsList) {
            wrapperComments = new ArrayList<>();
            wrapperLikes1 = new ArrayList<>();
            for(PostComments comments : post.getComments()){
                wrapperComments.add(new WrapperComments(comments.getId(),comments.getUserId(),post.getPostId(),comments.getFirstName(),comments.getComment(),comments.getFormattedDate()));
            }
            for(Likes likes : post.getLikes()){
                wrapperLikes1.add(new WrapperLike(likes.getId(), likes.getUserId(),post.getPostId()));
            }

            wrapperPosts.add(new WrapperPosts2(post.getPostId(), post.getPostName(), post.getFormattedDate(), post.getImageName(), post.getLikeCount(), post.getComments().size(),post.getUser().getFirstname(),wrapperComments,wrapperLikes1));
        }
        return wrapperPosts;
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
                        String formattedDate = post.getEventDate() != null
                                ? post.getEventDate().format(formatter)
                                : "";
                        post.setLikeCount(post.getLikes() != null ? post.getLikes().size() : 0);
                        post.setCommentCount(post.getComments() != null ? post.getComments().size() : 0);
                        post.setFormattedDate(formattedDate);
                        List<PostComments> comments = commentsRepo.findByPostPostId(post.getPostId()).stream().map(
                                comment -> {
                                    if (comment != null) {
                                        String formattedDate1 = comment.getLocalDateTime() != null
                                                ? comment.getLocalDateTime().format(formatter)
                                                : "";
                                        comment.setFormattedDate(formattedDate1);
                                        comment.setPost(null);
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
