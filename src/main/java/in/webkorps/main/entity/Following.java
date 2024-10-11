package in.webkorps.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Following {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer followerId;
    @Enumerated(EnumType.STRING)
    private FollowStatus followStatus;

    public enum FollowStatus {
        FOLLOW, UNFOLLOW, REQUESTED;
    }

}
