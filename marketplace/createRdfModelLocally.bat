
REM This script builds the RDF model outside of the Web application and stores it in the file as specified in "eu.bigiot.marketplace.database.BIGIoT_DAO.rdfFilePath".

CALL gradle createRDF
CALL gradle importFiles -Pfiles=vocabularies
CALL gradle importFiles -Pfiles=samples
CALL gradle applyRules
