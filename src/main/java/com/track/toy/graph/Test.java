package com.track.toy.graph;

public class Test {
    private static final Graph<TestData, Double, String, String> GRAPH = new Graph<>(new IGraph<TestData, Double, String, String>() {
        @Override
        public String edgeKey(TestData source, TestData target) {
            return source.getName() + "-" + target.getName();
        }

        @Override
        public Double edgeRight(TestData source, TestData target) {
            return source.getScore().doubleValue() / target.getScore().doubleValue();
        }

        @Override
        public String nodeKey(TestData data) {
            return data.getName();
        }
    });


    public static void main(String[] args) {
        TestData a = new TestData(1, "A");
        TestData b = new TestData(2, "B");
        TestData c = new TestData(3, "C");
        TestData d = new TestData(4, "D");
        TestData e = new TestData(5, "E");

        GRAPH.getNodeHandler().newNode(a);
        GRAPH.getNodeHandler().newNode(b);
        GRAPH.getNodeHandler().newNode(c);
        GRAPH.getNodeHandler().newNode(d);
        GRAPH.getNodeHandler().newNode(e);
//        GRAPH.newNode(e);

        GRAPH.getEdgeHandler().newEdgeByKey("A","B");
        GRAPH.getEdgeHandler().newEdgeByKey("B","C");
        GRAPH.getEdgeHandler().newEdgeByKey("C","D");
        GRAPH.getEdgeHandler().newEdge(d,e);
//        GRAPH.newEdge(a,b);
//        GRAPH.newEdgeByKey("A","B");

//        GRAPH.removeEdge(a,b);
//        GRAPH.removeEdgeByKey("A","B");

//        GRAPH.removeEdgeByKey("A-C");
//        GRAPH.removeEdgeByKey("A","C");
//        GRAPH.removeEdge(a,c);

        TestData a1 = GRAPH.getNodeHandler().getNode("A");
        String nodeKey = GRAPH.getNodeHandler().getNodeKey(a);
        Double right = GRAPH.getEdgeHandler().getRight(a, b);
        Double rightByKey1 = GRAPH.getEdgeHandler().getRightByKey("A", "B");
        Double rightByKey = GRAPH.getEdgeHandler().getRightByKey("A-B");

        TestData f = GRAPH.getNodeHandler().getNode("F");
        Double rightByKey1f = GRAPH.getEdgeHandler().getRightByKey("F", "E");
        Double rightByKeyf = GRAPH.getEdgeHandler().getRightByKey("A-F");

        HierarchyNode<TestData> hierarchy = GRAPH.getHierarchy("C", -6, 6);

        hierarchy.getData().setScore(8);
        System.out.println();

    }
}
