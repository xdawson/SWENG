import com.sun.org.apache.xpath.internal.operations.Bool;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.soap.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.Pane;

// TODO parse Transitions

public class XMLParser extends DefaultHandler{
	private Presentation presentation;
	private Defaults defaults;
	private Slide currentSlide;
	private FLText currentText;
	private FLButton currentButton;
	private FLAudio currentAudio;

	private boolean inText = false;
	private boolean inFormat = false;
	private boolean inButton = false;
	private TextStyle currentStyle;
	private Colors currentColor;

	public XMLParser(String inputFile, Defaults programDefault){
        defaults = programDefault;
        System.out.println("Starting to parse " + inputFile);

	    try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputFile, this);
		}
		catch (ParserConfigurationException pce) { pce.printStackTrace(); }
		catch (SAXException saxe) { saxe.printStackTrace(); }
		catch (IOException ioe) { ioe.printStackTrace(); }
	}

    @Override
	public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
	    String elementName = localName;
		if (elementName.equals("")) {
			elementName = qName;
		}

		switch (elementName) {
			case "Presentation":
				this.presentation = new Presentation(this.defaults, new Position(getAttributeDouble(attrs, "width"), getAttributeDouble(attrs, "height")));
                break;
			case "Slide": {
				this.currentSlide = new Slide(getAttributeString(attrs, "id"));
				this.currentSlide.setSlideDefaults(this.defaults);

				String color = getAttributeString(attrs, "color");
				String fill = getAttributeString(attrs, "fill");
				String font = getAttributeString(attrs, "font");
				String textSize = getAttributeString(attrs, "textsize");
				String italic = getAttributeString(attrs, "italic");
				String bold = getAttributeString(attrs, "bold");
				String underline = getAttributeString(attrs, "underline");

				if(color != null) { this.currentSlide.getSlideDefaults().getDefaultColors().setColor(color); }
				if(fill != null) { this.currentSlide.getSlideDefaults().getDefaultColors().setFill(fill); }

				if(font != null) { this.currentSlide.getSlideDefaults().getDefaultStyle().setFontFamily(font); }
				if(textSize != null) { this.currentSlide.getSlideDefaults().getDefaultStyle().setSize(Integer.parseInt(textSize)); }
				if(italic != null) { this.currentSlide.getSlideDefaults().getDefaultStyle().setItalic(Boolean.parseBoolean(italic)); }
				if(bold != null) { this.currentSlide.getSlideDefaults().getDefaultStyle().setBold(Boolean.parseBoolean(bold)); }
				if(underline != null) { this.currentSlide.getSlideDefaults().getDefaultStyle().setUnderlined(Boolean.parseBoolean(underline)); }

			}
				break;
			// TODO: when a new text is created without a textsize being specified, it takes on the size of the last created piece of text
			case "Text": {
                this.inText = true;
				this.currentText = new FLText(getAttributeString(attrs, "id"),
											  new Position(getAttributeDouble(attrs, "x"), getAttributeDouble(attrs,"y")),
                                        	  (getAttributeDouble(attrs, "x2") - getAttributeDouble(attrs, "x")),
											  this.currentSlide.getSlideDefaults(),
                                       	 	  new Transitions("trigger", 0, 0));

                String color = getAttributeString(attrs, "color");
                String font = getAttributeString(attrs, "font");
                String textSize = getAttributeString(attrs, "textsize");
                String italic = getAttributeString(attrs, "italic");
                String bold = getAttributeString(attrs, "bold");
                String underline = getAttributeString(attrs, "underline");

                if(color != null) { currentText.setColor(new Colors(color)); }
                if(font != null) { currentText.getStyle().setFontFamily(font); }
                if(textSize != null) { currentText.getStyle().setSize(Integer.parseInt(textSize)); }
                if(italic != null) { currentText.getStyle().setItalic(Boolean.parseBoolean(italic)); }
                if(bold != null) { currentText.getStyle().setBold(Boolean.parseBoolean(bold)); }
                if(underline != null) { currentText.getStyle().setUnderlined(Boolean.parseBoolean(underline)); }
            }
                break;
            case "Format": {
                this.inText = true;
                this.inFormat = true;
                String color = getAttributeString(attrs, "color");
                String font = getAttributeString(attrs, "font");
                String textSize = getAttributeString(attrs, "textsize");
                String italic = getAttributeString(attrs, "italic");
                String bold = getAttributeString(attrs, "bold");
                String underline = getAttributeString(attrs, "underline");

                this.currentColor = new Colors(currentText.getColor());
                if(color != null) { this.currentColor = new Colors(color); }

                this.currentStyle = new TextStyle(currentText.getStyle());

                if(font != null) { this.currentStyle.setFontFamily(font); }
                if(textSize != null) { this.currentStyle.setSize(Integer.parseInt(textSize)); }
                if(italic != null) { this.currentStyle.setItalic(Boolean.parseBoolean(italic)); }
                if(bold != null) { this.currentStyle.setBold(Boolean.parseBoolean(bold)); }
                if(underline != null) { this.currentStyle.setUnderlined(Boolean.parseBoolean(underline)); }

            }
                break;
            case "Image":
				currentSlide.add(new FLImage(getAttributeString(attrs, "id"),
											 getAttributeString(attrs, "path"),
                                             new Position(getAttributeDouble(attrs, "x"), getAttributeDouble(attrs, "y")),
                                             (getAttributeDouble(attrs, "x2") - getAttributeDouble(attrs, "x")),
                                             (getAttributeDouble(attrs, "y2")) - getAttributeDouble(attrs, "y")));
				break;
			case "Audio":
			    currentSlide.add(new FLAudio(getAttributeString(attrs, "id"),
                                             getAttributeString(attrs, "path"),
                                             new Position(getAttributeDouble(attrs, "x"), getAttributeDouble(attrs, "y"))));
				break;
			case "Video":	//NOTE leave until we get module
				break;
			case "Shape":	//NOTE leave until we get module
				break;
			case "Br":
				this.currentText.add("\n");
				break;
			case "Meta":
				presentation.addMeta(new Meta(getAttributeString(attrs, "key"), getAttributeString(attrs, "value")));
				break;
			case "Button":
				if(getAttributeString(attrs,"background") != null) {
					this.currentButton = new FLButton(getAttributeString(attrs, "id"),
							new Position(getAttributeDouble(attrs, "x"),
									getAttributeDouble(attrs, "y")),
									getAttributeString(attrs, "action"),
									getAttributeString(attrs, "background"));

				} else {
					this.currentButton = new FLButton(getAttributeString(attrs, "id"),
							new Position(getAttributeDouble(attrs, "x"),
									getAttributeDouble(attrs, "y")),
							getAttributeString(attrs, "action"));
				}
				switch(getAttributeString(attrs, "action")) {
					case "nextSlide":
						this.currentButton.getButton().setOnMouseClicked((clickEvent) -> {
							//this.presentation.getNextID();
							this.presentation.moveNextSlide();
						});
						break;
					case "moveQ":
						this.currentButton.getButton().setOnMouseClicked((clickEvent) -> {
							this.presentation.moveSlide("Q");
						});
						break;
					case "moveX":
						this.currentButton.getButton().setOnMouseClicked((clickEvent) -> {
							this.presentation.moveSlide("X");
						});
						break;
					case "moveS":
						this.currentButton.getButton().setOnMouseClicked((clickEvent) -> {
							this.presentation.moveSlide("S");
						});
						break;
					case "correctAnswer": {
                        String currentSlideID = this.currentSlide.getId();
                        this.currentButton.getButton().setOnMouseClicked((clickEvent) -> {
                            this.presentation.playAudio(currentSlideID, "Memes");
                        });
                    }
						break;
					case "wrongAnswer": {
                        String currentSlideID = this.currentSlide.getId();
                        this.currentButton.getButton().setOnMouseClicked((clickEvent) -> {
                            this.presentation.playAudio(currentSlideID, "Dabs");
                        });
                    }
						break;
				}
				this.inButton = true;
            default:
                break;
		}
	}

    @Override
	public void characters(char ch[], int start, int length) throws SAXException {
        String textString = new String(ch, start, length);

        if(this.inText) {
            if(this.inFormat) {
                this.currentText.add(textString, this.currentColor, this.currentStyle);
            } else {
                this.currentText.add(textString.trim());
            }
        }
        if(this.inButton) {
        	this.currentButton.addText(textString.trim());
		}
	}

    @Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		String elementName = localName;
		if("".equals(elementName)) {
			elementName = qName;
		}

		switch (elementName) {
			case "Presentation":
				this.presentation.renderSlide();
				break;
			case "Text":
				this.currentSlide.add(this.currentText);
				this.inText = false;
				break;
			case "Slide":
				presentation.addSlide(currentSlide);
				break;
			case "Format":
				this.inFormat = false;
				break;
			case "Button":
				this.currentSlide.add(this.currentButton);
				this.inButton = false;
				break;
			default:
				break;
		}
	}

    @Override
	public void endDocument() throws SAXException { System.out.println("\nFinished parsing."); }

	private String getAttributeString(Attributes attrs, String qName) { return attrs.getValue(qName); }
	private double getAttributeDouble(Attributes attrs, String qName) { return Double.parseDouble(attrs.getValue(qName)); }
	private int getAttributeInteger(Attributes attrs, String qName) { return Integer.parseInt(attrs.getValue(qName)); }
	private boolean getAttributeBoolean(Attributes attrs, String qName) { return Boolean.parseBoolean(attrs.getValue(qName)); }

	public Presentation getPresentation() { return presentation; }

	public String trimLeading(String string) { return string.replaceAll("^\\s+", ""); }
}
