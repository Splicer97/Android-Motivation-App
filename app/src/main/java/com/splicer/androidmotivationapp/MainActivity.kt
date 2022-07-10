package com.splicer.androidmotivationapp;

import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.splicer.androidmotivationapp.adapters.CategoryAdapter
import com.splicer.androidmotivationapp.adapters.ContentManager
import com.splicer.androidmotivationapp.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity(), CategoryAdapter.Listener, Animation.AnimationListener {
    private lateinit var binding: ActivityMainBinding
    private var adapter: CategoryAdapter? = null
    private var interAd: InterstitialAd? = null

    //    private var timer: CountDownTimer? = null
    private var posM: Int = 0
    private lateinit var inAnimation: Animation
    private lateinit var outAnimation: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_in)
        outAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_out)
        outAnimation.setAnimationListener(this)
        initAdMob()

        (application as AppMainState).showAdIfAvailable(this) {}
        initRcView()
        binding.imageBg.setOnClickListener {
//            getResult()
        }

    }

    private fun initRcView() = with(binding) {
        adapter = CategoryAdapter(this@MainActivity)
        rcViewCat.layoutManager = LinearLayoutManager(
            this@MainActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
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

//    private fun getResult() {
//        var counter = 0
//        timer?.cancel()
//        timer = object : CountDownTimer(5000, 100) {
//            override fun onTick(p0: Long) {
//                counter++
//                if (counter > 3) counter = 0
//                binding.imageBg.setImageResource(MainConst.imageList[counter])
//            }
//
//            override fun onFinish() {
//                getMessage()
//            }
//        }.start()
//    }

    private fun initAdMob() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun loadInterAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,
            "ca-app-pub-3940256099942544/1033173712", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interAd = ad
                }
            })
    }

    private fun showInterAd() {
        if (interAd != null) {
            interAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
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

    private fun showContent() {
        Toast.makeText(this, "Запуск контента", Toast.LENGTH_LONG).show()
    }

    private fun getMessage() = with(binding) {
        tvMessage.startAnimation(inAnimation)
        tvName.startAnimation(inAnimation)
        imageBg.startAnimation(inAnimation)
        val currentArray = resources.getStringArray(MainConst.arrayList[posM])
        val message = currentArray[Random.nextInt(currentArray.size)]
        val messageList = message?.split("|")
        tvMessage.text = messageList?.get(0) ?: ""
        tvName.text = messageList?.get(1) ?: ""
        imageBg.setImageResource(MainConst.imageList[Random.nextInt(4)])
    }

    override fun onClick(pos: Int) {
        binding.apply {
            tvMessage.startAnimation(outAnimation)
            tvName.startAnimation(outAnimation)
            imageBg.startAnimation(outAnimation)
        }
        posM = pos
//        getResult()
    }

    override fun onAnimationStart(animation: Animation?) {
        TODO("Not yet implemented")
    }

    override fun onAnimationEnd(animation: Animation?) {
        getMessage()
    }

    override fun onAnimationRepeat(animation: Animation?) {
        TODO("Not yet implemented")
    }
}