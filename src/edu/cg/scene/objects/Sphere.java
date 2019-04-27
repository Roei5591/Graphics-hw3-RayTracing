package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

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


	public Hit intersect1(Ray ray)
	{
		return null;
	}





	@Override
	public Hit intersect(Ray ray) {
		// TODO: implement this method. done
		Point sourcePoint = ray.source(); //p
		Vec directionVec = ray.direction(); //d
		//Vec projectFromCenter = sourcePoint.sub(center);// p to c
		Vec projectFromCenter = center.sub(sourcePoint);

		//sourcePoint + (directionVec.projectFromCenter) * directionVec
		//http://www.lighthouse3d.com/tutorials/maths/ray-sphere-intersection/
		Point pc = sourcePoint.add(directionVec.mult(directionVec.dot(projectFromCenter)));

		double disOfRayFromCenter = pc.dist(center);// |c - pc|
		double dist = Math.sqrt(Math.pow(radius,2) - Math.pow(disOfRayFromCenter,2));

		Hit hit = null;
		double t;
		Point intersection;

		//Vec projectFromCenter = sourcePoint.sub(center);  // this is the vector from p to c

		if ( (projectFromCenter.dot(directionVec)) < 0) // when the sphere is behind the origin p
		{
			if (projectFromCenter.length() == radius) // when source point is on the sphere
			{
				intersection = sourcePoint;
				Vec normalToSphere = intersection.sub(center).normalize();
				hit = new Hit(0,normalToSphere);
			}
			else if ( projectFromCenter.length() < radius) //  when source point is inside the sphere
			{
				t = dist - pc.dist(sourcePoint);
				// intersetionPoint = sourcePoint + t * directionVe
				intersection = sourcePoint.add( directionVec.mult(t));

				Vec normalToSphere = intersection.sub(center).normalize();
				hit = new Hit(t,normalToSphere);
				hit.setWithin();
			}
		}
		else // center of sphere projects on the ray
		{
			if (disOfRayFromCenter <= radius)
			{
				boolean isWithin;

				if (projectFromCenter.length() > radius) // source point is outside the sphere
				{
					t = sourcePoint.dist(pc) - dist;
					isWithin = false;
				}
				else // source point is inside or on the sphere
				{
					t = sourcePoint.dist(pc) + dist;
					isWithin = true;
				}

				// intersetionPoint = sourcePoint + t * directionVe
				intersection = sourcePoint.add( directionVec.mult(t));

				Vec normalToSphere = intersection.sub(center).normalize();
				hit = new Hit(t,normalToSphere);
				hit.setIsWithin(isWithin);
			}
		}

		return hit;

	}
}
