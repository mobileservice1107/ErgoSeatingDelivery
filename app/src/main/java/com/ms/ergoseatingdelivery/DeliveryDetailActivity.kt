package com.ms.ergoseatingdelivery

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings.LOAD_NO_CACHE
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.gcacace.signaturepad.views.SignaturePad
import com.ms.ergoseatingdelivery.api.ApiInterface
import com.ms.ergoseatingdelivery.model.DeliveryItem
import com.ms.ergoseatingdelivery.model.SuccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.concurrent.schedule


class DeliveryDetailActivity : AppCompatActivity() {

    lateinit var deliveryItem: DeliveryItem
    lateinit var productType: String

    lateinit var webView: WebView
    lateinit var btnSignature: Button
    lateinit var dialogSignature: Dialog
    lateinit var signaturePad: SignaturePad

    var bmpSignature: Bitmap? = null
    lateinit var loadingDialog: LoadingDialog


    companion object {
        const val EXTRA_DELIVERY_DATA = "delivery_data"
        const val EXTRA_PRODUCT_TYPE = "product_type"

        fun newIntent(context: Context, item: DeliveryItem, type: String): Intent {
            val intent = Intent(context, DeliveryDetailActivity::class.java)
            intent.putExtra(EXTRA_DELIVERY_DATA, item)
            intent.putExtra(EXTRA_PRODUCT_TYPE, type)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_detail)

        initView()
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_delivery_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                webView.reload()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {
        title = "Details"
        btnSignature = findViewById(R.id.btn_signature)
        webView = findViewById(R.id.webview)
        btnSignature.setOnClickListener{
            signaturePad.clear()
            dialogSignature.show()
        }
        initSignatureDialog()
        initWebView()
        loadingDialog = LoadingDialog(this)
    }
    private fun loadData() {
        deliveryItem = intent.getSerializableExtra(EXTRA_DELIVERY_DATA) as DeliveryItem
        productType = intent.getStringExtra(EXTRA_PRODUCT_TYPE).toString()
        generatePDF()
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (webView.contentHeight == 0) {
                    webView.loadUrl(url)// RE-Loading
                } else {
                    loadingDialog.dismiss()
                }
            }
        }
    }

    private fun generatePDF() {
        loadingDialog.show()
        val token = "Bearer " + Preferences(this).token
        val apiInterface = ApiInterface.create().generateDeliveryPDF(token, productType, deliveryItem.id)
        apiInterface.enqueue(object: Callback<SuccessResponse> {
            override fun onResponse(
                call: Call<SuccessResponse>,
                response: Response<SuccessResponse>
            ) {
                loadingDialog.dismiss()
                val res = response.body()
                loadDeliveryPDF()
            }

            override fun onFailure(call: Call<SuccessResponse>, t: Throwable) {
                loadingDialog.dismiss()
                loadDeliveryPDF()
            }

        })
    }

    private fun loadDeliveryPDF() {
        loadingDialog.show()
        val pdf = deliveryItem.pdfURL
        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=$pdf")
    }


    private fun initSignatureDialog() {
        dialogSignature = Dialog(this)
        dialogSignature.setTitle("Signature")
        dialogSignature.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogSignature.setCancelable(true)
        dialogSignature.setContentView(R.layout.dialog_signature)
        val width = (resources.displayMetrics.widthPixels* 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.50).toInt()
        dialogSignature.window!!.setLayout(width, height)

        signaturePad = dialogSignature.findViewById(R.id.signature_pad)
        val btnClear = dialogSignature.findViewById<Button>(R.id.btn_clear)
        val btnFinish = dialogSignature.findViewById<Button>(R.id.btn_save)
        btnClear.setOnClickListener(View.OnClickListener {
            signaturePad.clear()
        })
        btnFinish.setOnClickListener(View.OnClickListener {
            dialogSignature.dismiss()
            uploadSignature()
        })
        signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {

            }
            override fun onSigned() {
                btnFinish.isEnabled = true
                btnClear.isEnabled = true
            }

            override fun onClear() {
                btnFinish.isEnabled = false
                btnClear.isEnabled = false
            }
        })
    }

    private fun uploadSignature() {
        bmpSignature = signaturePad.signatureBitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bmpSignature!!.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream)
        val base64Image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)

        loadingDialog.show()
        val token = "Bearer " + Preferences(this).token
        val apiInterface = ApiInterface.create().uploadSignature(token, productType, deliveryItem.id, base64Image)
        apiInterface.enqueue(object: Callback<SuccessResponse> {
            override fun onResponse(
                call: Call<SuccessResponse>,
                response: Response<SuccessResponse>
            ) {
                loadingDialog.dismiss()
                val res = response.body()
                if(res != null && res.success) {
//                    btnSignature.visibility = View.INVISIBLE
//                    deliveryItem.delivered = true
//                    intent.putExtra(EXTRA_DELIVERY_DATA, deliveryItem);
//                    setResult(RESULT_OK, intent);
//                    finish()
                    webView.reload()
                }
            }

            override fun onFailure(call: Call<SuccessResponse>, t: Throwable) {
                loadingDialog.dismiss()
                Toast.makeText(this@DeliveryDetailActivity, t.localizedMessage, Toast.LENGTH_LONG).show()
            }

        })
    }


}