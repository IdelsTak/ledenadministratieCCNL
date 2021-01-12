package nl.ealse.ccnl.ledenadministratie.model.dao;

import java.util.List;
import nl.ealse.ccnl.ledenadministratie.model.Document;
import nl.ealse.ccnl.ledenadministratie.model.DocumentType;
import nl.ealse.ccnl.ledenadministratie.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

  List<Document> findByDocumentType(DocumentType type);

  List<Document> findByOwnerOrderByCreationDateDesc(Member owner);

  List<Document> findByOwnerAndDocumentType(Member owner, DocumentType type);



}
