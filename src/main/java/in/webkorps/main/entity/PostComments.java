package in.webkorps.main.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private String comment;
    private String firstName;
    private LocalDateTime localDateTime;
    private String formattedDate;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Posts post;

}
