package com.rayara.sevasetu.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.TextPaint
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
    
    // Page dimensions (A4 size in points: 595 x 842)
    private val PAGE_WIDTH = 595f
    private val PAGE_HEIGHT = 842f
    private val MARGIN = 30f
    private val CONTENT_WIDTH = PAGE_WIDTH - (2 * MARGIN)
    
    // Text sizes
    private val TITLE_SIZE = 16f
    private val SUBTITLE_SIZE = 12f
    private val NORMAL_SIZE = 10f
    private val SMALL_SIZE = 9f
    private val TABLE_HEADER_SIZE = 9f
    private val TABLE_CELL_SIZE = 8f
    
    private fun createTextPaint(size: Float, bold: Boolean = false): TextPaint {
        return TextPaint().apply {
            isAntiAlias = true
            textSize = size
            color = android.graphics.Color.BLACK
            typeface = if (bold) {
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            } else {
                Typeface.DEFAULT
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
        
        // Create PDF document
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(
            PAGE_WIDTH.toInt(),
            PAGE_HEIGHT.toInt(),
            1
        ).create()
        
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        
        var yPosition = MARGIN
        
        // Draw header
        yPosition = drawExportHeader(canvas, startDate, endDate, yPosition)
        yPosition += 20f
        
        // Draw summary
        yPosition = drawSummary(canvas, receipts, yPosition)
        yPosition += 20f
        
        // Draw transaction table
        drawTransactionTable(canvas, receipts, yPosition)
        
        pdfDocument.finishPage(page)
        
        // Write to file
        pdfDocument.writeTo(FileOutputStream(pdfFile))
        pdfDocument.close()
        
        return pdfFile
    }
    
    private fun drawExportHeader(canvas: Canvas, startDate: String, endDate: String, startY: Float): Float {
        var y = startY
        
        // Organization name (centered, bold)
        val titlePaint = createTextPaint(TITLE_SIZE, bold = true)
        val orgName = Constants.Organization.NAME
        val orgNameWidth = titlePaint.measureText(orgName)
        canvas.drawText(orgName, (PAGE_WIDTH - orgNameWidth) / 2, y, titlePaint)
        y += TITLE_SIZE + 5f
        
        // Organization address (centered)
        val subtitlePaint = createTextPaint(SUBTITLE_SIZE)
        val orgAddress = Constants.Organization.ADDRESS
        val orgAddressWidth = subtitlePaint.measureText(orgAddress)
        canvas.drawText(orgAddress, (PAGE_WIDTH - orgAddressWidth) / 2, y, subtitlePaint)
        y += SUBTITLE_SIZE + 10f
        
        // Divider line
        val linePaint = Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = 1f
        }
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 10f
        
        // Report title (centered, bold)
        val reportTitlePaint = createTextPaint(14f, bold = true)
        val reportTitle = "ವಹಿವಾಟು ವರದಿ"
        val reportTitleWidth = reportTitlePaint.measureText(reportTitle)
        canvas.drawText(reportTitle, (PAGE_WIDTH - reportTitleWidth) / 2, y, reportTitlePaint)
        y += 14f + 5f
        
        // Date range (centered)
        val dateRangePaint = createTextPaint(NORMAL_SIZE)
        val dateRangeText = "$startDate ರಿಂದ $endDate ವರೆಗೆ"
        val dateRangeWidth = dateRangePaint.measureText(dateRangeText)
        canvas.drawText(dateRangeText, (PAGE_WIDTH - dateRangeWidth) / 2, y, dateRangePaint)
        y += NORMAL_SIZE + 15f
        
        return y
    }
    
    private fun drawSummary(canvas: Canvas, receipts: List<Receipt>, startY: Float): Float {
        var y = startY
        val summary = calculateSummary(receipts)
        
        // Summary title (bold)
        val titlePaint = createTextPaint(SUBTITLE_SIZE, bold = true)
        canvas.drawText("ಸಾರಾಂಶ", MARGIN, y, titlePaint)
        y += SUBTITLE_SIZE + 10f
        
        // Summary rows
        val labelPaint = createTextPaint(NORMAL_SIZE, bold = true)
        val valuePaint = createTextPaint(NORMAL_SIZE)
        val rowHeight = NORMAL_SIZE + 5f
        
        // Total transactions
        canvas.drawText("ಒಟ್ಟು ವಹಿವಾಟುಗಳು:", MARGIN, y, labelPaint)
        val value1 = "${summary.totalTransactions}"
        val value1Width = valuePaint.measureText(value1)
        canvas.drawText(value1, PAGE_WIDTH - MARGIN - value1Width, y, valuePaint)
        y += rowHeight
        
        // Total amount
        canvas.drawText("ಒಟ್ಟು ಮೊತ್ತ:", MARGIN, y, labelPaint)
        val value2 = "₹${String.format("%.2f", summary.totalAmount)}"
        val value2Width = valuePaint.measureText(value2)
        canvas.drawText(value2, PAGE_WIDTH - MARGIN - value2Width, y, valuePaint)
        y += rowHeight
        
        // Cash
        canvas.drawText("ನಗದು (${summary.cashCount}):", MARGIN, y, labelPaint)
        val value3 = "₹${String.format("%.2f", summary.cashAmount)}"
        val value3Width = valuePaint.measureText(value3)
        canvas.drawText(value3, PAGE_WIDTH - MARGIN - value3Width, y, valuePaint)
        y += rowHeight
        
        // PhonePe
        canvas.drawText("PhonePe (${summary.phonePeCount}):", MARGIN, y, labelPaint)
        val value4 = "₹${String.format("%.2f", summary.phonePeAmount)}"
        val value4Width = valuePaint.measureText(value4)
        canvas.drawText(value4, PAGE_WIDTH - MARGIN - value4Width, y, valuePaint)
        y += rowHeight
        
        // Online
        canvas.drawText("ಆನ್‌ಲೈನ್ (${summary.onlineCount}):", MARGIN, y, labelPaint)
        val value5 = "₹${String.format("%.2f", summary.onlineAmount)}"
        val value5Width = valuePaint.measureText(value5)
        canvas.drawText(value5, PAGE_WIDTH - MARGIN - value5Width, y, valuePaint)
        y += rowHeight + 10f
        
        // Divider line
        val linePaint = Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = 1f
        }
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 15f
        
        return y
    }
    
    private fun drawTransactionTable(canvas: Canvas, receipts: List<Receipt>, startY: Float) {
        var y = startY
        
        // Table title
        val titlePaint = createTextPaint(SUBTITLE_SIZE, bold = true)
        canvas.drawText("ವಿವರವಾದ ವಹಿವಾಟುಗಳು", MARGIN, y, titlePaint)
        y += SUBTITLE_SIZE + 10f
        
        // Column widths (percentages of content width)
        val col1Width = CONTENT_WIDTH * 0.10f  // ಸಂ.
        val col2Width = CONTENT_WIDTH * 0.15f  // ದಿನಾಂಕ
        val col3Width = CONTENT_WIDTH * 0.25f  // ಗ್ರಾಹಕ
        val col4Width = CONTENT_WIDTH * 0.20f  // ದೂರವಾಣಿ
        val col5Width = CONTENT_WIDTH * 0.15f  // ಮೊತ್ತ
        val col6Width = CONTENT_WIDTH * 0.15f  // ಪಾವತಿ
        
        // Column X positions
        val col1X = MARGIN
        val col2X = col1X + col1Width
        val col3X = col2X + col2Width
        val col4X = col3X + col3Width
        val col5X = col4X + col4Width
        val col6X = col5X + col5Width
        
        // Header row (with gray background)
        val headerPaint = createTextPaint(TABLE_HEADER_SIZE, bold = true)
        val grayPaint = Paint().apply {
            color = android.graphics.Color.LTGRAY
        }
        canvas.drawRect(MARGIN, y - TABLE_HEADER_SIZE, PAGE_WIDTH - MARGIN, y + 5f, grayPaint)
        
        canvas.drawText("ಸಂ.", col1X + 2f, y, headerPaint)
        canvas.drawText("ದಿನಾಂಕ", col2X + 2f, y, headerPaint)
        canvas.drawText("ಗ್ರಾಹಕ", col3X + 2f, y, headerPaint)
        canvas.drawText("ದೂರವಾಣಿ", col4X + 2f, y, headerPaint)
        canvas.drawText("ಮೊತ್ತ", col5X + 2f, y, headerPaint)
        canvas.drawText("ಪಾವತಿ", col6X + 2f, y, headerPaint)
        y += 5f
        
        // Table border
        val borderPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = 1f
        }
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, borderPaint)
        y += 5f
        
        // Data rows
        val cellPaint = createTextPaint(TABLE_CELL_SIZE)
        receipts.forEach { receipt ->
            canvas.drawText(receipt.receiptNumber.toString(), col1X + 2f, y, cellPaint)
            canvas.drawText(receipt.date, col2X + 2f, y, cellPaint)
            canvas.drawText(receipt.customerName, col3X + 2f, y, cellPaint)
            canvas.drawText(receipt.customerPhone, col4X + 2f, y, cellPaint)
            canvas.drawText(receipt.getFormattedAmount(), col5X + 2f, y, cellPaint)
            canvas.drawText(receipt.getPaymentModeEnum().kannadaName, col6X + 2f, y, cellPaint)
            y += TABLE_CELL_SIZE + 5f
        }
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
