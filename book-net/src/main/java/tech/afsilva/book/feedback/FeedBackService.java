package tech.afsilva.book.feedback;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.afsilva.book.book.Book;
import tech.afsilva.book.book.BookRepository;
import tech.afsilva.book.common.PageResponse;
import tech.afsilva.book.exception.OperationNotAllowedException;
import tech.afsilva.book.user.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedBackService {
    private final FeedbackRepository feedbackRepository;
    private final BookRepository bookRepository;
    private final FeedbackMapper mapper;
    private final FeedbackMapper feedbackMapper;

    public Integer saveFeedback(Authentication currentUser,
                                FeedbackRequest feedback,
                                Integer bookId
                                ) {
        User user = ((User) currentUser.getPrincipal());
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->new EntityNotFoundException("Book not found with id: "+bookId));

        if(book.isArchive() || !book.isShareable()){
            throw new OperationNotAllowedException("You cannot give feedback to an archived or non-shareable book");
        }
        if(book.getOwner().getId().equals(user.getId())){
            throw new OperationNotAllowedException("You cannot give feedback to your own book");
        }
        Feedback feedbackReal = mapper.toFeedback(feedback);
        feedbackReal.setBook(book);

        return feedbackRepository.save(feedbackReal).getId();
    }

    public PageResponse<FeedbackResponse> showBookFeedbacks(
            Integer bookId,
            Integer page,
            Integer size,
            Authentication currentUser
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        User user = ((User) currentUser.getPrincipal());
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponse = feedbacks.stream()
                .map(f-> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponse,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalPages(),
                feedbacks.getTotalElements(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
