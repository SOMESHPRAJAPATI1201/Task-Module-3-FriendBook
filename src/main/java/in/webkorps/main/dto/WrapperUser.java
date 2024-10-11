package in.webkorps.main.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrapperUser {

    private Integer userId;
    private String firstname;
    private String lastname;
    private String username;
    private String address;
    private String address2;
    private String zip;
    private String state;
    private String country;
    private String imageType;
    private String imageName;
    @Lob
    private byte[] imageFile;
    @Lob
    private String imageStringFile;
    private String followStatus;
    private String followBackStatus;

}
