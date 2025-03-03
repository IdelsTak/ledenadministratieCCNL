package nl.ealse.ccnl.service;

import java.util.List;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import nl.ealse.ccnl.ledenadministratie.model.Document;
import nl.ealse.ccnl.ledenadministratie.model.DocumentType;
import nl.ealse.ccnl.ledenadministratie.model.Member;
import nl.ealse.ccnl.ledenadministratie.pdf.MailFOGenerator;
import nl.ealse.ccnl.ledenadministratie.pdf.PDFGenerator;
import nl.ealse.ccnl.ledenadministratie.pdf.content.FOContent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

/**
  * Service for sending email on behalf of the Ledenadministratie.
 * @author ealse
 *
 */
@Service
@Slf4j
public class MailService {

  private static final String FILE_NAME = "OpzegMailLid%d.pdf";

  private final JavaMailSender emailSender;

  private final DocumentService documentService;

  private final MailFOGenerator fo = new MailFOGenerator();

  private final PDFGenerator generator = new PDFGenerator();

  @Value("${ccnl.mail.from}")
  private String from;
  
  @Value("${spring.mail.properties.mail.smtp.auth}")
  private String mailSmtpAuth;
  
  @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
  private String starttlsEnable;


  public MailService(JavaMailSender emailSender, DocumentService documentService) {
    log.info("Service created");
    this.emailSender = emailSender;
    this.documentService = documentService;
  }

  /**
   * Send an email.
   * @param to - for the email
   * @param subject - of the email
   * @param text - content of the meail
   * @return the email ad was sent
   */
  public SimpleMailMessage sendMail(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    emailSender.send(message);
    return message;
  }

  /**
   * Save the content of an email with the related member.
   * @param member - to whom the email belongs
   * @param message - the email
   */
  public void saveMail(Member member, SimpleMailMessage message) {
    final String[] tos = message.getTo();
    if (tos != null && tos.length > 0) {
      String to = tos[0];
      FOContent content = fo.generateFO(to, null, message.getSubject(), message.getText());
      byte[] pdf = generator.generateMailPDF(content);
      List<Document> letters =
          documentService.findDocuments(member, DocumentType.MEMBERSHIP_CANCELATION_MAIL);
      Document document;
      if (letters.isEmpty()) {
        document = new Document();
        document.setDocumentType(DocumentType.MEMBERSHIP_CANCELATION_MAIL);
        document.setDocumentName(String.format(FILE_NAME, member.getMemberNumber()));
        document.setDescription(DocumentType.MEMBERSHIP_CANCELATION_MAIL.getDescription());
        document.setOwner(member);
      } else {
        document = letters.get(0);
      }
      document.setPdf(pdf);
      documentService.saveDocument(document);

    }
  }
  
  /**
   * Spring default behavior is to read spring.mail.properties from an the application.properties file.
   * In this application however these properties are defined in the database 
   * and loaded via the Spring environment.
   * So we need to explicitly add them to the configuration.
   */
  @PostConstruct
  private void initializeStarttls() {
    if (Boolean.parseBoolean(starttlsEnable)) {
      JavaMailSenderImpl impl = (JavaMailSenderImpl) emailSender;
      impl.getJavaMailProperties().put("mail.smtp.auth", mailSmtpAuth);
      impl.getJavaMailProperties().put("mail.smtp.starttls.enable", starttlsEnable);
    }
  }


}
