package kr.ac.kumoh.s20181180.catdoctor
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoMap {
    @GET("v2/local/search/keyword.json")    // Keyword.json의 정보를 받아옴
    fun getSearchKeyword(
            @Header("Authorization") key: String,     // 카카오 API 인증키 [필수]
            @Query("query") query: String,            // 검색을 원하는 질의어 [필수]
            @Query("x") x:String,   // 중심 좌표의 x값 혹은 longitude(경도)
            @Query("y") y:String,   // 중심 좌표의 y값 혹은 latitude(위도)
            @Query("radius") radius: Int,           // 반경 거리 단위 meter
            @Query("page") page: Int                  // 결과 페이지 번호
    ): Call<ResultSearchKeyword>     // 받아온 정보가 ResultSearchKeyword 클래스의 구조로 담김
}