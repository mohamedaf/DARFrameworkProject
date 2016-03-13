package goodDeal.model;

import java.util.ArrayList;

public class AdsProvider {

    private static ArrayList<Ad> ads = new ArrayList<Ad>();   
    
    public ArrayList<Ad> getAds() {
	return ads;
    }
    
    public boolean addAd(Ad ad) {
	
	if(ad != null &&  ad.getTitle() != null && ad.getPrice() > 0) {
	    ads.add(0, ad);
	    return true;
	}
	return false;
	
    }
    
    public Ad getAd(int id) {
	
	for(Ad ad : ads) {
	    if(ad.getId() == id)
		return ad;
	}
	return null;
	
    }
    
    public void updateAd(int id, String title, String content, int price) {
	
	Ad ad = getAd(id);
	if(ad != null) {
	    ad.setTitle(title);
	    ad.setContent(content);
	    ad.setPrice(price);
	}
	
    }
    
    public void removeAd(int id) {
	
	Ad ad = getAd(id);
	if(ad != null)
	    ads.remove(ad);
	
    }
    
    public boolean isExistingAd(int id) {
	return getAd(id) != null;
    }
    
    public boolean isAdPocessor(User user, int adId) {
	
	for(Ad ad : ads) {
	    if(ad.getUser() == user && ad.getId() == adId)
		return true;
	}
	return false;
	
    }
    
}
