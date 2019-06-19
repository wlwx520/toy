package com.track.toy.test.core.factory;

import com.track.toy.graph.Graph;
import com.track.toy.helper.FileHelper;
import com.track.toy.helper.XmlHelper;
import com.track.toy.test.core.Constant;
import com.track.toy.test.core.common.TestGraph;
import com.track.toy.test.core.common.TestGraphBuilder;
import com.track.toy.test.core.node.HttpTestNode;
import com.track.toy.test.core.node.TestNode;
import com.track.toy.test.core.prepare.PrepareType;
import org.dom4j.Element;

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
        String expressedPath = Constant.express(path);
        String expressedDataFolder = Constant.express(dataFolder);

        TestGraph testGraphTemplate = new TestGraph(expressedPath, expressedDataFolder);
        Graph<TestNode, Double, String, String> templateGraph = new Graph<>(new TestGraphBuilder());
        testGraphTemplate.loadData(templateGraph);

        LoggerFactory.systemLog("load template path = {} , dataFolder = {}", expressedPath, expressedDataFolder);

        Element templateElement = XmlHelper.read(expressedPath);
        List<Element> httpNodeElementList = templateElement.elements("http-node");
        if (httpNodeElementList != null) {
            httpNodeElementList.forEach(httpNodeElement -> {
                String name = httpNodeElement.attributeValue("name");
                String url = httpNodeElement.attributeValue("url");
                String prepare = httpNodeElement.attributeValue("prepare");
                String prepareValue = httpNodeElement.attributeValue("prepareValue");
                PrepareType prepareType = prepare == null || prepare.trim().isEmpty() ? PrepareType.ALL : PrepareType.getFromName(prepare);
                HttpTestNode httpTestNode = new HttpTestNode(name, "template", testGraphTemplate, prepareType, prepareValue, null, null, url);
                templateGraph.getNodeHandler().newNode(httpTestNode);
                LoggerFactory.systemLog("add httpTestNode name = {}", httpTestNode.getName());
            });
        }

        List<Element> linkElementList = templateElement.elements("link");
        if (linkElementList != null) {
            linkElementList.forEach(linkElement -> {
                String source = linkElement.attributeValue("source");
                String target = linkElement.attributeValue("target");
                templateGraph.getEdgeHandler().newEdgeByKey(source, target);
                LoggerFactory.systemLog("add link source = {} , target = {}", source, target);
            });
        }

        this.testGraphTemplate = testGraphTemplate;
    }

    public TestGraph poll() {
        File dataFile = pollFile();
        if (dataFile == null) {
            return null;
        }

        String dataName = dataFile.getName().replaceAll(".xml", "");

        LoggerFactory.systemLog("start to test data = {}", dataFile.getName());
        TestGraph testGraphCopy = testGraphTemplate.copy();
        Graph<TestNode, Double, String, String> dataCopy = testGraphTemplate.getTempGraphData().getPlusHandler()
                .copy(testNode -> testNode.copy(testGraphCopy, dataName));
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
