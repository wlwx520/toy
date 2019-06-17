package com.track.toy.test.core.factory;

import com.track.toy.graph.Graph;
import com.track.toy.helper.FileHelper;
import com.track.toy.test.core.common.TestGraph;
import com.track.toy.test.core.node.TestNode;

import java.io.File;
import java.util.List;

public class DataFactory {
    private String path;
    private String dataFolder;
    private TestGraph testGraphTemplate;
    private List<File> dataFiles;

    public DataFactory(String path, String dataFolder) {
        this.path = path;
        this.dataFolder = dataFolder;
        this.dataFiles = FileHelper.listFiles(new File(dataFolder));
    }

    public void loadTemplate() {
        TestGraph testGraphTemplate = new TestGraph(path, dataFolder);
        //TODO load template

        this.testGraphTemplate = testGraphTemplate;
    }

    public TestGraph poll() {
        File dataFile = pollFile();
        if (dataFile == null) {
            return null;
        }

        TestGraph testGraphCopy = testGraphTemplate.copy();
        Graph<TestNode, Double, String, String> dataCopy = testGraphTemplate.getTempGraphData().getPlusHandler()
                .copy(testNode -> testNode.copy(testGraphCopy));
        //TODO load data headNode logRoot
        testGraphCopy.loadData(dataCopy);
        return testGraphCopy;
    }

    private File pollFile() {
        if (dataFiles.isEmpty()) {
            return null;
        }
        return dataFiles.remove(0);
    }

}
