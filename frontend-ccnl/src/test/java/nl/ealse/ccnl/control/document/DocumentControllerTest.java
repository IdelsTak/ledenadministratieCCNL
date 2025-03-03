package nl.ealse.ccnl.control.document;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.Parent;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseEvent;
import nl.ealse.ccnl.control.menu.MenuChoice;
import nl.ealse.ccnl.control.menu.PageController;
import nl.ealse.ccnl.control.menu.PageName;
import nl.ealse.ccnl.event.MemberSeLectionEvent;
import nl.ealse.ccnl.ledenadministratie.model.Document;
import nl.ealse.ccnl.ledenadministratie.model.Member;
import nl.ealse.ccnl.service.DocumentService;
import nl.ealse.ccnl.test.FXMLBaseTest;
import nl.ealse.javafx.FXMLMissingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class DocumentControllerTest extends FXMLBaseTest<DocumentController> {

  private static PageController pageController;
  private static DocumentService documentService;
  private static MouseEvent mouseEvent;
  private static Document document;

  private DocumentController sut;

  @Test
  void testController() {
    sut = new DocumentController(pageController, documentService);
    AtomicBoolean ar = new AtomicBoolean();
    AtomicBoolean result = runFX(() -> {
      prepare();
      doTest();
      ar.set(true);
    }, ar);
    Assertions.assertTrue(result.get());
  }

  private void doTest() {
    Member m = member();
    MemberSeLectionEvent event = new MemberSeLectionEvent(sut, MenuChoice.VIEW_DOCUMENT, m);
    sut.viewDocument(event);
    verify(pageController).setActivePage(PageName.VIEW_DOCUMENTS);
    sut.selectDocument(mouseEvent);
    sut.printDocument();
    sut.deleteDocument();
    verify(pageController).showMessage("Het document is verwijderd");
    sut.closeDocument();
  }


  private void prepare() {
    try {
      getPage(sut, PageName.VIEW_DOCUMENTS);
      Parent p = getPage(sut, PageName.VIEW_DOCUMENT_SHOW);
      when(pageController.loadPage(PageName.VIEW_DOCUMENT_SHOW)).thenReturn(p);
      TableRow<Document> row = new TableRow<>();
      row.setItem(document());
      when(mouseEvent.getSource()).thenReturn(row);
    } catch (FXMLMissingException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @BeforeAll
  static void setup() {
   
    pageController = mock(PageController.class);
    documentService = mock(DocumentService.class);
    document = document();
    List<Document> documents = new ArrayList<>();
    documents.add(document);
    when(documentService.findDocuments(any(Member.class))).thenReturn(documents);
    mouseEvent = mock(MouseEvent.class);
  }

  private static Member member() {
    Member m = new Member();
    m.setMemberNumber(1234);
    m.setInitials("T.");
    m.setLastNamePrefix("de");
    m.setLastName("Tester");
    List<Document> documents = new ArrayList<>();
    documents.add(document);
    m.setDocuments(documents);
    return m;
  }

  private static Document document() {
    Document d = new Document();
    d.setDocumentName("MachtigingsformulierSEPA.pdf");
    d.setPdf(getBlob("MachtigingsformulierSEPA.pdf"));
    d.setOwner(member());
    return d;
  }

  private static byte[] getBlob(String name) {
    byte[] b = null;
    Resource r = new ClassPathResource(name);
    try (InputStream is = r.getInputStream()) {
      b = is.readAllBytes();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return b;
  }



}
