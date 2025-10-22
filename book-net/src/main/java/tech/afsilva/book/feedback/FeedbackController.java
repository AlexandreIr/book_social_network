package tech.afsilva.book.feedback;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.afsilva.book.common.PageResponse;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {

    private final FeedBackService feedbackService;

    @PostMapping(value = "/{book-id}")
    public ResponseEntity<Integer> saveFeedback(
            Authentication currentUser,
            @Valid @RequestBody FeedbackRequest feedback,
            @PathVariable("book-id") Integer bookId
    ){
        return ResponseEntity.ok(feedbackService.saveFeedback(currentUser, feedback, bookId));
    }

    @GetMapping(value = "/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> showBookFeedbacks(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication currentUser
    ){
        return ResponseEntity.ok(feedbackService.showBookFeedbacks(bookId, page, size, currentUser));
    }

    @GetMapping(value = "/user")
    public ResponseEntity<PageResponse<OwnFeedbackResponse>> showUserFeedbacks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication currentUser
    ){
        return ResponseEntity.ok(feedbackService.showUserFeedbacks(page, size, currentUser));
    }

}
