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

	//@Override
	public Hit intersect2(Ray ray)
	{
		//// TODO You need to implement. done
		//https://tavianator.com/fast-branchless-raybounding-box-intersections/

		double tmin = Double.NEGATIVE_INFINITY;
		double tmax = Double.POSITIVE_INFINITY;

		Hit hit = null;

		if(WillNeverMeet(ray)){
			return null;
		}

		double tx1;
		double tx2;
		if(Math.abs(ray.direction().x) > Ops.epsilon){
			tx1 = (minPoint.x - ray.source().x)/ ray.direction().x;
			tx2 = (maxPoint.x - ray.source().x)/ ray.direction().x;
			tmin = max(tmin, min(tx1, tx2));
			tmax = min(tmax, max(tx1, tx2));
			if(tmax < tmin || tmax < Ops.epsilon){
				return null;
			}
		}else{
			//when ray direction is 0 we will meet at the beginning or never meet
			if (Math.abs(ray.source().x - minPoint.x) > Ops.epsilon) {
				tx1 = Ops.infinity;
			}else{
				tx1 = 0;
			}

			if (Math.abs(ray.source().x - maxPoint.x) > Ops.epsilon) {
				tx2 = Ops.infinity;
			}else{
				tx2 = 0;
			}
		}
		return null;
	}

	private boolean WillNeverMeet(Ray ray) {
		double[] raySource = ray.source().asArray();
		double[] rayDirection = ray.direction().asArray();
		double[] minPoint = this.minPoint.asArray();
		double[] maxPoint = this.maxPoint.asArray();
		for (int i = 0; i < 3; i++) {
			if (Math.abs(rayDirection[i]) <= Ops.epsilon) {
				if (raySource[i] < minPoint[i] || raySource[i] > maxPoint[i]) {
					return true;
				}
			}
		}
		return false;
	}

	//@Override
	public Hit intersect22(final Ray ray)
	{

		if(ray.direction().x == Double.NaN)
		{
			System.out.println("NABA");
		}
		double t1;
		double t2;
		double tmin = -Ops.infinity;
		double tmax = Ops.infinity;
		double[] sourcePoint = ray.source().asArray();
		double[] direction = ray.direction().asArray();
		double[] minPoint = this.minPoint.asArray();
		double[] maxPoint = this.maxPoint.asArray();

		for (int i = 0; i < 3; ++i)
		{
			if (Math.abs(direction[i]) > Ops.epsilon)
			{
				if(Math.abs(minPoint[i] - sourcePoint[i]) == Ops.epsilon || Math.abs(maxPoint[i] - sourcePoint[i]) == Ops.epsilon)
				{
					System.out.println("WTFFFFFFFF");
				}

				t1 = (minPoint[i] - sourcePoint[i]) / direction[i];
				t2 = (maxPoint[i] - sourcePoint[i]) / direction[i];


				tmin = max(tmin, min(t1, t2));
				tmax = min(tmax, max(t1, t2));

				if (tmax < tmin || tmax < Ops.epsilon)
				{
					return null;
				}

			}
			else
				{
					if (sourcePoint[i] < minPoint[i] || sourcePoint[i] > maxPoint[i])
					{
						return null;
					}
				}
		}

		Hit hit;
		Vec norm;

		if (tmin < Ops.epsilon) //inside
		{
			norm = this.normal(ray.add(tmax));
			hit = new Hit(tmax , norm);
			hit.setWithin();

		}
		else //outside
		{
			norm = this.normal(ray.add(tmin));
			hit = new Hit(tmin , norm);
			hit.setOutside();
		}

		return hit;
	}

	@Override
	public Hit intersect(final Ray ray) {
		double tNear = -1.0E8;
		double tFar = 1.0E8;
		final double[] rayP = ray.source().asArray();
		final double[] rayD = ray.direction().asArray();
		final double[] minP = this.minPoint.asArray();
		final double[] maxP = this.maxPoint.asArray();
		for (int i = 0; i < 3; ++i)
		{
			if (Math.abs(rayD[i]) <= 1.0E-5) {
				if (rayP[i] < minP[i] || rayP[i] > maxP[i])
				{
					return null;
				}
			}
			else {
				double t1 = (minP[i] - rayP[i]) / rayD[i];
				double t2 = (maxP[i] - rayP[i]) / rayD[i];

				if (Double.isNaN(t1) || Double.isNaN(t2))
				{
					return null;
				}

				tNear = max(tNear, min(t1, t2));
				tFar = min(tFar, max(t1, t2));

				if (tNear > tFar || tFar < 1.0E-5) {
					return null;
				}
			}
		}

		if (tNear < 1.0E-5)
		{
			return new Hit(tFar, this.normal(ray.add(tFar)).neg()).setWithin();
		}
		else
		{
			return new Hit(tNear, this.normal(ray.add(tNear)));
		}


	}

	private static double findIntersectionParameter(final double a, final double b, final double c) {
		if (Math.abs(a) < 1.0E-5 && Math.abs(b - c) > 1.0E-5) {
			return 1.0E8;
		}
		if (Math.abs(a) < 1.0E-5 && Math.abs(b - c) < 1.0E-5) {
			return 0.0;
		}
		final double t = (c - b) / a;
		return t;
	}


	private Vec normal(final Point p) {
		if (Math.abs(p.z - this.minPoint.z) <= 1.0E-5) {
			return new Vec(0.0, 0.0, -1.0);
		}
		if (Math.abs(p.z - this.maxPoint.z) <= 1.0E-5) {
			return new Vec(0.0, 0.0, 1.0);
		}
		if (Math.abs(p.y - this.minPoint.y) <= 1.0E-5) {
			return new Vec(0.0, -1.0, 0.0);
		}
		if (Math.abs(p.y - this.maxPoint.y) <= 1.0E-5) {
			return new Vec(0.0, 1.0, 0.0);
		}
		if (Math.abs(p.x - this.minPoint.x) <= 1.0E-5) {
			return new Vec(-1.0, 0.0, 0.0);
		}
		if (Math.abs(p.x - this.maxPoint.x) <= 1.0E-5) {
			return new Vec(1.0, 0.0, 0.0);
		}
		return null;
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
