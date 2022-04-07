package com.apitest.dataModel;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private int userStatus;

    @Getter
    @AllArgsConstructor
    public enum UserStatuses {
        REGISTERED (1),
        ACTIVE (2),
        CLOSED (3),
        UNDEFINED(4);

        private final int status;
    }
}
