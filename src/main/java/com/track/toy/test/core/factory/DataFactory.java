package com.track.toy.test.core.factory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.track.toy.graph.Graph;
import com.track.toy.helper.FileHelper;
import com.track.toy.helper.XmlHelper;
import com.track.toy.test.core.Constant;
import com.track.toy.test.core.asserts.GroupTestAssert;
import com.track.toy.test.core.asserts.TestAssert;
import com.track.toy.test.core.asserts.TestAssertType;
import com.track.toy.test.core.common.TestGraph;
import com.track.toy.test.core.common.TestGraphBuilder;
import com.track.toy.test.core.node.HeadNode;
import com.track.toy.test.core.node.HttpTestNode;
import com.track.toy.test.core.node.TailNode;
import com.track.toy.test.core.node.TestNode;
import com.track.toy.test.core.prepare.PrepareType;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataFactory {
    private String path;
    private String dataFolder;
    private TestGraph testGraphTemplate;
    private List<File> dataFiles;

    public DataFactory(String path, String dataFolder) {
        this.path = path;
        this.dataFolder = dataFolder;
        List<File> files = FileHelper.listFiles(new File(dataFolder));
        this.dataFiles = new ArrayList<>();
        this.dataFiles.addAll(files);
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
        templateGraph.getNodeHandler().newNode(new HeadNode(testGraphTemplate, "headNode"));
        templateGraph.getNodeHandler().newNode(new TailNode(testGraphTemplate, "tailNode"));
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

        Element dataElement = XmlHelper.read(dataFile);

        List<Element> nodeElementList = dataElement.elements("node");
        if (nodeElementList != null) {
            nodeElementList.forEach(nodeElement -> {
                Element inputElement = nodeElement.element("input");
                Element assertElement = nodeElement.element("assert");

                JSONObject inputJson = new JSONObject();
                toJsonObject(inputJson, inputElement);

                GroupTestAssert groupTestAssert = new GroupTestAssert();
                toAssertGroup(groupTestAssert, assertElement);

                String name = nodeElement.attributeValue("name");
                TestNode testNode = dataCopy.getNodeHandler().getNode(name);
                testNode.setInput(inputJson);
                testNode.setGroupTestAssert(groupTestAssert);
            });
        }

        testGraphCopy.loadData(dataCopy);
        return testGraphCopy;
    }

    public void shuntDown(){
        testGraphTemplate.shuntDown();
    }

    private void toAssertGroup(GroupTestAssert groupTestAssert, Element assertElement) {
        String type = assertElement.attributeValue("type");
        TestAssertType fromType = TestAssertType.getFromType(type);

        if (!(fromType.equals(TestAssertType.AND) || fromType.equals(TestAssertType.OR))) {
            throw new RuntimeException("assert type is not AND or OR , cannot to GroupTestAssert");
        }

        if (fromType.equals(TestAssertType.AND)) {
            groupTestAssert.setAnd(true);
        }

        if (fromType.equals(TestAssertType.OR)) {
            groupTestAssert.setAnd(false);
        }

        List<Element> subAssertElementList = assertElement.elements("assert");
        if (subAssertElementList != null) {
            subAssertElementList.forEach(subAssertElement -> {
                String subType = subAssertElement.attributeValue("type");
                TestAssertType subFromType = TestAssertType.getFromType(subType);

                if (subFromType.equals(TestAssertType.AND) || subFromType.equals(TestAssertType.OR)) {
                    GroupTestAssert subGroupTestAssert = new GroupTestAssert();
                    toAssertGroup(subGroupTestAssert, subAssertElement);
                    groupTestAssert.addChild(subGroupTestAssert);
                } else {
                    TestAssert subTestAssert = new TestAssert();
                    toAssert(subTestAssert, subAssertElement);
                    groupTestAssert.addChild(subTestAssert);
                }
            });
        }
    }

    private void toAssert(TestAssert testAssert, Element assertElement) {
        String type = assertElement.attributeValue("type");
        TestAssertType fromType = TestAssertType.getFromType(type);

        if (fromType.equals(TestAssertType.AND) || fromType.equals(TestAssertType.OR)) {
            throw new RuntimeException("assert type is AND or OR , cannot to TestAssert");
        }

        String source = assertElement.attributeValue("source");
        String target = assertElement.attributeValue("target");
        testAssert.setSource(source);
        testAssert.setTarget(target);
        testAssert.setType(fromType);
    }

    private void toJsonObject(JSONObject json, Element element) {
        String key = element.attributeValue("key");
        String value = element.attributeValue("value");
        if (key == null) {
            return;
        }

        if (value != null) {
            json.put(key, value);
            return;
        }

        List<Element> objectElementList = element.elements("object");
        if (objectElementList != null) {
            JSONObject sub = new JSONObject();
            objectElementList.forEach(objectElement -> {
                toJsonObject(sub, objectElement);
            });
            json.put(key, sub);
        }

        List<Element> arrayElementList = element.elements("array");
        if (objectElementList != null) {
            JSONArray array = new JSONArray();
            arrayElementList.forEach(objectElement -> {
                toJsonArray(array, objectElement);
            });
            json.put(key, array);
        }
    }

    private void toJsonArray(JSONArray array, Element element) {
        List<Element> itemElementList = element.elements("item");
        if (itemElementList != null) {
            itemElementList.forEach(itemElement -> {
                String value = itemElement.attributeValue("value");
                if (value != null) {
                    array.add(value);
                    return;
                }

                Element objectElement = element.element("object");
                if (objectElement != null) {
                    JSONObject sub = new JSONObject();
                    toJsonObject(sub, objectElement);
                    array.add(sub);
                    return;
                }

                Element arrayElement = element.element("array");
                if (arrayElement != null) {
                    JSONArray subArray = new JSONArray();
                    toJsonArray(subArray, arrayElement);
                    array.add(subArray);
                    return;
                }
            });
        }
    }

    private File pollFile() {
        if (dataFiles.isEmpty()) {
            return null;
        }
        return dataFiles.remove(0);
    }

}
