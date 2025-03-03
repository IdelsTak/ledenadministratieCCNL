package nl.ealse.ccnl.control.settings;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.concurrent.Task;
import nl.ealse.ccnl.control.menu.PageController;
import nl.ealse.ccnl.event.MenuChoiceEvent;
import nl.ealse.ccnl.service.BackupRestoreService;
import nl.ealse.javafx.util.WrappedFileChooser;
import nl.ealse.javafx.util.WrappedFileChooser.FileExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;

@Controller
public class BackupRestoreCommand {

  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHmmss");

  private static final String FILE_NAME = "backup-%s";

  @Value("${ccnl.directory.db:c:/temp}")
  private String dbDirectory;

  private final PageController pageController;

  private final TaskExecutor executor;

  private final BackupRestoreService service;

  private WrappedFileChooser fileChooser;

  public BackupRestoreCommand(PageController pageController, BackupRestoreService service,
      TaskExecutor executor) {
    this.pageController = pageController;
    this.service = service;
    this.executor = executor;
  }

  @EventListener(condition = "#event.name('MANAGE_BACKUP_DATABASE')")
  public void backup(MenuChoiceEvent event) {
    if (fileChooser == null) {
      initialize();
    }
    String fileName = String.format(FILE_NAME, formatter.format(LocalDateTime.now()));
    fileChooser.setInitialFileName(fileName);
    File backupFile = fileChooser.showSaveDialog();
    if (backupFile != null) {
      pageController.showPermanentMessage("Backup wordt aangemaakt; even geduld a.u.b.");
      BackupTask asyncTask = new BackupTask(backupFile, service);
      asyncTask.setOnSucceeded(t -> pageController.showMessage("Backup is aangemaakt"));
      asyncTask.setOnFailed(t -> pageController.showErrorMessage("Aanmaken backup is mislukt"));
      executor.execute(asyncTask);
    }
  }

  @EventListener(condition = "#event.name('MANAGE_RESTORE_DATABASE')")
  public void restore(MenuChoiceEvent event) {
    if (fileChooser == null) {
      initialize();
    }
    fileChooser.setInitialFileName(null);
    File backupFile = fileChooser.showOpenDialog();
    if (backupFile != null) {
      pageController.showPermanentMessage("Backup wordt teruggezet; even geduld a.u.b.");
      RestoreTask asyncTask = new RestoreTask(backupFile, service);
      asyncTask.setOnSucceeded(t -> {
        if (asyncTask.getValue().booleanValue()) {
          pageController.showMessage("Backup is teruggezet");
        } else {
          pageController.showErrorMessage("Onjuist bestand; Terugzetten backup is mislukt");
        }
      });
      asyncTask.setOnFailed(t -> pageController.showErrorMessage("Terugzetten backup is mislukt"));
      executor.execute(asyncTask);
    }
  }

  private void initialize() {
    fileChooser = new WrappedFileChooser(pageController.getPrimaryStage(), FileExtension.ZIP);
    fileChooser.setInitialDirectory(new File(dbDirectory));
  }

  protected static class BackupTask extends Task<Void> {

    private final BackupRestoreService service;
    private final File backupFile;

   BackupTask(File backupFile, BackupRestoreService service) {
      this.backupFile = backupFile;
      this.service = service;
    }

    @Override
    protected Void call() throws Exception {
      service.backupDatabase(backupFile);
      return null;
   }

  }
  protected static class RestoreTask extends Task<Boolean> {

    private final BackupRestoreService service;
    private final File backupFile;

    RestoreTask(File backupFile, BackupRestoreService service) {
      this.backupFile = backupFile;
      this.service = service;
    }

    @Override
    protected Boolean call() throws Exception {
      return service.restoreDatabase(backupFile);
    }

  }

}
