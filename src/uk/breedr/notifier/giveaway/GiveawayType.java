/**
 * GiveawayTypw.java
 * GiveawayGrowl
 *
 * Created by Ed George on 27 Dec 2014
 *
 */
package uk.breedr.notifier.giveaway;

/**
 * @author edgeorge
 *
 */
public enum GiveawayType {
	OTHER, GIVEAWAY, SPECIAL_GIVEAWAY, CONTEST_GIVEAWAY;
	
	private String symbol;
	
	static{
		OTHER.symbol = "?";
		GIVEAWAY.symbol = "[g]";
		SPECIAL_GIVEAWAY.symbol = "[sg]";
		CONTEST_GIVEAWAY.symbol = "[cg]";
	}

	/**
	 * @return the symbol used at the start of posts
	 */
	public String getSymbol() {
		return symbol;
	}	
	
	public boolean isGiveaway(){
		return !this.equals(OTHER);
	}
}
