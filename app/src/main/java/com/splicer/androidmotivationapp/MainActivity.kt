package com.splicer.androidmotivationapp;

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.splicer.androidmotivationapp.adapters.CategoryAdapter
import com.splicer.androidmotivationapp.adapters.ContentManager
import com.splicer.androidmotivationapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var adapter: CategoryAdapter? = null
    private var interAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAdMob()
        (application as AppMainState).showAdIfAvailable(this){}
        initRcView()

    }

    private fun initRcView() = with(binding){
        adapter = CategoryAdapter()
        rcViewCat.layoutManager = LinearLayoutManager(
            this@MainActivity,
            LinearLayoutManager.HORIZONTAL,
            false)
        rcViewCat.adapter = adapter
        adapter?.submitList(ContentManager.list)
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
        loadInterAd()
    }

    override fun onPause() {
        super.onPause()
        binding.adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.adView.destroy()
    }

    private fun initAdMob(){
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun loadInterAd(){
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,
            "ca-app-pub-3940256099942544/1033173712", adRequest,
            object : InterstitialAdLoadCallback(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interAd = ad
                }
            })
    }

    private fun showInterAd(){
        if(interAd != null){
            interAd?.fullScreenContentCallback =
                object : FullScreenContentCallback(){
                    override fun onAdDismissedFullScreenContent() {
                        showContent()
                        interAd = null
                        loadInterAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        showContent()
                        interAd = null
                        loadInterAd()
                    }

                    override fun onAdShowedFullScreenContent() {
                        interAd = null
                        loadInterAd()
                    }
                }

            interAd?.show(this)
        } else {
            showContent()
        }
    }

    private fun showContent(){
        Toast.makeText(this, "Запуск контента", Toast.LENGTH_LONG).show()
    }
}