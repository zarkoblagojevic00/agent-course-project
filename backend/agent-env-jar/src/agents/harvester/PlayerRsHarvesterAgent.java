package agents.harvester;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
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
public class PlayerRsHarvesterAgent extends BaseHarvesterAgent {

	
	private static final long serialVersionUID = 4852110185927694838L;
	
	private static final String ROOT = "https://www.player.rs";
	private static final String ELECTRIC_GUITARS = "/gitare/elektricne-gitare/0/0/0-0";
	private static final String ACOUSTIC_GUITARS = "/gitare/akusticne-gitare/0/0/0-0";
	private static final String CLASSICAL_GUITARS = "/gitare/klasicne-gitare/0/0/0-0";
	private static final String BASS_GUITARS = "/gitare/bas-gitare/0/0/0-0";
	private static final String ELEMENT_MATCHER = "<div class=\"product-meta text-center\">";
	
	public static final AgentType AGENT_TYPE = new AgentType(PlayerRsHarvesterAgent.class.getSimpleName(), false);

	
	public PlayerRsHarvesterAgent() {
		super(ROOT, 
				new HashMap<InstrumentType, String>() {{
					put(InstrumentType.ELECTRIC_GUITAR, ELECTRIC_GUITARS);
					put(InstrumentType.ACOUSTIC_GUITAR, ACOUSTIC_GUITARS);
					put(InstrumentType.CLASSICAL_GUITAR, CLASSICAL_GUITARS);
					put(InstrumentType.BASS_GUITAR, BASS_GUITARS);
				}}, 
				ELEMENT_MATCHER);
	}

	
	@Override
	protected Function<Element, Hit> getElementToHitMapper(InstrumentType type) {
		return (productElement) -> {
			try {
				Element linkAndItemElement = productElement.getElement(0);
				String item = linkAndItemElement.getElement(0).getChildText();
				String href = linkAndItemElement.getAt("href");
				Element priceElement = productElement.findFirst("<span class=\"product-price\">");
				double price = 0;
				try {
					NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
					price = nf.parse(priceElement.getTextContent().trim().split(" ")[0]).doubleValue();
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

}
