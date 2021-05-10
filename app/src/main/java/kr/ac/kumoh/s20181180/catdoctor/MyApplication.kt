package kr.ac.kumoh.s20181180.catdoctor

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }
}