package com.rayara.sevasetu.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.TextPaint
import com.rayara.sevasetu.data.database.entities.Receipt
import java.io.File
import java.io.FileOutputStream

class PDFGenerator(private val context: Context) {
    
    // Page dimensions (A6 size in points: 297 x 420, increased to 500 for content)
    private val PAGE_WIDTH = 297f
    private val PAGE_HEIGHT = 550f
    private val MARGIN = 20f
    private val CONTENT_WIDTH = PAGE_WIDTH - (2 * MARGIN)
    
    // Text sizes
    private val TITLE_SIZE = 14f
    private val SUBTITLE_SIZE = 10f
    private val NORMAL_SIZE = 10f
    private val LARGE_SIZE = 12f
    
    // Color mode
    private val prefsManager = PreferencesManager(context)
    private val isColorMode: Boolean
        get() = prefsManager.isColorReceiptEnabled()
    
    // Handwriting font for signature
    private val signatureTypeface: Typeface by lazy {
        try {
            Typeface.createFromAsset(context.assets, "fonts/dancing_script.ttf")
        } catch (e: Exception) {
            Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        }
    }
    
    // Colors
    private fun getColor(colorValue: Int, bwValue: Int = android.graphics.Color.BLACK): Int {
        return if (isColorMode) colorValue else bwValue
    }
    
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
    
    fun generateReceiptPDF(receipt: Receipt): File {
        val pdfDir = File(context.getExternalFilesDir(null), Constants.PDF.RECEIPT_FOLDER)
        if (!pdfDir.exists()) {
            pdfDir.mkdirs()
        }
        
        val pdfFile = File(
            pdfDir,
            "${Constants.PDF.FILE_PREFIX}${receipt.receiptNumber}${Constants.PDF.FILE_EXTENSION}"
        )
        
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
        yPosition = drawHeader(canvas, yPosition)
        yPosition += 15f
        
        // Draw receipt details
        yPosition = drawReceiptDetails(canvas, receipt, yPosition)
        yPosition += 10f
        
        // Draw service details
        yPosition = drawServiceDetails(canvas, receipt, yPosition)
        yPosition += 10f
        
        // Draw footer
        drawFooter(canvas, yPosition, receipt)
        
        pdfDocument.finishPage(page)
        
        // Write to file
        pdfDocument.writeTo(FileOutputStream(pdfFile))
        pdfDocument.close()
        
        return pdfFile
    }
    
    private fun drawHeader(canvas: Canvas, startY: Float): Float {
        var y = startY
        
        // Om symbol (centered)
        val omPaint = createTextPaint(18f, bold = true)
        omPaint.color = getColor(
            android.graphics.Color.rgb(139, 0, 0),  // Dark red (color)
            android.graphics.Color.BLACK  // Black (B&W)
        )
        val om = "ॐ"
        val omWidth = omPaint.measureText(om)
        canvas.drawText(om, (PAGE_WIDTH - omWidth) / 2, y, omPaint)
        y += 18f + 8f
        
        // Guru mantra (centered)
        val mantraPaint = createTextPaint(10f)
        mantraPaint.color = getColor(
            android.graphics.Color.rgb(101, 67, 33),  // Dark brown (color)
            android.graphics.Color.BLACK  // Black (B&W)
        )
        val mantra = "ಶ್ರೀ ಗುರುಭ್ಯೋ ನಮః"
        val mantraWidth = mantraPaint.measureText(mantra)
        canvas.drawText(mantra, (PAGE_WIDTH - mantraWidth) / 2, y, mantraPaint)
        y += 10f + 5f
        
        // Organization name (centered, bold, smaller size to fit)
        val titlePaint = createTextPaint(11f, bold = true)
        titlePaint.color = getColor(
            android.graphics.Color.rgb(139, 0, 0),  // Dark red (color)
            android.graphics.Color.BLACK  // Black (B&W)
        )
        val orgName = Constants.Organization.NAME
        val orgNameWidth = titlePaint.measureText(orgName)
        canvas.drawText(orgName, (PAGE_WIDTH - orgNameWidth) / 2, y, titlePaint)
        y += 11f + 3f
        
        // Organization address (centered)
        val subtitlePaint = createTextPaint(SUBTITLE_SIZE)
        subtitlePaint.color = getColor(
            android.graphics.Color.rgb(101, 67, 33),  // Dark brown (color)
            android.graphics.Color.BLACK  // Black (B&W)
        )
        val orgAddress = Constants.Organization.ADDRESS
        val orgAddressWidth = subtitlePaint.measureText(orgAddress)
        canvas.drawText(orgAddress, (PAGE_WIDTH - orgAddressWidth) / 2, y, subtitlePaint)
        y += SUBTITLE_SIZE + 5f
        
        // Raghavendra mantra (centered, small)
        val smallMantraPaint = createTextPaint(8f)
        smallMantraPaint.color = getColor(
            android.graphics.Color.rgb(101, 67, 33),  // Dark brown (color)
            android.graphics.Color.BLACK  // Black (B&W)
        )
        val raghavendra = "\"ಪೂಜ್ಯಾಯ ರಾಘವೇಂದ್ರಾಯ ಸತ್ಯಧರ್ಮ ರತಾಯ ಚ\""
        val raghavendraWidth = smallMantraPaint.measureText(raghavendra)
        canvas.drawText(raghavendra, (PAGE_WIDTH - raghavendraWidth) / 2, y, smallMantraPaint)
        y += 8f + 10f
        
        // Thick decorative line
        val thickLinePaint = Paint().apply {
            color = getColor(
                android.graphics.Color.rgb(204, 102, 0),  // Dark orange (color)
                android.graphics.Color.BLACK  // Black (B&W)
            )
            strokeWidth = 3f
        }
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, thickLinePaint)
        y += 12f
        
