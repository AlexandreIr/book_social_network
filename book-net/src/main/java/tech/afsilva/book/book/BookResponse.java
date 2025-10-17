package tech.afsilva.book.book;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
    private Integer id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private boolean archive;
    private boolean shareable;
    private String owner;
    private byte[] cover;
    private double rate;
}
