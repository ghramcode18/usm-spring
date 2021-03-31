package org.closure.app.UserModule.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import lombok.NoArgsConstructor;
import lombok.With;

@Getter @Setter 
@RequiredArgsConstructor
@AllArgsConstructor
@With
@ToString 
@EqualsAndHashCode
public class UserResponse {
    private Long id;
    private String name;
    private String img;
}
