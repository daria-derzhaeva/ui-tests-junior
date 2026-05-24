package generators;

import models.CreateUserRequest;
import models.LoginUserRequest;
import models.UpdateProfileNameRequest;
import models.UserRole;

public class RandomModelGenerator {

    private RandomModelGenerator() {
    }

    public static CreateUserRequest getUserCreateRequest() {
        return CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();
    }

    public static LoginUserRequest getLoginUserRequest(String username) {
        return LoginUserRequest.builder()
                .username(username)
                .password(RandomData.getPassword())
                .build();
    }

    public static UpdateProfileNameRequest getUpdateProfileNameRequest(String name) {
        return UpdateProfileNameRequest.builder()
                .name(name)
                .build();
    }
}