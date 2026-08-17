/*
====================================================================
QUESTION: BRIDGE + ADAPTER
====================================================================

An information extraction company provides a service that processes
JSON data.

The existing system works with JSON through the following interface:

    interface InformationExtractor {
        String extract(String json);
    }

However, a new external system provides information in XML format.
An existing XML parser is already available and cannot be modified.

The company also wants to support different extraction modes:

    - Basic Extraction
    - Detailed Extraction
    - Summary Extraction

The extraction mode and the input format should be independently
extensible.

In the future:
    - New extraction modes may be added.
    - New input formats may be added.

The existing XML parser should be reused without modifying it.

Requirements:

1. Use an Adapter to make the existing XML parser compatible with
   the new information extraction system.

2. Use a Bridge so that:
       Extraction Mode
              and
       Input Format
   can vary independently.

3. Demonstrate:
       XML + Basic Extraction
       XML + Detailed Extraction
       JSON + Summary Extraction


====================================================================
PATTERNS:

    ADAPTER
    -------
    ExistingXMLParser -> XMLAdapter -> DataFormat

    BRIDGE
    ------
    ExtractionService
            |
            | HAS-A
            v
       DataFormat

    Different extraction modes can therefore use different
    DataFormats independently.


====================================================================
*/


// ================================================================
// 1. BRIDGE IMPLEMENTATION INTERFACE
// ================================================================
//
// DataFormat is the IMPLEMENTATION side of the Bridge.
//
// Different input formats implement this interface.
//
// Examples:
//
//      JSONFormat
//      XMLAdapter
//      Future CSVFormat
//      Future YAMLFormat
//
// ================================================================

interface DataFormat {

    String convert(String data);
}


// ================================================================
// 2. EXISTING XML PARSER
// ================================================================
//
// This is an OLD / EXTERNAL class.
//
// Assume that this class already exists.
//
// IMPORTANT:
// We CANNOT modify this class.
//
// Its interface does not match DataFormat.
//
// It has:
//
//      parseXML()
//
// while our system expects:
//
//      convert()
//
// Therefore we need an ADAPTER.
// ================================================================

class ExistingXMLParser {

    public String parseXML(String xml) {

        System.out.println(
                "Existing XML Parser parsing XML..."
        );

        // Assume the parser converts XML into JSON.
        return "{\"name\":\"John\", \"age\":25}";
    }
}


// ================================================================
// 3. ADAPTER
// ================================================================
//
// XMLAdapter makes ExistingXMLParser compatible with DataFormat.
//
//
//       ExistingXMLParser
//              |
//              | parseXML()
//              v
//         XMLAdapter
//              |
//              | implements
//              v
//         DataFormat
//
//
// The existing parser is NOT modified.
// ================================================================

class XMLAdapter implements DataFormat {

    private ExistingXMLParser parser;


    public XMLAdapter(ExistingXMLParser parser) {

        this.parser = parser;
    }


    @Override
    public String convert(String xml) {

        // Adapter translates the expected method
        // into the method understood by the old parser.

        return parser.parseXML(xml);
    }
}


// ================================================================
// 4. JSON IMPLEMENTATION
// ================================================================
//
// JSON already follows DataFormat.
//
// Therefore JSON does NOT require an Adapter.
// ================================================================

class JSONFormat implements DataFormat {

    @Override
    public String convert(String json) {

        System.out.println(
                "Processing JSON directly..."
        );

        return json;
    }
}


// ================================================================
// 5. BRIDGE ABSTRACTION
// ================================================================
//
// THIS is the important correction.
//
// ExtractionService is the ABSTRACTION side of Bridge.
//
// It HAS-A reference to the IMPLEMENTATION interface:
//
//          DataFormat
//
// This is the Bridge:
//
//      ExtractionService
//              |
//              | HAS-A
//              v
//         DataFormat
//
// Therefore Extraction modes and input formats can vary
// independently.
//
// ================================================================

