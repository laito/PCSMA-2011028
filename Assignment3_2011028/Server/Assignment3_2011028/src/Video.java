import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class Video {

	private String ID;
	private String name;
	private String duration;
	private String type;
	private String description;
	private String rating;
	private static Integer count = 1;
	
	
	static String[] validAttributes = {"Name", "Duration", "Type", "Description", "Rating"};
	
	public Video(HashMap<String, String> attributes) throws InvalidAttribute {
		this.setID(Integer.toString(count++));
		for(String attribute: validAttributes) {
			if(attributes.containsKey(attribute)) {
				try {
					Method setter = this.getClass().getMethod("set"+attribute, String.class);
					setter.invoke(this, attributes.get(attribute));
				} catch (NoSuchMethodException | SecurityException e) {
					throw new InvalidAttribute();
				} catch (IllegalAccessException e) {
					throw new InvalidAttribute();
				} catch (IllegalArgumentException e) {
					throw new InvalidAttribute();
				} catch (InvocationTargetException e) {
					throw new InvalidAttribute();
				} 
			} else {
				throw new InvalidAttribute();
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) throws InvalidAttribute {
		Float ratingFloat = null;
		try {
			ratingFloat = Float.parseFloat(rating);
		} catch (NumberFormatException ex) {
			throw new InvalidAttribute();
		}
		if(ratingFloat >= 0 && ratingFloat <= 5) {
			this.rating = rating;
		} else {
			throw new InvalidAttribute();
		}
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

}