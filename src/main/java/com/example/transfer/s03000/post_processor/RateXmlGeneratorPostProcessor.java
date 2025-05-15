package com.example.transfer.s03000.post_processor;

import com.example.transfer.dbf.exception.DbfException;
import com.example.transfer.dbf.processor.PostProcessor;
import com.example.transfer.s03000.entity.Rate;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.FileWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class RateXmlGeneratorPostProcessor implements PostProcessor {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void process() {
        String fileName = "L:\\W_BUH\\МНС\\Плательщик V30\\description\\currency_rate.xml";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("<?xml version=\"1.0\" encoding=\"windows-1251\" ?>\n");
            writer.write("<root>\n");

            String sql = "SELECT r FROM Rate r WHERE r.kod IN (1, 978, 858) AND r.date >= :startDate ORDER BY r.date";
            TypedQuery<Rate> query = entityManager.createQuery(sql, Rate.class);
            query.setParameter("startDate", Date.valueOf("2010-01-01"));

            List<Rate> rates = query.getResultList();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            int k = 0;
            for (Rate rate : rates) {
                k++;
                writer.write("<item id=\"");
                writer.write(String.valueOf(k));
                writer.write("\" code=\"");

                if (rate.getKod() == 858) {
                    writer.write("643");
                } else if (rate.getKod() == 1) {
                    writer.write("840");
                } else if (rate.getKod() == 978) {
                    writer.write("978");
                }

                writer.write("\" date=\"");
                writer.write(dateFormat.format(rate.getDate()));
                writer.write("\" value=\"");
                writer.write(String.valueOf(rate.getKurs()));
                writer.write("\"/>\n");
            }

            writer.write("</root>\n");
        } catch (Exception e) {
            throw new DbfException("Ошибка при генерации XML-файла: " + fileName);
        }
    }
}