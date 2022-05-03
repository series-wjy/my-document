package com.wjy.jpa.dto;

import com.wjy.jpa.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;

import java.util.Iterator;

@RequiredArgsConstructor(staticName = "of")
public class Products implements Streamable<Product> {

  private Streamable<Product> streamable;
  public MonetaryAmount getTotal() {
    return (MonetaryAmount) streamable.stream() //
            .map(Priced :: getPrice)
            .reduce(Money.of(0), MonetaryAmount::add);
  }

  @Override
  public Iterator<Product> iterator() {
    return null;
  }
}