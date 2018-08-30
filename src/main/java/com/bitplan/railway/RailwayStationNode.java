package com.bitplan.railway;

import java.util.Map;
import java.util.stream.Stream;

public class RailwayStationNode extends SimpleNodeImpl{

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
