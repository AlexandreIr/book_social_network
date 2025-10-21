package tech.afsilva.book.book;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.afsilva.book.common.PageResponse;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {

    private final BookService service;

    @PostMapping
    public ResponseEntity<Integer> saveBook(
            @RequestBody @Valid BookRequest request,
                    Authentication currentUser
    ){
        return ResponseEntity.ok(service.saveBook(request, currentUser));
    }

    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> findBookById(
            @PathVariable("book-id") Integer id){
        return ResponseEntity.ok(service.findBookById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication currentUser
    ){
        return ResponseEntity.ok(service.findAllBooks(page, size, currentUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication currentUser
    ){
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, currentUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication currentUser
    ){
        return ResponseEntity.ok(service.findAllBorrowedBooks(page, size, currentUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication currentUser
    ){
        return ResponseEntity.ok(service.findAllReturnedBooks(page, size, currentUser));
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareable(
            @PathVariable (name = "book-id") Integer bookId,
            Authentication currentUser
    ){
        return ResponseEntity.ok(service.updateShareableStatus(bookId, currentUser));
    }

    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchived(
            @PathVariable (name = "book-id") Integer bookId,
            Authentication currentUser
    ){
        return ResponseEntity.ok(service.updateArchivedStatus(bookId, currentUser));
    }

    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable (name = "book-id") Integer bookId,
            Authentication currentUser
    ){
        return ResponseEntity.ok(service.borrowBook(bookId, currentUser));
    }

    @PatchMapping("/return/{book-id}")
    public ResponseEntity<Integer> returnBook(
            @PathVariable (name = "book-id") Integer bookId,
            Authentication currentUser
    ){
        return ResponseEntity.ok(service.returnBook(bookId, currentUser));
    }

    @PatchMapping("/return/approve-return/{book-id}")
    public ResponseEntity<Integer> approvereturnBook(
            @PathVariable (name = "book-id") Integer bookId,
            Authentication currentUser
    ){
        return ResponseEntity.ok(service.approveReturnBook(bookId, currentUser));
    }

    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCover(
            @PathVariable (name = "book-id") Integer bookId,
            @Parameter()
            Authentication currentUser,
            @RequestPart("file")MultipartFile file
    ){
        service.uploadBookCover(file, currentUser, bookId);
        return ResponseEntity.accepted().build();
    }
}
