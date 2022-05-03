package com.wjy.jpa.repository;

import com.wjy.jpa.model.hierarchy.RedBook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RedBookRepositoryTest {

    @Autowired
    private RedBookRepository redBookRepository;

    @Test
    public void testRedBook() {
        RedBook redBook = new RedBook();
        redBook.setTitle("redbook");
        redBook.setRedMark("redmark");
        redBook.setId(1L);
        redBookRepository.saveAndFlush(redBook);
        RedBook r = redBookRepository.findById(1L).get();
        System.out.println(r.getId() + ":" + r.getTitle() + ":" + r.getRedMark());

    }

}
