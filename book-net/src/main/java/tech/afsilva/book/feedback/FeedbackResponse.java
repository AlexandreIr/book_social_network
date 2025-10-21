package tech.afsilva.book.feedback;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackResponse {
    private boolean ownFeedback;
    private Double score;
    private String comment;
}
