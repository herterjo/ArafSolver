package gui;

import dataHelper.ColorHelper;
import dataHelper.Point;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import structure.Grid;

public class DrawableGrid extends Grid {
    private final Rectangle[][] rects;
    private final int timeout;
    private static final int size = 50;
    private final Stage stage;

    public DrawableGrid(int lenX, int lenY, Stage stage, int timeout) {
        super(lenX, lenY);
        this.timeout = timeout;
        rects = new Rectangle[lenY][lenX];
        this.stage = stage;
    }

    public void init() {
        var fxGroup = new Group();
        for (int y = 0; y < lenY; y++) {
            for (int x = 0; x < lenX; x++) {
                Rectangle rect = new Rectangle(0, 0, size, size);
                rect.setStroke(Color.BLACK);
                rect.setStrokeType(StrokeType.INSIDE);
                rect.setStrokeWidth(((double) size) / 20);
                rect.setFill(Color.TRANSPARENT);
                var number = getCell(x, y).getNumber();
                Text text = new Text(number == null ? "" : number.toString());
//                text.setX(size * x);
//                text.setY(size * y);
                StackPane stack = new StackPane();
                stack.setLayoutX(size * x);
                stack.setLayoutY(size * y);
                stack.getChildren().addAll(rect, text);
                fxGroup.getChildren().add(stack);
                rects[y][x] = rect;
            }
        }
        stage.setScene(new Scene(fxGroup, size * lenY, size * lenX));
    }

    @Override
    public void setCellGroup(int posX, int posY, structure.Group group) {
        super.setCellGroup(posX, posY, group);
        update(posX, posY, group.getId());
    }

    @Override
    public void setCellGroup(Point p, structure.Group group) {
        super.setCellGroup(p, group);
        update(p.getX(), p.getY(), group.getId());
    }

    @Override
    public void deleteCellFromGroup(Point p) {
        super.deleteCellFromGroup(p);
        update(p.getX(), p.getY(), null);
    }

    @Override
    public void deleteCellFromGroup(int posX, int posY) {
        super.deleteCellFromGroup(posX, posY);
        update(posX, posY, null);
    }

    private void update(int posX, int posY, Integer groupId) {
        try {
            Thread.sleep(timeout);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        new Thread(() -> {
            var rect = rects[posY][posX];
            if (groupId != null) {
                byte[] colorBytes;
                int added;
                do {
                    added = 0;
                    colorBytes = ColorHelper.intToColor(groupId);
                    for (byte colorByte : colorBytes) {
                        added += colorByte;
                    }
                }while(added < (-196*3) || added > (196*3));
                var bytesCopyBecauseOfFinal = colorBytes;
                Platform.runLater(
                        () -> rect.setFill(Color.rgb(bytesCopyBecauseOfFinal[0]
                                        + 128, bytesCopyBecauseOfFinal[1]
                                        + 128, bytesCopyBecauseOfFinal[2] + 128)
                        ));
            } else {
                Platform.runLater(() -> rect.setFill(Color.TRANSPARENT));
            }
        }).start();
    }
}
