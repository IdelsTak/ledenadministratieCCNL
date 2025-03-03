package nl.ealse.ccnl.ledenadministratie.util;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

@UtilityClass
@Slf4j
public class XmlValidator {

  public boolean validate(Resource xsd, String xml) {
    try {
      SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
      factory.setProperty("http://javax.xml.XMLConstants/property/accessExternalSchema", ""); // Compliant
      factory.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", ""); // Compliant

      Schema schema = factory.newSchema(new StreamSource(xsd.getInputStream()));
      Validator validator = schema.newValidator();
      validator.validate(new StreamSource(new StringReader(xml)));
    } catch (SAXException | IOException e) {
      log.error("Validation failed", e);
      return false;
    }
    return true;
  }

}
