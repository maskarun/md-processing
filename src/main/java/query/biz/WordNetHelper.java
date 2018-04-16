package query.biz;

import java.io.FileInputStream;
import java.util.Iterator;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.list.PointerTargetNode;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.data.relationship.AsymmetricRelationship;
import net.didion.jwnl.data.relationship.Relationship;
import net.didion.jwnl.data.relationship.RelationshipFinder;
import net.didion.jwnl.data.relationship.RelationshipList;
import net.didion.jwnl.dictionary.Dictionary;
import query.domain.WordDomain;
import query.ontology.LoadOntology;

/** A class to demonstrate the functionality of the JWNL package. */
public class WordNetHelper {

	public static WordDomain getWordNetDetails(String word) {
		WordDomain wordDomain = new WordDomain();
		wordDomain.setWord(word);
		try {
			JWNL.initialize(new FileInputStream("file_properties.xml"));
			System.out.println("Word to search :" + word);
			String[] gloss = null;
			IndexWord indexWord = Dictionary.getInstance().getIndexWord(POS.VERB, word);
			if(indexWord != null){
				gloss = demonstrateListOperation(indexWord);
			}
			IndexWord indexNoun = Dictionary.getInstance().getIndexWord(POS.NOUN, word);
			if(indexNoun != null){
				if(gloss !=null){
				} else {
					gloss = demonstrateListOperation(indexNoun);
				}
			}
			wordDomain.setGloss(gloss);
			
			if(wordDomain != null){
				String[] value = LoadOntology.getInstanes().searchJena(word);
				wordDomain.setOntology(value);
			}
			
			if(wordDomain.getGloss() == null && wordDomain.getOntology().length <= 0){
				wordDomain.setShow(true);
				wordDomain.setStatusMessage("No Result Found");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return wordDomain;
	}

	private void demonstrateMorphologicalAnalysis(String phrase) throws JWNLException {
		// "running-away" is kind of a hard case because it involves
		// two words that are joined by a hyphen, and one of the words
		// is not stemmed. So we have to both remove the hyphen and stem
		// "running" before we get to an entry that is in WordNet
		System.out.println("Base form for \"" + phrase + "\": " +
		                   Dictionary.getInstance().lookupIndexWord(POS.VERB, phrase));
	}

	private static String[] demonstrateListOperation(IndexWord word) throws JWNLException {
		PointerTargetNodeList hypernyms = PointerUtils.getInstance().getDirectHypernyms(word.getSense(1));
		System.out.println("Direct hypernyms of \"" + word.getLemma() + "\":");
		String []gloss = new String[hypernyms.size()];
		for(int i=0;i<hypernyms.size();i++){
			PointerTargetNode pointerTargetNode = (PointerTargetNode) hypernyms.get(i);
			gloss[i] = pointerTargetNode.getSynset().getGloss();
			System.out.println(pointerTargetNode.getSynset().getGloss());
		}
		return gloss;
	}

	private void demonstrateTreeOperation(IndexWord word) throws JWNLException {
		// Get all the hyponyms (children) of the first sense of <var>word</var>
		PointerTargetTree hyponyms = PointerUtils.getInstance().getHyponymTree(word.getSense(1));
		System.out.println("Hyponyms of \"" + word.getLemma() + "\":");
		hyponyms.print();
	}

	private void demonstrateAsymmetricRelationshipOperation(IndexWord start, IndexWord end) throws JWNLException {
		// Try to find a relationship between the first sense of <var>start</var> and the first sense of <var>end</var>
		RelationshipList list = RelationshipFinder.getInstance().findRelationships(start.getSense(1), end.getSense(1), PointerType.HYPERNYM);
		System.out.println("Hypernym relationship between \"" + start.getLemma() + "\" and \"" + end.getLemma() + "\":");
		for (Iterator itr = list.iterator(); itr.hasNext();) {
			((Relationship) itr.next()).getNodeList().print();
		}
		System.out.println("Common Parent Index: " + ((AsymmetricRelationship) list.get(0)).getCommonParentIndex());
		System.out.println("Depth: " + ((Relationship) list.get(0)).getDepth());
	}

	private void demonstrateSymmetricRelationshipOperation(IndexWord start, IndexWord end) throws JWNLException {
		// find all synonyms that <var>start</var> and <var>end</var> have in common
		RelationshipList list = RelationshipFinder.getInstance().findRelationships(start.getSense(1), end.getSense(1), PointerType.SIMILAR_TO);
		System.out.println("Synonym relationship between \"" + start.getLemma() + "\" and \"" + end.getLemma() + "\":");
		for (Iterator itr = list.iterator(); itr.hasNext();) {
			((Relationship) itr.next()).getNodeList().print();
		}
		System.out.println("Depth: " + ((Relationship) list.get(0)).getDepth());
	}
	
//	public static void main(String []args){
//		WordNetHelper.getWordNetDetails("accomplish");
//	}
}