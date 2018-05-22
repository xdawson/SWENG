import javafx.scene.control.Button;
import java.util.ArrayList;

public class Slide {

	// Have a super list of all the media objects
	private ArrayList<FLMedia> mediaList;
	private String id;
	private String type;
	private Defaults slideDefaults;
	//private Interaction interaction;

	public Slide(String id, String type) {
		this.id = id;
		this.type = type;
		this.mediaList = new ArrayList<FLMedia>();
	 }

	public String getId() { return this.id; }

	public String getType() { return this.type; }

	public void setSlideDefaults(Defaults newDefault) { this.slideDefaults = newDefault; }

	public Defaults getSlideDefaults() { return this.slideDefaults; }

	public void add(FLAudio newAudio) { this.mediaList.add(newAudio); }

	public void add(FLImage newImage) { this.mediaList.add(newImage); }

	public void add(FLText newText) { this.mediaList.add(newText); }

	//public void add(Interaction newInteraction) { this.interaction = newInteraction; }

	public void add(FLButton newButton) {this.mediaList.add(newButton); }

	public ArrayList<FLMedia> getMediaList() { return this.mediaList; }

	public FLAudio getAudio(String audioID) {
		for(FLMedia media : this.mediaList) {
			if(media.getId().equals(audioID)) {
				return (FLAudio)media;
			}
		}
		return null;
	}

	public FLImage getImage(String imageID) {
		for(FLMedia media : this.mediaList) {
			if(media.getId().equals(imageID)) {
				return (FLImage)media;
			}
		}
		return null;
	}
}
