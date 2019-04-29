package edu.cg.scene.camera;

import edu.cg.algebra.Ops;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class PinholeCamera {
	//TODO Add your fields done
	Point cameraPosition;
	Point centerPoint;
	Vec towardsVec;
	Vec upVec;
	Vec rightVec;
	double distanceToPlain;
	double viewPlainWidth;
	double ratio;
	double width;
	double height;
	/**
	 * Initializes a pinhole camera model with default resolution 200X200 (RxXRy) and image width 2.
	 * @param cameraPosition - The position of the camera.
	 * @param towardsVec - The towards vector of the camera (not necessarily normalized).
	 * @param upVec - The up vector of the camera.
	 * @param distanceToPlain - The distance of the camera (position) to the center point of the image-plain.
	 *
	 */
	public PinholeCamera(Point cameraPosition, Vec towardsVec, Vec upVec, double distanceToPlain) {
		// TODO: Initialize your fields done
		this.cameraPosition = cameraPosition;
		this.towardsVec = towardsVec.normalize();
		this.distanceToPlain = distanceToPlain;
		this.centerPoint = new Ray(cameraPosition, towardsVec).add(distanceToPlain);
		this.rightVec = towardsVec.cross(upVec).normalize();
		this.upVec = rightVec.cross(towardsVec).normalize();
		this.width = 200.0;
		this.height = 200.0;
		this.viewPlainWidth = 2.0;
	}
	/**
	 * Initializes the resolution and width of the image.
	 * @param height - the number of pixels in the y direction.
	 * @param width - the number of pixels in the x direction.
	 * @param viewPlainWidth - the width of the image plain in world coordinates.
	 */
	public void initResolution(int height, int width, double viewPlainWidth)
	{
		//TODO: init your fields done
		this.width = width;
		this.height = height;
		this.viewPlainWidth = viewPlainWidth;
		this.ratio =  viewPlainWidth/width;
	}

	/**
	 * Transforms from pixel coordinates to the center point of the corresponding pixel in model coordinates.
	 * @param x - the index of the x direction of the pixel.
	 * @param y - the index of the y direction of the pixel.
	 * @return the middle point of the pixel (x,y) in the model coordinates.
	 */
	public Point transform(int x, int y)
	{

		this.ratio =  viewPlainWidth/width;

		Point Pc = centerPoint;

		double RightScalar = x - Math.floor(width/2.0);
		Vec RightComponent = rightVec.mult(RightScalar * ratio);

		double UpScalar = -1 * (y - Math.floor(height/2.0));
		Vec UpComponent = upVec.mult(UpScalar * ratio);

		Point transformPoint = Pc.add(RightComponent.add(UpComponent));
		return transformPoint;
	}

	/**
	 * Returns a copy of the camera position
	 * @return a "new" point representing the camera position.
	 */
	public Point getCameraPosition() {
		//return new Point(this.cameraPosition.x, this.cameraPosition.y, this.cameraPosition.z);
		return cameraPosition;
	}
}
