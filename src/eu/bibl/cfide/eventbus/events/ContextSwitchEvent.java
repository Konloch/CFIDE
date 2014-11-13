package eu.bibl.cfide.eventbus.events;

import eu.bibl.cfide.context.CFIDEContext;
import eu.bibl.cfide.eventbus.Event;

public final class ContextSwitchEvent implements Event {
	
	private CFIDEContext newContext;
	
	public ContextSwitchEvent(CFIDEContext newContext) {
		this.newContext = newContext;
	}
	
	public CFIDEContext getNewContext() {
		return newContext;
	}
}