        return y
    }
    
    private fun drawReceiptDetails(canvas: Canvas, receipt: Receipt, startY: Float): Float {
        var y = startY
        
        // Receipt title (centered)
        val titlePaint = createTextPaint(11f, bold = true)
        titlePaint.color = getColor(
            android.graphics.Color.rgb(204, 102, 0),  // Dark orange (color)
            android.graphics.Color.BLACK  // Black (B&W)
        )
        val receiptTitle = "ರಸೀದಿ"
        val receiptTitleWidth = titlePaint.measureText(receiptTitle)
        canvas.drawText(receiptTitle, (PAGE_WIDTH - receiptTitleWidth) / 2, y, titlePaint)
        y += 11f + 8f
        
        val normalPaint = createTextPaint(NORMAL_SIZE, bold = true)
        val regularPaint = createTextPaint(NORMAL_SIZE)
        
        // Receipt number and date (two columns)
        val receiptText = "${Constants.Receipt.RECEIPT_PREFIX} ${receipt.receiptNumber}"
        canvas.drawText(receiptText, MARGIN, y, normalPaint)
        
        val dateText = "${Constants.Receipt.DATE_PREFIX} ${receipt.date}"
        val dateWidth = normalPaint.measureText(dateText)
        canvas.drawText(dateText, PAGE_WIDTH - MARGIN - dateWidth, y, normalPaint)
        y += NORMAL_SIZE + 8f
        
        // Customer name
        val customerText = "${Constants.Receipt.CUSTOMER_PREFIX} ${receipt.customerName}"
        canvas.drawText(customerText, MARGIN, y, regularPaint)
        y += NORMAL_SIZE + 5f
        
        // Customer phone
        val phoneText = "${Constants.Receipt.PHONE_PREFIX} ${receipt.customerPhone}"
        canvas.drawText(phoneText, MARGIN, y, regularPaint)
        y += NORMAL_SIZE + 5f
        
        // Created by (if available)
        if (receipt.createdByUserName.isNotEmpty()) {
            val createdByText = "ರಚಿಸಿದವರು: ${receipt.createdByUserName}"
            canvas.drawText(createdByText, MARGIN, y, regularPaint)
            y += NORMAL_SIZE + 10f
        } else {
            y += 10f
        }
        
        // Single thick line
        val thickLinePaint = Paint().apply {
            color = getColor(
                android.graphics.Color.rgb(204, 102, 0),  // Dark orange (color)
                android.graphics.Color.BLACK  // Black (B&W)
            )
            strokeWidth = 2.5f
        }
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, thickLinePaint)
        y += 12f
        
        // Service details title (centered)
        val serviceTitlePaint = createTextPaint(10f, bold = true)
        serviceTitlePaint.color = getColor(
            android.graphics.Color.rgb(204, 102, 0),  // Dark orange (color)
            android.graphics.Color.BLACK  // Black (B&W)
        )
        val serviceTitle = "ಸೇವೆಯ ವಿವರ"
        val serviceTitleWidth = serviceTitlePaint.measureText(serviceTitle)
        canvas.drawText(serviceTitle, (PAGE_WIDTH - serviceTitleWidth) / 2, y, serviceTitlePaint)
        y += 10f + 10f
        
        // Single thick line
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, thickLinePaint)
        y += 12f
        
        return y
    }
    
    private fun drawServiceDetails(canvas: Canvas, receipt: Receipt, startY: Float): Float {
        var y = startY
        val normalPaint = createTextPaint(NORMAL_SIZE)
        
        // Service description and amount
        canvas.drawText(receipt.serviceDescription, MARGIN, y, normalPaint)
        val amountText = receipt.getFormattedAmount()
        val amountWidth = normalPaint.measureText(amountText)
        canvas.drawText(amountText, PAGE_WIDTH - MARGIN - amountWidth, y, normalPaint)
        y += NORMAL_SIZE + 10f
        
        // Single thick line
        val thickLinePaint = Paint().apply {
            color = getColor(
                android.graphics.Color.rgb(204, 102, 0),  // Dark orange (color)
                android.graphics.Color.BLACK  // Black (B&W)
            )
            strokeWidth = 2.5f
        }
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, thickLinePaint)
        y += 12f
        
        // Total (bold, larger)
        val boldPaint = createTextPaint(LARGE_SIZE, bold = true)
        boldPaint.color = getColor(
            android.graphics.Color.rgb(139, 0, 0),  // Dark red (color)
            android.graphics.Color.BLACK  // Black (B&W)
        )
        canvas.drawText(Constants.Receipt.TOTAL_LABEL, MARGIN, y, boldPaint)
        val totalAmountWidth = boldPaint.measureText(amountText)
        canvas.drawText(amountText, PAGE_WIDTH - MARGIN - totalAmountWidth, y, boldPaint)
        y += LARGE_SIZE + 10f
        
        // Single thick line
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, thickLinePaint)
        y += 12f
        
        // Payment mode
        val paymentText = "${Constants.Receipt.PAYMENT_MODE_LABEL} ${receipt.getPaymentModeEnum().kannadaName}"
        canvas.drawText(paymentText, MARGIN, y, normalPaint)
        y += NORMAL_SIZE + 10f
        
        return y
    }
    
    private fun drawFooter(canvas: Canvas, startY: Float, receipt: Receipt) {
        var y = startY + 20f
        
        // Thick decorative line
        val thickLinePaint = Paint().apply {
            color = getColor(
                android.graphics.Color.rgb(204, 102, 0),  // Dark orange (color)
                android.graphics.Color.BLACK  // Black (B&W)
            )
            strokeWidth = 3f
        }
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, thickLinePaint)
        y += 15f
        
        // Blessing with flowers (centered)
        val blessingPaint = createTextPaint(9f)
        blessingPaint.color = getColor(
            android.graphics.Color.rgb(101, 67, 33),  // Dark brown (color)
            android.graphics.Color.BLACK  // Black (B&W)
        )
        val blessing = "🌺 ಶ್ರೀ ರಾಘವೇಂದ್ರ ಸ್ವಾಮಿಗಳವರ ಅನುಗ್ರಹ ಸದಾ ಇರಲಿ 🌺"
        val blessingWidth = blessingPaint.measureText(blessing)
        canvas.drawText(blessing, (PAGE_WIDTH - blessingWidth) / 2, y, blessingPaint)
        y += 9f + 8f
        
        // Decorative line before signature
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 10f
        
        // Username signature (centered, handwriting font, 10sp)
        val signaturePaint = TextPaint().apply {
            textSize = 10f
            typeface = signatureTypeface
            color = getColor(
                android.graphics.Color.rgb(101, 67, 33),  // Dark brown (color)
                android.graphics.Color.BLACK  // Black (B&W)
            )
            isAntiAlias = true
        }
        
        // Draw username signature (from authenticated user)
        if (receipt.createdByUserName.isNotEmpty()) {
            val username = receipt.createdByUserName
            val usernameWidth = signaturePaint.measureText(username)
            canvas.drawText(username, (PAGE_WIDTH - usernameWidth) / 2, y, signaturePaint)
            y += 12f
        } else {
            // Show placeholder if no creator name
            val placeholder = "(ಸಹಿ)"
            val placeholderWidth = signaturePaint.measureText(placeholder)
            canvas.drawText(placeholder, (PAGE_WIDTH - placeholderWidth) / 2, y, signaturePaint)
            y += 12f
        }
        
        // Trust name (centered, small)
        val trustPaint = createTextPaint(8f)
        val trustName = "ಶ್ರೀ ರಾಘವೇಂದ್ರ ಗುರುಸಾರ್ವಭೌಮ ಸೇವಾ ಟ್ರಸ್ಟ್ (ರಿ.)"
        val trustWidth = trustPaint.measureText(trustName)
        canvas.drawText(trustName, (PAGE_WIDTH - trustWidth) / 2, y, trustPaint)
        y += 8f + 10f
        
        // Decorative line
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, thickLinePaint)
        y += 10f
        
        // Final mantra (centered, bold)
        val finalMantraPaint = createTextPaint(9f, bold = true)
        finalMantraPaint.color = getColor(
            android.graphics.Color.rgb(139, 0, 0),  // Dark red (color)
            android.graphics.Color.BLACK  // Black (B&W)
        )
        val finalMantra1 = "ಶ್ರೀ ರಾಘವೇಂದ್ರಾಯ ನಮః"
        val finalMantra1Width = finalMantraPaint.measureText(finalMantra1)
        canvas.drawText(finalMantra1, (PAGE_WIDTH - finalMantra1Width) / 2, y, finalMantraPaint)
        y += 9f + 3f
        
        // Universal peace mantra (centered)
        val peacePaint = createTextPaint(8f)
        peacePaint.color = getColor(
            android.graphics.Color.rgb(101, 67, 33),  // Dark brown (color)
            android.graphics.Color.BLACK  // Black (B&W)
        )
        val peace = "ಸರ್ವೇ ಜನಾಃ ಸುಖಿನೋ ಭವಂತು"
        val peaceWidth = peacePaint.measureText(peace)
        canvas.drawText(peace, (PAGE_WIDTH - peaceWidth) / 2, y, peacePaint)
        y += 8f + 5f
        
        // Final decorative line
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, thickLinePaint)
    }
}
