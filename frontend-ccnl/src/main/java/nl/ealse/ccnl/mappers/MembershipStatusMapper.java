package nl.ealse.ccnl.mappers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import lombok.Getter;
import nl.ealse.ccnl.ledenadministratie.model.MembershipStatus;
import nl.ealse.javafx.mappers.PropertyMapper;

public class MembershipStatusMapper implements PropertyMapper<ChoiceBox<String>, MembershipStatus> {

  public static final String ACTIVE = "actief";
  public static final String LAST_YEAR_MEMBERSHIP = "laatste jaar";
  public static final String OVERDUE = "niet betaald";
  public static final String INACTIVE = "opgezegd";

  @Getter
  private static final ObservableList<String> statuses =
      FXCollections.observableArrayList(ACTIVE, LAST_YEAR_MEMBERSHIP, OVERDUE, INACTIVE);

  @Override
  public MembershipStatus getPropertyFromJavaFx(ChoiceBox<String> javaFx) {
    switch (javaFx.getValue()) {
      case LAST_YEAR_MEMBERSHIP:
        return MembershipStatus.LAST_YEAR_MEMBERSHIP;
      case OVERDUE:
        return MembershipStatus.OVERDUE;
      case INACTIVE:
        return MembershipStatus.INACTIVE;
      case ACTIVE:
      default:
        return MembershipStatus.ACTIVE;
    }
  }

  @Override
  public void mapPropertyToJavaFx(MembershipStatus modelProperty, ChoiceBox<String> javaFx) {
    javaFx.setItems(statuses);
    switch (modelProperty) {
      case INACTIVE:
        javaFx.setValue(INACTIVE);
        break;
      case LAST_YEAR_MEMBERSHIP:
        javaFx.setValue(LAST_YEAR_MEMBERSHIP);
        break;
      case OVERDUE:
        javaFx.setValue(OVERDUE);
        break;
      case ACTIVE:
      default:
        javaFx.setValue(ACTIVE);
        break;

    }

  }

}
