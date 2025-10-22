package tech.afsilva.book.feedback;

import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {
    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .score(request.score())
                .comment(request.comment())
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer userId) {
        return FeedbackResponse.builder()
                .score(feedback.getScore())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), userId))
                .build();
    }

    public OwnFeedbackResponse toOwnFeedbackResponse(Feedback feedback) {
        return OwnFeedbackResponse.builder()
                .bookId(feedback.getBook().getId())
                .comment(feedback.getComment())
                .score(feedback.getScore())
                .build();
    }
}
