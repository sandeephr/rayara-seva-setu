package com.rayara.sevasetu.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.rayara.sevasetu.data.database.entities.Receipt
import java.io.File
import java.io.FileOutputStream

class PDFGenerator(private val context: Context) {
    
    private fun getKannadaFont(): PdfFont? {
        return try {
            // First try to load from bundled assets (guaranteed to work)
            val assetManager = context.assets
            val fontStream = assetManager.open("fonts/NotoSansKannada-Regular.ttf")
            val fontBytes = fontStream.readBytes()
            fontStream.close()
            
            PdfFontFactory.createFont(
                fontBytes, 
                PdfEncodings.IDENTITY_H, 
                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
            )
        } catch (e: Exception) {
            try {
                // Fallback to system font if assets fail
                PdfFontFactory.createFont(
                    "/system/fonts/NotoSansKannada-Regular.ttf", 
                    PdfEncodings.IDENTITY_H, 
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
                )
            } catch (e2: Exception) {
                try {
                    // Last fallback
                    PdfFontFactory.createFont(
                        "/system/fonts/DroidSansFallback.ttf", 
                        PdfEncodings.IDENTITY_H, 
                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
                    )
                } catch (e3: Exception) {
                    null
                }
            }
        }
    }
    
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
        
        val kannadaFont = getKannadaFont()
        
        addHeader(document, kannadaFont)
        addReceiptDetails(document, receipt, kannadaFont)
        addServiceDetails(document, receipt, kannadaFont)
        addFooter(document, kannadaFont)
        
        document.close()
        
        return pdfFile
    }
    
    private fun addHeader(document: Document, font: PdfFont?) {
        val orgName = Paragraph(Constants.Organization.NAME)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(14f)
            .setBold()
            .setMarginBottom(5f)
        font?.let { orgName.setFont(it) }
        
        val orgAddress = Paragraph(Constants.Organization.ADDRESS)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(10f)
            .setMarginBottom(15f)
        font?.let { orgAddress.setFont(it) }
        
        document.add(orgName)
        document.add(orgAddress)
        document.add(Paragraph("―――――――――――――――――――――――――――――――――――").setTextAlignment(TextAlignment.CENTER).setMarginTop(5f).setMarginBottom(10f))
    }
    
    private fun addReceiptDetails(document: Document, receipt: Receipt, font: PdfFont?) {
        val receiptInfo = Table(2)
        receiptInfo.setWidth(UnitValue.createPercentValue(100f))
        receiptInfo.setMarginTop(10f)
        receiptInfo.setMarginBottom(10f)
        
        val receiptNumPara = Paragraph("${Constants.Receipt.RECEIPT_PREFIX} ${receipt.receiptNumber}")
            .setFontSize(10f)
            .setBold()
        font?.let { receiptNumPara.setFont(it) }
        receiptInfo.addCell(receiptNumPara)
        
        val datePara = Paragraph("${Constants.Receipt.DATE_PREFIX} ${receipt.date}")
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.RIGHT)
        font?.let { datePara.setFont(it) }
        receiptInfo.addCell(datePara)
        
        document.add(receiptInfo)
        
        val customerName = Paragraph("${Constants.Receipt.CUSTOMER_PREFIX} ${receipt.customerName}")
            .setFontSize(10f)
            .setMarginBottom(5f)
        font?.let { customerName.setFont(it) }
        
        val customerPhone = Paragraph("${Constants.Receipt.PHONE_PREFIX} ${receipt.customerPhone}")
            .setFontSize(10f)
            .setMarginBottom(10f)
        font?.let { customerPhone.setFont(it) }
        
        document.add(customerName)
        document.add(customerPhone)
        document.add(Paragraph("―――――――――――――――――――――――――――――――――――").setTextAlignment(TextAlignment.CENTER).setMarginTop(5f).setMarginBottom(10f))
    }
    
    private fun addServiceDetails(document: Document, receipt: Receipt, font: PdfFont?) {
        val serviceTable = Table(2)
        serviceTable.setWidth(UnitValue.createPercentValue(100f))
        serviceTable.setMarginTop(10f)
        serviceTable.setMarginBottom(10f)
        
        val servicePara = Paragraph(receipt.serviceDescription)
            .setFontSize(11f)
        font?.let { servicePara.setFont(it) }
        serviceTable.addCell(servicePara)
        
        val amountPara = Paragraph(receipt.getFormattedAmount())
            .setFontSize(11f)
            .setTextAlignment(TextAlignment.RIGHT)
        font?.let { amountPara.setFont(it) }
        serviceTable.addCell(amountPara)
        
        document.add(serviceTable)
        document.add(Paragraph("―――――――――――――――――――――――――――――――――――").setTextAlignment(TextAlignment.CENTER).setMarginTop(5f).setMarginBottom(10f))
        
        val totalTable = Table(2)
        totalTable.setWidth(UnitValue.createPercentValue(100f))
        totalTable.setMarginTop(10f)
        totalTable.setMarginBottom(5f)
        
        val totalLabelPara = Paragraph(Constants.Receipt.TOTAL_LABEL)
            .setFontSize(12f)
            .setBold()
        font?.let { totalLabelPara.setFont(it) }
        totalTable.addCell(totalLabelPara)
        
        val totalAmountPara = Paragraph(receipt.getFormattedAmount())
            .setFontSize(12f)
            .setBold()
            .setTextAlignment(TextAlignment.RIGHT)
        font?.let { totalAmountPara.setFont(it) }
        totalTable.addCell(totalAmountPara)
        
        document.add(totalTable)
        
        val paymentMode = Paragraph(
            "${Constants.Receipt.PAYMENT_MODE_LABEL} ${receipt.getPaymentModeEnum().kannadaName}"
        )
            .setFontSize(10f)
            .setMarginBottom(10f)
        font?.let { paymentMode.setFont(it) }
        
        document.add(paymentMode)
        document.add(Paragraph("―――――――――――――――――――――――――――――――――――").setTextAlignment(TextAlignment.CENTER).setMarginTop(5f).setMarginBottom(10f))
    }
    
    private fun addFooter(document: Document, font: PdfFont?) {
        val trust = Paragraph(Constants.Organization.TRUST)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(9f)
            .setMarginTop(15f)
            .setMarginBottom(10f)
        font?.let { trust.setFont(it) }
        
        val thankYou = Paragraph(Constants.Receipt.THANK_YOU)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(11f)
            .setBold()
            .setMarginTop(10f)
        font?.let { thankYou.setFont(it) }
        
        document.add(trust)
        document.add(thankYou)
    }
}
