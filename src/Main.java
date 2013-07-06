import static akka.actor.Actors.actorOf;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

public class Main {

	/**
	 * Run the smoker's problem for nCount interations
	 * @param nCount
	 */
	public static void main(String[] args) {
		int nCount = Integer.parseInt(args[0]);
		
		// Create & parameterize smokers
		List<ActorRef> smokers = new ArrayList<ActorRef>(3);
		for (int i = 0; i <= 2; i++) { smokers.add(actorOf(new SmokerFactory((byte) (1 << i))).start()); }
		
		// Create and start our arbiter
		ActorRef arbiter = actorOf(new ArbiterFactory(nCount, smokers)).start();
		System.out.format("Arbiter started.\n");
	}
}

/**
 * Poison pill
 */
class Pill {}

/**
 * Confirmation message
 */
class Acknowledgement{}

/**
 * Factory for creating fully-constructed Smokers 
 */
class SmokerFactory implements UntypedActorFactory{
	private Byte ingredients;
	public SmokerFactory(Byte ingredients) { this.ingredients = ingredients; }

	@Override
	public UntypedActor create() { return new Smoker(ingredients); }
}

/**
 * Factory for creating fully-constructed Arbiters 
 */
class ArbiterFactory implements UntypedActorFactory{
	private int nCount;
	private List<ActorRef> smokers;
	public ArbiterFactory(int nCount, List<ActorRef> smokers) { 
		this.nCount = nCount;
		this.smokers = smokers;
	}

	@Override
	public UntypedActor create() { return new Arbiter(nCount, smokers); }
}


