package com.rayara.sevasetu.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.rayara.sevasetu.data.database.entities.Receipt
import java.io.File
import java.io.FileOutputStream

class PDFGenerator(private val context: Context) {
    
    fun generateReceiptPDF(receipt: Receipt): File {
        val pdfDir = File(context.getExternalFilesDir(null), Constants.PDF.RECEIPT_FOLDER)
        if (!pdfDir.exists()) {
            pdfDir.mkdirs()
        }
        
        val pdfFile = File(
            pdfDir,
            "${Constants.PDF.FILE_PREFIX}${receipt.receiptNumber}${Constants.PDF.FILE_EXTENSION}"
        )
        
        val writer = PdfWriter(FileOutputStream(pdfFile))
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument, PageSize.A6)
        
        document.setMargins(20f, 20f, 20f, 20f)
        
        addHeader(document)
        addReceiptDetails(document, receipt)
        addServiceDetails(document, receipt)
        addFooter(document)
        
        document.close()
        
        return pdfFile
    }
    
    private fun addHeader(document: Document) {
        val orgName = Paragraph(Constants.Organization.NAME)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(14f)
            .setBold()
            .setMarginBottom(5f)
        
        val orgAddress = Paragraph(Constants.Organization.ADDRESS)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(10f)
            .setMarginBottom(15f)
        
        document.add(orgName)
        document.add(orgAddress)
        document.add(LineSeparator(SolidBorder(1f) as com.itextpdf.layout.element.ILineDrawer))
    }
    
    private fun addReceiptDetails(document: Document, receipt: Receipt) {
        val receiptInfo = Table(2)
        receiptInfo.setWidth(UnitValue.createPercentValue(100f))
        receiptInfo.setMarginTop(10f)
        receiptInfo.setMarginBottom(10f)
        
        receiptInfo.addCell(
            Paragraph("${Constants.Receipt.RECEIPT_PREFIX} ${receipt.receiptNumber}")
                .setFontSize(10f)
                .setBold()
        )
        receiptInfo.addCell(
            Paragraph("${Constants.Receipt.DATE_PREFIX} ${receipt.date}")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.RIGHT)
        )
        
        document.add(receiptInfo)
        
        val customerName = Paragraph("${Constants.Receipt.CUSTOMER_PREFIX} ${receipt.customerName}")
            .setFontSize(10f)
            .setMarginBottom(5f)
        
        val customerPhone = Paragraph("${Constants.Receipt.PHONE_PREFIX} ${receipt.customerPhone}")
            .setFontSize(10f)
            .setMarginBottom(10f)
        
        document.add(customerName)
        document.add(customerPhone)
        document.add(LineSeparator(SolidBorder(0.5f) as com.itextpdf.layout.element.ILineDrawer))
    }
    
    private fun addServiceDetails(document: Document, receipt: Receipt) {
        val serviceTable = Table(2)
        serviceTable.setWidth(UnitValue.createPercentValue(100f))
        serviceTable.setMarginTop(10f)
        serviceTable.setMarginBottom(10f)
        
        serviceTable.addCell(
            Paragraph(receipt.serviceDescription)
                .setFontSize(11f)
        )
        serviceTable.addCell(
            Paragraph(receipt.getFormattedAmount())
                .setFontSize(11f)
                .setTextAlignment(TextAlignment.RIGHT)
        )
        
        document.add(serviceTable)
        document.add(LineSeparator(SolidBorder(0.5f) as com.itextpdf.layout.element.ILineDrawer))
        
        val totalTable = Table(2)
        totalTable.setWidth(UnitValue.createPercentValue(100f))
        totalTable.setMarginTop(10f)
        totalTable.setMarginBottom(5f)
        
        totalTable.addCell(
            Paragraph(Constants.Receipt.TOTAL_LABEL)
                .setFontSize(12f)
                .setBold()
        )
        totalTable.addCell(
            Paragraph(receipt.getFormattedAmount())
                .setFontSize(12f)
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT)
        )
        
        document.add(totalTable)
        
        val paymentMode = Paragraph(
            "${Constants.Receipt.PAYMENT_MODE_LABEL} ${receipt.getPaymentModeEnum().kannadaName}"
        )
            .setFontSize(10f)
            .setMarginBottom(10f)
        
        document.add(paymentMode)
        document.add(LineSeparator(SolidBorder(0.5f) as com.itextpdf.layout.element.ILineDrawer))
    }
    
    private fun addFooter(document: Document) {
        val trust = Paragraph(Constants.Organization.TRUST)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(9f)
            .setMarginTop(15f)
            .setMarginBottom(10f)
        
        val thankYou = Paragraph(Constants.Receipt.THANK_YOU)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(11f)
            .setBold()
            .setMarginTop(10f)
        
        document.add(trust)
        document.add(thankYou)
    }
}
