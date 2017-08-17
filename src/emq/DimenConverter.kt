package emq

import org.dom4j.DocumentHelper
import org.dom4j.Element
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import java.io.File
import java.io.FileWriter

class DimenConverter {
    private lateinit var mRoot: Element
    private val mNodeList = java.util.ArrayList<Node>()
    private var mFactor: Float = 0.toFloat()

    constructor(mFactor: Float) {
        this.mFactor = mFactor
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            var transform = DimenConverter(java.lang.Float.parseFloat(args[2]))
            transform.readXml(args[0])
            transform.writeXml(args[1])
        }
    }

    fun readXml(path : String) {
        val reader = SAXReader()
        val document = reader.read(File(path))
        mRoot = document.rootElement
        val it = mRoot.elementIterator()
        while (it.hasNext()) {
            val element = it.next()
            val attributeList = element.attributes()

            val list = java.util.ArrayList<Array<String>>()
            for (attr in attributeList) {
                val pair = Array<String>(2, {"init"})
                pair[0] = attr.qualifiedName
                pair[1] = attr.stringValue
                list.add(pair)
            }
            val node = Node(element.name, element.stringValue, list)
            mNodeList.add(node)
        }
    }

    fun writeXml(path: String) {
        val document = DocumentHelper.createDocument()
        val root = document.addElement(mRoot.qualifiedName)

        for (node in mNodeList) {
            val element = root.addElement(node.name)
            for (list in node.attributeList) {
                element.addAttribute(list[0], list[1])
            }
            element.addText(convertValue(node.value))
        }

        val format = OutputFormat()
        format.isNewlines = true
        format.isTrimText = true
        format.isPadText = true
        format.setIndentSize(4)

        val out = FileWriter(path)
        val writer = XMLWriter(out, format)
        writer.write(document)
        writer.close()
    }

    fun convertValue(value: String): String {
        var minusPrefix : String = ""
        if (value.startsWith("-", false)) {
            minusPrefix = "-"
        }
        val digits : String = value.replace("\\D+".toRegex(), "")
        val unit = value.substring(digits.length + minusPrefix.length)
        if (digits.isEmpty()) {
            return ""
        } else {

            return minusPrefix + Math.round(java.lang.Float.parseFloat(digits) * mFactor).toString() + unit
        }
    }

    data class Node(val name : String, val value : String, val attributeList : ArrayList<Array<String>>)
}



