package query.ontology;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import query.domain.RestaurantDetails;

public class LoadOntology {

	private static OWLModel owlModel = null;
	private static LoadOntology loadOntology = null; 
	private static OntModel ontModel = null;
	private static Map<String, String> ontologyMap = new HashMap<String, String>();
	private LoadOntology(){
		FileInputStream is;
		try {
			is = new FileInputStream("DiningOntology.owl");
			ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
			ontModel.read(is, null);
			System.out.println("Ontology loaded successfully");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static LoadOntology getInstanes(){
		if(loadOntology == null){
			loadOntology = new LoadOntology();
		}
		return loadOntology;
	}

	public void loadfile(Resource resource){
		try {
			InputStream inpuptStream = resource.getInputStream();
			ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
			ontModel.read(inpuptStream, null);
			System.out.println("Ontology loaded successfully");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadJenaClasses(){
		if(ontModel != null){
			Iterator<OntClass> classes = ontModel.listClasses();
			while(classes.hasNext()){
				OntClass ontClass = classes.next();
				String className = ontClass.getLocalName();
				System.out.println(className);
				if(StringUtils.isNotBlank(className) && className != "null"){
					String value = ontClass.getComment(null);
					ontologyMap.put(className, value);
					//            		System.out.println(className +" - " +value);
				}
			}
		}
	}

	public ArrayList<String> loadSubClasses(String keyword){
		ArrayList<String> value = new ArrayList<String>(); 
		if(ontModel != null && StringUtils.isNotBlank(keyword)){
			OntClass classes = ontModel.getOntClass("http://www.semanticweb.org/ontologies/2010/6/Rest.owl#"+ keyword);
			if(classes != null && classes.listSubClasses() != null){
				for ( final ExtendedIterator merlots = classes.listSubClasses(); merlots.hasNext(); ) {
					String classValue = merlots.next().toString();
					if(StringUtils.isNotEmpty(classValue) && StringUtils.contains(classValue, "#")){
						value.add(StringUtils.split(classValue, "#")[1]);
					} else {
						System.out.println(classValue);
					}
				}
			} else {
				System.out.println("Load ontology - cannot find the class name");
			}
		} else {
			System.out.println("Load ontology - ont model is null/empty - " + keyword);
		}
		return value;
	}

	public String getQuestion(String keyword){
		String question = null;
		if(ontModel != null && StringUtils.isNotBlank(keyword)){
			OntClass classes = ontModel.getOntClass("http://www.semanticweb.org/ontologies/2010/6/Rest.owl#"+ keyword);
			if(classes != null && classes.listSeeAlso() != null){
				for ( final ExtendedIterator merlots = classes.listSeeAlso(); merlots.hasNext(); ) {
					question = merlots.next().toString();
					question = StringUtils.contains(question, "^") ? StringUtils.substring(question, 0, question.indexOf("^")) : "";
					System.out.println(question);
				}
			} else {
				System.out.println("getQuestion - ontology - cannot find the seealso");
			}
		} else {
			System.out.println("getQuestion - ontology - ont model is null/empty - " + keyword);
		}
		return question;
	}

	public ArrayList<RestaurantDetails> loadRestaurant(String className){
		ArrayList<RestaurantDetails> restaurantDetailList = new ArrayList<RestaurantDetails>();
		if(ontModel != null){
			OntClass classes = ontModel.getOntClass("http://www.semanticweb.org/ontologies/2010/6/Rest.owl#"+ className);
			if(classes != null && classes.listInstances() != null){
				for ( final ExtendedIterator merlots = classes.listInstances(); merlots.hasNext(); ) {
					String value = merlots.next().toString();
					restaurantDetailList.add(getRestaurantDetails(value));
				}
			}
		}
		return restaurantDetailList;
	}
	
	public ArrayList<RestaurantDetails> loadAllRestaurant(){
		ArrayList<RestaurantDetails> restaurantDetailList = new ArrayList<RestaurantDetails>();
		if(ontModel != null && ontModel.listIndividuals() != null){
			for ( final ExtendedIterator merlots = ontModel.listIndividuals(); merlots.hasNext(); ) {
				String value = merlots.next().toString();
				System.out.println(value);
				restaurantDetailList.add(getRestaurantDetails(value));
			}
		}
		return restaurantDetailList;
	}

	private RestaurantDetails getRestaurantDetails(String value){
		RestaurantDetails restDetails = new RestaurantDetails();
		if(ontModel != null && StringUtils.isNotBlank(value)){
			String name = StringUtils.contains(value, "#") ? StringUtils.substring(value, StringUtils.lastIndexOf(value, "#") + 1, StringUtils.length(value)) : "";
			restDetails.setName(name);
			Individual individual = ontModel.getIndividual(value);
			if(individual != null && individual.listProperties() != null){
				for ( final StmtIterator stmt = individual.listProperties(); stmt.hasNext(); ) {
					StatementImpl stmtImpl = (StatementImpl) stmt.next(); 
					String valueObject = StringUtils.contains(stmtImpl.getObject().toString(), "^^") ? 
					StringUtils.substring(stmtImpl.getObject().toString(), 0, StringUtils.indexOf(stmtImpl.getObject().toString(), "^^")) : "";
					String property = stmtImpl.getPredicate().getLocalName();
//	            	System.out.println(stmtImpl.getPredicate() +"-"+ stmtImpl.getObject().toString());
					if(property.equals("type")){
						if(StringUtils.contains(stmtImpl.getObject().toString(), "http://www.semanticweb.org/ontologies/2010/6/Rest.owl")){
							String place = stmtImpl.getObject().toString();
							restDetails.setPlace(StringUtils.contains(place, "#") ? StringUtils.substring(place, StringUtils.lastIndexOf(place, "#") + 1, StringUtils.length(place)) : "");
						} else {
							restDetails.setType(valueObject);
						}
					} else if(property.equals("dishType")){
						restDetails.setDish(valueObject);
					} else if(property.equals("cuisine")){
						restDetails.setCuisine(valueObject);
					} else if(property.equals("address")){
						restDetails.setAddress(valueObject);
					} else if(property.equals("startHour")){
						restDetails.setStartHour(valueObject);
					} else if(property.equals("stopHour")){
						restDetails.setStopHour(valueObject);
					} else {
						System.out.println("Cannot find the type :" + property +" - "+ valueObject);
					}
				}
			}
			System.out.println(restDetails.toString());
		}
		return restDetails;
	}

	public String[] searchJena(String keyword){
		String[] comment = null;
		if(StringUtils.isNotBlank(keyword) && ontologyMap != null){
			String comments = ontologyMap.get(keyword);
			if(StringUtils.contains(comments, "\n")){
				comment = StringUtils.split(comments, "\n");
			} else {
				comment = new String[] {comments};
			}
		}
		return comment;
	}

//	public Collection<String> getSubClass(String keyword){
//		try{
//			OWLNamedClass owlSubClass = owlModel.getOWLNamedClass(keyword);
//			Collection<String> subClass = owlSubClass.getSubclasses();
//			/*for (Iterator it = subClass.iterator(); it.hasNext();) { 
//               OWLNamedClass curClass = (OWLNamedClass) it.next();
//               subClass.add(curClass.getName()); 
//           }*/
//			return subClass;
//		}catch(Exception ee){
//			ee.printStackTrace();
//			System.exit(1);
//		}
//		return null;
//	}
//
//	public Collection<String> getObjectProp(String keyword){
//		try{
//			OWLObjectProperty hasObjectProperty = owlModel.getOWLObjectProperty(keyword);
//			Collection<String> objectProperty = hasObjectProperty.getAllowedValues();
//			return objectProperty;
//		}catch(Exception ee){
//			ee.printStackTrace();
//			System.exit(1);
//		}
//		return null;
//	}
//
//	public Collection<String> getIndividual(String keyword){
//		try{
//			OWLIndividual individ = owlModel.getOWLIndividual(keyword);
//			//	Collection<String> individual = individ.getPossibleRDFProperties();
//			Collection<String> individual = individ.getDirectTypes();
//			return individual;
//		}catch(Exception ee){
//			ee.printStackTrace();
//			System.exit(1);
//		}
//		return null;
//	}

	public Vector<String> searchResult(String keyword)
	{
		Vector<String> className = new Vector<String>(); 
		try {
			Collection classes = owlModel.getUserDefinedOWLNamedClasses();
			for (Iterator it = classes.iterator(); it.hasNext();) {
				OWLNamedClass cls = (OWLNamedClass) it.next();
				if(cls.getBrowserText().contains(keyword)){
					className.add(cls.getBrowserText());
					Collection instances = cls.getInstances(false);
					System.out.println("Class " + cls.getBrowserText() + " (" + instances.size() + ")");
					for (Iterator jt = instances.iterator(); jt.hasNext();) {
						OWLIndividual individual = (OWLIndividual) jt.next();
						System.out.println(" - " + individual.getBrowserText());
						className.add(individual.getBrowserText());
					}
				}
			}
			return className;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String []args)
	{
		LoadOntology ss = new LoadOntology();
		/*Collection gg = ss.getIndividual("female");
		for (Iterator it = gg.iterator(); it.hasNext();) { 
        System.out.println(it.next()); 
		}*/
		//		System.out.println("---"+ss.searchResult("Vege"));
		//		System.out.println("---"+ss.searchJena("Place").toString());
		//		ss.loadClasses("New_Delhi");
		//		System.out.println(ss.loadSubClasses("Place"));
		//		System.out.println(ss.loadSubClasses("Mumbai"));
//		ss.loadAllRestaurant();
		ss.loadRestaurant("Goa");
		//		System.out.println(ss.getQuestion("2_Dish"));
		//		System.out.println(ss.loadSubClasses("Non-VegetarianDish"));
		//		System.out.println(ss.loadSubClasses("VegetarianDish"));
		//		System.out.println(ss.loadSubClasses("ChineseCuisine"));
		//		System.out.println(ss.loadSubClasses("NorthIndianCuisine"));
		//		System.out.println(ss.loadSubClasses("SouthIndianCuisine"));
		//		System.out.println("----------");
		//		ss.loadSuperClasses("Burma_Burma");
		//		ss.loadNamedClasses();
	}

	/*is = new FileInputStream("Sports.owl");      
    owlModel = ProtegeOWL.createJenaOWLModelFromInputStream(is);
    OWLNamedClass worldClass = owlModel.getOWLNamedClass("Games");

    //subclass
    OWLNamedClass destinationClass = owlModel.getOWLNamedClass("Destination");
    OWLObjectProperty hasContactProperty = owlModel.getOWLObjectProperty("hasContact");
    OWLDatatypeProperty hasZipCodeProperty = owlModel.getOWLDatatypeProperty("hasZipCode");
    OWLIndividual sydney = owlModel.getOWLIndividual("Sydney");*/
}

