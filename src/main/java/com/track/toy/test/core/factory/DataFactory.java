package com.track.toy.test.core.factory;

import com.track.toy.graph.Graph;
import com.track.toy.helper.FileHelper;
import com.track.toy.test.core.node.TestNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataFactory {
    private static List<File> DATA_FILES = new ArrayList<>();
    private static Graph<TestNode, Double, String, String> GRAPH;

    public static void init(Graph<TestNode, Double, String, String> graph, String dataFolder) {
        DATA_FILES = FileHelper.listFiles(new File(dataFolder));
        GRAPH = graph;
    }

    public static Graph<TestNode, Double, String, String> poll() {
        File dataFile = pollFile();
        if (dataFile == null) {
            return null;
        }

        Graph<TestNode, Double, String, String> copy = GRAPH.getPlusHandler().copy();
        //TODO load data
        return copy;
    }

    private static File pollFile() {
        if (DATA_FILES.isEmpty()) {
            return null;
        }
        return DATA_FILES.remove(0);
    }


}
