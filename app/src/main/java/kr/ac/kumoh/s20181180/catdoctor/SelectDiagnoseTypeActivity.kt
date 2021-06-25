package kr.ac.kumoh.s20181180.catdoctor

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import kotlinx.android.synthetic.main.activity_select_diagnose_type.*

class SelectDiagnoseTypeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_diagnose_type)

        val diagnose1 = "Simple Diagnose\n\n반려묘에게 보여지는 증상을\n직접 선택하여\n질병을 에측해주는 방법"
        val title1 = "Simple Diagnose"
        val start1 = diagnose1.indexOf(title1)
        val end1 = start1 + title1.length
        val spannable1 = SpannableStringBuilder(diagnose1)
        spannable1.setSpan(StyleSpan(Typeface.BOLD), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable1.setSpan(RelativeSizeSpan(1.3f), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable1.setSpan(UnderlineSpan(), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        diagnose_btn1.setText(spannable1)

        val diagnose2 = "Photo Diagnose\n\n반려묘에게 보여지는 증상을\n사진을 통해 확인하여\n질병을 예측해주는 방법"
        val title2 = "Photo Diagnose"
        val start2 = diagnose2.indexOf(title2)
        val end2 = start2 + title2.length
        val spannable2 = SpannableStringBuilder(diagnose2)
        spannable2.setSpan(StyleSpan(Typeface.BOLD), start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable2.setSpan(RelativeSizeSpan(1.3f), start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable2.setSpan(UnderlineSpan(), start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        diagnose_btn2.setText(spannable2)

        diagnose_btn1.setOnClickListener {
            startActivity(Intent(this, SymptomClassifyActivity::class.java))
        }

        diagnose_btn2.setOnClickListener {
            startActivity(Intent(this, PhotoDiagnoseActivity::class.java))
        }
    }
}