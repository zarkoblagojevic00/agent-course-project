package agents.harvester;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.NotFound;
import com.jaunt.UserAgent;

import model.harvester.Hit;

public class HarvestMain {

	private static final String MITROS_MUSIC_ROOT = "https://www.mitrosmusic.com";
	private static final String MITROS_MUSIC_ELECTRIC_GUITARS = "/proizvodi/k:2-Električne-gitare.b:1";
	private static final String MITROS_MUSIC_ACOUSTIC_GUITARS = "/proizvodi/k:4-Akustične-gitare.b:1";
	private static final String MITROS_MUSIC_CLASSICAL_GUITARS = "/proizvodi/k:30-Klasične-gitare.b:1";
	private static final String MITROS_MUSIC_BASS_GUITARS = "/proizvodi/k:23-Električne-bas-gitare.b:1";
	
	private static final String PLAYER_RS_ROOT = "https://www.player.rs";
	private static final String PLAYER_RS_ELECTRIC_GUITARS = "/gitare/elektricne-gitare/0/0/0-0";
	private static final String PLAYER_RS_ACOUSTIC_GUITARS = "/gitare/akusticne-gitare/0/0/0-0";
	private static final String PLAYER_RS_CLASSICAL_GUITARS = "/gitare/klasicne-gitare/0/0/0-0";
	private static final String PLAYER_RS_BASS_GUITARS = "/gitare/bas-gitare/0/0/0-0";
	
