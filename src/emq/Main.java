package emq;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
    private Element mRoot;
    private ArrayList<Node> mNodeList = new ArrayList<>();
    private float mFactor;

    public static void main(String args[]) {
        Float.parseFloat(args[0]);
        Main main = new Main();
        main.mFactor = 1.15f;
        main.readXml("raw/dimens.xml");
        main.writeXml("raw/test1.xml");
    }

    public class Node {
        String mName;
        String mValue;
        ArrayList<String[]> mAttributeList;

        public Node(String name, String value, ArrayList<String[]> attributeList) {
            mName = name;
            mValue = value;
            mAttributeList = attributeList;
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
                    String[] attributePair = new String[2];
                    attributePair[0] = attr.getQualifiedName();
                    attributePair[1] = attr.getStringValue();
                    list.add(attributePair);
                }
                Node node = new Node(element.getName(), element.getStringValue(), list);
                mNodeList.add(node);
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void writeXml(String path) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(mRoot.getQualifiedName());

        for(Node node : mNodeList) {
            Element element =  root.addElement(node.mName);
            for (String[] list : node.mAttributeList) {
                element.addAttribute(list[0], list[1]);
            }
            element.addText(convertValue(node.mValue));
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

    private String convertValue(String value) {
        String digits;
        digits = value.replaceAll("\\D+","");
        String unit = value.substring(digits.length());
        if (digits.isEmpty()) {
            return "";
        }  else {
            return String.valueOf(Math.round(Float.parseFloat(digits) * mFactor)) + unit;
        }
    }
}
