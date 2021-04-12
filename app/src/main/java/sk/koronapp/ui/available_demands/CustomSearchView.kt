package sk.koronapp.ui.available_demands

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView

class CustomSearchView(context: Context, attrs: AttributeSet?) : SearchView(context, attrs) {

    private var mSearchSrcTextView: SearchAutoComplete =
        this.findViewById(androidx.appcompat.R.id.search_src_text)
    private var mCloseButton: ImageView =
        this.findViewById(androidx.appcompat.R.id.search_close_btn)

    override fun setOnQueryTextListener(listener: OnQueryTextListener?) {
        super.setOnQueryTextListener(listener)
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        mSearchSrcTextView.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                listener?.onQueryTextSubmit(query.toString())
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
                if (mSearchSrcTextView.text.isEmpty()) {
                    mSearchSrcTextView.isCursorVisible = false
                    mCloseButton.visibility = View.GONE
                }
                return true
            }
        })
    }

    override fun setOnCloseListener(listener: OnCloseListener?) {
        super.setOnCloseListener(listener)
        mCloseButton.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                listener?.onClose()
                mSearchSrcTextView.setText("")
                mSearchSrcTextView.requestFocus()
                mSearchSrcTextView.isCursorVisible = false
                mCloseButton.visibility = View.GONE
            }
        })
    }

}