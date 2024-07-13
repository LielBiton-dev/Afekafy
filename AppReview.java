package afekafy;
import java.time.LocalDate;

public class AppReview {
	
	protected int rate;
	protected String reviewTxt;
	protected LocalDate reviewDate;
	// Who created the review will be the owner user ID of it (user signed in).
	
	public AppReview(String text, int rate) {
		this.reviewTxt = text;
		this.rate = rate; // Rate needs to be between 1->5, where this condition is being checked? 
		reviewDate = LocalDate.now();
	}

}
