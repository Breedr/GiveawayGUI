/**
 * Giveaway.java
 * GiveawayGrowl
 *
 * Created by Ed George on 31 Dec 2014
 *
 */
package uk.breedr.notifier.giveaway;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;
import net.dean.jraw.paginators.TimePeriod;

import org.apache.commons.lang3.builder.ToStringBuilder;

import uk.breedr.notifier.giveaway.gui.utils.TableUtils;

import com.google.gson.annotations.Expose;

/**
 * @author edgeorge
 *
 */
public class Giveaway {
	
	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM HH:mm");
	
	private Submission submission;
	@Expose
	private String title;
	@Expose
	private Date createdDate;
	@Expose
	private URL url;	
	@Expose
	private GiveawayType type;
	
	public Giveaway(Submission submission) {
		this.submission = submission;
		this.type = determinGiveawayType(submission);
		this.createdDate = submission.getCreatedUtc();
		this.url = submission.getShortURL();
		this.title = submission.getTitle();
	}
	
	public Submission getSubmission() {
		return submission;
	}
	
	private GiveawayType determinGiveawayType(Submission s) {
		final String body = s.getSelftext().toString();
		if(!body.substring(0, 1).equals("["))
			return GiveawayType.OTHER;

		String code = body.substring(0, 3).toLowerCase();

		if(code.equals(GiveawayType.GIVEAWAY.getSymbol())){
			return GiveawayType.GIVEAWAY;
		}

		code = code.concat("]");

		if(code.equals(GiveawayType.CONTEST_GIVEAWAY.getSymbol()))
			return GiveawayType.CONTEST_GIVEAWAY;

		if(code.equals(GiveawayType.SPECIAL_GIVEAWAY.getSymbol()))
			return GiveawayType.SPECIAL_GIVEAWAY;

		return GiveawayType.OTHER;
	}

	public GiveawayType getType() {
		return type;
	}

	public boolean isGiveaway(){
		return type.isGiveaway();
	}
	
	public static List<Giveaway> getRecentGiveaways() throws NetworkException{
		RedditClient reddit = new RedditClient("Breedr-Giveaway-Notification");
		SubredditPaginator frontPage = new SubredditPaginator(reddit, "pokemongiveaway");
		frontPage.setLimit(10);                    
		frontPage.setTimePeriod(TimePeriod.ALL); 
		frontPage.setSorting(Sorting.NEW);
		Listing<Submission> submissions = frontPage.next();
		List<Giveaway> giveaways = new ArrayList<Giveaway>();
		for (Submission s : submissions) {
			Giveaway temp = new Giveaway(s);
			if(temp.isGiveaway()){		
				giveaways.add(temp);	
			}
		}
		return giveaways;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * @return
	 */
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Title: " + title + "\n");
		sb.append("Type: " + type.toString().replace("_", " ") + "\n");
		sb.append("Created: " + DATE_FORMAT.format(createdDate) + "\n");
		sb.append("Elapsed Time: " + TableUtils.get_duration(createdDate, new Date()) + "\n");
		sb.append("Link: " + url.toString() + "\n\n");
		sb.append(submission.getSelftext().html());
		return sb.toString();
	}
	
	public String getTitle() {
		return title;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public URL getUrl() {
		return url;
	}

	
}
