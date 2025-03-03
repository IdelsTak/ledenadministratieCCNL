package nl.ealse.ccnl.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public abstract class CompositeValidator implements CallbackLauncher {

  protected final List<AbstractValidator> validators = new ArrayList<>();

  private Consumer<Boolean> vc;

  public boolean isValid(boolean validate) {
    boolean valid = true;
    for (AbstractValidator v : validators) {
      if (validate && !v.isValid()) {
        v.validate();
      }
      valid = valid && v.isValid();
    }
    return valid;
  }

  public void required(TextField requiredField, Label errorMessagelabel) {
    RequiredValidator rv = new RequiredValidator(requiredField, errorMessagelabel);
    addValidator(requiredField, rv);
  }

  public void addValidator(Control targetField, AbstractValidator validator) {
    validators.add(validator);
    validator.setCallbackLauncher(this);
    targetField.focusedProperty().addListener(new FocusListener(validator));
  }

  public void setCallback(Consumer<Boolean> vc) {
    this.vc = vc;
  }

  /**
   * Call this method on a page switch. It will invoke all validators with a 'invalid' state.
   */
  public void validate() {
    if (vc != null) {
      vc.accept(isValid(true));
      resetErrorMessages();
    }
  }

  /**
   * This method is called by a validator. It will report the overall state of all validators.
   */
  @Override
  public void fireCallback() {
    if (vc != null) {
      vc.accept(isValid(false));
    }
  }

  public void initialize() {
    validators.forEach(AbstractValidator::initialize);
  }



  /**
   * Handle to avoid showing too much messages.
   */
  protected abstract void resetErrorMessages();

}
