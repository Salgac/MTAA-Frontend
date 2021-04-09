package sk.koronapp.utilities

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.ImageRequest

class CustomImageLoader(queue: RequestQueue, imageCache: ImageCache) :
    ImageLoader(queue, imageCache) {

    private var progressBar: ProgressBar? = null
    fun setProgressBar(progressBar: ProgressBar) {
        this.progressBar = progressBar
    }

    private fun showProgressBar() {
        if (progressBar != null)
            progressBar!!.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        if (progressBar != null)
            progressBar!!.visibility = View.GONE
    }

    /**
     * Overriding of this method was necessary to add authorization header,
     * because in image loader it has protected modifier.
     */
    override fun makeImageRequest(
        requestUrl: String?,
        maxWidth: Int,
        maxHeight: Int,
        scaleType: ImageView.ScaleType?,
        cacheKey: String?
    ): Request<Bitmap> {
        showProgressBar()
        return object : ImageRequest(
            requestUrl,
            Response.Listener {
                onGetImageSuccess(cacheKey, it)
                hideProgressBar()
            },
            maxWidth,
            maxHeight,
            scaleType,
            Bitmap.Config.RGB_565,
            Response.ErrorListener {
                onGetImageError(cacheKey, it)
                hideProgressBar()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                return HttpRequestManager.defaultHeaders()
            }
        }
    }
}