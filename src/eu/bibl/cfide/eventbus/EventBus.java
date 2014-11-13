package eu.bibl.cfide.eventbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {

	protected HashMap<Class<? extends Event>, List<EventListenerData>> registeredListeners;

	public EventBus() {
		registeredListeners = new HashMap<Class<? extends Event>, List<EventListenerData>>();
	}

	/**
	 * Registers all the methods marked with the {@link EventTarget} annotation as listeners.
	 * @param src Source object
	 */
	public void register(Object src) {
		if (src == null)
			return;
		for (Method method : src.getClass().getDeclaredMethods()) {
			if (!isValid(method))
				continue;
			@SuppressWarnings("unchecked")
			Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
			EventListenerData data = new EventListenerData(method.getAnnotation(EventTarget.class).priority(), src, method);
			putMap(eventClass, data);
		}
	}

	/**
	 * Registers all the methods marked with the {@link EventTarget} annotation that uses the appropriate event type.
	 * @param src Source object.
	 * @param eventClass Appropriate event type.
	 */
	public void register(Object src, Class<? extends Event> eventClass) {
		if (src == null)
			return;
		for (Method method : src.getClass().getDeclaredMethods()) {
			if (!isValid(method))
				continue;
			if (!method.getParameterTypes()[0].equals(eventClass))
				continue;
			EventListenerData data = new EventListenerData(method.getAnnotation(EventTarget.class).priority(), src, method);
			putMap(eventClass, data);
		}
	}

	/**
	 * Unregisters all of the methods that have been registered as listeners. <br>
	 * <b>NOTE: it is faster to use the {@link #unregister(Object, Class)} method to remove specific listener types.
	 * @param src Source object.</b>
	 */
	public void unregister(Object src) {
		if (src == null)
			return;
		for (Class<? extends Event> eventClass : registeredListeners.keySet()) {
			List<EventListenerData> dataList = registeredListeners.get(eventClass);
			if (dataList == null)
				continue;
			ArrayList<EventListenerData> safeList = new ArrayList<EventListenerData>(dataList);
			for (EventListenerData data : safeList) {
				if (data.src.equals(src))
					dataList.remove(data);
			}
		}
	}

	/**
	 * Unregisters the methods that have been registered as listeners of the appropriate event type.
	 * @param src Source object
	 * @param eventClass Appropriate event type.
	 */
	public void unregister(Object src, Class<? extends Event> eventClass) {
		if (src == null)
			return;
		List<EventListenerData> dataList = registeredListeners.get(eventClass);
		if (dataList == null)
			return;
		ArrayList<EventListenerData> safeList = new ArrayList<EventListenerData>(dataList);
		for (EventListenerData data : safeList) {
			if (data.src.equals(src))
				dataList.remove(data);
		}
	}

	/**
	 * Sends event to all of the registered listeners of the appropriate type.
	 * @param event Event to send.
	 */
	public void dispatch(Event event) {
		Class<? extends Event> eventClass = event.getClass();
		List<EventListenerData> dataList = registeredListeners.get(eventClass);
		if (dataList == null)
			return;
		if (event instanceof EventStoppable) {
			EventStoppable stoppable = (EventStoppable) event;
			for (EventListenerData data : dataList) {
				try {
					data.method.invoke(data.src, event);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				}
				if (stoppable.isStopped())
					break;
			}
		} else {
			for (EventListenerData data : dataList) {
				try {
					data.method.invoke(data.src, event);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				}
			}
		}
	}

	protected void putMap(Class<? extends Event> eventClasss, EventListenerData data) {
		List<EventListenerData> dataList = registeredListeners.get(eventClasss);
		if (dataList == null)
			dataList = new CopyOnWriteArrayList<EventListenerData>();
		dataList.add(data);
		if (!registeredListeners.containsKey(eventClasss))
			registeredListeners.put(eventClasss, dataList);
		prioritise(eventClasss);
	}

	protected void prioritise(Class<? extends Event> eventClass) {
		List<EventListenerData> dataList = registeredListeners.get(eventClass);
		List<EventListenerData> newList = new CopyOnWriteArrayList<EventListenerData>();
		if (dataList != null) {
			for (EventPriority priority : EventPriority.values()) {
				for (EventListenerData data : dataList) {
					if (data.priority == priority)
						newList.add(data);
				}
			}
			registeredListeners.put(eventClass, newList);
		}
	}

	/**
	 * Checks whether the method is valid to be registered as a listener method.
	 * @param method Method to check.
	 * @return Whether it is valid.
	 */
	protected boolean isValid(Method method) {
		return (method.getParameterTypes().length == 1) && method.isAnnotationPresent(EventTarget.class) && Event.class.isAssignableFrom(method.getParameterTypes()[0]);
	}
}