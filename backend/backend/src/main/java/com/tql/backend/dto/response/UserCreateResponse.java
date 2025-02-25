package com.tql.backend.dto.response;


import com.tql.backend.entity.Image;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateResponse {
    String id;
    String username;
    String firstName;
    String lastName;
    String password;
    String email;
    List<Image> images;
}
