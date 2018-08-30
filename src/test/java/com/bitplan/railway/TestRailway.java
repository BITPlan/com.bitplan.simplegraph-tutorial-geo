package com.bitplan.railway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import java.io.File;

import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.excel.ExcelSystem;

/**
 * test railway example
 * 
 * @author wf
 *
 */
public class TestRailway {

  public static boolean debug = false;

  @Test
  public void testReadStations() throws Exception {

    /**
     * we get our data from some excel tables
     */
    File railwayExcel = new File("src/test/data/railway/Railway.xlsx");
    assertTrue(railwayExcel.exists());

    String entityNames[] = { "City", "Station", "Route" };
    int expectedCount[] = { 12, 18, 4 };
    /**
     * read the cities excel file and create CityNodes
     */
    debug = true;
    ExcelSystem es = new ExcelSystem();
    es.connect();
    es.moveTo(railwayExcel.toURI().toString());
    if (debug)
      es.forAll(SimpleNode.printDebug);

    for (int i = 0; i < entityNames.length; i++) {
      String entityName = entityNames[i];

      long entityCount = es.g().V().hasLabel(entityName).count().next()
          .longValue();
      assertEquals(expectedCount[i], entityCount);
    }

    /**
     * add edge from current city to current railway station
     * 
     * sg.g().V().has("city", cityName).addE("station").to(rs.getVertex()); //
     * error - no edges
     */

    // sg.g().V().has("city").group().outE("station");
  }
}
