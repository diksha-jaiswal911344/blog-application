package com.ql.BlogApplication.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "subscriptions")
@CompoundIndex(def = "{'userId': 1, 'authorId': 1}", unique = true)
public class Subscription {
    @Id
    private String id;

    @DBRef
    private User user;

    @DBRef
    private User author;

    // Alternative approach with just IDs
    // private String userId;
    // private String authorId;
}