package com.rayara.sevasetu.utils

import android.content.Context
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.rayara.sevasetu.data.database.entities.Receipt
import com.rayara.sevasetu.data.models.PaymentMode
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

data class TransactionSummary(
    val totalTransactions: Int,
    val totalAmount: Double,
    val cashAmount: Double,
    val phonePeAmount: Double,
    val onlineAmount: Double,
    val cashCount: Int,
    val phonePeCount: Int,
    val onlineCount: Int
)

class PDFExporter(private val context: Context) {
    
    private fun getKannadaFont(): PdfFont? {
        return try {
            PdfFontFactory.createFont("/system/fonts/NotoSansKannada-Regular.ttf", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED)
        } catch (e: Exception) {
            try {
                PdfFontFactory.createFont("/system/fonts/DroidSansFallback.ttf", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED)
            } catch (e2: Exception) {
                null
            }
        }
    }
    
    fun exportTransactions(
        receipts: List<Receipt>,
        startDate: String,
        endDate: String
    ): File {
        val pdfDir = File(context.getExternalFilesDir(null), "exports")
        if (!pdfDir.exists()) {
            pdfDir.mkdirs()
        }
        
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        val pdfFile = File(pdfDir, "transactions_${timestamp}.pdf")
        
        val writer = PdfWriter(FileOutputStream(pdfFile))
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument, PageSize.A4)
        
        document.setMargins(30f, 30f, 30f, 30f)
        
        val font = getKannadaFont()
        
        addExportHeader(document, font, startDate, endDate)
        addSummary(document, font, receipts)
        addTransactionTable(document, font, receipts)
        
        document.close()
        