interface Extraction {

    String extract(String data);
}


// ================================================================
// 6. BASE EXTRACTION SERVICE
// ================================================================
//
// This class represents the abstraction side.
//
// It contains the Bridge reference:
//
//      protected DataFormat dataFormat;
//
// The concrete extraction classes inherit this behavior.
//
// ================================================================

abstract class ExtractionService
        implements Extraction {

    // ============================================================
    // THE BRIDGE
    // ============================================================
    //
    // One interface instance is stored inside another class.
    //
    // This is exactly the relationship you were asking about.
    //
    // ============================================================

    protected DataFormat dataFormat;


    public ExtractionService(DataFormat dataFormat) {

        this.dataFormat = dataFormat;
    }
}


// ================================================================
// 7. REFINED ABSTRACTION - BASIC EXTRACTION
// ================================================================

class BasicExtraction
        extends ExtractionService {

    public BasicExtraction(DataFormat dataFormat) {

        super(dataFormat);
    }


    @Override
    public String extract(String data) {

        // Use the Bridge to convert the input.
        String json =
                dataFormat.convert(data);


        return "Basic Extraction from: "
                + json;
    }
}


// ================================================================
// 8. REFINED ABSTRACTION - DETAILED EXTRACTION
// ================================================================

class DetailedExtraction
        extends ExtractionService {

    public DetailedExtraction(DataFormat dataFormat) {

        super(dataFormat);
    }


    @Override
    public String extract(String data) {

        // The format is provided through the Bridge.
        String json =
                dataFormat.convert(data);


        return "Detailed Extraction from: "
                + json;
    }
}


// ================================================================
// 9. REFINED ABSTRACTION - SUMMARY EXTRACTION
// ================================================================

class SummaryExtraction
        extends ExtractionService {

    public SummaryExtraction(DataFormat dataFormat) {

        super(dataFormat);
    }


    @Override
    public String extract(String data) {

        // Again, delegate format handling to DataFormat.
        String json =
                dataFormat.convert(data);


        return "Summary Extraction from: "
                + json;
    }
}


// ================================================================
// 10. CLIENT
// ================================================================

public class Bridge_Adapter {

    public static void main(String[] args) {


        // ========================================================
        // ADAPTER
        // ========================================================
        //
        // Create the existing XML parser.
        // ========================================================

        ExistingXMLParser existingParser =
                new ExistingXMLParser();


        // ========================================================
        // Wrap it with Adapter.
        //
        // XMLAdapter is now a DataFormat.
        // ========================================================

        DataFormat xml =
                new XMLAdapter(existingParser);


        // ========================================================
        // JSON implementation.
        //
        // JSON already follows DataFormat,
        // so no Adapter is required.
        // ========================================================

        DataFormat json =
                new JSONFormat();


        // ========================================================
        // BRIDGE
        // ========================================================
        //
        // XML + Basic Extraction
        //
        // BasicExtraction HAS-A DataFormat.
        //
        // DataFormat happens to be XMLAdapter.
        // ========================================================

        Extraction basicXML =
                new BasicExtraction(xml);


        System.out.println(
                basicXML.extract(
                        "<person>" +
                        "<name>John</name>" +
                        "</person>"
                )
        );


        // ========================================================
        // XML + Detailed Extraction
        //
        // Same XML implementation.
        // Different extraction abstraction.
        // ========================================================

        Extraction detailedXML =
                new DetailedExtraction(xml);


        System.out.println(
                detailedXML.extract(
                        "<person>" +
                        "<name>John</name>" +
                        "</person>"
                )
        );


        // ========================================================
        // JSON + Summary Extraction
        //
        // Different DataFormat.
        // Different Extraction Mode.
        //
        // Neither class needs to be modified.
        // ========================================================

        Extraction summaryJSON =
                new SummaryExtraction(json);


        System.out.println(
                summaryJSON.extract(
                        "{\"name\":\"John\"}"
                )
        );
    }
}