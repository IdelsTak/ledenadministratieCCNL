package nl.ealse.ccnl.control.other;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;
import nl.ealse.ccnl.control.menu.MenuChoice;
import nl.ealse.ccnl.control.menu.PageController;
import nl.ealse.ccnl.control.menu.PageName;
import nl.ealse.ccnl.event.ExternalOtherSelectionEvent;
import nl.ealse.ccnl.ledenadministratie.model.Address;
import nl.ealse.ccnl.ledenadministratie.model.ExternalRelationOther;
import nl.ealse.ccnl.service.relation.ExternalOtherService;
import nl.ealse.ccnl.test.FXMLBaseTest;
import nl.ealse.javafx.FXMLMissingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ExternalOtherDeleteControllerTest extends FXMLBaseTest<ExternalOtherDeleteController> {

  private static PageController pageController;
  private static ExternalOtherService service;

  private ExternalOtherDeleteController sut;
  private ExternalRelationOther relation;

  @Test
  void testController() {
    sut = new ExternalOtherDeleteController(pageController, service);
    relation = externalRelationOther();
    final AtomicBoolean ar = new AtomicBoolean();
    AtomicBoolean result = runFX(() -> {
      prepare();
      doTest();
      ar.set(true);
    }, ar);
    Assertions.assertTrue(result.get());
  }

  private void doTest() {
    ExternalOtherSelectionEvent event =
        new ExternalOtherSelectionEvent(sut, MenuChoice.DELETE_EXTERNAL_RELATION, relation);
    sut.onApplicationEvent(event);

    sut.delete();
    verify(pageController).showMessage("Gegevens zijn verwijderd");
  }

  @BeforeAll
  static void setup() {
   
    pageController = mock(PageController.class);
    service = mock(ExternalOtherService.class);
  };

  private void prepare() {
    try {
      getPage(sut, PageName.EXTERNAL_RELATION_DELETE);
    } catch (FXMLMissingException e) {
      Assertions.fail(e.getMessage());
    }
  }

  private ExternalRelationOther externalRelationOther() {
    ExternalRelationOther r = new ExternalRelationOther();
    Address a = r.getAddress();
    a.setStreet("straat");
    a.setAddressNumber("99");
    a.setAddressNumberAppendix("B");
    a.setCity("Ons Dorp");
    a.setPostalCode("1234 AA");

    r.setEmail("info@club.com");
    r.setContactNamePrefix("t.n.v.");
    r.setRelationInfo("Some info");
    r.setRelationSince(LocalDate.of(1998, 12, 5));
    r.setRelationName("Een Relatie");
    r.setModificationDate(LocalDate.of(2020, 12, 5));
    r.setContactName("Pietje Puk");
    r.setTelephoneNumber("06-01234567");
    r.setRelationNumber(8201);
    return r;
  }

}
