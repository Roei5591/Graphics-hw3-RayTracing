package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.*;

public class Sphere extends Shape {
	private Point center;
	private double radius;

	public Sphere(Point center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	public Sphere() {
		this(new Point(0, -0.5, -6), 0.5);
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Sphere:" + endl + "Center: " + center + endl + "Radius: " + radius + endl;
	}

	public Sphere initCenter(Point center) {
		this.center = center;
		return this;
	}

	public Sphere initRadius(double radius) {
		this.radius = radius;
		return this;
	}

    @Override
    public Hit intersect(Ray ray) {
        double a = 1;
        double b = ray.direction().mult(2.0).dot(ray.source().sub(center));
        double c = ray.source().distSqr(center) - Math.pow(radius, 2);
        double delta = Math.pow(b, 2) - (4 * a * c) ;
        if(delta < 0) {
            return null;
        }
        if(delta == 0){
            double t = -b / ( 2 * a);
            if(t < Ops.epsilon || t > Ops.infinity){
                return null;
            }
            Vec norm = ray.add(t).sub(center).normalize();
            return new Hit(t, norm);
        }
        //delta > 0
        double t1 = (-b + Math.sqrt(delta)) / ( 2 * a);
        double t2 = (-b - Math.sqrt(delta)) / ( 2 * a);
        if(t1 < Ops.epsilon) return null;// both are behind
        double t = t2;
        boolean isWithin = false;
        Vec norm = ray.add(t).sub(center).normalize();
        if(t2 < Ops.epsilon) {// we are inside
            t = t1;
            isWithin = true;
            norm = ray.add(t).sub(center).normalize().neg();
        }
        if(t > Ops.infinity){
            return null;
        }
        return new Hit(t, norm).setIsWithin(isWithin);
    }
}
