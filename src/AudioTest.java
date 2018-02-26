// NOTE (Chris): The system so far will NOT play any mp3 files
import java.io.File;
import javax.sound.sampled.*;

class AudioTest {

	private Clip audioClip;

	public AudioTest(String audioFile) {
		try {
	        AudioInputStream stream = AudioSystem.getAudioInputStream(new File(audioFile).getAbsoluteFile());
	        this.audioClip = AudioSystem.getClip();
	        this.audioClip.open(stream);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	// This function is non-blocking
	public void play() { this.audioClip.start(); }

	public Clip getClip() {return this.audioClip; }
	public void setClip(Clip audioClip) {this.audioClip = audioClip; }
	public void setClip(String audioFile) {
		try {
	        AudioInputStream stream = AudioSystem.getAudioInputStream(new File(audioFile).getAbsoluteFile());
	        this.audioClip = AudioSystem.getClip();
	        this.audioClip.open(stream);
	    } catch (Exception e) {
	        e.printStackTrace();
		}
	}
}
