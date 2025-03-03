package nl.ealse.ccnl.ledenadministratie.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.ealse.ccnl.ledenadministratie.excel.base.AdresColumnDefinition;
import nl.ealse.ccnl.ledenadministratie.excel.base.CCNLBestand;
import nl.ealse.ccnl.ledenadministratie.excel.base.ColumnDefinition;
import nl.ealse.ccnl.ledenadministratie.excel.lid.LidColumnDefinition;
import nl.ealse.ccnl.ledenadministratie.excelexport.CommercialPartnerAddressExport;
import nl.ealse.ccnl.ledenadministratie.excelexport.ExternalClubExport;
import nl.ealse.ccnl.ledenadministratie.excelexport.InternalRelationAddressExport;
import nl.ealse.ccnl.ledenadministratie.excelexport.MemberAddressExport;
import nl.ealse.ccnl.ledenadministratie.model.ExternalRelation;
import nl.ealse.ccnl.ledenadministratie.model.ExternalRelationClub;
import nl.ealse.ccnl.ledenadministratie.model.InternalRelation;
import nl.ealse.ccnl.ledenadministratie.model.Member;

public class Adresbestand extends CCNLBestand {

  public Adresbestand(File bestand, CCNLColumnProperties properties) throws IOException {
    super(bestand, properties);
    addSheet("Adressen");
  }

  public void addHeading() {
    addRow();
    List<ColumnDefinition> list = new ArrayList<>();
    for (int ix = 0 ; ix < 4 ; ix++) {
      list.add(LidColumnDefinition.values()[ix]);
    }
    list.addAll(Arrays.asList(AdresColumnDefinition.values()));
    list.forEach(c -> addCell(c.heading(), getProperties().getKolomnummer(c)));
  }

  public void addMember(Member member) {
    MemberAddressExport.addMember(this, member);
  }

  public void addClub(ExternalRelationClub club) {
    ExternalClubExport.addExternalRelation(this, club);
  }

  public void addExternalRelation(ExternalRelation relation) {
    CommercialPartnerAddressExport.addExternalRelation(this, relation);
  }

  public void addInternalRelation(InternalRelation relation) {
    InternalRelationAddressExport.addInternalRelation(this, relation);
  }

}
