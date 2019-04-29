package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.*;

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
	public Hit intersect(final Ray ray) {
		double tMin = -1 * Ops.infinity;
		double tMax = Ops.infinity;
		final double[] rayPoint = ray.source().asArray();
		final double[] rayDirection = ray.direction().asArray();
		final double[] minP = this.minPoint.asArray();
		final double[] maxP = this.maxPoint.asArray();
		for (int i = 0; i < 3; i++)
		{
			if (Math.abs(rayDirection[i]) <= Ops.epsilon) {
				if (rayPoint[i] < minP[i] || rayPoint[i] > maxP[i])
				{
					return null;
				}
			}
			else {
				double t1 = (minP[i] - rayPoint[i]) / rayDirection[i];
				double t2 = (maxP[i] - rayPoint[i]) / rayDirection[i];

				if (Double.isNaN(t1) || Double.isNaN(t2))
				{
					return null;
				}

				tMin = max(tMin, min(t1, t2)); // max of all min
				tMax = min(tMax, max(t1, t2)); // min of all max

				if (tMin > tMax || tMax < Ops.epsilon) {
					return null;
				}
			}
		}

		return tMin < Ops.epsilon ?
				new Hit(tMax, findNormalOnIntersectPoint(ray.add(tMax)).neg()).setWithin()
				: new Hit(tMin, findNormalOnIntersectPoint(ray.add(tMin))).setOutside();
	}


	public Vec findNormalOnIntersectPoint(Point IntersectPoint)
	{
		Vec normalOnIntersectPoint = null;

		if(Math.abs(IntersectPoint.x - minPoint.x) < Ops.epsilon)
		{
			normalOnIntersectPoint = new Vec(-1 ,0 ,0);
		}
		else if(Math.abs(IntersectPoint.x - maxPoint.x) < Ops.epsilon)
		{
			normalOnIntersectPoint = new Vec(1 ,0 ,0);
		}
		else if(Math.abs(IntersectPoint.y - minPoint.y) < Ops.epsilon)
		{
			normalOnIntersectPoint = new Vec( 0,-1 ,0);
		}
		else if(Math.abs(IntersectPoint.y - maxPoint.y) < Ops.epsilon)
		{
			normalOnIntersectPoint = new Vec(0 ,1 ,0);
		}
		else if(Math.abs(IntersectPoint.z - minPoint.z) < Ops.epsilon)
		{
			normalOnIntersectPoint = new Vec(0 ,0 ,-1);
		}
		else if(Math.abs(IntersectPoint.z - maxPoint.z) < Ops.epsilon)
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
