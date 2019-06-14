package com.track.toy.test.core.factory;

import com.track.toy.graph.Graph;
import com.track.toy.helper.FileHelper;
import com.track.toy.test.core.node.TestNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataFactory {
    private List<File> dataFiles = new ArrayList<>();
    private Graph<TestNode, Double, String, String> graph;

    public static DataFactory init(Graph<TestNode, Double, String, String> graph, String dataFolder) {
        DataFactory dataFactory = new DataFactory();
        dataFactory.dataFiles = FileHelper.listFiles(new File(dataFolder));
        dataFactory.graph = graph;
        return dataFactory;
    }

    public Graph<TestNode, Double, String, String> poll() {
        File dataFile = pollFile();
        if (dataFile == null) {
            return null;
        }

        Graph<TestNode, Double, String, String> copy = graph.getPlusHandler().copy();
        //TODO load data
        return copy;
    }

    private File pollFile() {
        if (dataFiles.isEmpty()) {
            return null;
        }
        return dataFiles.remove(0);
    }


}
