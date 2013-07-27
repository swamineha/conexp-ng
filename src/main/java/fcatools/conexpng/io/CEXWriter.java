package fcatools.conexpng.io;

import de.tudresden.inf.tcs.fcaapi.FCAImplication;
import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.Conf;
import fcatools.conexpng.model.AssociationRule;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;

public class CEXWriter {

    private Conf state;

    private XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private HashMap<String, String> ids = new HashMap<>();

    public CEXWriter(Conf state) throws XMLStreamException, FileNotFoundException {
        this.state = state;
        XMLEventWriter writer = null;
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

        writer = outputFactory.createXMLEventWriter(new FileOutputStream(state.filePath));

        writer.add(eventFactory.createStartDocument());
        writer.add(eventFactory.createStartElement("", "", "ConceptualSystem"));
        writer.add(eventFactory.createStartElement("", "", "Version"));
        // to show that this is our cex-format (2.0)
        writer.add(eventFactory.createAttribute("MajorNumber", "2"));
        writer.add(eventFactory.createAttribute("MinorNumber", "0"));
        writer.add(eventFactory.createEndElement("", "", "Version"));
        addContext(writer);
        addAssociations(writer);
        addImplications(writer);
        addGUIState(writer);
        writer.add(eventFactory.createEndElement("", "", "ConceptualSystem"));
        writer.add(eventFactory.createEndDocument());
        writer.close();
    }

    private void addImplications(XMLEventWriter writer) throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", "Implications"));
        for (FCAImplication<String> i : state.implications) {
            writer.add(eventFactory.createStartElement("", "", "Implication"));
            writer.add(eventFactory.createStartElement("", "", "Premise"));
            for (String premise : i.getPremise()) {
                writer.add(eventFactory.createStartElement("", "", "HasAttribute"));
                writer.add(eventFactory.createAttribute("AttributeIdentifier", "" + ids.get(premise)));
                writer.add(eventFactory.createEndElement("", "", "HasAttribute"));
            }
            writer.add(eventFactory.createEndElement("", "", "Premise"));
            writer.add(eventFactory.createStartElement("", "", "Conclusion"));
            for (String conc : i.getConclusion()) {
                writer.add(eventFactory.createStartElement("", "", "HasAttribute"));
                writer.add(eventFactory.createAttribute("AttributeIdentifier", "" + ids.get(conc)));
                writer.add(eventFactory.createEndElement("", "", "HasAttribute"));
            }
            writer.add(eventFactory.createEndElement("", "", "Conclusion"));
            writer.add(eventFactory.createEndElement("", "", "Implication"));
        }
        writer.add(eventFactory.createEndElement("", "", "Implications"));
    }

    private void addAssociations(XMLEventWriter writer) throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", "Associations"));
        for (AssociationRule a : state.associations) {
            writer.add(eventFactory.createStartElement("", "", "Association"));
            writer.add(eventFactory.createAttribute("Support", "" + a.getSupport()));
            writer.add(eventFactory.createAttribute("Confidence", "" + a.getConfidence()));

            writer.add(eventFactory.createStartElement("", "", "Premise"));
            for (String premise : a.getPremise()) {
                writer.add(eventFactory.createStartElement("", "", "HasAttribute"));
                writer.add(eventFactory.createAttribute("AttributeIdentifier", "" + ids.get(premise)));
                writer.add(eventFactory.createEndElement("", "", "HasAttribute"));
            }
            writer.add(eventFactory.createEndElement("", "", "Premise"));

            writer.add(eventFactory.createStartElement("", "", "Consequent"));
            for (String cons : a.getConsequent()) {
                writer.add(eventFactory.createStartElement("", "", "HasAttribute"));
                writer.add(eventFactory.createAttribute("AttributeIdentifier", "" + ids.get(cons)));
                writer.add(eventFactory.createEndElement("", "", "HasAttribute"));
            }
            writer.add(eventFactory.createEndElement("", "", "Consequent"));
            writer.add(eventFactory.createEndElement("", "", "Association"));
        }
        writer.add(eventFactory.createEndElement("", "", "Associations"));
    }

    private void addGUIState(XMLEventWriter writer) throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", "Settings"));

        for (Field f : state.guiConf.getClass().getDeclaredFields()) {
            if (f.getName().equals("columnWidths")) {
                addColumnWidths(writer);
                continue;
            }
            try {
                writer.add(eventFactory.createStartElement("", "", f.getName()));
                writer.add(eventFactory.createCharacters("" + f.get(state.guiConf)));
                writer.add(eventFactory.createEndElement("", "", f.getName()));

            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        writer.add(eventFactory.createEndElement("", "", "Settings"));
    }

    private void addColumnWidths(XMLEventWriter writer) throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", "ColumnWidths"));

        for (int column : state.guiConf.columnWidths.keySet()) {
            writer.add(eventFactory.createStartElement("", "", "Column"));
            writer.add(eventFactory.createAttribute("Number", "" + column));
            writer.add(eventFactory.createStartElement("", "", "Width"));

            writer.add(eventFactory.createCharacters("" + state.guiConf.columnWidths.get(column)));

            writer.add(eventFactory.createEndElement("", "", "Width"));
            writer.add(eventFactory.createEndElement("", "", "Column"));
        }
        writer.add(eventFactory.createEndElement("", "", "ColumnWidths"));

    }

    private void addContext(XMLEventWriter writer) throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", "Contexts"));
        writer.add(eventFactory.createStartElement("", "", "Context"));
        writer.add(eventFactory.createAttribute("Type", "Binary"));
        writer.add(eventFactory.createAttribute("Identifier", "0"));

        addAttributes(writer);

        addObjects(writer);

        writer.add(eventFactory.createEndElement("", "", "Context"));
        writer.add(eventFactory.createEndElement("", "", "Contexts"));

    }

    private void addAttributes(XMLEventWriter writer) throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", "Attributes"));

        for (int i = 0; i < state.context.getAttributeCount(); i++) {
            writer.add(eventFactory.createStartElement("", "", "Attribute"));
            writer.add(eventFactory.createAttribute("Identifier", "" + i));
            writer.add(eventFactory.createStartElement("", "", "Name"));

            ids.put(state.context.getAttributeAtIndex(i), "" + i);
            writer.add(eventFactory.createCharacters(state.context.getAttributeAtIndex(i)));

            writer.add(eventFactory.createEndElement("", "", "Name"));
            writer.add(eventFactory.createEndElement("", "", "Attribute"));
        }
        writer.add(eventFactory.createEndElement("", "", "Attributes"));

    }

    private void addObjects(XMLEventWriter writer) throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", "Objects"));

        for (FullObject<String, String> obj : state.context.getObjects()) {

            writer.add(eventFactory.createStartElement("", "", "Object"));
            writer.add(eventFactory.createStartElement("", "", "Name"));
            writer.add(eventFactory.createCharacters(obj.getIdentifier()));
            writer.add(eventFactory.createEndElement("", "", "Name"));
            writer.add(eventFactory.createStartElement("", "", "Intent"));
            for (String attr : state.context.getAttributesForObject(obj.getIdentifier())) {
                writer.add(eventFactory.createStartElement("", "", "HasAttribute"));
                writer.add(eventFactory.createAttribute("AttributeIdentifier", "" + ids.get(attr)));
                writer.add(eventFactory.createEndElement("", "", "HasAttribute"));
            }
            writer.add(eventFactory.createEndElement("", "", "Intent"));
            writer.add(eventFactory.createEndElement("", "", "Object"));
        }
        writer.add(eventFactory.createEndElement("", "", "Objects"));

    }

}