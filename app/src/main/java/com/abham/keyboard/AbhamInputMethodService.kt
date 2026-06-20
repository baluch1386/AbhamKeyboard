package com.abham.keyboard

import android.inputmethodservice.InputMethodService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

/**
 * AbhamInputMethodService - The brain of "کیبورد ابهام".
 *
 * This builds a simple Persian + English keyboard. It supports:
 *  - Switching between Persian and English layouts
 *  - Basic keys: letters, space, enter, backspace
 *  - A custom decorative font applied to all key labels
 *
 * Note: this is a SIMPLE keyboard meant as a learning project / personal build,
 * not a full replacement for production keyboards (no autocorrect, no symbols
 * page, no long-press accents, etc). Those can be added later.
 */
class AbhamInputMethodService : InputMethodService() {

    private var isPersian = true

    // Persian keyboard rows (a simplified, common arrangement)
    private val persianRows = listOf(
        listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج", "چ"),
        listOf("ش", "س", "ی", "ب", "ل", "ا", "ت", "ن", "م", "ک", "گ"),
        listOf("ظ", "ط", "ز", "ر", "ذ", "د", "پ", "و", "."),
    )

    // English keyboard rows (QWERTY)
    private val englishRows = listOf(
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
        listOf("z", "x", "c", "v", "b", "n", "m", "."),
    )

    private lateinit var row1: LinearLayout
    private lateinit var row2: LinearLayout
    private lateinit var row3: LinearLayout
    private lateinit var row4: LinearLayout
    private lateinit var brandLabel: TextView

    private var customFont: android.graphics.Typeface? = null

    override fun onCreateInputView(): View {
        val view = LayoutInflater.from(this).inflate(R.layout.keyboard_view, null)

        row1 = view.findViewById(R.id.row1)
        row2 = view.findViewById(R.id.row2)
        row3 = view.findViewById(R.id.row3)
        row4 = view.findViewById(R.id.row4)
        brandLabel = view.findViewById(R.id.brandLabel)

        customFont = ResourcesCompat.getFont(this, R.font.abham_script)
        brandLabel.typeface = customFont

        buildLetterRows()
        buildBottomRow()

        return view
    }

    /** Builds the three rows of letter keys based on the current language. */
    private fun buildLetterRows() {
        row1.removeAllViews()
        row2.removeAllViews()
        row3.removeAllViews()

        val rows = if (isPersian) persianRows else englishRows
        val targetRows = listOf(row1, row2, row3)

        for ((index, rowLayout) in targetRows.withIndex()) {
            val keys = rows.getOrNull(index) ?: emptyList()
            for (key in keys) {
                rowLayout.addView(createKeyButton(key))
            }
        }
    }

    /** Builds the bottom row: language switch, space, backspace, enter. */
    private fun buildBottomRow() {
        row4.removeAllViews()

        val switchButton = createSpecialButton(if (isPersian) "EN" else "فا") {
            isPersian = !isPersian
            buildLetterRows()
            buildBottomRow()
        }

        val spaceButton = createSpecialButton(getString(R.string.app_name)) {
            currentInputConnection?.commitText(" ", 1)
        }
        spaceButton.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4f)

        val backspaceButton = createSpecialButton("⌫") {
            currentInputConnection?.deleteSurroundingText(1, 0)
        }

        val enterButton = createSpecialButton(if (isPersian) "اینتر" else "Enter") {
            currentInputConnection?.commitText("\n", 1)
        }

        row4.addView(switchButton)
        row4.addView(spaceButton)
        row4.addView(backspaceButton)
        row4.addView(enterButton)
    }

    /** Creates a standard letter key. */
    private fun createKeyButton(label: String): Button {
        val button = Button(this)
        button.text = label
        button.textSize = 18f
        button.setTextColor(resources.getColor(R.color.text_light, theme))
        button.setBackgroundResource(R.drawable.key_background)
        button.typeface = customFont
        button.setPadding(0, 0, 0, 0)
        button.layoutParams = LinearLayout.LayoutParams(0, dpToPx(48), 1f).apply {
            setMargins(dpToPx(2), 0, dpToPx(2), 0)
        }
        button.setOnClickListener {
            currentInputConnection?.commitText(label, 1)
        }
        return button
    }

    /** Creates a function key (space, backspace, enter, language switch). */
    private fun createSpecialButton(label: String, onClick: () -> Unit): Button {
        val button = Button(this)
        button.text = label
        button.textSize = 14f
        button.setTextColor(resources.getColor(R.color.text_on_accent, theme))
        button.setBackgroundResource(R.drawable.key_background)
        button.typeface = customFont
        button.layoutParams = LinearLayout.LayoutParams(0, dpToPx(48), 1f).apply {
            setMargins(dpToPx(2), 0, dpToPx(2), 0)
        }
        button.setOnClickListener { onClick() }
        return button
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}
