package agents.harvester;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import com.jaunt.Element;
import com.jaunt.NotFound;

import agents.Agent;
import agents.AgentType;
import model.harvester.Hit;
import model.harvester.InstrumentType;

@Singleton
@Remote(Agent.class)
public class SkyMusicHarvesterAgent extends BaseHarvesterAgent {
	
	private static final String ROOT = "https://www.skymusiccenter.com";
	private static final String ELECTRIC_GUITARS = "/srpski/gitare/elektricne-gitare.html?p=2";
	private static final String ACOUSTIC_GUITARS = "/srpski/instrumenti/gitare/akusticne-gitare/western-gitare.html";
	private static final String CLASSICAL_GUITARS = "/srpski/gitare/akusticne-gitare/klasi.html";
	private static final String BASS_GUITARS = "/srpski/gitare/bas-gitare.html";
	private static final String ELEMENT_MATCHER = "<div class=\"product details product-item-details\">";
	
	
	public static final AgentType AGENT_TYPE = new AgentType(SkyMusicHarvesterAgent.class.getSimpleName(), false);


	public SkyMusicHarvesterAgent() {
		super(ROOT, 
				new HashMap<InstrumentType, String>() {{
					put(InstrumentType.ELECTRIC_GUITAR, ELECTRIC_GUITARS);
					put(InstrumentType.ACOUSTIC_GUITAR, ACOUSTIC_GUITARS);
					put(InstrumentType.CLASSICAL_GUITAR, CLASSICAL_GUITARS);
					put(InstrumentType.BASS_GUITAR, BASS_GUITARS);
				}}, 
				ELEMENT_MATCHER);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -344141993242068473L;

	@Override
	protected Function<Element, Hit> getElementToHitMapper(InstrumentType type) {
		return (productElement) -> {
			try {
				Element linkAndItemElement = productElement.findFirst("<a href>");
				String item = linkAndItemElement.getChildText().trim();
				String href = linkAndItemElement.getAt("href");
				
				
				Element priceElement = productElement.findFirst("<span class=\"price\">");
				double price = 0;
				try {
					NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
					price = nf.parse(priceElement.getTextContent().trim().split("&")[0]).doubleValue();
				} catch (Exception e) {
				}
				System.out.println("item: " + item);
				System.out.println("href: " + href);
				System.out.println("price:" + price);
				System.out.println();
				return new Hit(item, price, type, href, rootPath);
			} catch (NotFound e) {
				e.printStackTrace();
			}
			
			return null;
		};
	}
	
	
	//<span class="price-container price-final_price tax weee">
	//<span id="product-price-8513" data-price-amount="13570" data-price-type="finalPrice" class="price-wrapper "><span class="price">13.570,00&nbsp;RSD </span></span>
	//</span>
	//
	//</div>                                                                        
	//
//	    <div class="product actions ">
//	        <div class="actions-primary">
//	                                                                                                                        <form class="add-to-car-button" data-role="tocart-form" action="https://www.skymusiccenter.com/srpski/checkout/cart/add/uenc/aHR0cHM6Ly93d3cuc2t5bXVzaWNjZW50ZXIuY29tL3NycHNraS9naXRhcmUvZWxla3RyaWNuZS1naXRhcmUuaHRtbD9wPTI%2C/product/8513/" method="post">
//	                    <input type="hidden" name="product" value="8513">
//	                    <input type="hidden" name="uenc" value="aHR0cHM6Ly93d3cuc2t5bXVzaWNjZW50ZXIuY29tL3NycHNraS9jaGVja291dC9jYXJ0L2FkZC91ZW5jL2FIUjBjSE02THk5M2QzY3VjMnQ1YlhWemFXTmpaVzUwWlhJdVkyOXRMM055Y0hOcmFTOW5hWFJoY21VdlpXeGxhM1J5YVdOdVpTMW5hWFJoY21VdWFIUnRiRDl3UFRJJTJDL3Byb2R1Y3QvODUxMy8,">
//	                                                            <input name="form_key" type="hidden" value="yXNbbDXK8gYXcCfL">                                        <button type="submit" title="Dodaj u korpu" class="action tocart primary">
	//
//	                        <span><img src="/pub/media/wysiwyg/cart-25.png">
//	                            </span>
//	                    </button>
//	                </form>
//	                                                                            </div>
//	                                                                            <a href="#" class="action towishlist actions-secondary" title="Dodaj u listu želja" aria-label="Dodaj u listu želja" data-post="{&quot;action&quot;:&quot;https:\/\/www.skymusiccenter.com\/srpski\/wishlist\/index\/add\/&quot;,&quot;data&quot;:{&quot;product&quot;:8513,&quot;uenc&quot;:&quot;aHR0cHM6Ly93d3cuc2t5bXVzaWNjZW50ZXIuY29tL3NycHNraS9naXRhcmUvZWxla3RyaWNuZS1naXRhcmUuaHRtbD9wPTI,&quot;}}" data-action="add-to-wishlist" role="button">
//	                    <span>
//	                        <img src="/pub/media/wysiwyg/male-user-25.png">
//	                        </span>
//	                </a>
//	                                                                    
//	                                            <a href="#" class="action tocompare actions-secondary" title="Dodaj u poređenja" aria-label="Dodaj u poređenja" data-post="{&quot;action&quot;:&quot;https:\/\/www.skymusiccenter.com\/srpski\/catalog\/product_compare\/add\/&quot;,&quot;data&quot;:{&quot;product&quot;:&quot;8513&quot;,&quot;uenc&quot;:&quot;aHR0cHM6Ly93d3cuc2t5bXVzaWNjZW50ZXIuY29tL3NycHNraS9naXRhcmUvZWxla3RyaWNuZS1naXRhcmUuaHRtbD9wPTI,&quot;}}" role="button">
//	                <span>
//	                    <img src="/pub/media/wysiwyg/compare-full.png" width="25">
//	                    </span>
//	            </a>
//	        
//	    </div>
//	    <div class="read_more_n">
//	        <a href="https://www.skymusiccenter.com/srpski/soundsation twanger r nt.html" title="
//	        Soundsation Twanger R NT" class="action more read-more">
//	            <img src="/pub/media/wysiwyg/strelica.png" width="25">
//	                                        </a>
//	    </div>
//	                        </div>

}
