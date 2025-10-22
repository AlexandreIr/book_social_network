package tech.afsilva.book.feedback;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnFeedbackResponse {
    private Double score;
    private String comment;
    private Integer bookId;
}
