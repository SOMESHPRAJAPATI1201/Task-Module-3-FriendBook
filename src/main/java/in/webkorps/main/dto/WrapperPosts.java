package in.webkorps.main.dto;

import in.webkorps.main.entity.Likes;
import in.webkorps.main.entity.PostComments;
import in.webkorps.main.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrapperPosts {

    private Integer postId;
    private String postName;
    private LocalDateTime eventDate;
    private String formattedDate;
    private String imageType;
    private String imageName;
    @Lob
    private byte[] imageFile;
    @Lob
    private String imageStringFile;
    private Integer likeCount;
    private User user;
    private List<PostComments> comments;
    private List<Likes> likes;

}
