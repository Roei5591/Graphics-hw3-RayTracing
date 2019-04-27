package edu.cg.scene.lightSources;

import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class PointLight extends Light {
	protected Point position;
	
	//Decay factors:
	protected double kq = 0.01;
	protected double kl = 0.1;
	protected double kc = 1;
	
	protected String description() {
		String endl = System.lineSeparator();
		return "Intensity: " + intensity + endl +
				"Position: " + position + endl +
				"Decay factors: kq = " + kq + ", kl = " + kl + ", kc = " + kc + endl;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Point Light:" + endl + description();
	}
	
	@Override
	public PointLight initIntensity(Vec intensity) {
		return (PointLight)super.initIntensity(intensity);
	}
	
	public PointLight initPosition(Point position) {
		this.position = position;
		return this;
	}
	
	public PointLight initDecayFactors(double kq, double kl, double kc) {
		this.kq = kq;
		this.kl = kl;
		this.kc = kc;
		return this;
	}

	@Override
	public Ray rayToLight(Point fromPoint) {
		// TODO Auto-generated method stub done
		return new Ray(fromPoint, position);
	}

	@Override
	public boolean isOccludedBy(Surface surface, Ray rayToLight) {
		// TODO Auto-generated method stub done
		Hit hitSurface = surface.intersect(rayToLight);
		if( hitSurface != null && hitSurface.t() <  position.dist(rayToLight.source())){
			return true;
		}
		return false;
	}

	@Override
	public Vec intensity(Point hittingPoint, Ray rayToLight) {
		// TODO Auto-generated method stub done
		double distance = position.dist(rayToLight.source());
		double Fatt = kq * Math.pow(distance, 2) + kl * distance + kc;
		Vec IL = intensity.mult(1.0 /Fatt);
		return IL;
	}

	//TODO: add some methods done
}
