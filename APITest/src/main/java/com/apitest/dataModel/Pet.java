package com.apitest.dataModel;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pet {
    private Long id;
    private Category category;
    private String name;
    private List<String> photoUrls = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();
    private String status;

    @Getter
    @AllArgsConstructor
    public enum PetStatuses {
        AVAILABLE ("available"),
        PENDING ("pending"),
        SOLD ("sold"),
        UNDEFINED("undefined");

        private final String status;
    }
}
