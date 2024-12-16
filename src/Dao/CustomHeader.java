/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 *
 * @author juanc
 */
public class CustomHeader extends PdfPageEventHelper {
    private int folio;

    public CustomHeader(int folio) {
        this.folio = folio;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        // Aquí puedes configurar el contenido del encabezado en cada página
        Font fontRojo = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.RED); // Agrega texto rojo
        PdfPTable table = new PdfPTable(1);
        PdfPCell cell = new PdfPCell(new Phrase("No. " + folio,fontRojo));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        table.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
        table.writeSelectedRows(0, -1, document.leftMargin(), document.top() + ((document.topMargin() + table.getTotalHeight()) / 2), writer.getDirectContent());
    }
}
