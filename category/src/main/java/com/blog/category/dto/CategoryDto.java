package com.blog.category.dto;

import com.blog.category.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

public class CategoryDto {

    @Getter
    @NoArgsConstructor
    public static class SaveRequest{
        private String categoryName;
        private String parentCategory;
        private Map<String,CategoryInfo> subCategory;

        @Builder
        public SaveRequest(String categoryName, String parentCategory, Map<String, CategoryInfo> subCategory) {
            this.categoryName = categoryName;
            this.parentCategory = parentCategory;
            this.subCategory = subCategory;
        }



        public Category toEntity(){
            Category category = Category.builder()
                    .categoryName(categoryName)
                    .build();
            return category;
        }
    }


    @Getter
    @NoArgsConstructor
    public static class CategoryInfo{
        private Long id;
        private String categoryName;
        private String parentCategory;

        @Builder
        public CategoryInfo(Category category) {
            this.id = category.getId();
            this.categoryName = category.getCategoryName();
            this.parentCategory = category.getParentCategory().getCategoryName();
        }

        public Category toEntity(){
            Category category = Category.builder()
                    .categoryName(categoryName)
                    .build();

            return category;
        }

    }

    @Getter
    @NoArgsConstructor
    @Builder
    public static class CategoryResponse{
        private Long id;
        private String categoryName;
        private Map<String,CategoryInfo> subCategory=new HashMap<>();

        public CategoryResponse(Long id, String categoryName, Map<String, CategoryInfo> subCategory) {
            this.id = id;
            this.categoryName = categoryName;
            this.subCategory = subCategory;
        }
    }
}

