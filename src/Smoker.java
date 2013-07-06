import akka.actor.UntypedActor;

/**
 * An impressively ill-prepared smoker 
 */
public class Smoker extends UntypedActor {
	// Bits, where
	// _XX : Tobacco
	// X_X : Paper
	// XX_ : Match
	private final byte ingredient;

	public Smoker(byte ingredient) { this.ingredient = ingredient; }

	@Override
	public void preStart() { System.out.format("Smoker started with %s.\n", 
			Arbiter.byteToIngredient(ingredient)); }

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Byte) { 
			if ((Byte)message + ingredient == Integer.parseInt("111", 2))
				/* If smoker can now smoke */ rollABogie((Byte)message);
			getContext().getSenderFuture().get().completeWithResult(new Acknowledgement());
		}
		if (message instanceof Pill) { getContext().stop(); }
	}

	/**
	 * Smoker uses the provided ingredients to roll
	 * and smoke a Tobaccorrito.
	 */
	private void rollABogie(Byte newIng) throws InterruptedException {
		System.out.format("%s has %s, takes the %s, and rolls a bogie.\n", 
				self().id(), Arbiter.byteToIngredient(ingredient), Arbiter.byteToIngredient(newIng));
		// Time taken to smoke not modeled (out of scope)
		System.out.println("Done smoking; replying.");
	}
}
