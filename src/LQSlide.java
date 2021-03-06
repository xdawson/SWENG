import javafx.scene.chart.BarChart;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class LQSlide{

    private ArrayList<PWSMedia> pwsMediaArrayList;
    private ArrayList<LQMedia> lqMediaArrayList;

    private String id;
    private String type;

    private PWSFonts pwsFonts;
    private PWSColors pwsColors;
    private PWSTransitions pwsTransitions;

    private Pane slidePane;

    private Boolean answered = false;//, correct = false; TODO answer slide?
    private String answerNum;
    private Boolean gotAnswerCorrect = false;
    private Boolean[] correctArray;
    private LQButton[] buttonArray;
    private PWSAudio incorrectAudio, correctAudio;
    protected PWSImage correctImage, incorrectImage0, incorrectImage1, incorrectImage2, incorrectImage3;

    public LQSlide(String id, String type, PWSFonts pwsFonts, PWSColors pwsColors, PWSTransitions pwsTransitions) {
        this.id = id;
        this.type = type;
        this.pwsMediaArrayList = new ArrayList<>();
        this.lqMediaArrayList = new ArrayList<>();
        this.pwsFonts = pwsFonts;
        this.pwsColors = pwsColors;
        this.pwsTransitions = pwsTransitions;
        this.slidePane = new Pane();

        //answer slide
        this.correctArray = new Boolean[4];
        this.answerNum = answerNum;

        this.correctAudio = new PWSAudio(" correct jingle", new PWSPosition(0, 0, 0, 0), new PWSTransitions("trigger", 0), "CORRECT.mp3");
        this.add(this.correctAudio);

        this.incorrectAudio = new PWSAudio(" incorrect jingle", new PWSPosition(0, 0, 0, 0), new PWSTransitions("trigger", 0), "INCORRECT.wav");
        this.add(this.incorrectAudio);
    }

    public Pane getSlidePane() {
        return this.slidePane;
    }

    public String getLQSlideId() { return id; }

    public String getLQSlideType() { return this.type; }

    public PWSFonts getPwsFonts() { return pwsFonts; }

    public PWSColors getPwsColors() { return pwsColors; }

    public PWSTransitions getPwsTransitions() { return pwsTransitions; }

    public void startTransitions() {
        for(PWSMedia pwsMedia : pwsMediaArrayList) {
            pwsMedia.getTimeline().playFrom("auto");
        }
        for(LQMedia lqMedia : lqMediaArrayList) {
            lqMedia.getTimeline().playFrom("auto");
        }
    }

    public void endTransitions() {
        for(PWSMedia pwsMedia : pwsMediaArrayList) {
            if(pwsMedia instanceof PWSAudio) {
                ((PWSAudio) pwsMedia).stop();
            }
            if(pwsMedia instanceof PWSVideo) {
                ((PWSVideo) pwsMedia).stop();
            }
            if(pwsMedia instanceof ContractVideo) {
                ((ContractVideo) pwsMedia).getPwsVideo().stop();
            }
            pwsMedia.getTimeline().stop();
        }
        for(LQMedia lqMedia : lqMediaArrayList) {
            lqMedia.getTimeline().stop();
        }
    }

    public void add(PWSText pwsText) {
        this.pwsMediaArrayList.add(pwsText);
        this.slidePane.getChildren().add(pwsText.getPwsMedia());
    }

    public void add(PWSShape pwsShape) {
        this.pwsMediaArrayList.add(pwsShape);
        this.slidePane.getChildren().add(pwsShape.getPwsMedia());
    }

    public void add(PWSImage pwsImage) {
        this.pwsMediaArrayList.add(pwsImage);
        this.slidePane.getChildren().add(pwsImage.getPwsMedia());
    }

    public void add(PWSAudio pwsAudio) {
        this.pwsMediaArrayList.add(pwsAudio);
//        this.slidePane.getChildren().add(pwsAudio.getPwsMedia());
    }

    public void add(PWSVideo pwsVideo) {
        this.pwsMediaArrayList.add(pwsVideo);
        this.slidePane.getChildren().add(pwsVideo.getPwsMedia());
    }

    public void add(ContractVideo contractVideo) {
        this.pwsMediaArrayList.add(contractVideo);
        this.slidePane.getChildren().add(contractVideo.getPwsMedia());
    }

    public void add(LQButton lqButton) {
        this.lqMediaArrayList.add(lqButton);
        this.slidePane.getChildren().add(lqButton.getLQMedia());
    }

    public void add(BarChart<String, Number> bc) {
        this.slidePane.getChildren().add(bc);
    }

    public void muteAudio(boolean mute) {
        for(PWSMedia pwsMedia : pwsMediaArrayList) {
            if(pwsMedia instanceof PWSAudio) {
                ((PWSAudio) pwsMedia).mute(mute);
            }
            if(pwsMedia instanceof PWSVideo) {
                ((PWSVideo) pwsMedia).mute(mute);
            }
            if(pwsMedia instanceof ContractVideo) {
                ((ContractVideo) pwsMedia).getPwsVideo().mute(mute);
            }
        }
    }

    public String getAnswerNum(){ return this.answerNum; }

    public void setCorrectArray(Boolean correct, Integer answerNum){
        this.correctArray[answerNum] = correct;
//        System.out.println(correctArray[0] + ", " + correctArray[1] + ", " + correctArray[2] + ", " + correctArray[3]);
    }

    public Boolean[] getCorrectArray() { return correctArray; }

    public int getCorrectAnswerNum() {
        for(int i = 0; i < correctArray.length; i++) {
            if(correctArray[i] == true) {
                return i + 1;
            }
        }
        return 0;
    }

    protected void setActionListeners(){
//        System.out.println("media list size = " + this.pwsMediaArrayList.size() + "id: " + id + " type: " + type);
//        System.out.println(this.getAnswered());
        LQButton button1;
        LQButton button2;
        LQButton button3;
        LQButton button4;

        this.buttonArray = new LQButton[4];
        int numberOfButtons = 0;

        for (LQMedia lqMediaArrayList : this.lqMediaArrayList) {
            if (lqMediaArrayList instanceof LQButton) {
                buttonArray[numberOfButtons++] = (LQButton) lqMediaArrayList;
            }
        }

        button1 = buttonArray[0];
        button1.getLQButton().setOnMouseClicked((clickEvent) -> {
            if(!this.getAnswered()) {
                this.setAnswered(true);
                checkCorrect(this.getCorrectArray(), 0);
                button1.getButtonTriggerAudio().trigger();
                button1.getButtonTriggerImage().trigger();
//                System.out.println("pressed answer 1");
            }
            else{
//                System.out.println("Already Answered!!");
            }
        });

        button2 = buttonArray[1];
        button2.getLQButton().setOnMouseClicked((clickEvent) -> {
            if(!this.getAnswered()) {
                this.setAnswered(true);
                checkCorrect(this.getCorrectArray(), 1);
                button2.getButtonTriggerAudio().trigger();
                button2.getButtonTriggerImage().trigger();
//                System.out.println("pressed answer 2");
            }
            else{
//                System.out.println("Already Answered!!");
            }
        });

        button3 = buttonArray[2];
        button3.getLQButton().setOnMouseClicked((clickEvent) -> {
            if(!this.getAnswered()) {
                this.setAnswered(true);
                checkCorrect(this.getCorrectArray(), 2);
                button3.getButtonTriggerAudio().trigger();
                button3.getButtonTriggerImage().trigger();
//                System.out.println("pressed answer 3");
            }
            else{
//                System.out.println("Already Answered!!");
            }
        });

        button4 = buttonArray[3];
        button4.getLQButton().setOnMouseClicked((clickEvent) -> {
            if(!this.getAnswered()) {
                this.setAnswered(true);
                checkCorrect(this.getCorrectArray(), 3);
                button4.getButtonTriggerAudio().trigger();
                button4.getButtonTriggerImage().trigger();
//                System.out.println("pressed answer 4");
            }
            else{
//                System.out.println("Already Answered!!");
            }
        });
    }

    private void setGotAnswerCorrect(Boolean correct) { this.gotAnswerCorrect = correct; }

    private void setAnswered(Boolean answered) { this.answered = answered; }

    public void setAnswerNum(String answerNum) { this.answerNum = answerNum; }

    public Boolean getGotAnswerCorrect() { return this.gotAnswerCorrect; }

    public Boolean getAnswered() { return this.answered; }

    public void resetAnswer(){
        setAnswered(false);
        setGotAnswerCorrect(false);
    }

    private void checkCorrect(Boolean[] correctArray, Integer answerNumInt) {
//        System.out.println("answerNumInt = " + answerNumInt);
        if (correctArray[answerNumInt]) {
//            this.correctAudio.play();
            //showImage();
            this.setGotAnswerCorrect(true);
        }
        else{
            //this.incorrectAudio.play();
            this.setGotAnswerCorrect(false);
            switch (answerNumInt) {
                case 0:
//                    this.incorrectAudio.play();
                    //showImage();
                    //this.incorrectImage0.setVisible();
                    break;
                case 1:
//                    this.incorrectAudio.play();
                    //showImage();
                    //this.incorrectImage1.setVisible();
                    break;
                case 2:
//                    this.incorrectAudio.play();
                    //showImage();
                    //this.incorrectImage2.setVisible();
                    break;
                case 3:
//                    this.incorrectAudio.play();
                    //showImage();
                    //this.incorrectImage3.setVisible();
                    break;
                default:
                    break;
            }
        }
//        System.out.println(this.getGotAnswerCorrect());
    }

    public String toString() {
        return "PWSSlide:\nid = " + this.id + "\ntype = " + this.type;
    }

    public LQButton[] getButtonArray(){
        return this.buttonArray;
    }
}
