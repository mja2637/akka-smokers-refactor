import static akka.dispatch.Futures.sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.Future;

/**
 * A kind soul who provides smoking consumables 
 */
public class Arbiter extends UntypedActor {
	private final int nCount;
	private int count;
	private List<ActorRef> smokers;
	
	public Arbiter(int nCount, List<ActorRef> smokers) { 
		this.nCount = nCount;
		this.count = nCount;
		this.smokers = smokers;
	}
	
	@Override
	// Get the ball rolling
	public void preStart() { self().tell(nextIngredients()); }	

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ActorRef) smokers.add((ActorRef)message);
		if (message instanceof Byte) {
			System.out.format("Iteration %d. Next ingredients: %s\n", 
					(nCount-count)+1, Arbiter.byteToIngredient((Byte)message));
			// Send ingredients to smokers
			List<Future<Acknowledgement>> futures = new ArrayList<Future<Acknowledgement>>(3);
			for (ActorRef smoker : smokers) futures.add((Future)smoker.ask((Byte)message));
			// Wait on smokers' response
	        sequence(futures).await();
			if (--count <= 0) self().tell(new Pill());
			else self().tell(nextIngredients());
		}
		if (message instanceof Pill) { 
			System.out.println("Kill message sent to Arbiter");
			// Relay kill message to smokers
			for (ActorRef smoker : smokers) {
				System.out.format("Killing %s\n", smoker.id());
				smoker.tell(message);
			}
			getContext().stop();
		}			
	}
	
	/**
	 * Quite possibly legal syntax
	 * @return 5 lbs flax
	 */
	private Byte nextIngredients() { return new Byte[] {3,5,6}[new Random().nextInt(3)]; }
	
	/**
	 * Utility message for pretty printing
	 * @param img integer storing ingredients as bits 
	 * @return String
	 */
	public static String byteToIngredient(Byte img) {
		if (img == 1) return "matches";
		if (img == 2) return "paper";
		if (img == 3) return "paper & matches";
		if (img == 4) return "tobacco";
		if (img == 5) return "tobacco & matches";
		if (img == 6) return "tobacco & paper";
		return null;
	}
}
