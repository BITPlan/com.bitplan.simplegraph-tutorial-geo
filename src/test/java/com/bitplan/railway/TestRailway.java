package com.bitplan.railway;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.excel.ExcelSystem;
import com.bitplan.simplegraph.impl.SimpleGraphImpl;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * test railway example
 * @author wf
 *
 */
public class TestRailway {

  public static boolean debug=false;
  
  @Test
  public void testReadStations() throws Exception {
    
    /**
     * we get our data from some excel tables
     */
    String testBase="src/test/data/railway/";
    String testCities = testBase+"cities.xlsx";
    String testStations = testBase+"railway-stations.xlsx";
    String testStredas = testBase+"railway-db-streda.xlsx";
    
    // create cities
    ExcelSystem ec = new ExcelSystem();
    ec.connect();
    File cityFile = new File(testCities);
    ec.moveTo(cityFile.toURI().toString());
    debug = true;
    // if (debug) ec.forAll(SimpleNode.printDebug);
    long cityCount = ec.g().V().has("city").count().next().longValue();
    System.out.println("amount cities: " + cityCount);

    List<Object> cityList = ec.g().V().values("city").toList();

    SimpleGraphImpl sg = new SimpleGraphImpl(null);
    for (int i = 0; i < cityCount; i++) {
      String cityName = (String) cityList.get(i);
      List<Object> geoVal = ec.g().V().has("city", cityName).values("geo")
          .toList();

      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("category", "city");
      map.put("name", cityName);
      map.put("geo", geoVal.get(0));

      //MNode mn = new MNode(sg, "city", new String[2]); // null);
      //mn.setVertexFromMap(map);
    }
    
    // create stations
    ExcelSystem es = new ExcelSystem();
    es.connect();
    File stationFile = new File(testStations);
    es.moveTo(stationFile.toURI().toString());
    long stationCount = es.g().V().count().next().longValue();
    long stationCities = es.g().V().values("city").dedup().count().next()
        .longValue();
    System.out.println("amount stations: " + stationCount);
    System.out.println("amount station cities: " + stationCities);

    for (int i = 0; i < stationCities; i++) {
      String cityName = (String) cityList.get(i);

      List<Object> stations = es.g().V().has("city", cityName)
          .values("Station-name").toList();
      System.out.println(
          (i + 1) + ", " + cityName + ", stations: " + stations.size());
      if (stations.size() > 0) {
        for (int j = 0; j < stations.size(); j++) {
          HashMap<String, Object> map = new HashMap<String, Object>();

          String stationName = (String) stations.get(j);
          System.out.print("    " + stationName);
          map.put("city", cityName);
          map.put("name", stationName);
          map.put("category", "station");
          map.put("type", "railway");

          List<Object> geoVal = es.g().V().has("Station-name", stationName)
              .values("geo").toList();

          System.out.print(", geo: " + geoVal);
          map.put("geo", geoVal.size() > 0 ? geoVal.get(0) : "");

          List<Object> lineNums = es.g().V().has("Station-name", stationName)
              .values("Line-number").toList();

          if (lineNums.size() > 0) {
            String[] lines = ((String) lineNums.get(0)).split(",");
            for (int k = 0; k < lines.length; k++) {
              System.out.print(", " + lines[k]);
              map.put("linenumber", lines[k]);
            }
          }
          System.out.println();

          RailwayStationNode rs = new RailwayStationNode(sg, "station", new String[2]); // null);
          rs.setVertexFromMap(map);

          // add edge from city to station
          sg.g().V().has("city", cityName).addE("station").to(rs.getVertex());
          // error - no edges
        }
      }
    }
    System.out.println();
    sg.forAll(SimpleNode.printDebug);

    // sg.g().V().has("city").group().outE("station");
    System.out.println();
  }
  
  class RailwayStationNode extends SimpleNodeImpl{

		public RailwayStationNode(SimpleGraph simpleGraph, String kind, String[] keys) {
			super(simpleGraph, kind, keys);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Map<String, Object> initMap() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Stream<SimpleNode> out(String edgeName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Stream<SimpleNode> in(String edgeName) {
			// TODO Auto-generated method stub
			return null;
		}
  	
  }
  

}
