package gui;

import dataHelper.IllegalGridStateException;
import decoder.GridDecoder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import solver.Backtracking;

public class Main extends Application {

    private DrawableGrid grid = null;
    private Backtracking solver = null;
    private Stage stage = null;
    private boolean solved = false;

    private Boolean Step() {
        if (solved || grid == null || solver == null || stage == null) {
            return null;
        }
        try {
            solved = solver.Step();
            if (solved) {
                System.out.println("Araf puzzle was solved");
            }
        } catch (IllegalGridStateException e) {
            System.out.println("Error while trying to step: " + e);
            return null;
        }
        return solved;
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, ke ->
        {
            var code = ke.getCode();
            switch (code) {
                case F8:
                    System.exit(0);
                    ke.consume();
                    break;
                case F5:
                    Step();
                    ke.consume();
                    break;
                case F6:
                    Boolean solved = false;
                    while (solved != null && !solved) {
                        solved = Step();
                    }
                    ke.consume();
                    break;
            }
        });

        Label label = new Label("Drag a file here to initialize your Araf puzzle.");
        StackPane root = new StackPane();
        VBox dragTarget = new VBox();
        dragTarget.getChildren().addAll(label);
        dragTarget.setOnDragOver(event ->
        {
            if (event.getGestureSource() != dragTarget
                    && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        dragTarget.setOnDragDropped(event ->
        {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                var path = db.getFiles().get(0);
                try {
                    grid = GridDecoder.getJsonDrawableGridFromFile(path.getPath(), stage, 100);
                    solver = new Backtracking(grid);
                    success = true;
                } catch (Exception e) {
                    System.out.println("Error at reading file: " + e);
                }
            }
            if (success) {
                dragTarget.setVisible(false);
            }
            event.setDropCompleted(success);
            event.consume();
        });

        root.getChildren().add(dragTarget);

        Scene scene = new Scene(root, 500, 250);

        primaryStage.setTitle("Araf Solver");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
