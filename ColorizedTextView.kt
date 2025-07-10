package com.russia.common.custom_view

import android.content.Context
import android.graphics.*
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.russia.online.R

class ColorizedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorizedTextView)
        val text = typedArray.getText(R.styleable.ColorizedTextView_android_text)
        text?.let { setParsedText(it.toString()) }
        typedArray.recycle()
    }

    override fun setText(text: CharSequence?, bufferType: BufferType?) {
        val str = text?.toString() ?: ""
        super.setText(parseTextWithColors(str), BufferType.SPANNABLE)
    }

    private fun setParsedText(text: String) {
        super.setText(parseTextWithColors(text), BufferType.SPANNABLE)
    }

    private fun parseTextWithColors(text: String): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder()
        var currentTextColor = currentTextColor
        val regex = Regex("\\{([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})\\}")
        var index = 0
        var remainingText = text

        while (remainingText.isNotEmpty()) {
            val matchResult = regex.find(remainingText)

            if (matchResult == null) {
                spannableStringBuilder.append(remainingText)
                spannableStringBuilder.setSpan(ForegroundColorSpan(currentTextColor), index, spannableStringBuilder.length, 33)
                break
            }

            val substring = remainingText.substring(0, matchResult.range.first)
            if (substring.isNotEmpty()) {
                spannableStringBuilder.append(substring)
                spannableStringBuilder.setSpan(ForegroundColorSpan(currentTextColor), index, spannableStringBuilder.length, 33)
                index += substring.length
            }

            val colorCode = matchResult.groups[1]?.value ?: ""
            currentTextColor = try {
                if (colorCode.length == 8) {
                    Color.parseColor("#$colorCode")
                } else {
                    Color.parseColor("#FF$colorCode")
                }
            } catch (e: IllegalArgumentException) {
                currentTextColor
            }

            remainingText = remainingText.substring(matchResult.range.last + 1)
        }

        return spannableStringBuilder
    }
}