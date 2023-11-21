package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Integer> {

}
