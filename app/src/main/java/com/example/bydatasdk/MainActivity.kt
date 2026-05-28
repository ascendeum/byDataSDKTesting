package com.example.bydatasdk

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import com.bydata.sdk.ByDataAnalyticsHolder
import com.eventslogger.EventsLoggerSdk
import com.example.bydatasdk.ui.theme.ByDataSdkTheme
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import org.prebid.mobile.BannerAdUnit
import org.prebid.mobile.BannerParameters
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.ResultCode
import org.prebid.mobile.Signals
import org.prebid.mobile.TargetingParams

private const val PREBID_SERVER_URL = "https://fast.nexx360.io/inapp"
    //"https://prebid-server-test-j.prebid.org/openrtb2/auction"
private const val PREBID_STORED_REQUEST_ID = "1225"  // "0689a263-318d-448b-a3d4-b02e8a709d9d"
private const val HOME_PREBID_BANNER_CONFIG_ID = "2d60yfch"
private const val SYMBOL_PREBID_BANNER_CONFIG_ID = "sckflmua"
//"prebid-demo-banner-320-50"
private const val HOME_GAM_AD_UNIT_ID = "/22404395434/stocktwitsandroidapp/HomePage_SmallBanner"
private const val SYMBOL_GAM_AD_UNIT_ID = "/22404395434/stocktwitsandroidapp/SymbolPage_SmallBanner"
private const val BANNER_WIDTH = 320
private const val BANNER_HEIGHT = 50

private const val TAG_STR  = "ADSTest"

private enum class AdDemoScreen {
    Home,
    Symbol,
}

val byData = ByDataAnalyticsHolder.getInstance()

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
                AdDemoScreen(isPrebidInitialized = isPrebidInitialized)
            }
        }
    }
}

@Composable
fun AdDemoScreen(
    isPrebidInitialized: Boolean,
    modifier: Modifier = Modifier,
) {
    var selectedScreen by remember { mutableStateOf(AdDemoScreen.Home) }

    Box(modifier = modifier.fillMaxSize()) {
        when (selectedScreen) {
            AdDemoScreen.Home -> CenteredAdScreen(isPrebidInitialized = isPrebidInitialized)
            AdDemoScreen.Symbol -> SymbolAdScreen(isPrebidInitialized = isPrebidInitialized)
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(onClick = { selectedScreen = AdDemoScreen.Home }) {
                Text(text = "Home")
            }
            Button(onClick = { selectedScreen = AdDemoScreen.Symbol }) {
                Text(text = "Symbol")
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
            PrebidGamBanner(
                gamAdUnitId = HOME_GAM_AD_UNIT_ID,
                prebidBannerConfigId = HOME_PREBID_BANNER_CONFIG_ID,
                screenName = "homescreen",
                screenTitle = "home",
            )
            byData.track(
                eventName = "page_view",
                screen = "homescreen",
                screenTitle = "home",
            )
        } else {
            Box(modifier = Modifier.size(BANNER_WIDTH.dp, BANNER_HEIGHT.dp))
        }
    }
}

@Composable
fun SymbolAdScreen(
    isPrebidInitialized: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (isPrebidInitialized && !LocalInspectionMode.current) {
            PrebidGamBanner(
                gamAdUnitId = SYMBOL_GAM_AD_UNIT_ID,
                prebidBannerConfigId = SYMBOL_PREBID_BANNER_CONFIG_ID,
                screenName = "symbolscreen",
                screenTitle = "symbol",
            )
            byData.track(
                eventName = "page_view",
                screen = "symbolscreen",
                screenTitle = "symbol",
            )
        } else {
            Box(modifier = Modifier.size(BANNER_WIDTH.dp, BANNER_HEIGHT.dp))
        }
    }
}

@Composable
fun PrebidGamBanner(
    gamAdUnitId: String,
    prebidBannerConfigId: String,
    screenName: String,
    screenTitle: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val adView = remember(gamAdUnitId, screenName, screenTitle) {
        AdManagerAdView(context).apply {
            adUnitId = gamAdUnitId
            setAdSizes(AdSize(BANNER_WIDTH, BANNER_HEIGHT))
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d(TAG_STR,"onAdLoaded");
                    EventsLoggerSdk.trackEvent(
                        eventName = "display_impression",
                        userId = "",
                        screenName = screenName,
                        screenTitle = screenTitle,
                        params = mapOf("cd1" to gamAdUnitId),
                    )
                    byData.track("display_impression",screenName,screenTitle,null,customDimension1 = gamAdUnitId)
                    setAdSizes(AdSize(BANNER_WIDTH, BANNER_HEIGHT))
                }
                override fun onAdClicked() {
                    EventsLoggerSdk.trackEvent(
                        eventName = "ad_click",
                        userId = "",
                        screenName = screenName,
                        screenTitle = screenTitle,
                        params = mapOf("cd1" to gamAdUnitId),
                    )
                    byData.track("ad_click",screenName,screenTitle,null,customDimension1 = gamAdUnitId)
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    EventsLoggerSdk.trackEvent(
                        eventName = "ad_load_failed",
                        screenName = screenName,
                        screenTitle = screenTitle,
                        params = mapOf(
                            "adUnitId" to gamAdUnitId,
                            "errorCode" to error.code,
                            "errorMessage" to error.message,
                            "errorDomain" to error.domain
                        )
                    )
                    byData.track("ad_load_failed",screenName,screenTitle,null,customDimension1 = gamAdUnitId)
                }
            }
        }
    }
    val adUnit = remember(prebidBannerConfigId) {
        BannerAdUnit(prebidBannerConfigId, BANNER_WIDTH, BANNER_HEIGHT).apply {
            bannerParameters = BannerParameters().apply {
                api = listOf(Signals.Api.MRAID_3, Signals.Api.OMID_1)
            }
            setAutoRefreshInterval(30)
        }
    }

    DisposableEffect(adView, adUnit) {
        val request = AdManagerAdRequest.Builder().build()
        adUnit.fetchDemand(request) { resultCode ->
            Log.d(TAG_STR,"fetchDemand result: $resultCode, KV: ${request.customTargeting}");
            if (resultCode == ResultCode.SUCCESS) {
                adView.loadAd(request)
            } else {
                EventsLoggerSdk.trackEvent(
                    eventName = "prebid_fetch_demand_failed",
                    screenName = screenName,
                    screenTitle = screenTitle,
                    params = mapOf(
                        "adUnitId" to gamAdUnitId,
                        "prebidConfigId" to prebidBannerConfigId,
                        "resultCode" to resultCode.name,
                    ),
                )
                byData.track("prebid_fetch_demand_failed",screenName,screenTitle,prebidBannerConfigId,customDimension1 = gamAdUnitId)
            }
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
        AdDemoScreen(isPrebidInitialized = false)
    }
}