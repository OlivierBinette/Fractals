package vue;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import modele.utils.Function;

public class Overlay extends BorderPane {

	public static final double	OPEN_OPACITY	= 0.35;

	private DoubleProperty		backgroundOpacityProperty;
	private Label				label;

	private TextField			textField;
	private Function			onReturnKey		= () -> {};

	public Overlay()
	{
		super();

		this.backgroundOpacityProperty = new SimpleDoubleProperty(0.0);

		this.textField = new TextField("");
		this.textField.setFocusTraversable(true);
		this.getChildren().add(textField);

		this.label = new Label();
		this.label.setStyle("-fx-text-fill : white; -fx-font-family : Helvetica; -fx-font-size : 24;");
		this.label.textProperty().bind(textField.textProperty());

		this.setCenter(this.label);

		initListeners();
		setBackgroundOpacity(0.0);
	}

	private void initListeners()
	{
		this.backgroundOpacityProperty.addListener((a, b, newValue) -> {
			setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, newValue.doubleValue()), null, null)));
		});

		this.textField.setOnKeyPressed((e) -> {
			if (e.getCode() == KeyCode.ENTER)
			{
				this.onReturnKey.execute();
				this.textField.clear();
			}
		});

		this.label.textProperty().addListener((a, b, newValue) ->
		{
			if (newValue.length() == 0)
			{

				setBackgroundOpacity(0);
			}
			else
			{
				setBackgroundOpacity(OPEN_OPACITY);
			}
		});
	}

	public void setBackgroundOpacity(final double val)
	{
		Timeline animation = new Timeline();
		animation.getKeyFrames().addAll(
			new KeyFrame(Duration.millis(100),
				new KeyValue(this.backgroundOpacityProperty, val),
				new KeyValue(this.label.opacityProperty(), val != 0 ? 1 : 0)
			)
			);
		animation.play();
	}

	public void setOnReturnKeyPressed(Function f)
	{
		this.onReturnKey = f;
	}

	public String getText()
	{
		return this.textField.getText();
	}

}
