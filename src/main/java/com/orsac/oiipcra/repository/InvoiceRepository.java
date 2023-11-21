package com.orsac.oiipcra.repository;


import com.orsac.oiipcra.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    Invoice findInvoiceById(Integer invoiceId);
}
