package twodo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_id")
    private TodoList todoList;

    private String content;

    @Builder.Default
    private boolean isTwo = false; // true: 핵심 두 목표

    @Builder.Default
    private boolean completed = false;

    @Builder.Default
    private int pointsEarned = 0;

}
