package in.webkorps.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String address;
    private String address2;
    private String zip;
    private String state;
    private String country;
    private String imageType;
    private String imageName;
    private String followStatus;
    private String followBackStatus;
    @Lob
    private byte[] imageFile;
    @Lob
    private String imageStringFile;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user",  fetch = FetchType.LAZY)
    private List<Posts> posts;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Following> followings;

}
