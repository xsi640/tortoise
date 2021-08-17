package com.github.xsi640.tortoise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : AppCompatActivity() {

    private lateinit var btnFeed: Button
    private val client = OkHttpClient()
    private val url = "http://192.168.1.254:18080/api/v1/mqtt/feed"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuOpen -> {
                val menuItemView = findViewById<View>(R.id.mnuOpen)
                showSettingsMenu(menuItemView)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSettingsMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.main, popup.menu)
        popup.setOnMenuItemClickListener {
            false
        }
        popup.setOnDismissListener {
            Toast.makeText(this, "close", Toast.LENGTH_SHORT).show()
        }
        popup.show()
    }

    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            1 -> {
                Toast.makeText(this, "喂食成功！", Toast.LENGTH_LONG).show()
            }
        }
        true
    }

    fun initView() {
        this.btnFeed = findViewById(R.id.btnFeed)
        this.btnFeed.setOnClickListener { btnFeedClicked(it) }

    }

    private fun btnFeedClicked(view: View) {
        Thread {
            val body = "{\"count\":1}".toRequestBody("application/json;charset=utf-8".toMediaType())
            val request = Request.Builder().url(url).post(body).build()
            client.newCall(request).execute().use { response ->
                if (response.code == 200) {
                    handler.sendEmptyMessage(1)
                }
            }
        }.start()
    }
}