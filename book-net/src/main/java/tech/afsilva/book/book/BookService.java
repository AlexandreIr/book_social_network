package tech.afsilva.book.book;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.afsilva.book.common.PageResponse;
import tech.afsilva.book.exception.OperationNotAllowedException;
import tech.afsilva.book.file.FileStorageService;
import tech.afsilva.book.history.BookTransactionalHistory;
import tech.afsilva.book.history.BookTransactionalHistoryRepository;
import tech.afsilva.book.user.User;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookTransactionalHistoryRepository bookTransactionalHistoryRepository;
    private final BookMapper bookMapper;
    private final FileStorageService fileStorageService;

    public Integer saveBook(@Valid BookRequest request, Authentication currentUser) {
        User user = ((User) currentUser.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findBookById(Integer id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(()->new RuntimeException("Book not found with id: "+id));
    }


    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication currentUser) {
        User user = (User) currentUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponse = books.getContent().stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalPages(),
                books.getTotalElements(),
                books.isLast(),
                books.isFirst()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication currentUser) {
        User user = (User) currentUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwner(user.getId()), pageable);
        List<BookResponse> bookResponse = books.getContent().stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalPages(),
                books.getTotalElements(),
                books.isLast(),
                books.isFirst()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication currentUser) {
        User user = (User) currentUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionalHistory> allBorrowedBooks =
                bookTransactionalHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.isLast(),
                allBorrowedBooks.isFirst()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication currentUser) {
        User user = (User) currentUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionalHistory> allBorrowedBooks =
                bookTransactionalHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.isLast(),
                allBorrowedBooks.isFirst()
        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication currentUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->new EntityNotFoundException("Book not found with id: "+bookId));
        User user = (User) currentUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotAllowedException("You cannot update book status that you don't own");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication currentUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->new EntityNotFoundException("Book not found with id: "+bookId));
        User user = (User) currentUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotAllowedException("You cannot update book status that you don't own");
        }
        book.setArchive(!book.isArchive());
        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication currentUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->new EntityNotFoundException("Book not found with id: "+bookId));
        User user = (User) currentUser.getPrincipal();
        if(book.isArchive()){
            throw new OperationNotAllowedException("You cannot borrow a book that is archived");
        }
        if(!book.isShareable()){
            throw new OperationNotAllowedException("You cannot borrow a book that is shareable");
        }
        if(book.getOwner().getId().equals(user.getId())){
            throw new OperationNotAllowedException("You cannot borrow a book that is yours");
        }
        final boolean alreadyBorrowed = bookTransactionalHistoryRepository.alreadyBorrowedByUser(bookId, user.getId());
        if(alreadyBorrowed){
            throw new OperationNotAllowedException("This book is already borrowed");
        }
        BookTransactionalHistory bookTransactionalHistory = BookTransactionalHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return bookTransactionalHistoryRepository.save(bookTransactionalHistory).getId();

    }

    public Integer returnBook(Integer bookId, Authentication currentUser) {
        BookTransactionalHistory bth = bookTransactionalHistoryRepository.findByBookId(bookId);
        User user = (User) currentUser.getPrincipal();

        if(!bth.getUser().getId().equals(user.getId())){
            throw new OperationNotAllowedException("You didn't lent this book, so you can't return it");
        }

        bth.setReturned(true);
        return  bookTransactionalHistoryRepository.save(bth).getId();
    }

    public Integer approveReturnBook(Integer bookId, Authentication currentUser) {
        BookTransactionalHistory bth = bookTransactionalHistoryRepository.findByBookId(bookId);
        User user = (User) currentUser.getPrincipal();

        if(!Objects.equals(bth.getBook().getOwner().getId(), user.getId())){
            throw new OperationNotAllowedException("You didn't borrow this book, so you can't approve the return");
        }
        if(!bth.isReturned()){
            throw new OperationNotAllowedException("You Can't approve a return that's not yet happened");
        }

        bth.setReturnApproved(true);
        return bookTransactionalHistoryRepository.save(bth).getId();
    }

    public void uploadBookCover(MultipartFile file, Authentication currentUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->new EntityNotFoundException("Book not found with id: "+bookId));
        User user = ((User) currentUser.getPrincipal());
        var bookCover = fileStorageService.saveFile(file, user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }
}
