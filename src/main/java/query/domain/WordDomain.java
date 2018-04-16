package query.domain;

public class WordDomain {

	public String word;
	public String[] gloss;
	public String[] ontology; 
	public boolean isShow = false;
	public String statusMessage;

	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public boolean isShow() {
		return isShow;
	}
	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String[] getGloss() {
		return gloss;
	}
	public void setGloss(String[] gloss) {
		this.gloss = gloss;
	}
	public String[] getOntology() {
		return ontology;
	}
	public void setOntology(String[] ontology) {
		this.ontology = ontology;
	}
	
}
