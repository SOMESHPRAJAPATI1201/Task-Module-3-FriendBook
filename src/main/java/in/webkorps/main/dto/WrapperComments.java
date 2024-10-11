package in.webkorps.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrapperComments {

    private Integer id;
    private Integer user_id;
    private Integer postId;
    private String firstName;
    private String comment;
    private String localDateTime;

}