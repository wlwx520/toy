package com.track.toy.test;

import com.track.toy.helper.FileHelper;
import com.track.toy.helper.XmlHelper;
import com.track.toy.test.core.Constant;
import com.track.toy.test.core.common.TestGraph;
import com.track.toy.test.core.factory.ConfigureFactory;
import com.track.toy.test.core.factory.DataFactory;
import com.track.toy.test.core.factory.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestApplicationContext {
    private static final List<DataFactory> DATA_FACTORIES = new ArrayList<>();

    public static void main(String[] args) {
        //初始化表达式
        Constant.initExpression();

        // 读取配置
        loadConfigure();

        LoggerFactory.startLog();

        b:
        for (DataFactory dataFactory : DATA_FACTORIES) {
            try {
                // 生成测试图模板
                dataFactory.loadTemplate();
            } catch (Exception e) {
                LoggerFactory.systemLog("load template exception e = {}", e.getCause());
                continue b;
            }

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

            dataFactory.shuntDown();
        }

        LoggerFactory.systemLog("test end ...");

        //主线程等待所有日志线程返回
        LoggerFactory.stopLog();

        System.out.println("test end ...");

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
                    throw new RuntimeException("attr of path not exist");
                }
                if (dataFolder == null) {
                    throw new RuntimeException("attr of dataFolder not exist");
                }

                path = Constant.express(path);
                dataFolder = Constant.express(dataFolder);

                if (!FileHelper.fileExist(path)) {
                    throw new RuntimeException("path of " + path + " not exist");
                }
                if (!FileHelper.fileExist(dataFolder)) {
                    throw new RuntimeException("path of " + dataFolder + " not exist");
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
