package doip.library.util;

import java.util.HashMap;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.MarkerManager;

public class PlantUmlx {
	
	public static final String LIGHT_BLUE = "#80ACEA";
	
	public static Logger logger = LogManager.getLogger(PlantUmlx.class);
	
	public static final Marker markerUml = MarkerManager.getMarker("UML"); 
	
	public static HashMap<String, String> colorMap = new HashMap<String, String>();

	public static String getInstanceName(Object obj ) {
		return obj.getClass().getSimpleName() + "@" + String.format("%08x", obj.hashCode());
	}
	
	public static synchronized void setColor(String context, String color) {
		colorMap.put(context, color);
	}

	public static synchronized void startUml(Class<? extends Object> clazz) {
		logger.debug(markerUml, "@startuml {}", clazz.getSimpleName());
		//logger.debug(markerUml, "skinparam dpi 150");
		//logger.debug(markerUml, "autoactivate on");
	}

	public static synchronized void endUml() {
		logger.debug(markerUml, "@enduml");
	}
	
	public static synchronized void addSeparator(String name) {
		logger.debug(markerUml, "== " + name + "==");
	}
	
	private static String getColor() {
		String color = "";
		String context = ThreadContext.get("context");
		if (context != null) {
			String tmp = colorMap.get(context);
			if (tmp != null) {
				color = tmp;
			}
		}
		return color;
	}
	
	public static synchronized void logCall(Object source, Object target, String function) {
		String sourceName = getInstanceName(source);
		String targetName = getInstanceName(target);
		logger.debug(markerUml, sourceName + " -> " + targetName + ": " + function);
		logger.debug(markerUml, "activate " + targetName + " " + getColor());
	}

	public static synchronized void logCall(Object target, String function) {
		String targetName = getInstanceName(target);
		logger.debug(markerUml, "[-> " + targetName + ": " + function);
		logger.debug(markerUml, "activate " + targetName + " " + getColor());
	}

	public static synchronized void logReturn(Object source, Object target) {
		String sourceName = getInstanceName(source);
		String targetName = getInstanceName(target);
		logger.debug(markerUml, sourceName + " <-- " + targetName);
		logger.debug(markerUml, "deactivate " + targetName);
	}

	public static synchronized void logReturn(Object target) {
		String targetName = getInstanceName(target);
		logger.debug(markerUml, "[<-- " + targetName);
		logger.debug(markerUml, "deactivate " + targetName);
	}
	
	public static synchronized void colorNote(Object obj, String note, String color) {
		String objName = getInstanceName(obj);
		logger.debug(markerUml, "note over " + objName + " " + color + " : " + note);
	}

	public static synchronized void note(Object obj, String note) {
		String objName = getInstanceName(obj);
		logger.debug(markerUml, "note over " + objName + " : " + note);
	}

}
