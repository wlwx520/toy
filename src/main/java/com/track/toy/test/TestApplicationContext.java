package com.track.toy.test;

import com.track.toy.graph.Graph;
import com.track.toy.helper.FileHelper;
import com.track.toy.helper.XmlHelper;
import com.track.toy.test.core.node.TestNode;
import org.dom4j.Element;

public class TestApplicationContext {
    public static void main(String[] args) {
        //TODO 读取配置
        loadConfigure();

        //TODO 生成测试图
        Graph<TestNode, Double, String, String> graph = null;

        //TODO 给测试图灌入数据

        //TODO 分批执行

    }

    private static void loadConfigure() {
        String path = FileHelper.getAppRoot() + "/configure/applicationContext.xml";
        try {
            Element element = XmlHelper.read(path);
        } catch (Exception e) {
            throw new RuntimeException("file of " + path + " not found");
        }
    }
}
