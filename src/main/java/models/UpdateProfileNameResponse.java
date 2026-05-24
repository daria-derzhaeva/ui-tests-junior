package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateProfileNameResponse extends BaseModel {

    private String message;
    private Customer customer;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Customer {

        private String name;
    }
}