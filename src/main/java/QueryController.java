import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import net.didion.jwnl.JWNLException;
import query.biz.FileDBHelper;
import query.biz.FlightHelper;
import query.biz.WordNetHelper;
import query.domain.Flight;
import query.domain.Restaurant;
import query.domain.User;
import query.domain.WordDomain;
import query.ontology.LoadOntology;

@RestController
@EnableAutoConfiguration
public class QueryController {

    @RequestMapping("/api/hello")
    String home() {
        return "Hello World!";
    }
    FlightHelper flightHelper = new FlightHelper();
    private static String userPath = "";
    private static String flightPath = "";

    public static void main(String[] args) throws Exception {
    	
    	ApplicationContext appContext = new ClassPathXmlApplicationContext();
    	Resource userResource = appContext.getResource("classpath:db/user.txt");
    	Resource flightResource = appContext.getResource("classpath:db/flight.txt");
    	Resource ontologyResource = appContext.getResource("classpath:db/DiningOntology.owl");
    	userPath = userResource.getURI().getPath();
    	flightPath = flightResource.getURI().getPath();
    	FileDBHelper fileDBHelper = FileDBHelper.getInstance();
        fileDBHelper.readFile(userResource);
        fileDBHelper.readFlightFile(flightResource);
        
        SpringApplication.run(QueryController.class, args);
        LoadOntology.getInstanes().loadfile(ontologyResource);
        
    }

	@RequestMapping(value="/api/wordnet", method=RequestMethod.POST, headers="Accept=application/json")
	public WordDomain getQuery(@RequestBody Map<String, String> word) throws JWNLException{
		WordDomain wordDomain = null;
		if(word != null){
			String searchStr = word.get("word");
			wordDomain = WordNetHelper.getWordNetDetails(searchStr);
		}
		return wordDomain;
	}
	
//	@CrossOrigin(origins="http://localhost:8100")
	@RequestMapping(value="/api/login", method=RequestMethod.POST, headers="Accept=application/json")
	public User login(@RequestBody User user){
		if(user != null){
			user = flightHelper.checkloginUser(user.getUsername(), user.getPassword());
		}
		return user;
	}
	
	/**
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/api/register", method=RequestMethod.POST, headers="Accept=application/json")
	public boolean bookFlight(@RequestBody User user){
		if(user != null){
			return flightHelper.registerUser(user, userPath);
		}
		return false;
	}
	
	@RequestMapping(value="/api/bookFlight", method=RequestMethod.POST, headers="Accept=application/json")
	public boolean bookFlight(@RequestBody Flight flight){
		if(flight != null){
			return flightHelper.bookFlight(flight, flightPath);
		}
		return false;
	}
	
	@RequestMapping(value="/api/restaurant", method=RequestMethod.POST, headers="Accept=application/json")
	public Restaurant restaurant(@RequestBody Restaurant restaurant){
		if(restaurant != null){
//			return flightHelper.loadRestaurantInfo(restaurant.reqType, restaurant);
			return flightHelper.loadRestaurantDetails(restaurant);
		}
		return null;
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer(){
		return new WebMvcConfigurerAdapter() {
			public void addCorsMappings(CorsRegistry registry){
				registry.addMapping("/api/*").allowedOrigins("http://localhost:8100");
//				registry.addMapping("/api/*").allowedOrigins("file://");
			}
		};
	}
}