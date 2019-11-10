package com.lxc.service.impl;

import com.lxc.entity.Book;
import com.lxc.repository.BookRepository;
import com.lxc.service.impl.BookServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityNotFoundException;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceImplTest {

    private final int PAGE_SIZE = 10;

    private final int PAGE_NUM = 0;

    private Pageable pageable = PageRequest.of(PAGE_NUM, PAGE_SIZE, new Sort(Sort.Direction.ASC, "id"));

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    public void findAllByPage_happyPath() {

        Page mockedPage = mock(Page.class);
        when(bookRepository.findAll(pageable)).thenReturn(mockedPage);

        assertThat(bookService.findAllByPage(PAGE_NUM), is(mockedPage));
    }

    @Test
    public void findAllByPage_shouldReturnNull_ifNoBooks() {

        when(bookRepository.findAll(pageable)).thenReturn(null);

        assertThat(bookService.findAllByPage(PAGE_NUM), is(nullValue()));
    }

    @Test
    public void update_happyPath() {

        Book newBook = Book.builder().id(1).build();
        Book temp = mock(Book.class);
        when(bookRepository.getOne(1)).thenReturn(temp);

        bookService.update(newBook);

        verify(bookRepository).saveAndFlush(temp);
    }

    @Test
    public void update_shouldDoNothing_ifBookIdDoesNotExist() {

        Book newBook = Book.builder().id(1).build();
        when(bookRepository.getOne(1)).thenThrow(EntityNotFoundException.class);

        bookService.update(newBook);

        verify(bookRepository, never()).saveAndFlush(any());
    }

    @Test
    public void deleteById_happyPath() {

        bookService.deleteById(1);

        verify(bookRepository).deleteById(1);
    }

    @Test
    public void deleteById_shouldDoNothing_ifBookIdDoesNotExist() {

        when(bookRepository.getOne(1)).thenThrow(EntityNotFoundException.class);

        bookService.deleteById(1);

        verify(bookRepository, never()).deleteById(1);
    }

    @Test
    public void findByName_happyPath() {

        Page mockedPage = mock(Page.class);
        when(bookRepository.findByBookName("abc", pageable)).thenReturn(mockedPage);

        assertThat(bookService.findPageableByCondition("name", "abc", PAGE_NUM), is(mockedPage));
    }

    @Test
    public void findByName_shouldReturnNull_ifBookNameDoesNotExist() {

        when(bookRepository.findByBookName("abc", pageable)).thenReturn(null);

        assertThat(bookService.findPageableByCondition("name", "abc", PAGE_NUM), is(nullValue()));
    }

    @Test
    public void findByAuthor_happyPath() {

        Page mockedPage = mock(Page.class);
        when(bookRepository.findByAuthor("abc", pageable)).thenReturn(mockedPage);

        assertThat(bookService.findPageableByCondition("author", "abc", PAGE_NUM), is(mockedPage));
    }

    @Test
    public void findByAuthor_shouldReturnNull_ifAuthorDoesNotExist() {

        when(bookRepository.findByAuthor("abc", pageable)).thenReturn(null);

        assertThat(bookService.findPageableByCondition("author", "abc", PAGE_NUM), is(nullValue()));
    }

    @Test
    public void findByIsbn_happyPath() {

        Book expected = Book.builder().isbn("123").build();
        when(bookRepository.findByIsbn("123")).thenReturn(expected);

        Book actual = bookService.findByIsbn("123");

        assertThat(actual, is(expected));
    }

    @Test
    public void findByIsbn_shouldReturnNull_ifIsbnDoesNotExist() {

        when(bookRepository.findByIsbn("123")).thenThrow(EntityNotFoundException.class);

        assertThat(bookService.findByIsbn("123"), is(nullValue()));
    }

    @Test
    public void setStatus_happyPath() {

        Book actual = new Book();
        when(bookRepository.getOne(1)).thenReturn(actual);

        bookService.setStatus("AVAILABLE", 1);

        verify(bookRepository).saveAndFlush(actual);
        assertThat(actual.getStatus(), equalTo("AVAILABLE"));
    }

    @Test
    public void setStatus_shouldDoNothing_ifBookIdDoesNotExist() {

        when(bookRepository.getOne(1)).thenThrow(EntityNotFoundException.class);

        bookService.setStatus("ANY", 1);

        verify(bookRepository, never()).saveAndFlush(any());
    }

    @Test
    public void findById_happyPath() {

        Book expected = new Book();
        when(bookRepository.getOne(1)).thenReturn(expected);

        Book actual = bookService.findById(1);

        assertThat(actual, is(expected));
    }

    @Test
    public void findById_shouldReturnNull_ifBookIdDoesNotExist() {

        when(bookRepository.getOne(1)).thenThrow(EntityNotFoundException.class);

        assertThat(bookService.findById(1), is(nullValue()));
    }

    @Test
    public void addBook_happyPath() {

        Book book = Book.builder().isbn("123").build();
        when(bookRepository.findByIsbn("123")).thenReturn(null);

        assertThat(bookService.addBook(book), is("success"));
        verify(bookRepository).saveAndFlush(book);
    }

    @Test
    public void addBook_shouldFail_ifBookExists() {

        Book book = Book.builder().isbn("123").build();
        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(book);

        assertThat(bookService.addBook(book), is("fail"));
    }

    @Test
    public void decreaseStock_happyPath() {

        Book book = createBook(1, 1);
        when(bookRepository.getOne(book.getId())).thenReturn(book);

        bookService.decreaseStock(book.getId());

        verify(bookRepository).saveAndFlush(book);
        assertThat(book.getStock(), is(0));
    }

    @Test
    public void decreaseStock_shouldBeZero_ifStockIsZero() {

        Book book = createBook(1, 0);
        when(bookRepository.getOne(book.getId())).thenReturn(book);

        bookService.decreaseStock(book.getId());

        verify(bookRepository).saveAndFlush(book);
        assertThat(book.getStock(), is(0));
    }

    @Test
    public void decreaseStock_shouldDoNothing_ifBookIdDoesNotExist() {

        when(bookRepository.getOne(1)).thenThrow(EntityNotFoundException.class);

        bookService.decreaseStock(1);

        verify(bookRepository, never()).saveAndFlush(any());
    }

    @Test
    public void increaseStock_happyPath() {

        Book book = createBook(1, 1);
        when(bookRepository.getOne(book.getId())).thenReturn(book);
<<<<<<< HEAD

        bookService.increaseStock(book.getId(), 1);

=======
        bookService.increaseStock(book.getId(), 1);
>>>>>>> aaf0422d94439d859bb47ee116a0f517071a213c
        verify(bookRepository).saveAndFlush(book);
        assertThat(book.getStock(), is(2));
    }

    @Test
    public void increaseStock_shouldDoNothing_ifBookIdDoesNotExist() {

        when(bookRepository.getOne(1)).thenThrow(EntityNotFoundException.class);
<<<<<<< HEAD

        bookService.increaseStock(1, 1);

=======
        bookService.increaseStock(1, 1);
>>>>>>> aaf0422d94439d859bb47ee116a0f517071a213c
        verify(bookRepository, never()).saveAndFlush(any());
    }

    private Book createBook(Integer bookId, Integer stock) {

        return Book.builder().id(bookId).stock(stock).build();
    }
}
