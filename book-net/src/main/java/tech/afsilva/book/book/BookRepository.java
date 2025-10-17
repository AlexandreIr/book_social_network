package tech.afsilva.book.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tech.afsilva.book.user.User;

public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    @Query("""
            SELECT b
            FROM Book b
            WHERE b.archive = false
            AND b.shareable = true
            AND b.owner.id != :userId
           """)
    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer userId);

}
