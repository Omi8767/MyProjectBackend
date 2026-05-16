package com.project.backend.service;

import com.project.backend.entity.Order;
import com.project.backend.entity.OrderItem;
import org.springframework.stereotype.Service;

import com.itextpdf.text.*;
import java.io.ByteArrayOutputStream;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class InvoiceService {

    public byte[] generateInvoice(Order order) {
        try {
            Document doc = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, out);

            doc.open();

            // TITLE
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            doc.add(new Paragraph(" "));

            // ORDER INFO
            doc.add(new Paragraph("Order ID: " + order.getId()));
            doc.add(new Paragraph("Date: " + order.getOrderDate()));
            doc.add(new Paragraph("Customer: " + order.getCustomer().getName()));

            doc.add(new Paragraph(" "));

            // TABLE
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            table.addCell("Product");
            table.addCell("Qty");
            table.addCell("Price");
            table.addCell("Total");

            for (OrderItem item : order.getItems()) {
                table.addCell(item.getProductName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell("₹" + item.getPrice());
                table.addCell("₹" + item.getTotal());
            }

            doc.add(table);

            doc.add(new Paragraph(" "));

            // TOTAL
            doc.add(new Paragraph("Subtotal: ₹" + order.getTotalAmount()));

            if (order.getPayment() != null) {
                doc.add(new Paragraph("GST: ₹" + order.getPayment().getGstAmount()));
                doc.add(new Paragraph("Discount: ₹" + order.getPayment().getDiscountAmount()));
                doc.add(new Paragraph("Final: ₹" + order.getPayment().getNetAmount()));
                if ("COD".equals(order.getPayment().getPaymentMethod())){
                    doc.add(new Paragraph(" "));
                    doc.add(new Paragraph("Pay Mode: ₹" + order.getPayment().getPaymentMethod()));
                    doc.add(new Paragraph("Payment Should Pay at doorstep before receiving the order...!!"));
                }
            }

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Invoice failed");
        }
    }

}