        return pdfFile
    }
    
    private fun addExportHeader(document: Document, font: PdfFont?, startDate: String, endDate: String) {
        val orgName = Paragraph(Constants.Organization.NAME)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(16f)
            .setBold()
            .setMarginBottom(5f)
        font?.let { orgName.setFont(it) }
        
        val orgAddress = Paragraph(Constants.Organization.ADDRESS)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(12f)
            .setMarginBottom(10f)
        font?.let { orgAddress.setFont(it) }
        
        val title = Paragraph("ವಹಿವಾಟು ವರದಿ")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(14f)
            .setBold()
            .setMarginBottom(5f)
        font?.let { title.setFont(it) }
        
        val dateRange = Paragraph("$startDate ರಿಂದ $endDate ವರೆಗೆ")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(11f)
            .setMarginBottom(15f)
        font?.let { dateRange.setFont(it) }
        
        document.add(orgName)
        document.add(orgAddress)
        document.add(Paragraph("―――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――").setTextAlignment(TextAlignment.CENTER).setMarginBottom(10f))
        document.add(title)
        document.add(dateRange)
    }
    
    private fun addSummary(document: Document, font: PdfFont?, receipts: List<Receipt>) {
        val summary = calculateSummary(receipts)
        
        val summaryTitle = Paragraph("ಸಾರಾಂಶ")
            .setFontSize(13f)
            .setBold()
            .setMarginBottom(10f)
        font?.let { summaryTitle.setFont(it) }
        document.add(summaryTitle)
        
        val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
        summaryTable.setWidth(UnitValue.createPercentValue(100f))
        summaryTable.setMarginBottom(15f)
        
        addSummaryRow(summaryTable, font, "ಒಟ್ಟು ವಹಿವಾಟುಗಳು:", "${summary.totalTransactions}")
        addSummaryRow(summaryTable, font, "ಒಟ್ಟು ಮೊತ್ತ:", "₹${String.format("%.2f", summary.totalAmount)}")
        addSummaryRow(summaryTable, font, "ನಗದು (${summary.cashCount}):", "₹${String.format("%.2f", summary.cashAmount)}")
        addSummaryRow(summaryTable, font, "PhonePe (${summary.phonePeCount}):", "₹${String.format("%.2f", summary.phonePeAmount)}")
        addSummaryRow(summaryTable, font, "ಆನ್‌ಲೈನ್ (${summary.onlineCount}):", "₹${String.format("%.2f", summary.onlineAmount)}")
        
        document.add(summaryTable)
        document.add(Paragraph("―――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――").setTextAlignment(TextAlignment.CENTER).setMarginTop(5f).setMarginBottom(15f))
    }
    
    private fun addSummaryRow(table: Table, font: PdfFont?, label: String, value: String) {
        val labelCell = Cell().add(Paragraph(label).setFontSize(10f).setBold())
        font?.let { labelCell.children[0].setFont(it) }
        
        val valueCell = Cell().add(Paragraph(value).setFontSize(10f).setTextAlignment(TextAlignment.RIGHT))
        font?.let { valueCell.children[0].setFont(it) }
        
        table.addCell(labelCell)
        table.addCell(valueCell)
    }
    
    private fun addTransactionTable(document: Document, font: PdfFont?, receipts: List<Receipt>) {
        val tableTitle = Paragraph("ವಿವರವಾದ ವಹಿವಾಟುಗಳು")
            .setFontSize(13f)
            .setBold()
            .setMarginBottom(10f)
        font?.let { tableTitle.setFont(it) }
        document.add(tableTitle)
        
        val table = Table(UnitValue.createPercentArray(floatArrayOf(10f, 15f, 25f, 20f, 15f, 15f)))
        table.setWidth(UnitValue.createPercentValue(100f))
        
        addTableHeader(table, font, "ಸಂ.")
        addTableHeader(table, font, "ದಿನಾಂಕ")
        addTableHeader(table, font, "ಗ್ರಾಹಕ")
        addTableHeader(table, font, "ದೂರವಾಣಿ")
        addTableHeader(table, font, "ಮೊತ್ತ")
        addTableHeader(table, font, "ಪಾವತಿ")
        
        receipts.forEach { receipt ->
            addTableCell(table, font, receipt.receiptNumber.toString())
            addTableCell(table, font, receipt.date)
            addTableCell(table, font, receipt.customerName)
            addTableCell(table, font, receipt.customerPhone)
            addTableCell(table, font, receipt.getFormattedAmount(), TextAlignment.RIGHT)
            addTableCell(table, font, receipt.getPaymentModeEnum().kannadaName)
        }
        
        document.add(table)
    }
    
    private fun addTableHeader(table: Table, font: PdfFont?, text: String) {
        val cell = Cell().add(Paragraph(text).setFontSize(9f).setBold())
        font?.let { cell.children[0].setFont(it) }
        cell.setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY)
        table.addHeaderCell(cell)
    }
    
    private fun addTableCell(table: Table, font: PdfFont?, text: String, alignment: TextAlignment = TextAlignment.LEFT) {
        val cell = Cell().add(Paragraph(text).setFontSize(8f).setTextAlignment(alignment))
        font?.let { cell.children[0].setFont(it) }
        table.addCell(cell)
    }
    
    private fun calculateSummary(receipts: List<Receipt>): TransactionSummary {
        var totalAmount = 0.0
        var cashAmount = 0.0
        var phonePeAmount = 0.0
        var onlineAmount = 0.0
        var cashCount = 0
        var phonePeCount = 0
        var onlineCount = 0
        
        receipts.forEach { receipt ->
            totalAmount += receipt.amount
            when (receipt.getPaymentModeEnum()) {
                PaymentMode.CASH -> {
                    cashAmount += receipt.amount
                    cashCount++
                }
                PaymentMode.PHONEPE -> {
                    phonePeAmount += receipt.amount
                    phonePeCount++
                }
                PaymentMode.ONLINE -> {
                    onlineAmount += receipt.amount
                    onlineCount++
                }
            }
        }
        
        return TransactionSummary(
            totalTransactions = receipts.size,
            totalAmount = totalAmount,
            cashAmount = cashAmount,
            phonePeAmount = phonePeAmount,
            onlineAmount = onlineAmount,
            cashCount = cashCount,
            phonePeCount = phonePeCount,
            onlineCount = onlineCount
        )
    }
}
