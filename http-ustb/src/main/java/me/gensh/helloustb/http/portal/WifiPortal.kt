package me.gensh.helloustb.http.portal

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by gensh on 2017/11/5.
 */

const val REDIRECT_URL1 = "202.204.48.66"
const val REDIRECT_URL2 = "202.204.48.82"

class WifiPortal {
    companion object {
        const val WALLED_GARDEN_URL = "http://connect.rom.miui.com/generate_204"

        fun isWifiSetPortal(): Status? {
            val client = OkHttpClient.Builder()
                    .connectTimeout(8000, TimeUnit.MILLISECONDS)
                    .readTimeout(8000, TimeUnit.MILLISECONDS)
                    .followRedirects(false)
                    .followSslRedirects(false)
                    .build()
            val request = Request.Builder().url(WALLED_GARDEN_URL).build()
            try {
                val response: Response = client.newCall(request).execute()
                val status = Status()
                if (response.code == 200) { //not 204.
                    status.needAuth = true
                    // try to resolve html content.
                    // <html><head><script type="text/javascript">location.href="http://202.204.48.66"</script></head><body><a href="http://202.204.48.66"></a></body></html>
                    if (response.body != null) {
                        val body = response.body !!.string()
                        if (body.contains(REDIRECT_URL1) || body.contains(REDIRECT_URL2)) {
                            status.isInnerNet = true
                        }
                    }
                } else {
                    status.needAuth = false
                }
                response.close()
                return status
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }

    class Status {
        var needAuth: Boolean = false
        var isInnerNet: Boolean = false
    }
}