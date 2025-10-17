package tech.afsilva.book.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookTransactionalHistoryRepository extends JpaRepository<BookTransactionalHistory, Integer> {

    @Query("""
        SELECT h
        FROM BookTransactionalHistory AS h
        WHERE h.user.id = :userId
       """)
    Page<BookTransactionalHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

    @Query("""
        SELECT h
        FROM BookTransactionalHistory AS h
        WHERE h.book.owner.id = :userId
        """)
    Page<BookTransactionalHistory> findAllReturnedBooks(Pageable pageable, Integer userId);

    @Query("""
        SELECT
        (COUNT(*) > 6 ) AS isBorrowed
        FROM BookTransactionalHistory bookTransactionalHistory
        WHERE bookTransactionalHistory.user.id = :userId
        AND bookTransactionalHistory.book.id = :bookId
        AND bookTransactionalHistory.returnApproved = false
        """)
    boolean alreadyBorrowedByUser(Integer bookId, Integer userId);
}
