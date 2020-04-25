package decoder;

import com.google.gson.Gson;
import gui.DrawableGrid;
import javafx.stage.Stage;
import structure.Grid;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public abstract class GridDecoder {
    public static Grid getJsonGridFromFile(String filepath) throws IOException {
        File file = new File(filepath);
        var myReader = new FileReader(file);
        var grid = getJsonGrid(myReader);
        myReader.close();
        return grid;
    }

    public static Grid getJsonGrid(Reader jsonReader) {
        Gson gson = new Gson();
        var jsonGrid = gson.fromJson(jsonReader, JsonGrid.class);
        return jsonGrid.getGrid();
    }

    public static DrawableGrid getJsonDrawableGridFromFile(String filepath, Stage stage) throws IOException {
        File file = new File(filepath);
        var myReader = new FileReader(file);
        var grid = getJsonDrawableGrid(myReader, stage);
        myReader.close();
        return grid;
    }

    public static DrawableGrid getJsonDrawableGrid(Reader jsonReader, Stage stage) {
        Gson gson = new Gson();
        var jsonGrid = gson.fromJson(jsonReader, JsonGrid.class);
        var grid = jsonGrid.getDrawableGrid(stage);
        grid.init();
        return grid;
    }
}
