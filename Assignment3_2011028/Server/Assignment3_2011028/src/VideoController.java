import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


public class VideoController {

	private ConcurrentHashMap<String, Video> videos;
	
	public VideoController() {
		videos = new ConcurrentHashMap<String, Video>();
	}
	
	public Status updateVideo(String videoID, HashMap<String, String> options) throws InvalidAttribute {
		Video v = videos.get(videoID);
		for(int counter = 0; counter < Video.validAttributes.length; counter++) {
			String attribute = Video.validAttributes[counter];
			Method setter = null;
			try {
				setter = v.getClass().getMethod("set"+attribute, String.class);
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			String value = options.get(attribute);
			try {
				setter.invoke(v, value);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new InvalidAttribute();
			}
		}
		return null;
	}
	
	public Status deleteVideo(String videoID) {
		Video v = videos.get(videoID);
		if (v != null) {
			videos.remove(videoID);
			return new Status("1", "Video Deleted Successfully");
		}
		return new Status("-1", "Video Not Found");
	}
	
	public synchronized Status addVideo(HashMap<String, String> options) throws InvalidAttribute {
		Video newVideo = null;
		newVideo = new Video(options);
		videos.put(newVideo.getID(), newVideo);
		return new Status(newVideo.getID(), "Successfully Added Video");
	}
	
	
	public String getVideoJSON(Video v) {
		StringBuilder response = new StringBuilder();
		response.append("{");
		response.append("\"id\": ");
		response.append("\""+v.getID()+"\",");
		for(int counter = 0; counter < Video.validAttributes.length; counter++) {
			String attribute = Video.validAttributes[counter];
			Method getter = null;
			try {
				getter = v.getClass().getMethod("get"+attribute);
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			String value = null;
			try {
				value = (String) getter.invoke(v);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
			
			response.append("\""+attribute.toLowerCase()+"\": ");
			
			response.append("\""+value.replaceAll("\"", "\\\\\"")+"\"");
			if(counter < Video.validAttributes.length - 1) {
				response.append(",");
			}
		}
		response.append("}");	
		return response.toString();
	}
	
	public String getVideo(String videoID, String responseType) {
		StringBuilder response = new StringBuilder();
		if(responseType.equals("JSON")) {
			Video v = videos.get(videoID);
			response.append(getVideoJSON(v));
		}
		return response.toString();
	}
	
	public String getVideos(String responseType) {
		StringBuilder response = new StringBuilder();
		if(responseType.equals("JSON")) {
			response.append("[");
			
			for(Iterator<Entry<String, Video>> videoIterator = videos.entrySet().iterator(); videoIterator.hasNext(); ) {
				Map.Entry pairs = (Map.Entry)videoIterator.next();
				Video v = (Video) pairs.getValue();
				response.append(getVideoJSON(v));
				if(videoIterator.hasNext()) {
					response.append(",");
				}
			}
			response.append("]");
		}
		return response.toString();
	}
}
