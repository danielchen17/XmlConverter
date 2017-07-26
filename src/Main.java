import com.sun.deploy.util.StringUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Main {
    private Element mRoot;
    private ArrayList<Node> nodeArrayList = new ArrayList<>();

    public static void main(String args[]) {
        Main main = new Main();
        main.readXml(args[0]);
        main.writeXml(args[1]);
    }

    private void readFile(String path) {
        File file = new File(path);
        if (file.exists()) {

        }
    }

    public class Node {
        String tag;
        String value;
        ArrayList<String[]> attributeList;

        public Node(String tag, String value, ArrayList<String[]> attributeList) {
            this.tag = tag;
            this.value = value;
            this.attributeList = attributeList;
        }

    }

    private void readXml(String path) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new File(path));

            mRoot = document.getRootElement();
            Iterator<Element> it = mRoot.elementIterator();

            while (it.hasNext()) {
                Element element = it.next();
                List<Attribute> attributeList = element.attributes();

                ArrayList<String[]> list = new ArrayList<>();
                for (Attribute attr : attributeList) {
                    System.out.println(attr.getQualifiedName()  + "," + attr.getValue());
                    String[] attributePair = new String[2];
                    attributePair[0] = attr.getQualifiedName();
                    attributePair[1] = attr.getStringValue();
                    list.add(attributePair);
                }
                Node node = new Node(element.getName(), element.getStringValue(), list);
                nodeArrayList.add(node);
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void writeXml(String path) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(mRoot.getQualifiedName());

        for(Node node : nodeArrayList) {
            Element element =  root.addElement(node.tag);

            for (String[] list : node.attributeList) {
                element.addAttribute(list[0], list[1]);
            }
            element.addText(convertValue(node.value));
        }


        try {
            FileWriter out = new FileWriter(path);
            XMLWriter writer = new XMLWriter(out);
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float mFactor = 1.15f;
    private String convertValue(String value) {
        value = value.replaceAll("\\D+","");
        if (value.isEmpty()) {
            return "";
        }  else {
            return String.valueOf(Math.round(Float.parseFloat(value) * mFactor)) + "dp";
        }
    }
}
