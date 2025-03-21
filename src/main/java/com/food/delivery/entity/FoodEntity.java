package com.food.delivery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Author: Sandeep Singh
 * Date: 21/03/25
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "foods")
public class FoodEntity {

    @Id
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String category;
    private double price;

}
