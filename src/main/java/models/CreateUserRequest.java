package models;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest extends BaseModel {

    private String username;
    private String password;
    private String role;
}