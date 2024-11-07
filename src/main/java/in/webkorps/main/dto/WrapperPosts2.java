package in.webkorps.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrapperPosts2 {

    private Integer postId;
    private String postName;
    private String formattedDate;
    private String imageName;
    private Integer likeCount;
    private Integer commentCount;
    private String firstName;
    private List<WrapperComments> comments;
    private List<WrapperLike> likes;

}
