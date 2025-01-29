package com.Library.LMS.dto;

import com.Library.LMS.Persistence.Entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // Makes it easy to create an object with Swagger Schema.
    private Long id;
    private String name;
    private String email;
    private String libraryId;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Role role;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<Long> borrowingIds;
}
