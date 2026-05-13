package com.example.bydatasdk

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.eventslogger.EventsLoggerSdk
import com.example.bydatasdk.ui.theme.ByDataSdkTheme
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import org.prebid.mobile.BannerAdUnit
import org.prebid.mobile.BannerParameters
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.Signals
import org.prebid.mobile.TargetingParams

private const val PREBID_SERVER_URL = "https://fast.nexx360.io/inapp"
    //"https://prebid-server-test-j.prebid.org/openrtb2/auction"
private const val PREBID_STORED_REQUEST_ID = "1225"  // "0689a263-318d-448b-a3d4-b02e8a709d9d"
private const val PREBID_BANNER_CONFIG_ID = "q0yz226t"
//"prebid-demo-banner-320-50"
private const val GAM_AD_UNIT_ID = "/22404395434/stocktwitsandroidapp/HomePage_SmallBanner"
private const val BANNER_WIDTH = 320
private const val BANNER_HEIGHT = 50

private const val TAG_STR  = "ADSTest"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var isPrebidInitialized by mutableStateOf(false)
        MobileAds.initialize(this)
        PrebidMobile.setPrebidServerAccountId(PREBID_STORED_REQUEST_ID)
        PrebidMobile.initializeSdk(applicationContext, PREBID_SERVER_URL) {
            PrebidMobile.checkGoogleMobileAdsCompatibility(MobileAds.getVersion().toString())
            runOnUiThread {
                isPrebidInitialized = true
                Log.d(TAG_STR,"isPrebidInitialized  $isPrebidInitialized");
            }
        }
        TargetingParams.setStoreUrl("https://play.google.com/store/apps/details?id=org.stocktwits.android.activity")
        TargetingParams.setBundleName("org.stocktwits.android.activity")

        setContent {
            ByDataSdkTheme {
                CenteredAdScreen(isPrebidInitialized = isPrebidInitialized)
            }
        }
    }
}

@Composable
fun CenteredAdScreen(
    isPrebidInitialized: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (isPrebidInitialized && !LocalInspectionMode.current) {
            PrebidGamBanner()
        } else {
            Box(modifier = Modifier.size(BANNER_WIDTH.dp, BANNER_HEIGHT.dp))
        }
    }
}

@Composable
fun PrebidGamBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val adView = remember {
        AdManagerAdView(context).apply {
            adUnitId = GAM_AD_UNIT_ID
            setAdSizes(AdSize(BANNER_WIDTH, BANNER_HEIGHT))
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d(TAG_STR,"onAdLoaded");
                    EventsLoggerSdk.trackEvent(
                        eventName = "display_impression",
                        userId = "",
                        screenName = "homescreen",
                        screenTitle = "home",
                        params = mapOf("cd1" to GAM_AD_UNIT_ID),
                    )
                    setAdSizes(AdSize(BANNER_WIDTH, BANNER_HEIGHT))
                }
                override fun onAdClicked() {
                    EventsLoggerSdk.trackEvent(
                        eventName = "ad_click",
                        userId = "",
                        screenName = "homescreen",
                        screenTitle = "home",
                        params = mapOf("cd1" to GAM_AD_UNIT_ID),
                    )
                }
            }
        }
    }
    val adUnit = remember {
        BannerAdUnit(PREBID_BANNER_CONFIG_ID, BANNER_WIDTH, BANNER_HEIGHT).apply {
            bannerParameters = BannerParameters().apply {
                api = listOf(Signals.Api.MRAID_3, Signals.Api.OMID_1)
            }
            setAutoRefreshInterval(30)
        }
    }

    DisposableEffect(adView, adUnit) {
        val request = AdManagerAdRequest.Builder().build()
        adUnit.fetchDemand(request) {
            Log.d(TAG_STR,"KV: ${request.customTargeting}");
            adView.loadAd(request)
        }

        onDispose {
            adUnit.stopAutoRefresh()
            adView.destroy()
        }
    }

    AndroidView(
        factory = { adView },
        modifier = modifier.size(BANNER_WIDTH.dp, BANNER_HEIGHT.dp),
    )
}

@Preview(showBackground = true)
@Composable
fun CenteredAdScreenPreview() {
    ByDataSdkTheme {
        CenteredAdScreen(isPrebidInitialized = false)
    }
}