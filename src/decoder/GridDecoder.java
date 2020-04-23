package decoder;

import com.google.gson.Gson;
import structure.Grid;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public abstract class GridDecoder {
    public static Grid getJsonGrid(Reader jsonReader) {
        Gson gson = new Gson();
        var jsonGrid = gson.fromJson(jsonReader, JsonGrid.class);
        return jsonGrid.getGrid();
    }

    public static Grid getJsonGridFromFile(String filepath) throws IOException {
        File file = new File(filepath);
        var myReader = new FileReader(file);
        var grid = getJsonGrid(myReader);
        myReader.close();
        return grid;
    }
}