	private static final String SKY_MUSIC_ROOT = "https://www.skymusiccenter.com";
	private static final String SKY_MUSIC_ELECTRIC_GUITARS = "/srpski/gitare/elektricne-gitare.html?p=2";
	private static final String SKY_MUSIC_ACOUSTIC_GUITARS = "/srpski/instrumenti/gitare/akusticne-gitare/western-gitare.html";
	private static final String SKY_MUSIC_CLASSICAL_GUITARS = "/srpski/gitare/akusticne-gitare/klasi.html";
	private static final String SKY_MUSIC_BASS_GUITARS = "/srpski/gitare/bas-gitare.html";
	
	
	public static void main(String[] args) {
//		harvestPage(appendToRoot(MITROS_MUSIC_ROOT, MITROS_MUSIC_ELECTRIC_GUITARS), HarvestMain::mapMitrosProductElementToHit, "<div class=\"oneListBox\">");
//		harvestPage(appendToRoot(PLAYER_RS_ROOT, PLAYER_RS_BASS_GUITARS), HarvestMain::mapPlayerProductElementToHit, "<div class=\"product-meta text-center\">");
		harvestPage(appendToRoot(SKY_MUSIC_ROOT, SKY_MUSIC_BASS_GUITARS), HarvestMain::mapSkyMusicProductElementToHit, "<div class=\"product details product-item-details\">");
	}
	
	
	private static String appendToRoot(String root, String path) {
		return String.format("%s%s", root, path);
	}
	
	
	private static List<Hit> harvestPage(String pageUrl, Function<Element, Hit> mapElementToHit, String elementMatcher) {
		UserAgent agent = new UserAgent();
		try {
			agent.visit(pageUrl);
			Elements productElements = agent.doc.findEvery(elementMatcher);
			return productElements.toList().stream().map(mapElementToHit).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	private static Hit mapSkyMusicProductElementToHit(Element productElement) {
		try {
			Element linkAndItemElement = productElement.findFirst("<a href>");
			String item = linkAndItemElement.getChildText().trim();
			String href = linkAndItemElement.getAt("href");
			System.out.println("item: " + item);
			System.out.println("href: " + href);
			
			Element priceElement = productElement.findFirst("<span class=\"price\">");
			double price = 0;
			try {
				NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
				price = nf.parse(priceElement.getTextContent().trim().split("&")[0]).doubleValue();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("price:" + price);
			System.out.println();
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
}
	

//	<div class="product details product-item-details">
//    
//    
//
//    <!-- labels -->
//
//                            <strong class="product name product-item-name">
//        <a class="product-item-link" href="https://www.skymusiccenter.com/srpski/soundsation twanger r nt.html">
//            Soundsation Twanger R NT                            </a>
//    </strong>
//    
//
//    <div class="product description product-item-description">
//        Električna gitara t_
//    </div>
//
//                                <div class="price-box price-final_price" data-role="priceBox" data-product-id="8513" data-price-box="product-id-8513">
//
//
//
//<span class="price-container price-final_price tax weee">
//<span id="product-price-8513" data-price-amount="13570" data-price-type="finalPrice" class="price-wrapper "><span class="price">13.570,00&nbsp;RSD </span></span>
//</span>
//
//</div>                                                                        
//
//    <div class="product actions ">
//        <div class="actions-primary">
//                                                                                                                        <form class="add-to-car-button" data-role="tocart-form" action="https://www.skymusiccenter.com/srpski/checkout/cart/add/uenc/aHR0cHM6Ly93d3cuc2t5bXVzaWNjZW50ZXIuY29tL3NycHNraS9naXRhcmUvZWxla3RyaWNuZS1naXRhcmUuaHRtbD9wPTI%2C/product/8513/" method="post">
//                    <input type="hidden" name="product" value="8513">
//                    <input type="hidden" name="uenc" value="aHR0cHM6Ly93d3cuc2t5bXVzaWNjZW50ZXIuY29tL3NycHNraS9jaGVja291dC9jYXJ0L2FkZC91ZW5jL2FIUjBjSE02THk5M2QzY3VjMnQ1YlhWemFXTmpaVzUwWlhJdVkyOXRMM055Y0hOcmFTOW5hWFJoY21VdlpXeGxhM1J5YVdOdVpTMW5hWFJoY21VdWFIUnRiRDl3UFRJJTJDL3Byb2R1Y3QvODUxMy8,">
//                                                            <input name="form_key" type="hidden" value="yXNbbDXK8gYXcCfL">                                        <button type="submit" title="Dodaj u korpu" class="action tocart primary">
//
//                        <span><img src="/pub/media/wysiwyg/cart-25.png">
//                            </span>
//                    </button>
//                </form>
//                                                                            </div>
//                                                                            <a href="#" class="action towishlist actions-secondary" title="Dodaj u listu želja" aria-label="Dodaj u listu želja" data-post="{&quot;action&quot;:&quot;https:\/\/www.skymusiccenter.com\/srpski\/wishlist\/index\/add\/&quot;,&quot;data&quot;:{&quot;product&quot;:8513,&quot;uenc&quot;:&quot;aHR0cHM6Ly93d3cuc2t5bXVzaWNjZW50ZXIuY29tL3NycHNraS9naXRhcmUvZWxla3RyaWNuZS1naXRhcmUuaHRtbD9wPTI,&quot;}}" data-action="add-to-wishlist" role="button">
//                    <span>
//                        <img src="/pub/media/wysiwyg/male-user-25.png">
//                        </span>
//                </a>
//                                                                    
//                                            <a href="#" class="action tocompare actions-secondary" title="Dodaj u poređenja" aria-label="Dodaj u poređenja" data-post="{&quot;action&quot;:&quot;https:\/\/www.skymusiccenter.com\/srpski\/catalog\/product_compare\/add\/&quot;,&quot;data&quot;:{&quot;product&quot;:&quot;8513&quot;,&quot;uenc&quot;:&quot;aHR0cHM6Ly93d3cuc2t5bXVzaWNjZW50ZXIuY29tL3NycHNraS9naXRhcmUvZWxla3RyaWNuZS1naXRhcmUuaHRtbD9wPTI,&quot;}}" role="button">
//                <span>
//                    <img src="/pub/media/wysiwyg/compare-full.png" width="25">
//                    </span>
//            </a>
//        
//    </div>
//    <div class="read_more_n">
//        <a href="https://www.skymusiccenter.com/srpski/soundsation twanger r nt.html" title="
//        Soundsation Twanger R NT" class="action more read-more">
//            <img src="/pub/media/wysiwyg/strelica.png" width="25">
//                                        </a>
//    </div>
//
//                        </div>	
	
private static Hit mapPlayerProductElementToHit(Element productElement) {
		try {
			Element linkAndItemElement = productElement.getElement(0);
			String item = linkAndItemElement.getElement(0).getChildText();
			String href = linkAndItemElement.getAt("href");
			System.out.println("item: " + item);
			System.out.println("href: " + href);
			
			Element priceElement = productElement.findFirst("<span class=\"product-price\">");
			double price = 0;
			try {
				NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
				price = nf.parse(priceElement.getTextContent().trim().split(" ")[0]).doubleValue();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("price:" + price);
			System.out.println();
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
		
	}
	
//	<div class="product-meta text-center">  
//
//	<!-- PRODUCT TITLE -->
//		<a class="product-name" href="https://www.player.rs/gitare/elektricne-gitare/t-model-gitare/aria--615-fronti-3ts">
//			<h2> ARIA  615 FRONTI 3TS </h2>
//		</a>
//
//        <div class="review">
//			     <i class="fa fa-star-o" aria-hidden="true" title="Artikal nije ocenjen"></i>
//                                    <i class="fa fa-star-o" aria-hidden="true" title="Artikal nije ocenjen"></i>
//                                    <i class="fa fa-star-o" aria-hidden="true" title="Artikal nije ocenjen"></i>
//                                    <i class="fa fa-star-o" aria-hidden="true" title="Artikal nije ocenjen"></i>
//                                    <i class="fa fa-star-o" aria-hidden="true" title="Artikal nije ocenjen"></i>
//            					
//        </div> 
//
//  <!-- PRODUCT PRICE -->
//		<div class="price-holder">
//						    <span class="product-price"> 19.080,00 <span class="global-currency"> rsd.</span> </span>		
//		         		           <span class="product-old-price">23.880,00 <span class="global-currency"> rsd.</span></span>
//		                            </div>	  
//
//<!-- 	 -->
//
//	<div class="add-to-cart-container">   
//
// <!-- ADD TO CART BUTTON -->
//	     		
//					 
//					<div data-roba_id="1511" title="Dodaj u korpu" class="dodavnje JSadd-to-cart buy-btn">
//						  Dodaj u korpu			 
//			        </div>	 	    
//								 	
//		  </div>             
//	 </div>
//	
	
//	 MITROS
	private static Hit mapMitrosProductElementToHit(Element productElement) {
		try {
			Element data;
			data = productElement.findFirst("<div class=\"data\">");
			Element linkAndItemElement = data.findFirst("<a href>");
			String item = linkAndItemElement.getChildText();
			String href = linkAndItemElement.getAt("href");
			System.out.println("item: " + item);
			System.out.println("href: " + href);
			
			Element priceDiv = data.findFirst("<div class=\"price\">");
			Element priceHref = priceDiv.getElement(0);
			double price = 0;
			try {
				NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
				price = nf.parse(priceHref.getTextContent().trim().split(" ")[0]).doubleValue();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("price:" + price);
			System.out.println();
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
		
	}
	
	
	
//	<div class="oneListBox">
//    <a name="15872">
//		<a href="/proizvod/15872-Fender-American-Vintage-%2758-Telecaster-MN-2TSB/" class="listImage ml" rel="nofollow">
//			<img src="https://www.mitrosmusic.com/media/inlineimage/upload_33571_1_t.jpg" width="133" height="100" alt="Fender American Vintage '58 Telecaster MN 2TSB električna gitara električna gitara">
//		</a>
//    </a>
//	  <div class="data">
//			<a name="15872"></a>
//			<h2 class="title" id="search-custom-title">
//				<a name="15872"></a>
//				<a href="/proizvod/15872-Fender-American-Vintage-%2758-Telecaster-MN-2TSB/">Fender American Vintage '58 Telecaster MN 2TSB električna gitara</a>
//        	</h2>
//        	<div class="description">
//			<div class="text" id="search-custom-text">
//             	&nbsp;The American Vintage series introduces an all-new lineup of  original-era model year guitars that bring Fender history and heritage  to authentic and exciting new life. With key features and pivotal design  elements spanning the mid-1950s to the mid-1960s, new American Vintage  series instruments delve deep into Fender's roots—expertly...                    <a href="/proizvod/15872-Fender-American-Vintage-%2758-Telecaster-MN-2TSB/" class="detaljnije ml" rel="nofollow">Detaljnije...</a>
//          </div>
//          <div class="price">
//                Cena:
//                <a href="/proizvod/15872-Fender-American-Vintage-%2758-Telecaster-MN-2TSB/" class="ml" rel="nofollow">245.404,00 DIN</a>
//				  <a href="javascript://" class="cart" onclick="toCart(15872, 245404)">&nbsp;</a>
//			</div>
//        </div>
//    </div>
//</div>


}
