package com.bitplan.simplegraph.geotutorial;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.excel.ExcelSystem;

public class TestChargingStations {

  boolean debug = false;

  /**
   * test reading the list of registered german charging stations from
   * Bundesnetzagentur
   * 
   * @throws Exception
   */
  @Test
  public void testBundesnetzagentur() throws Exception {
    // The original file has some superfluous sheets and the title row is not
    // in the first line so we downloaded and adapted it a bit to avoid to do
    // this in software e.g.
    // as outlined in
    // https://stackoverflow.com/questions/1834971/removing-a-row-from-an-excel-sheet-with-apache-poi-hssf
    // String url =
    // "https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Energie/Unternehmen_Institutionen/HandelundVertrieb/Ladesaeulen/Ladesaeulenkarte_Datenbankauszug20.xlsx?__blob=publicationFile&v=2";
    File excelFile = new File(
        "src/test/data/Bundesnetzagentur/Ladesaeulenkarte_Datenbankauszug20.xlsx");
    ExcelSystem es = new ExcelSystem();
    es.connect();
    es.moveTo(excelFile.toURI().toString());
    long count = es.g().V().count().next().longValue();
    assertEquals(7733, count);
    if (debug)
      SimpleNode.dumpGraph(es.graph());
    es.g().V().has("row").forEachRemaining(v -> {
      if (debug)
        SimpleNode.printDebug.accept(v);
      String address = v.property("Adresse").value().toString();
      String ziploc = v.property("Postleitzahl Ort").value().toString();
      double lat = Double
          .parseDouble(v.property("Breitengrad [DG]").value().toString());
      double lon = Double
          .parseDouble(v.property("Längengrad [DG]").value().toString());
      System.out.println(String.format("%30s %30s %5.2f %5.2f", ziploc, address, lat, lon));
    });
  }

}
