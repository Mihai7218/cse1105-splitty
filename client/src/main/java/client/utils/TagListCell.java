package client.utils;

import client.scenes.MainCtrl;
import commons.Tag;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.ObservableMap;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;


public class TagListCell extends ListCell<Tag> {
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final ConfigInterface config;
    private final ServerUtils server;
    private Label tagColor;
    private Label tagName;
    private Button edit;
    private Button remove;
    private FlowPane details;
    private HBox hBox;

    private VBox vBox;
    private Region autogrowRight;

    private Rectangle rectangle;



    /**
     * Constructor for the TagListCell.
     */
    public TagListCell(MainCtrl mainCtrl,
                       LanguageManager languageManager,
                       ConfigInterface config,
                       ServerUtils server) {
        super();
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.languageManager = languageManager;
        this.config = config;
    }

    /**
     * Creates the graphic for the Tag.
     *
     * @param item - tag item
     */
    private void createGraphic(Tag item) {
        tagColor = new Label();
        tagName = new Label();
        edit = new Button();
        edit.setText("\uD83D\uDD89");
        edit.setOnAction(param -> {
            mainCtrl.getTagCtrl().setTag(item);
            mainCtrl.showEditTag();
            //System.out.println(item);
        });
        remove = new Button();
        remove.setText("\uD83D\uDDD1");
        remove.setId("cancel");
        remove.setOnAction(param -> {
            try {
                server.removeTag(mainCtrl.getEvent().getInviteCode(), item.getId());
            } catch (WebApplicationException e) {
                System.out.println(e);
            }
        });

        rectangle= new Rectangle(100, 20);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1);
        autogrowRight = new Region();
        details = new FlowPane(tagName, tagColor);
        vBox = new VBox(details, rectangle);
        hBox = new HBox(vBox, autogrowRight, edit, remove);
        hBox.setSpacing(5);
        vBox.setSpacing(5);
        details.setHgap(3);
        details.setOrientation(Orientation.HORIZONTAL);
        details.setPrefWidth(200);
        tagName.setStyle("-fx-font-weight: 700;");
        tagColor.setStyle("-fx-font-weight: 700;");
        HBox.setHgrow(autogrowRight, Priority.ALWAYS);
    }

    /**
     * Updates the item in the list to have the event name and the open and close buttons.
     *
     * @param item  - item in the list
     * @param empty - whether the item is empty or not
     */
    @Override
    protected void updateItem(Tag item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            createGraphic(item);
            update();
        }
    }

    /**
     * Updates the labels and sets the graphic to the HBox.
     */
    private void update() {
        try {
            tagName.setText(this.getItem().getName());
        } catch (NullPointerException e) {
            tagName.setText("<no color>");
        }

        rectangle.setFill(Paint.valueOf(this.getItem().getColor()));

        setGraphic(hBox);
    }
    /**
     * set the languageManager for testing
     * @param languageManager the languageManager to set it to
     */
    public void setLanguageManager(ObservableMap<String, Object> languageManager) {
        this.languageManager.set(languageManager);
    }

    /**
     * set the tagName for testing
     * @param tagName the tagName to set it to
     */
    public void setTagName(Label tagName) {
        this.tagName = tagName;
    }

    /**
     * set the edit for testing
     * @param edit the edit to set it to
     */
    public void setEdit(Button edit) {
        this.edit = edit;
    }

    /**
     * set the remove for testing
     * @param remove the remove to set it to
     */
    public void setRemove(Button remove) {
        this.remove = remove;
    }

    /**
     * set the details for testing
     * @param details the details to set it to
     */
    public void setDetails(FlowPane details) {
        this.details = details;
    }
    /**
     * set the hBox for testing
     * @param hBox the hBox to set it to
     */
    public void sethBox(HBox hBox) {
        this.hBox = hBox;
    }
    /**
     * set the vBox for testing
     * @param vBox the vBox to set it to
     */
    public void setvBox(VBox vBox) {
        this.vBox = vBox;
    }

    /**
     * set the autogrowRight for testing
     * @param autogrowRight the autogrowRight to set it to
     */
    public void setAutogrowRight(Region autogrowRight) {
        this.autogrowRight = autogrowRight;
    }

    /**
     * set the rectangle for testing
     * @param rectangle the rectangle to set it to
     */
    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }


}
