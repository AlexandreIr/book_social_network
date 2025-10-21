package tech.afsilva.book.book;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import tech.afsilva.book.file.FileUtils;
import tech.afsilva.book.history.BookTransactionalHistory;

@Service
public class BookMapper {

    public Book toBook(@Valid BookRequest request) {
        return  Book.builder()
                .id(request.id())
                .title(request.title())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .archive(false)
                .shareable(request.shareable())
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .archive(book.isArchive())
                .shareable(book.isShareable())
                .rate(book.getRate())
                .owner(book.getOwner().getFullName())
                .cover(FileUtils.readFileFromLocation(book.getBookCover()))
                .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionalHistory history) {
        return  BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved())
                .build();
    }
}
