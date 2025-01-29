package com.Library.LMS.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {

// Data Transfer Object (DTO) to ensure sensitive data is not exposed in endpoint responses

    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // Makes it easy to create an object with Swagger Schema.
    private Long id;
    private String title;
    private String author;
    private Long isbn;
    private boolean availability_status;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<Long> borrowingIds;
}
