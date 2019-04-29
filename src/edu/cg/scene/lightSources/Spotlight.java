package edu.cg.scene.lightSources;

import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class Spotlight extends PointLight {
	private Vec direction;
	
	public Spotlight initDirection(Vec direction) {
		this.direction = direction;
		return this;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Spotlight: " + endl +
				description() + 
				"Direction: " + direction + endl;
	}
	
	@Override
	public Spotlight initPosition(Point position) {
		return (Spotlight)super.initPosition(position);
	}
	
	@Override
	public Spotlight initIntensity(Vec intensity) {
		return (Spotlight)super.initIntensity(intensity);
	}
	
	@Override
	public Spotlight initDecayFactors(double q, double l, double c) {
		return (Spotlight)super.initDecayFactors(q, l, c);
	}

	@Override
	public Vec intensity(Point hittingPoint, Ray rayToLight) {
		double distance = position.dist(rayToLight.source());
		double Fatt =getFatt(distance);
		double VVd = direction.dot(rayToLight.direction().neg());
		//double VVd = new Vec(hittingPoint.sub(position)).dot(direction);
		Vec IL = intensity.mult(VVd /Fatt);
		return IL;
	}
}
