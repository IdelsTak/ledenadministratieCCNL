package nl.ealse.ccnl.control;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nl.ealse.ccnl.control.exception.PDFViewerException;
import nl.ealse.ccnl.ledenadministratie.model.Document;
import nl.ealse.ccnl.ledenadministratie.model.Member;
import nl.ealse.javafx.ImagesMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * Simple PDF-view with paging capability.
 * @author ealse
 *
 */
@Slf4j
public class PDFViewer extends BorderPane {

  private static final String HEADER_TEXT = "Pagina %d van %d";

  private Scene scene;

  private Stage pdfStage;

  /**
   * Title of the popup window of the PDF-viewer.
   */
  @Getter
  @Setter
  private Object windowTitle;


  private Label header;

  private Button prevButton;

  private Button nextButton;

  private PDDocument document;

  private int pageNum;

  private int pages;

  @Getter
  private byte[] pdf;

  public PDFViewer() {
    this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
        CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    this.setPadding(new Insets(5, 0, 5, 0));
  }

  /**
   * Show the PDF page by page
   * @param pdf binary PDF-content
   * @param member PDF-owner
   */
  public void showPDF(byte[] pdf, Member member) {
    this.pdf = pdf;
    try {
      document = PDDocument.load(pdf);
      pages = document.getPages().getCount();
      if (pages == 1) {
        initializeSinglePage();
      } else {
        initializeMultiPage();
      }
      pdfStage.show();
      pageNum = 0;
      showPage(member);
    } catch(IOException e) {
      log.error("Error rendering PDF", e);
      throw new PDFViewerException("Error rendering PDF", e);
    }
  }

  /**
   * Show a PDF for the document owner
   * @param pdf - document to show (PDF +owner)
   */
  public void showPDF(Document pdf) {
    showPDF(pdf.getPdf(), pdf.getOwner());
  }

  /**
   * Display aPDF-document
   * @param selectedFile PDF-file
   * @param member the owner of the PDF-document
   */
  public void showPDF(File selectedFile, Member member) {
    showPDF(toByteArray(selectedFile), member);
  }

  /**
   * 
   */
  public void close() {
    pdfStage.close();
  }

  private void initializeScene() {
    if (scene == null) {
      scene = new Scene(getParent());
      pdfStage = new Stage();
      pdfStage.initModality(Modality.APPLICATION_MODAL);
      pdfStage.setAlwaysOnTop(true);
      pdfStage.getIcons().add(ImagesMap.get("Citroen.png"));
      pdfStage.setScene(scene);
    }
  }

  /**
   * Initialize for a single page PDF
   */
  private void initializeSinglePage() {
    Region parent = (Region) getParent();
    parent.setPrefWidth(620);
    parent.setPrefHeight(920);

    initializeScene();

    this.setRight(null);
    this.setLeft(null);
    this.setTop(null);
  }

  /**
   * Initialize for a mult page PDF
   */
  private void initializeMultiPage() {
    Region parent = (Region) getParent();
    parent.setPrefWidth(737);
    parent.setPrefHeight(965);

    initializeScene();

    nextButton = new PagingButton("\u00BB");
    nextButton.setOnAction(e -> nextPage());
    this.setRight(nextButton);
    prevButton = new PagingButton("\u00AB");
    prevButton.setOnAction(e -> previousPage());
    prevButton.setDisable(true);
    this.setLeft(prevButton);

    header = new Label();
    header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    BorderPane.setAlignment(header, Pos.CENTER);
    this.setTop(header);
  }

  private void previousPage() {
    pageNum--;
    newPage();
  }

  private void nextPage() {
    pageNum++;
    newPage();
  }

  private void newPage() {
    nextButton.setDisable(pageNum == pages - 1);
    prevButton.setDisable(pageNum == 0);
    showPage();
  }

  private void showPage(Member member) {
    pdfStage.setTitle(String.format(windowTitle.toString(), member.getId(), member.getFullName()));
    showPage();
  }

  private void showPage() {
    try {
      PDFRenderer renderer = new PDFRenderer(document);
      BufferedImage bufferedImage = renderer.renderImage(pageNum);
      Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
      ImageView imageView = new ImageView(fxImage);
      this.setCenter(imageView);
      if (pages > 1) {
        header.setText(String.format(HEADER_TEXT, pageNum + 1, pages));
      }
    } catch (IOException e) {
      log.error("Error getting page", e);
      Thread.currentThread().interrupt();
      throw new PDFViewerException("Error getting page", e);
    }

  }

  private byte[] toByteArray(File selectedFile) {
    try (FileInputStream fis = new FileInputStream(selectedFile)) {
      return fis.readAllBytes();
    } catch (IOException e) {
      log.error("Error reading file", e);
      throw new PDFViewerException(e);
    }
  }

  private static class PagingButton extends Button {

    public PagingButton(String text) {
      initialize(text);
    }

    private void initialize(String text) {
      setText(text);
      setStyle("-fx-font-size: 18px; -fx-font-weight: bold;" + "-fx-background-radius: 5em; "
          + "-fx-min-width: 30px; " + "-fx-min-height: 30px;"
          + "-fx-max-width: 30px; -fx-alignment: top-center;"
          + "-fx-max-height: 30px; -fx-padding: 0px;");
      BorderPane.setAlignment(this, Pos.CENTER);
      BorderPane.setMargin(this, new Insets(15));
    }

  }

}
