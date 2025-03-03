package nl.ealse.ccnl.ledenadministratie.excel.lid;

import java.io.EOFException;
import nl.ealse.ccnl.ledenadministratie.excel.CCNLColumnProperties;
import nl.ealse.ccnl.ledenadministratie.excel.base.CCNLSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class CCNLLidSheet extends CCNLSheet<CCNLLid> {

  public CCNLLidSheet(Sheet sheet, CCNLColumnProperties properties) {
    super(sheet, properties);
  }

  @Override
  protected CCNLLid newInstance(Row currentRow) throws EOFException {
    return new CCNLLid(currentRow, getProperties());
  }

}
