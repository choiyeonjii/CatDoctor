package kr.ac.kumoh.s20181180.catdoctor

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_map.*
import kr.ac.kumoh.s20181180.catdoctor.databinding.ActivityMapBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Thread.sleep
import java.util.*


class MapActivity : AppCompatActivity() {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 82e70293b56bcc9e592b091d1cb39d1a"  // REST API 키
    }
    private lateinit var binding : ActivityMapBinding
    private lateinit var mapView: MapView
    val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    private val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = "동물병원"        // 검색 키워드
    private val ACCESS_FINE_LOCATION = 1000     // Request Code
    private var gpsTracker: GpsTracker? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var isper = 0
    private var eventListener = MarkerEventListener(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mapView = binding.mapView
        mapView.setPOIItemEventListener(eventListener)

        // 위치추적 버튼
        gps_btn.setOnClickListener {
            if (checkLocationService()) {
                // GPS가 켜져있을 경우
                gpsTracker = GpsTracker(this)
                latitude = gpsTracker!!.getLatitude()   // x
                longitude = gpsTracker!!.getLongitude() // y
                Toast.makeText(this, "현재위치 \n위도 $latitude\n경도 $longitude", Toast.LENGTH_LONG).show()
                permissionCheck()
                pageNumber = 1
                searchKeyword(keyword, longitude.toString(), latitude.toString(), pageNumber)
            } else {
                // GPS가 꺼져있을 경우
                Toast.makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
            }
        }

        // 리사이클러 뷰
        binding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter
        // 리스트 아이템 클릭 시 해당 위치로 이동
        listAdapter.setItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                stopTracking()
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y.toDouble(), listItems[position].x.toDouble())
                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
            }
        })

        listAdapter.setCallClickListener(object : ListAdapter.OnCallClickListener{
            override fun onClick(v: View, position: Int) {
                val input = listItems[position].phone
                val myUri = Uri.parse("tel:${input}")
                intent = Intent(Intent.ACTION_DIAL, myUri)
                startActivity(intent)
            }
        })

        listAdapter.setRouteClickListener(object : ListAdapter.OnRouteClickListener{
            override fun onClick(v: View, position: Int) {
                val builder = AlertDialog.Builder(this@MapActivity)
                val itemList = arrayOf("자동차", "대중교통", "도보")
                val url = "kakaomap://route?sp=${latitude},${longitude}&ep=${listItems[position].y},${listItems[position].x}"
                builder.setTitle("길찾기")
                builder.setItems(itemList) { dialog, which ->
                    when(which) {
                        0 -> {
                            val car_url = Uri.parse("${url}&by=CAR")
                            intent = Intent(Intent.ACTION_VIEW, car_url)
                            startActivity(intent)
                        }
                        1 ->{
                            val pub_url = Uri.parse("${url}&by=PUBLICTRANSIT")
                            intent = Intent(Intent.ACTION_VIEW, pub_url)
                            startActivity(intent)
                        }
                        2 ->{
                            val foot_url = Uri.parse("${url}&by=FOOT")
                            intent = Intent(Intent.ACTION_VIEW, foot_url)
                            startActivity(intent)
                        }
                    }
                }
                builder.show()
            }
        })

        // 이전 페이지 버튼
        binding.btnPrevPage.setOnClickListener {
            pageNumber--
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, longitude.toString(), latitude.toString(), pageNumber)
            binding.rvList.smoothScrollToPosition(0)
        }

        // 다음 페이지 버튼
        binding.btnNextPage.setOnClickListener {
            pageNumber++
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, longitude.toString(), latitude.toString(), pageNumber)
            binding.rvList.smoothScrollToPosition(0)
        }
    }
    // 위치 권한 확인
    private fun permissionCheck() {
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                }
                builder.setNegativeButton("취소") { dialog, which ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                } else {
                    // 다시 묻지 않음 클릭 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { dialog, which ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") { dialog, which ->

                    }
                    builder.show()
                }
            }
        } else {
            // 권한이 있는 상태
            startTracking()
            isper = 1
        }
    }
    // 권한 요청 후 행동
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 후 승인됨 (추적 시작)
                Toast.makeText(this, "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
                startTracking()
            } else {
                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
                Toast.makeText(this, "위치 권한이 거절되었습니다", Toast.LENGTH_SHORT).show()
                permissionCheck()
            }
        }
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // 위치추적 시작
    private fun startTracking() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    // 위치추적 중지
    private fun stopTracking() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String, longitude: String, latitude: String, page: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoMap::class.java)            // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword, longitude, latitude, 5000, page)    // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                // 통신 성공
                addItemsAndMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }

    // 검색 결과 처리 함수
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty() && isper == 1) {
            // 검색 결과 있음
            //stopTracking()
            listItems.clear()                   // 리스트 초기화
            binding.mapView.removeAllPOIItems() // 지도의 마커 모두 제거
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                if(document.road_address_name.equals("")){
                    val item = ListLayout(document.place_name,
                        document.address_name,
                        document.phone,
                        document.x,
                        document.y,
                        document.distance,
                        document.place_url)
                    listItems.add(item)
                }
                else{
                    val item = ListLayout(document.place_name,
                        document.road_address_name,
                        document.phone,
                        document.x,
                        document.y,
                        document.distance,
                        document.place_url)
                    listItems.add(item)
                }

                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                        document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding.mapView.addPOIItem(point)
            }
            listAdapter.notifyDataSetChanged()

            binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            binding.btnPrevPage.isEnabled = pageNumber != 1             // 1페이지가 아닐 경우 이전 버튼 활성화

        } else {
            // 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    fun getplaceurl(name:String){
        for( item in listItems ){
            if (name.equals(item.name)){
                val uri = Uri.parse(item.place_url)
                intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
    }

    inner class MarkerEventListener(val context: Context): MapView.POIItemEventListener {
        override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {
        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {
            //Toast.makeText(context, poiItem?.itemName, Toast.LENGTH_SHORT).show()
            getplaceurl(poiItem!!.itemName)
        }

        override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {
        }
    }
}