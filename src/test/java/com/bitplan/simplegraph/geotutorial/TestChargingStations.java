/**
 * Copyright (c) 2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.simplegraph-tutorial-geo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.simplegraph.geotutorial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Locale;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.excel.ExcelSystem;
import com.bitplan.simplegraph.json.JsonSystem;

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
    es.g().V().has("row").limit(100).forEachRemaining(v -> {
      if (debug)
        SimpleNode.printDebug.accept(v);
      String address = v.property("Adresse").value().toString();
      String ziploc = v.property("Postleitzahl Ort").value().toString();
      double lat = Double
          .parseDouble(v.property("Breitengrad [DG]").value().toString());
      double lon = Double
          .parseDouble(v.property("Längengrad [DG]").value().toString());
      System.out.println(
          String.format("%30s %30s %.4f %.4f", ziploc, address, lat, lon));
      // get up to 3 stations nearby
      int maxresults = 3;
      String apiUrl = String.format(Locale.ENGLISH,
          "http://api.openchargemap.io/v2/poi/?output=json&latitude=%.4f8&longitude=%.4f&maxresults=10",
          lat, lon, maxresults);
      JsonSystem js = new JsonSystem();
      try {
        js.connect();
      } catch (Exception e) {
        fail(e.getMessage());
      }
      js.moveTo(apiUrl);
      
      GraphTraversal<Vertex, Vertex> addressesByDistance = js.g().V().hasLabel("AddressInfo").order().by("Distance");
      assertTrue(addressesByDistance.hasNext());
      Vertex av = addressesByDistance.next();
      double maxDist=1.0;
      Number adistance = (Number) av.property("Distance").value();
      Number alat=(Number)av.property("Latitude").value();
      Number alon=(Number)av.property("Longitude").value();
      // assertTrue(adistance.doubleValue()<maxDist);
      String azip=av.property("Postcode").value().toString();
      String acity=av.property("Town").value().toString();
      String aadr=av.property("AddressLine1").value().toString();
      
      System.out.println(
          String.format("%30s %30s %.4f %.4f: %.3f km", azip+" "+acity, aadr, alat.doubleValue(), alon.doubleValue(),adistance.doubleValue()));
      
      if (debug) {
        SimpleNode.printDebug.accept(av);
        SimpleNode.dumpGraph(js.graph());
      }
       
    });

  }

}
