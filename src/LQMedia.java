import javafx.animation.Timeline;

abstract class LQMedia<T> {

    private String id;
    private PWSPosition pwsPosition;
    private PWSTransitions pwsTransitions;
    private Timeline timeline;

    public abstract T getLQMedia();

    public void setId(String id) { this.id = id; }

    public String getId() { return id; }

    public void setPwsPosition(PWSPosition pwsPosition) { this.pwsPosition = pwsPosition; }

    public PWSPosition getPwsPosition() { return pwsPosition; }

    public void setPwsTransitions(PWSTransitions pwsTransitions) { this.pwsTransitions = pwsTransitions; }

    public PWSTransitions getPwsTransitions() { return pwsTransitions; }

    public void setTimeline(Timeline timeline) { this.timeline = timeline; }

    public Timeline getTimeline() { return timeline; }

    public abstract void setTransition(PWSTransitions pwsTransitions);

    public void trigger() { this.timeline.playFrom("trigger"); }
}
