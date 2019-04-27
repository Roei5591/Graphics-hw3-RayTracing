package edu.cg.scene.lightSources;

import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class DirectionalLight extends Light {
	private Vec direction = new Vec(0, -1, -1);

	public DirectionalLight initDirection(Vec direction) {
		this.direction = direction;
		return this;
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Directional Light:" + endl + super.toString() +
				"Direction: " + direction + endl;
	}

	@Override
	public DirectionalLight initIntensity(Vec intensity) {
		return (DirectionalLight)super.initIntensity(intensity);
	}

	@Override
	public Ray rayToLight(Point fromPoint) {
		// TODO Auto-generated method stub done
		return new Ray(fromPoint , direction.neg());
	}

	@Override
	public boolean isOccludedBy(Surface surface, Ray rayToLight) {
		// TODO Auto-generated method stub done
        Hit hitSurface = surface.intersect(rayToLight);
        if( hitSurface != null ){
            return true;
        }
        return false;
	}

	@Override
	public Vec intensity(Point hittingPoint, Ray rayToLight) {
		// TODO Auto-generated method stub done
		return new Vec(intensity);
	}
	
	//TODO: add some methods
}
