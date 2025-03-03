package nl.ealse.ccnl.control.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.Parent;
import nl.ealse.ccnl.control.menu.MenuChoice;
import nl.ealse.ccnl.control.menu.PageController;
import nl.ealse.ccnl.control.menu.PageName;
import nl.ealse.ccnl.event.InternalRelationSelectionEvent;
import nl.ealse.ccnl.ledenadministratie.model.Address;
import nl.ealse.ccnl.ledenadministratie.model.InternalRelation;
import nl.ealse.ccnl.service.relation.InternalRelationService;
import nl.ealse.ccnl.test.FXMLBaseTest;
import nl.ealse.javafx.FXMLMissingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class InternalRelationControllerTest extends FXMLBaseTest<InternalRelationController> {

  private static PageController pageController;
  private static InternalRelationService internalRelationService;


  private InternalRelationController sut;
  private InternalRelation rel;

  @Test
  void testController() {
    sut = new InternalRelationController(pageController, internalRelationService);
    rel = internalRelation();
    final AtomicBoolean ar = new AtomicBoolean();
    AtomicBoolean result = runFX(() -> {
      prepare();
      doTest();
      ar.set(true);
    }, ar);
    Assertions.assertTrue(result.get());
  }

  private void doTest() {
    InternalRelationSelectionEvent event =
        new InternalRelationSelectionEvent(sut, MenuChoice.NEW_INTERNAL_RELATION, rel);
    sut.handleRelation(event);

    sut.save();
    verify(pageController).showMessage("Functiegegevens opgeslagen");

    sut.nextPage();
    sut.previousPage();

  }

  private void prepare() {
    try {
      getPage(sut, PageName.INTERNAL_RELATION_PERSONAL);
      Parent p = getPage(sut, PageName.INTERNAL_RELATION_ADDRESS);
      when(pageController.loadPage(PageName.INTERNAL_RELATION_ADDRESS)).thenReturn(p);
    } catch (FXMLMissingException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @BeforeAll
  static void setup() {
   
    pageController = mock(PageController.class);
    internalRelationService = mock(InternalRelationService.class);
  }

  private InternalRelation internalRelation() {
    InternalRelation r = new InternalRelation();
    Address a = r.getAddress();
    a.setStreet("straat");
    a.setAddressNumber("99");
    a.setAddressNumberAppendix("B");
    a.setCity("Ons Dorp");
    a.setPostalCode("1234 AA");

    r.setTitle("Ledenadministratie");
    r.setModificationDate(LocalDate.of(2020, 12, 5));
    r.setContactName("Pietje Puk");
    r.setTelephoneNumber("06-01234567");
    r.setRelationNumber(8506);
    return r;
  }

}
