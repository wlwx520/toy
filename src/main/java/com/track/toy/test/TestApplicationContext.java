package com.track.toy.test;

import com.track.toy.helper.FileHelper;
import com.track.toy.helper.XmlHelper;
import com.track.toy.test.core.Constant;
import com.track.toy.test.core.common.TestGraph;
import com.track.toy.test.core.factory.ConfigureFactory;
import com.track.toy.test.core.factory.DataFactory;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestApplicationContext {
    private static final List<DataFactory> DATA_FACTORIES = new ArrayList<>();

    public static void main(String[] args) {
        // 读取配置
        loadConfigure();

        //初始化表达式
        Constant.initExpression();

        for (DataFactory dataFactory : DATA_FACTORIES) {
            // 生成测试图模板
            dataFactory.loadTemplate();

            a:
            while (true) {
                //每次拉取一份测试数据进行测试
                TestGraph tempTestGraph = dataFactory.poll();

                //当拉去不到数据时，结束该测试图
                if (tempTestGraph == null) {
                    break a;
                }

                //开启测试
                tempTestGraph.doTest();
            }
        }
    }


    private static void loadConfigure() {
        String path = FileHelper.getAppRoot() + "/configure/applicationContext.xml";
        try {
            Element rootElement = XmlHelper.read(path);

            loadProperties(rootElement);

            loadTestGraphPaths(rootElement);

        } catch (Exception e) {
            throw new RuntimeException("file of " + path + " not found");
        }
    }

    private static void loadTestGraphPaths(Element rootElement) {
        List<Element> testGraphElementList = rootElement.elements("test-graph");
        if (testGraphElementList == null) {
            return;
        }

        for (Element testGraphElement : testGraphElementList) {
            if (testGraphElement.attributeValue("switch") == null
                    || !testGraphElement.attributeValue("switch").equals("true")
                    || !testGraphElement.attributeValue("switch").equals("TRUE")) {
                String path = testGraphElement.attributeValue("path");
                String dataFolder = testGraphElement.attributeValue("dataFolder");
                if (path == null) {
                    log.info("attr of path not exist");
                    continue;
                }
                if (dataFolder == null) {
                    log.info("attr of dataFolder not exist");
                    continue;
                }

                path = Constant.express(path);
                dataFolder = Constant.express(dataFolder);

                if (!FileHelper.fileExist(path)) {
                    log.info("path of {} not exist", path);
                    continue;
                }
                if (!FileHelper.fileExist(dataFolder)) {
                    log.info("path of {} not exist", dataFolder);
                    continue;
                }

                DATA_FACTORIES.add(new DataFactory(path, dataFolder));
            }
        }
    }

    private static void loadProperties(Element rootElement) {
        Element propertiesElement = rootElement.element("properties");
        List<Element> propertyElementList = propertiesElement.elements("property");
        if (propertyElementList == null) {
            return;
        }
        propertyElementList.forEach(propertyElement -> {
            ConfigureFactory.load(propertyElement.attributeValue("key"), propertyElement.attributeValue("value"));
        });

    }
}
