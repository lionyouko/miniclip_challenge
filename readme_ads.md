## Instructions For Using Advertads library

Advertads library offers a BannerView custom view class that is able to load and show advertise images.

It downloads the images when the view is created.

BannerView is able to inform when it downloads ads, when it is triggered, dismissed and clicked.

To setup a bannerview in the project, you can do the following:

        // Have a var for it
    	private lateinit var bannerView: BannerView

    	//find it on onCreate()
    	bannerView = findViewById(R.id.bannerAd)
    	setupBannerView()

    	//setup it with some customization available in the library
	    private fun setupBannerView(){
	        bannerView.setmAdDownloader(ImageAdsDownloadHelper.getInstance(getApplicationContext()))
	        bannerView.setUrlResourceHelper(URLResourceHelper())
	        bannerView.setmBannerListener(this) //set this if u want to know what happens in bannerview
	        lifecycle.addObserver(bannerView)
	        bannerView.downloadAds(this.applicationContext)
	        bannerView.setSizeAdImages(300,100)
	        bannerView.prepareAdImages()
	        // Here I want to show the add just when the person clicks to start a game and them keep it there
	        // If I let it there, it starts with activity taking the portion designed to it
	        // So it complies what is asked, but for demonstration, I want to start it only when player press play.
	        bannerView.visibility = View.GONE
	        bannerView.setOnClickListener(bannerView)
	    }

	    // Implement BannerListener
	        override fun onBannerAdClicked() {
		        Toast.makeText(applicationContext, "Banner clicked",
		            Toast.LENGTH_SHORT).show()
		    }

		    override fun onAdsLoaded() {
		        Toast.makeText(applicationContext, "Ads Loaded",
		            Toast.LENGTH_SHORT).show()
		    }

		    override fun onBannerAdTriggered() {
		        Toast.makeText(applicationContext, "Banner Triggered",
		            Toast.LENGTH_SHORT).show()
		    }

		    override fun onBannerAdDismissed() {
		        Toast.makeText(applicationContext, "Banner Dismissed",
		            Toast.LENGTH_SHORT).show()
		    }

		    //Trigger banner view when needed 
		    bannerView.triggerBannerView()


One could create different types of URLResourceHelper and ImageAdsDownloadHelper and provide to banner view.