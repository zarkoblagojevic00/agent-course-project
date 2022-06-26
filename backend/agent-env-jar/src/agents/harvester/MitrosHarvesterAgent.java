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
public class MitrosHarvesterAgent extends BaseHarvesterAgent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3711762245381433450L;
	
	private static final String ROOT = "https://www.mitrosmusic.com";
	private static final String ELECTRIC_GUITARS = "/proizvodi/k:2-Električne-gitare.b:1";
	private static final String ACOUSTIC_GUITARS = "/proizvodi/k:4-Akustične-gitare.b:1";
	private static final String CLASSICAL_GUITARS = "/proizvodi/k:30-Klasične-gitare.b:1";
	private static final String BASS_GUITARS = "/proizvodi/k:23-Električne-bas-gitare.b:1";
	private static final String ELEMENT_MATCHER = "<div class=\"oneListBox\">";
	
	public static final AgentType AGENT_TYPE = new AgentType(MitrosHarvesterAgent.class.getSimpleName(), false);

	public MitrosHarvesterAgent() {
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
				Element data = productElement.findFirst("<div class=\"data\">");
				Element linkAndItemElement = data.findFirst("<a href>");
				String item = linkAndItemElement.getChildText();
				String href = linkAndItemElement.getAt("href");
				
				Element priceDiv = data.findFirst("<div class=\"price\">");
				Element priceHref = priceDiv.getElement(0);
				double price = 0;
				try {
					NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
					price = nf.parse(priceHref.getTextContent().trim().split(" ")[0]).doubleValue();
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
	
//	<div class="oneListBox">
//  <a name="15872">
//		<a href="/proizvod/15872-Fender-American-Vintage-%2758-Telecaster-MN-2TSB/" class="listImage ml" rel="nofollow">
//			<img src="https://www.mitrosmusic.com/media/inlineimage/upload_33571_1_t.jpg" width="133" height="100" alt="Fender American Vintage '58 Telecaster MN 2TSB električna gitara električna gitara">
//		</a>
//  </a>
//	  <div class="data">
//			<a name="15872"></a>
//			<h2 class="title" id="search-custom-title">
//				<a name="15872"></a>
//				<a href="/proizvod/15872-Fender-American-Vintage-%2758-Telecaster-MN-2TSB/">Fender American Vintage '58 Telecaster MN 2TSB električna gitara</a>
//      	</h2>
//      	<div class="description">
//			<div class="text" id="search-custom-text">
//           	&nbsp;The American Vintage series introduces an all-new lineup of  original-era model year guitars that bring Fender history and heritage  to authentic and exciting new life. With key features and pivotal design  elements spanning the mid-1950s to the mid-1960s, new American Vintage  series instruments delve deep into Fender's roots—expertly...                    <a href="/proizvod/15872-Fender-American-Vintage-%2758-Telecaster-MN-2TSB/" class="detaljnije ml" rel="nofollow">Detaljnije...</a>
//        </div>
//        <div class="price">
//              Cena:
//              <a href="/proizvod/15872-Fender-American-Vintage-%2758-Telecaster-MN-2TSB/" class="ml" rel="nofollow">245.404,00 DIN</a>
//				  <a href="javascript://" class="cart" onclick="toCart(15872, 245404)">&nbsp;</a>
//			</div>
//      </div>
//  </div>
//</div>


	
}
