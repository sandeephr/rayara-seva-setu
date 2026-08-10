package com.rayara.sevasetu.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.rayara.sevasetu.data.database.entities.Receipt

class BluetoothPrinterManager(private val context: Context) {
    
    private val bluetoothManager: BluetoothManager? = 
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }
    
    fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    fun getPairedPrinters(): Array<BluetoothConnection>? {
        if (!hasBluetoothPermissions()) return null
        return try {
            BluetoothPrintersConnections().list
        } catch (e: Exception) {
            null
        }
    }
    
    fun printReceipt(receipt: Receipt, printerConnection: BluetoothConnection): Boolean {
        return try {
            val printer = EscPosPrinter(printerConnection, 203, 48f, 32)
            
            val receiptText = formatReceiptForPrinter(receipt)
            
            printer.printFormattedText(receiptText)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    private fun formatReceiptForPrinter(receipt: Receipt): String {
        return buildString {
            // Header
            append("[C]<font size='big'>${Constants.Organization.NAME}</font>\n")
            append("[C]${Constants.Organization.ADDRESS}\n")
            append("[C]${Constants.Organization.TRUST}\n")
            append("[C]--------------------------------\n")
            
            // Receipt Details
            append("[L]\n")
            append("[L]${Constants.Receipt.RECEIPT_PREFIX} ${receipt.receiptNumber}[R]${Constants.Receipt.DATE_PREFIX} ${receipt.date}\n")
            append("[L]\n")
            append("[L]${Constants.Receipt.CUSTOMER_PREFIX}\n")
            append("[L]${receipt.customerName}\n")
            append("[L]${Constants.Receipt.PHONE_PREFIX} ${receipt.customerPhone}\n")
            append("[L]\n")
            append("[C]--------------------------------\n")
            
            // Service Details
            append("[L]\n")
            append("[L]${receipt.serviceDescription}[R]${receipt.getFormattedAmount()}\n")
            append("[L]\n")
            append("[C]--------------------------------\n")
            
            // Total
            append("[L]\n")
            append("[L]<font size='big'>${Constants.Receipt.TOTAL_LABEL}</font>[R]<font size='big'>${receipt.getFormattedAmount()}</font>\n")
            append("[L]\n")
            append("[L]${Constants.Receipt.PAYMENT_MODE_LABEL}\n")
            append("[L]${receipt.getPaymentModeEnum().kannadaName}\n")
            append("[L]\n")
            append("[C]--------------------------------\n")
            
            // Footer
            append("[L]\n")
            append("[C]<font size='big'>${Constants.Receipt.THANK_YOU}</font>\n")
            append("[L]\n")
            append("[L]\n")
            append("[L]\n")
        }
    }
}
