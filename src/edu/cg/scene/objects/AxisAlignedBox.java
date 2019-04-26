package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class AxisAlignedBox extends Shape {
	private Point minPoint;
	private Point maxPoint;
	private String name = "";
	static private int CURR_IDX;

	/**
	 * Creates an axis aligned box with a specified minPoint and maxPoint.
	 */
	public AxisAlignedBox(Point minPoint, Point maxPoint) {
		this.minPoint = minPoint;
		this.maxPoint = maxPoint;
		name = new String("Box " + CURR_IDX);
		CURR_IDX += 1;
		fixBoundryPoints();
	}

	/**
	 * Creates a default axis aligned box with a specified minPoint and maxPoint.
	 */
	public AxisAlignedBox() {
		minPoint = new Point(-1.0, -1.0, -1.0);
		maxPoint = new Point(1.0, 1.0, 1.0);
	}
	
	/**
	 * This methods fixes the boundary points minPoint and maxPoint so that the values are consistent.
	 */
	private void fixBoundryPoints() {
		double min_x = min(minPoint.x, maxPoint.x), max_x = max(minPoint.x, maxPoint.x),
				min_y = min(minPoint.y, maxPoint.y), max_y = max(minPoint.y, maxPoint.y),
				min_z = min(minPoint.z, maxPoint.z), max_z = max(minPoint.z, maxPoint.z);
		minPoint = new Point(min_x, min_y, min_z);
		maxPoint = new Point(max_x, max_y, max_z);
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return name + endl + "Min Point: " + minPoint + endl + "Max Point: " + maxPoint + endl;
	}
	
	//Initializers
	public AxisAlignedBox initMinPoint(Point minPoint) {
		this.minPoint = minPoint;
		fixBoundryPoints();
		return this;
	}

	public AxisAlignedBox initMaxPoint(Point maxPoint) {
		this.maxPoint = maxPoint;
		fixBoundryPoints();
		return this;
	}

	@Override
	public Hit intersect(Ray ray)
	{
		// TODO You need to implement thismethod.
		double tmin = Double.NEGATIVE_INFINITY;
		double tmax = Double.POSITIVE_INFINITY;

		if (ray.direction().x != 0.0) {
			double tx1 = (minPoint.x - ray.source().x)/ ray.direction().x;
			double tx2 = (maxPoint.x - ray.source().x)/ ray.direction().x;
			tmin = max(tmin, min(tx1, tx2));
			tmax = min(tmax, max(tx1, tx2));
		}

		if (ray.direction().y != 0.0) {
			double ty1 = (minPoint.y - ray.source().y)/ ray.direction().y;
			double ty2 = (maxPoint.y - ray.source().y)/ ray.direction().y;

			tmin = max(tmin, min(ty1, ty2));
			tmax = min(tmax, max(ty1, ty2));
		}

		if (ray.direction().z != 0.0) {
			double tz1 = (minPoint.z - ray.source().z)/ ray.direction().z;
			double tz2 = (maxPoint.z - ray.source().z)/ ray.direction().z;

			tmin = max(tmin, min(tz1, tz2));
			tmax = min(tmax, max(tz1, tz2));
		}

		if (tmax >= tmin)
		{
			Point hitPoint = ray.source().add(tmin , ray.direction());
			return new Hit(tmin , findNormalOnIntersectPoint(hitPoint));
		}

			return null;
	}

	public Vec findNormalOnIntersectPoint(Point IntersectPoint)
	{
		Vec normalOnIntersectPoint = null;

		if(IntersectPoint.x == minPoint.x)
		{
			normalOnIntersectPoint = new Vec(-1 ,0 ,0);
		}
		else if(IntersectPoint.x == maxPoint.x)
		{
			normalOnIntersectPoint = new Vec(1 ,0 ,0);
		}
		else if(IntersectPoint.y == minPoint.y)
		{
			normalOnIntersectPoint = new Vec( 0,-1 ,0);
		}
		else if(IntersectPoint.y == maxPoint.y)
		{
			normalOnIntersectPoint = new Vec(0 ,1 ,0);
		}
		else if(IntersectPoint.z == minPoint.z)
		{
			normalOnIntersectPoint = new Vec(0 ,0 ,-1);
		}
		else if(IntersectPoint.z == maxPoint.z)
		{
			normalOnIntersectPoint = new Vec(0 ,0 ,1);
		}
		else
		{
			System.out.println("Error in  AxisAlignedBox");
		}

		return normalOnIntersectPoint;
	}



	
}
