//package Practice.Structural.23.C1;
interface DataProcessor
{
    String process(String data);
}

class XmlParser
{
    public String parse(String xml)
    {
        return "{\"name\":\"John\", \"age\":25}";
    }
}

class XmlToJsonAdapter implements DataProcessor
{
    private XmlParser xmlParser;

    public XmlToJsonAdapter(XmlParser xmlParser)
    {
        this.xmlParser = xmlParser;
    }

    @Override
    public String process(String data)
    {
        return "Coverted to json: " + xmlParser.parse(data);
    }
}

public class C1 {
    
}
