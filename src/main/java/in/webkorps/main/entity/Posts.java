package in.webkorps.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;
    private String postName;
    private LocalDateTime eventDate;
    private String formattedDate;
    private String imageName;
    private Integer likeCount;
    private Integer commentCount;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post",  fetch = FetchType.LAZY)
    private List<PostComments> comments;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post",  fetch = FetchType.LAZY)
    private List<Likes> likes;

}
