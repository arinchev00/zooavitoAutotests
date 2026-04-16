package DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnouncementResponse {
    private Long id;
    private String title;
    private int price;
    private String description;
    private String dateOfPublication;
    private UserResponse user;
    private CategoryResponse category;
    private SubcategoryResponse subcategory;
    private List<Object> images;      // может быть список изображений
    private List<Object> comments;    // может быть список комментариев
    private int commentsCount;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserResponse {
        private Long id;
        private String fullName;
        private String email;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CategoryResponse {
        private Long id;
        private String title;
        private Integer displayOrder;
        private Boolean isHidden;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SubcategoryResponse {
        private Long id;
        private String title;
    }
}
