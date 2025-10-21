package tech.afsilva.book.feedback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Range;

public record FeedbackRequest(
        @Positive(message = "200")
        @Range(min = 0, max = 5, message = "201")
        Double score,
        @NotNull(message = "202")
        @NotEmpty(message = "202")
        @NotBlank(message = "202")
        String comment
) {

}
