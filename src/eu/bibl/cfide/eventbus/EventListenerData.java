package eu.bibl.cfide.eventbus;

import java.lang.reflect.Method;

public final class EventListenerData {

	public EventPriority priority;
	public Object src;
	public Method method;

	public EventListenerData(EventPriority priority, Object src, Method method) {
		super();
		this.priority = priority;
		this.src = src;
		this.method = method;
		if (!method.isAccessible())
			method.setAccessible(true);
	}
}