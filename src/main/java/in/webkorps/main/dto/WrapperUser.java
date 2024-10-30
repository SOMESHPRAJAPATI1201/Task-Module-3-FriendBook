package in.webkorps.main.dto;

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
    private String displayname;
    private String zip;
    private String state;
    private String city;
    private String imageName;
    private String followStatus;
    private String followBackStatus;

}
